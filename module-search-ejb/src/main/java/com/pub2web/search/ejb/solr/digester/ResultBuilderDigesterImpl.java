/*
 * ResultBuilderDigesterImpl
 * 
 * Copyright 2010 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.digester;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.pub2web.search.ejb.solr.ResultBuilder;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.CallParamRule;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.digester.Rules;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.ingenta.search.domain.SearchFailedException;
import com.ingenta.search.store.RawResult;
import com.ingenta.search.store.solr.SolrResult;

/**
 * Implementation of the <code>ResultBuilder</code> that builds the <code>RawResult</code>
 * by running Commons Digester (arrrgh) on the XML returned by SOLR.
 * @author jbeard
 */
public class ResultBuilderDigesterImpl implements ResultBuilder {

    private static final String XSLT_FILE = "result-builder-digester-impl.xsl";

    private static final Logger LOG = Logger.getLogger(ResultBuilderDigesterImpl.class);

    private final SolrRuleSet digesterRules;

    public ResultBuilderDigesterImpl() {
        digesterRules = new SolrRuleSet();
    }

    /**
     * Builds the result from the given search results XML.
     * @param start index of result on which to start
     * @param pageSize page size to indicate number of results to return
     * @return result object
     * @throws SearchFailedException if there was a problem building the result
     */
    @Override
    public RawResult buildResult(InputStream xmlStream, int start, int pageSize)
    throws SearchFailedException {


        RawResult result = new SolrResult(start, pageSize);
        Digester digester = new Digester();
        digester.push(result);
        digesterRules.addRuleInstances(digester);

        try {
            // First have to transform the XML returned by SOLR because Commons Digester is
            // unable to use XPath properly (no allowance for filtering on attributes)
            digester.parse(transformForDigester(xmlStream));
            if (LOG.isDebugEnabled()) {
                LOG.debug("After digesting, FacetElements are: " + result.getFacets());
            }
        }
        catch (TransformerException e) {
            LOG.error("Error transforming the XML using " + XSLT_FILE, e);
            throw new SearchFailedException("Got a TransformerException: " + e.getMessage());
        }
        catch (SAXException e) {
            LOG.error("Error parsing response body from Solr" ,e);
            throw new SearchFailedException("Error parsing response body from Solr: " + e.getMessage());
        }
        catch (IOException e) {
            LOG.error("Error retrieving response body from Solr", e);
            throw new SearchFailedException("Error retrieving response body from Solr: " + e.getMessage());
        }
        return result;
    }

    /**
     * Transforms the XML stream into something more useable by the Digester
     * @param xmlStream original stream
     * @return transformed stream
     * @throws javax.xml.transform.TransformerException if there was an error in the transformation
     */
    private InputStream transformForDigester(InputStream xmlStream)
    throws TransformerException {
        StringWriter writer = new StringWriter();
        InputStream xslt = getClass().getResourceAsStream(XSLT_FILE);
        if (xslt == null) {
            throw new IllegalStateException("Could not load XSLT file: " + XSLT_FILE);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Successfully loaded XSLT file: " + XSLT_FILE);
        }
        Transformer transformer =
            TransformerFactory.newInstance().newTransformer(new StreamSource(xslt));
        transformer.transform(new StreamSource(xmlStream), new StreamResult(writer));
        if (LOG.isDebugEnabled()) {
            LOG.debug("After transform, result xml is: " + writer.toString());
        }
        return new ByteArrayInputStream(writer.toString().getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Class that contains the digester rules used to construct the RawResult
     * from the XML returned by SOLR
     */
    private static class SolrRuleSet extends RuleSetBase {
        private Rules rules;

        public SolrRuleSet() {

            rules = new ExtendedBaseRules();
            rules.add("response/result", new CallMethodRule("setHitCount", 1, new Class[]{Integer.TYPE}));
            rules.add("response/result", new CallParamRule(0, "numFound"));
            rules.add("response/result/doc", new ObjectCreateRule(com.ingenta.search.store.Document.class));

            // multi-valued fields
            rules.add("response/result/doc/arr", new ObjectCreateRule(com.ingenta.search.store.Field.class));
            rules.add("response/result/doc/arr", new SetPropertiesRule()); // will call setName
            rules.add("response/result/doc/arr/*", new CallMethodRule("addValue", 1));
            rules.add("response/result/doc/arr/*", new CallParamRule(0));
            rules.add("response/result/doc/arr", new SetNextRule("addField"));

            // single-valued fields
            rules.add("response/result/doc/*", new ObjectCreateRule(com.ingenta.search.store.Field.class));
            rules.add("response/result/doc/*", new SetPropertiesRule()); // will call setName
            // for some reason the set-next rule needs to come before the call-method rule
            // because this actually ensures that set-next happens *after* call-method (sigh)
            rules.add("response/result/doc/*", new SetNextRule("addField"));
            rules.add("response/result/doc/*", new CallMethodRule("setValue", 1));
            rules.add("response/result/doc/*", new CallParamRule(0));

            rules.add("response/result/doc", new SetNextRule("addDocument"));

            // facets
            rules.add("response/lst/facets/lst", new ObjectCreateRule(com.ingenta.search.store.FacetElement.class));
            rules.add("response/lst/facets/lst", new SetPropertiesRule()); // will call setName, which is actually the unique id
            rules.add("response/lst/facets/lst/*", new ObjectCreateRule(com.ingenta.search.store.Field.class)); // create a field
            rules.add("response/lst/facets/lst/*", new SetPropertiesRule()); // will call setName
            rules.add("response/lst/facets/lst/*", new CallMethodRule("addValue", 1)); // set field value
            rules.add("response/lst/facets/lst/*", new CallParamRule(0));
            rules.add("response/lst/facets/lst/*", new SetNextRule("addField"));
            rules.add("response/lst/facets/lst", new SetNextRule("addFacetElement")); // add FacetElement to RawResult

            // highlighting rules
            rules.add("response/highlights/lst", new ObjectCreateRule(com.ingenta.search.store.Highlight.class)); // create highlight object
            rules.add("response/highlights/lst", new SetPropertiesRule()); // will call setName, which is actually the unique id
            rules.add("response/highlights/lst/arr", new ObjectCreateRule(com.ingenta.search.store.Field.class)); // create a field
            rules.add("response/highlights/lst/arr", new SetPropertiesRule()); // will call setName
            rules.add("response/highlights/lst/arr/*", new CallMethodRule("addValue", 1)); // set field value
            rules.add("response/highlights/lst/arr/*", new CallParamRule(0));
            rules.add("response/highlights/lst/arr", new SetNextRule("addField")); // add field to highlight object
            rules.add("response/highlights/lst", new SetNextRule("addHighlight")); // add highlight to SolrResult
            
         //suggestion rules
            rules.add("response/lst/suggestions/lst", new ObjectCreateRule(com.ingenta.search.store.Suggestion.class));
            rules.add("response/lst/suggestions/lst", new SetPropertiesRule()); 
            rules.add("response/lst/suggestions/lst/arr", new ObjectCreateRule(com.ingenta.search.store.Field.class));
            rules.add("response/lst/suggestions/lst/arr", new SetPropertiesRule()); 
            rules.add("response/lst/suggestions/lst/arr/*", new CallMethodRule("addValue", 1));
            rules.add("response/lst/suggestions/lst/arr/*", new CallParamRule(0));
            rules.add("response/lst/suggestions/lst/arr", new SetNextRule("addField")); 
            rules.add("response/lst/suggestions/lst", new SetNextRule("addSuggestion")); 
        }

        @Override
        public void addRuleInstances(Digester digester) {
            digester.setRules(rules);
        }
    }
}