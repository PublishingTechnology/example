/*
 * AutosuggestResultFactory
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.autosuggest;

import org.apache.commons.httpclient.methods.GetMethod;

import com.pub2web.search.api.autosuggest.AutosuggestException;
import com.pub2web.search.api.autosuggest.AutosuggestResult;
import com.pub2web.search.ejb.solr.SolrClientConfig;

/**
 * Factory that builds autosuggest results from a GET request to an appropriate SOLR URL.
 * @author jbeard
 */
public interface AutosuggestResultFactory {
    /**
     * Gets an autosuggest result using an HTTP GET method 
     * (presumably using an appropriate autosuggest URL).
     * @param config, the solr config
     * @param get HTTP GET request for autosuggest results
     * @return result of the autosuggest query
     * @throws AutosuggestException if there is a problem getting autosuggest results using
     * the given GET method.
     */
    public AutosuggestResult getResult(SolrClientConfig config, GetMethod get) throws AutosuggestException;
    /**
     * Gets a string representing the writer type that SOLR should use for this factory.
     * This is the value required by the "wt" (writer type) parameter passed to the SOLR autosuggest URL.
     * Examples are "json" and "xml".
     * @return SOLR writer type to use to generate the autosuggest results
     */
    public String getSolrWriterType();
}