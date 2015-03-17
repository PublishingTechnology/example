/*
 * SolrClientService
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.SessionContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

import com.ingenta.search.domain.SearchFailedException;
import com.ingenta.search.store.RawResult;
import com.pub2web.search.api.SearchService;
import com.pub2web.search.api.autosuggest.AutosuggestException;
import com.pub2web.search.api.autosuggest.AutosuggestResult;
import com.pub2web.search.api.morelikethis.MoreLikeThisResult;
import com.pub2web.search.ejb.solr.autosuggest.AutosuggestResultFactory;
import com.pub2web.search.ejb.solr.digester.ResultBuilderDigesterImpl;
import com.pub2web.search.ejb.solr.morelikethis.MoreLikeThisResultFactory;

/**
 * Implementation of the <code>SearchService</code> that uses SOLR.
 * 
 * @author jbeard
 */
public class SolrClientService implements SearchService {
    private static final String QUERY = "q";
    private static final String START = "start";
    private static final String PAGE_SIZE = "rows";
    private static final String FIELDS = "fl";
    private static final String SORT = "sort";

    // Facets
    private static final String FACET = "facet";
    private static final String FACET_FIELD = "facet.field";
    private static final String FACET_MINCOUNT = "facet.mincount";
    private static final String FACET_SORT = "facet.sort";
    private static final String FACET_QUERY = "facet.query";

    // Highlights
    private static final String HIGHLIGHT = "hl";
    private static final String HIGHLIGHT_FIELDS = "hl.fl";
    private static final String HIGHLIGHT_FRAGSIZE = "hl.fragsize";
    private static final String HIGHLIGHT_SIMPLE_PRE = "hl.simple.pre";
    private static final String HIGHLIGHT_SIMPLE_POST = "hl.simple.post";
    private static final String HIGHLIGHT_REQUIRE_FIELDMATCH = "hl.requireFieldMatch";

    // Autosuggest
    private static final String AUTOSUGGEST_FIELD = "terms.fl";
    private static final String AUTOSUGGEST_PREFIX = "terms.prefix";
    private static final String AUTOSUGGEST_LIMIT = "terms.limit";
    private static final String RESULT_FORMAT = "wt";

    // More Like This
    private static final String MORELIKETHIS_FIELD = "mlt.fl";
    private static final String MORELIKETHIS_RESTRICTION = "fq";
    private static final String MORELIKETHIS_MIN_TERM_FREQ = "mlt.mintf";
    private static final String MORELIKETHIS_MIN_DOCUMENT_FREQ = "mlt.mindf";
    private static final String MORELIKETHIS_INCLUDE_MATCH = "mlt.match.include";

    private static final String SHARDS = "shards";

    private static final Logger LOG = Logger.getLogger(SolrClientService.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private SolrClientConfig config;

    private int maxConnections = 10; // default value of 10 if unset
    private MultiThreadedHttpConnectionManager connectionManager;
    private HttpClient httpClient;

    /** The session context. */
    @SuppressWarnings("unused")
    private SessionContext sessionContext;

    /**
     * Perform a search, specifying the lucene query, start and page size values,
     * and optionally the names of the fields to retrieve.
     * @param query The Solr query, which may include a sort clause
     * @param start The first result to display (zero-indexed)
     * @param pageSize The number of results on a page
     * @param fields Array of field names that should be returned in the Solr response.
     * @param highlight Array of field names that Solr should attempt to highlight
     * @return the raw result built from the SOLR response
     * @throws SearchFailedException if the search fails for any reason
     */
    public RawResult search(String query, int start, int pageSize, String[] fields, String[] highlight)
    throws SearchFailedException {

        int startSort = query.lastIndexOf(";");
        String sortClause = "";
        if (startSort > 0){
            sortClause = query.substring(startSort + 1);
        }

        GetMethod get = new GetMethod(config.getSolrURL());
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new NameValuePair(QUERY, query));
        parameters.add(new NameValuePair(START, String.valueOf(start)));
        parameters.add(new NameValuePair(PAGE_SIZE, String.valueOf(pageSize)));
        parameters.add(new NameValuePair(SORT, sortClause));
        if (config.useFacets()) {
            parameters.add(new NameValuePair(FACET, "" + config.useFacets()));
            for (String facetField : config.getFacetFields()) {
                parameters.add(new NameValuePair(FACET_FIELD, facetField));
            }
            parameters.add(new NameValuePair(FACET_MINCOUNT, "" + config.getFacetMinCount()));
            parameters.add(new NameValuePair(FACET_SORT, "" + config.sortFacetsOnCount()));
            for (String facetQueryField : config.getFacetQueryFields()) {
               parameters.add(new NameValuePair(FACET_QUERY, facetQueryField));
           }

        }

        if (fields != null) {
            parameters.add(new NameValuePair(FIELDS, commaSeparate(fields)));
        }
        if (highlight != null) {
            parameters.add(new NameValuePair(HIGHLIGHT, "true"));
            parameters.add(new NameValuePair(HIGHLIGHT_FIELDS, commaSeparate(highlight)));
            parameters.add(new NameValuePair(HIGHLIGHT_FRAGSIZE, "" + config.getHighlightsFragmentSize()));
            parameters.add(new NameValuePair(HIGHLIGHT_SIMPLE_PRE, config.getHighlightsPreText()));
            parameters.add(new NameValuePair(HIGHLIGHT_SIMPLE_POST, config.getHighlightsPostText()));
            if (config.requireFieldMatch()) {
                parameters.add(new NameValuePair(HIGHLIGHT_REQUIRE_FIELDMATCH, "" + config.requireFieldMatch()));
            }

        }

        if(!config.getShardsList().isEmpty()) {
      	  	parameters.add(new NameValuePair(SHARDS, config.getShardsList()));
        }
        
        
        get.setQueryString(parameters.toArray(new NameValuePair[parameters.size()]));
        try {
            get = executeSolrMethod(get);
            ResultBuilder rb = new ResultBuilderDigesterImpl();
            return rb.buildResult(get.getResponseBodyAsStream(), start, pageSize);
        }
        catch (IOException ex) {
            throw new SearchFailedException("IO Error getting SOLR response: " + ex.getMessage());
        }
        finally {
            // release HTTP connection
            get.releaseConnection();
        }
    }

    /**
     * Perform a search, specifying the lucene query, start and page size values,
     * and optionally the names of the fields to retrieve.
     * @param query The Solr query, which may include a sort clause
     * @param start The first result to display (zero-indexed)
     * @param pageSize The number of results on a page
     * @param fields Array of field names that should be returned in the Solr response.
     * @return the raw result built from the SOLR response
     * @throws SearchFailedException if the search fails for any reason
     */
    public RawResult search(String query, int start, int pageSize, String[] fields)
    throws SearchFailedException {
        return search(query, start, pageSize, fields, null);
    }

    /**
     * Perform a search, specifying the lucene query, start and page size values,
     * and optionally the names of the fields to retrieve.
     * @param query The Solr query, which may include a sort clause
     * @param start The first result to display (zero-indexed)
     * @param pageSize The number of results on a page
     * @param fields Array of field names that should be returned in the Solr response.
     * @return String value which is the search results XML.
     * @throws SearchFailedException if the search fails for any reason
     */
    public String searchAsXml(String query, int start, int pageSize, String[] fields)
    throws SearchFailedException {
        GetMethod get = new GetMethod(config.getSolrURL());
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new NameValuePair(QUERY, query));
        parameters.add(new NameValuePair(START, String.valueOf(start)));
        parameters.add(new NameValuePair(PAGE_SIZE, String.valueOf(pageSize)));
        if (fields != null)
        {
            parameters.add(new NameValuePair(FIELDS, commaSeparate(fields)));
        }
        get.setQueryString(parameters.toArray(new NameValuePair[parameters.size()]));
        try {
            get = executeSolrMethod(get);
            return get.getResponseBodyAsString();
        }
        catch (IOException ex) {
            throw new SearchFailedException("IO Error getting SOLR response: " + ex.getMessage());
        }
        finally {
            // release HTTP connection
            get.releaseConnection();
        }
    }

    /**
     * Performs an autosuggest, given a user-specified search prefix, plus a limit on the number
     * of results. Highest-frequency results will be returned, in descending order of frequency.
     * @param prefix the search prefix entered by the user, from which suggestions will be generated.
     * @param limit limit on number of results returned
     * @return result object containing the autosuggestions
     * @throws SearchFailedException if the autosuggest fails for any reason
     */
    public AutosuggestResult autosuggest(String prefix, int limit)
    throws SearchFailedException {
        GetMethod get = new GetMethod(config.getAutosuggestURL());
        AutosuggestResultFactory factory = config.getAutosuggestResultFactory();
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (String field : config.getAutosuggestFields()) {
           parameters.add(new NameValuePair(AUTOSUGGEST_FIELD, field));
        }
        parameters.add(new NameValuePair(AUTOSUGGEST_PREFIX, prefix));
        parameters.add(new NameValuePair(AUTOSUGGEST_LIMIT, "" + limit));
        parameters.add(new NameValuePair(RESULT_FORMAT, factory.getSolrWriterType()));
        get.setQueryString(parameters.toArray(new NameValuePair[parameters.size()]));
        try {
            get = executeSolrMethod(get);
            return factory.getResult(config, get);
        }
        catch (AutosuggestException e) {
            throw new SearchFailedException("Error using SOLR autosuggest: " + e.getMessage());
        }
        finally {
            // release HTTP connection
            get.releaseConnection();
        }
    }

    /**
     * Performs a 'more like this' request on the item with given web id. It will use the given
     * SOLR fields to work out similar results, applying any given restrictions to these results,
     * and limiting the number of results to a maximum of the given limit.
     * @param webId web id of item for which we find similar items
     * @param fields comma-separated list of fields to use to judge 'similarity'
     * @param restrictions restrictions on result set in SOLR-query format (e.g. "contentType:Book")
     * @param limit limit on number of results returned
     * @return result object containing the similar results
     * @throws SearchFailedException on any failure
     */
    public MoreLikeThisResult moreLikeThis(String webId, String fields, String restrictions, int limit)
    throws SearchFailedException {
        GetMethod get = new GetMethod(config.getMoreLikeThisURL());
        MoreLikeThisResultFactory factory = config.getMoreLikeThisResultFactory();
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new NameValuePair(QUERY, config.getWebIdField() + ":" + webId));
        parameters.add(new NameValuePair(MORELIKETHIS_FIELD, fields));
        parameters.add(new NameValuePair(MORELIKETHIS_RESTRICTION, restrictions));
        parameters.add(new NameValuePair(MORELIKETHIS_MIN_TERM_FREQ,
                "" + config.getMoreLikeThisMinimumTermFrequency()));
        parameters.add(new NameValuePair(MORELIKETHIS_MIN_DOCUMENT_FREQ,
                "" + config.getMoreLikeThisMinimumDocumentFrequency()));
        parameters.add(new NameValuePair(MORELIKETHIS_INCLUDE_MATCH, "false"));
        parameters.add(new NameValuePair(FIELDS, "id"));
        parameters.add(new NameValuePair(PAGE_SIZE, "" + limit));
        get.setQueryString(parameters.toArray(new NameValuePair[parameters.size()]));
        try {
            get = executeSolrMethod(get);
            return factory.getResult(get, limit);
        }
        finally {
            // release HTTP connection
            get.releaseConnection();
        }
    }

    /**
     * Sets the config for this <code>SolrClientService</code>
     * @param config the configuration object
     */
    protected void setConfig(SolrClientConfig config) {
        this.config = config;
    }

    /**
     * Initialises the HTTP connection manager and client
     */
    protected void initHttp() {
        connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setDefaultMaxConnectionsPerHost(maxConnections);
        params.setMaxTotalConnections(maxConnections);
        connectionManager.setParams(params);
        httpClient = new HttpClient(connectionManager);
        HttpClientParams clientParams = new HttpClientParams();
        clientParams.setContentCharset("utf-8");
        httpClient.setParams(clientParams);
    }

    /**
     * Shuts down the HTTP connection manager and client
     */
    protected void shutdownHttp(){
        connectionManager.shutdown();
    }

    /**
     * Executes the solr method that is defined in the given get method.
     * @param get get method containing solr connection details and parameters
     * @return get method containign results
     * @throws SearchFailedException if the method fails
     */
    private GetMethod executeSolrMethod(GetMethod get) throws SearchFailedException {
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug("SOLR query URL is: " + get.getURI());
            }
            catch (URIException ex) {}
        }

        try {
            int status = httpClient.executeMethod(get);
            if (status == 200) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Solr result xml is: " + get.getResponseBodyAsString());
                }
                return get;
            }
            else {
                // TODO what other codes might Solr return, are there meaningful error messages?
                throw new SearchFailedException("Http Error code "+status+" from Solr. Message: "+get.getStatusText());
            }
        }
        catch (IOException e) {
            throw new SearchFailedException("IO Error communicating with Solr" + e.getMessage());
        }
    }

    /**
     * Returns a single comma-separated string of the given array of strings
     * @param fields array of field strings
     * @return fields strings combined into a comma-separated string
     */
    private String commaSeparate(String[] fields) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<fields.length;i++) {
            buffer.append(fields[i]);
            if (i < (fields.length-1))
                buffer.append(",");
        }
        return buffer.toString();
    }
}
