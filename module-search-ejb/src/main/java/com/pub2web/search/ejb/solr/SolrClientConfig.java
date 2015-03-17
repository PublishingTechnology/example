/*
 * SolrClientConfig
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr;

import java.util.List;

import com.pub2web.search.ejb.solr.autosuggest.AutosuggestResultFactory;
import com.pub2web.search.ejb.solr.morelikethis.MoreLikeThisResultFactory;

/**
 * Class containing configuration properties for the <code>SolrClient</code>
 * @author jbeard
 */
public interface SolrClientConfig {
    /**
     * Gets the SOLR select URL to which to connect
     * @return the SOLR url
     */
    public String getSolrURL();
    /**
     * Should a faceted search be performed?
     * @return true if yes, false if no
     */
    public boolean useFacets();
    /**
     * Gets the minimum count that a term/constraint within a facet must have in
     * order for it to be included in the list of resaults for that facet.
     * @return minimum count
     */
    public int getFacetMinCount();
    /**
     * Should the terms within each facet be sorted on count?
     * @return true if yes, false if no
     */
    public boolean sortFacetsOnCount();
    /**
     * Gets the fields on which to facet searches
     * @return facet fields
     */
    public Iterable<String> getFacetFields();

    /**
     * Gets the facet.query queries to be run
     * @return facet query fields 
     */
    public Iterable<String> getFacetQueryFields();
    
    /**
     * Gets the fragment size (in characters) for highlight display - 0 indicates entire field
     * @return highlight fragment size
     */
    public int getHighlightsFragmentSize();
    /**
     * Gets the text to display before a highlighted term
     * @return text to display before a highlighted term
     */
    public String getHighlightsPreText();
    /**
     * Gets the text to display after a highlighted term
     * @return text to display after a highlighted term
     */
    public String getHighlightsPostText();
    /**
     * Gets the SOLR autosuggest URL to which to connect
     * @return the SOLR autosuggest url
     */
    public String getAutosuggestURL();
    /**
     * Gets the name of the SOLR field index for autosuggest
     * @return the SOLR autosuggest field name
     */
    public List<String> getAutosuggestFields();
    /**
     * Gets the factory for creating autosuggest results
     * @return the autosuggest result factory
     */
    public AutosuggestResultFactory getAutosuggestResultFactory();
    /**
     * Should the highlighting be restricted to the fields
     * that are being searched on?
     * @return true if yes, false if no
     */
    public boolean requireFieldMatch();
    /**
     * Gets the SOLR 'more like this' URL to which to connect
     * @return the SOLR 'more like this' URL
     */
    public String getMoreLikeThisURL();
    /**
     * Gets the factory for creating morelikethis results
     * @return the morelikethis result factory
     */
    public MoreLikeThisResultFactory getMoreLikeThisResultFactory();
    /**
     * Gets the minimum term frequency used for MoreLikeThis
     * @return the minimum term frequency
     */
    public int getMoreLikeThisMinimumTermFrequency();
    /**
     * Gets the minimum document frequency used for MoreLikeThis
     * @return the minimum document frequency
     */
    public int getMoreLikeThisMinimumDocumentFrequency();
    /**
     * Gets the name of the field used for the web id in the SOLR schema
     * @return the web id field name
     */
    public String getWebIdField();

    /**
     * Gets the Shards list (comma-separated)
     * @return Shards list
     */
    public String getShardsList();
}