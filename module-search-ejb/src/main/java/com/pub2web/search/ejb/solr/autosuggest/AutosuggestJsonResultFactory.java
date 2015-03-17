/*
 * AutosuggestJsonResultFactory
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.autosuggest;

import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.pub2web.search.api.autosuggest.AutosuggestException;
import com.pub2web.search.api.autosuggest.AutosuggestResult;
import com.pub2web.search.ejb.solr.SolrClientConfig;

/**
 * <code>AutosuggestResultFactory</code> that expects JSON back from an HTTP GET request
 * to a SOLR autosuggest URL.
 * <p>
 * Requires the JSON format as returned by SOLR, which has a single JSON array named "terms",
 * which contains two elements, the string "autosuggest" and another array of 
 * comma-separated list of pairs of terms and the term frequencies,
 * e.g. {"terms":["autosuggest",["asthma",167,"allergic rhinitis",129,"allergy",69]]}
 * </p>
 * @author jbeard
 */
public class AutosuggestJsonResultFactory implements AutosuggestResultFactory {
    
    private static final String JSON_FORMAT = "json";
    
    private static final Logger LOG = Logger.getLogger(AutosuggestJsonResultFactory.class);

    /**
     * Gets an autosuggest result using an HTTP GET method that supplies JSON on the response.
     * @param config, the solr config
     * @param get HTTP GET request for autosuggest results
     * @return result of the autosuggest query
     * @throws AutosuggestException if there is a problem getting autosuggest results using
     * the given GET method.
     */
    @Override
    public AutosuggestResult getResult(SolrClientConfig config, GetMethod get) throws AutosuggestException {
        try {
            JSONObject json = new JSONObject(get.getResponseBodyAsString());
            return new AutosuggestJsonResult(config.getAutosuggestFields(), json);
        }
        catch (IOException ex) {
            LOG.error(ex);
            throw new AutosuggestException("Exception getting json from response:" + ex, ex);
        }
        catch (JSONException ex) {
            LOG.error(ex);
            throw new AutosuggestException("Exception parsing json: " + ex, ex);
        }
    }

    /**
     * Gets a string representing the writer type that SOLR should use for this factory.
     * This is the value required by the "wt" (writer type) parameter passed to the SOLR autosuggest URL.
     * In this case, the value is "json".
     * @return "json"
     */
    @Override
    public String getSolrWriterType() {
        return JSON_FORMAT;
    }
}