/*
 * SolrClientConfigImpl
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.pub2web.search.ejb.solr.autosuggest.AutosuggestResultFactory;
import com.pub2web.search.ejb.solr.morelikethis.MoreLikeThisResultFactory;

/**
 * Default implementation of the <code>SolrClientConfig</code> interface.
 * @author jbeard
 */
class SolrClientConfigImpl implements SolrClientConfig {

    private static final Logger LOG = Logger.getLogger(SolrClientConfigImpl.class);

    private String solrURL;
    private boolean useFacets;
    private int facetMinCount;
    private boolean sortFacetsOnCount;
    private String facetFieldString;
    private String facetQueryFieldString;
    private List<String> facetQueryFields;
    private List<String> facetFields;
    private int highlightsFragmentSize;
    private String highlightsPreText;
    private String highlightsPostText;
    private String autosuggestURL;
    private List<String> autosuggestField;
    private AutosuggestResultFactory autosuggestResultFactory;
    private boolean requireFieldMatch;
    private String moreLikeThisURL;
    private MoreLikeThisResultFactory moreLikeThisResultFactory;
    private int moreLikeThisMininumTermFrequency;
    private int moreLikeThisMininumDocumentFrequency;
    private String webIdField;
    private String shardsList;

    /**
     * Gets the SOLR select URL to which to connect
     * @return the SOLR select url
     */
    @Override
    public String getSolrURL() {
        return solrURL;
    }

    /**
     * Sets the SOLR select URL
     * @param url SOLR select URL
     */
    public void setSolrURL(String url) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting SOLR url to: " + url);
        }
        this.solrURL = url;
    }

    /**
     * Should a faceted search be performed?
     * @return true if yes, false if no
     */
    @Override
    public boolean useFacets() {
        return useFacets;
    }

    /**
     * Sets whether the search should use facets or not
     * @param useFacets true to use facets, false for no facets
     */
    public void setUseFacets(boolean useFacets) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting useFacets to: " + useFacets);
        }
        this.useFacets = useFacets;
    }

    /**
     * Gets the minimum count that a term/constraint within a facet must have in
     * order for it to be included in the list of resaults for that facet.
     * @return minimum count
     */
    @Override
    public int getFacetMinCount() {
        return facetMinCount;
    }

    /**
     * Sets the minimum count that a term/constraint within a facet must have in
     * order for it to be included in the list of resaults for that facet.
     * @param minCount minimum count
     */
    public void setFacetMinCount(int minCount) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting facet minCount to: " + minCount);
        }
        this.facetMinCount = minCount;
    }

    /**
     * Should the terms within each facet be sorted on count?
     * @return true if yes, false if no
     */
    @Override
    public boolean sortFacetsOnCount() {
        return sortFacetsOnCount;
    }

    /**
     * Sets whether the terms within each facet be sorted on count
     * @param sortOnCount true for sorting on count, false otherwise
     */
    public void setSortFacetsOnCount(boolean sortOnCount) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting sortFacetsOnCount to: " + sortOnCount);
        }
        this.sortFacetsOnCount = sortOnCount;
    }

    /**
     * Gets the fields on which to facet searches
     * @return facet fields
     */
    @Override
    public Iterable<String> getFacetFields() {
        return facetFields;
    }

    /**
     * Gets the fields on which to facet.query searches should be performed
     * @return facet query fields
     */
    @Override
    public Iterable<String> getFacetQueryFields() {
        return facetQueryFields;
    }
    
    /**
     * Gets the comma-separated string containing the facet fields
     * @return facet field string
     */
    public String getFacetFieldString() {
        return facetFieldString;
    }

    /**
     * Gets the comma-separated string containing the facet.query fields
     * @return facet field string
     */
    public String getFacetQueryFieldString() {
        return facetQueryFieldString;
    }

    /**
     * Sets the fields on which to facet searches (comma-separated string)
     * @param facetFields comma-separated string containing the fields
     * on which to create facets within the search
     */
    public void setFacetFieldString(String facetFieldString) {
        String[] arr = facetFieldString.split(",");
        this.facetFields = new ArrayList<String>();
        for (int inx = 0; inx < arr.length; ++inx) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adding facet field: " + arr[inx]);
            }
            this.facetFields.add(arr[inx]);
        }
        this.facetFieldString = facetFieldString;
    }

    /**
     * Sets the facet.query queries to be run (comma-separated string)
     * @param facetQueryFieldString comma-separated string containing the facet.query queries
     * on which to dynamically create facets within the search
     */
    public void setFacetQueryFieldString(String facetQueryFieldString) {
       this.facetQueryFields = new ArrayList<String>();
       this.facetQueryFieldString = facetQueryFieldString;
        if (! facetQueryFieldString.equals("")) {
           String[] arr = facetQueryFieldString.split(",");
           for (int inx = 0; inx < arr.length; ++inx) {
               if (LOG.isDebugEnabled()) {
                   LOG.debug("Adding facet query field: " + arr[inx]);
               }
               this.facetQueryFields.add(arr[inx]);
           }
        }
    }
    
    /**
     * Gets the fragment size (in characters) for highlight display - 0 indicates entire field
     * @return highlight fragment size
     */
    @Override
    public int getHighlightsFragmentSize() {
        return highlightsFragmentSize;
    }

    /**
     * Sets the fragment size for highlight display
     * @param fragmentSize fragment size (in characters) - 0 indicates entire field
     */
    public void setHighlightsFragmentSize(int fragmentSize) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting highlights fragment size to: " + fragmentSize);
        }
        this.highlightsFragmentSize = fragmentSize;
    }

    /**
     * Gets the text to display before a highlighted term
     * @return text to display before a highlighted term
     */
    @Override
    public String getHighlightsPreText() {
        return highlightsPreText;
    }

    /**
     * Sets the text to display before a highlighted term
     * @param preText text to display before a highlighted term
     */
    public void setHighlightsPreText(String preText) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting highlights pre-text to: " + preText);
        }
        this.highlightsPreText = preText;
    }

    /**
     * Gets the text to display after a highlighted term
     * @return text to display after a highlighted term
     */
    @Override
    public String getHighlightsPostText() {
        return highlightsPostText;
    }

    /**
     * Sets the text to display after a highlighted term
     * @param postText text to display after a highlighted term
     */
    public void setHighlightsPostText(String postText) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting highlights post-text to: " + postText);
        }
        this.highlightsPostText = postText;
    }

    /**
     * Gets the SOLR autosuggest URL to which to connect
     * @return the SOLR autosuggest url
     */
    @Override
    public String getAutosuggestURL() {
        return autosuggestURL;
    }

    /**
     * Sets the SOLR Autosuggest URL
     * @param url SOLR Autosuggest URL
     */
    public void setAutosuggestURL(String url) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting Autosuggest url to: " + url);
        }
        this.autosuggestURL = url;
    }

    /**
     * Gets the SOLR autosuggest field name
     * @return the SOLR autosuggest field name
     */
    @Override
    public List<String> getAutosuggestFields() {
        return autosuggestField;
    }

    /**
     * Sets the SOLR Autosuggest field name
     * @param field SOLR Autosuggest field name
     */
    public void setAutosuggestField(String field) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting Autosuggest field to: " + field);
        }
        this.autosuggestField = Arrays.asList(field.split(","));
    }

    /**
     * Gets the factory for creating 'morelikethis' results
     * @return the 'morelikethis' result factory
     */
    @Override
    public AutosuggestResultFactory getAutosuggestResultFactory() {
        return autosuggestResultFactory;
    }

    /**
     * Sets the factory for creating 'morelikethis' results
     * @param factory the 'morelikethis' result factory
     */
    public void setAutosuggestResultFactory(AutosuggestResultFactory factory) {
        this.autosuggestResultFactory = factory;
    }

    /**
     * Should the highlighting be restricted to the fields
     * that are being searched on?
     * @return true if yes, false if no
     */
    @Override
    public boolean requireFieldMatch() {
        return this.requireFieldMatch;
    }
    /**
     * Sets whether the highlighting be restricted to the fields
     * that are being searched on
     * @param useFacets true to use facets, false for no facets
     */
    public void setRequireFieldMatch(boolean requireFieldMatch) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting useFacets to: " + useFacets);
        }
        this.requireFieldMatch = requireFieldMatch;
    }

    /**
     * Gets the SOLR 'more like this' URL to which to connect
     * @return the SOLR 'more like this' URL
     */
    @Override
    public String getMoreLikeThisURL() {
        return this.moreLikeThisURL;
    }
    /**
     * Sets the SOLR 'more like this' URL
     * @param moreLikeThisURL the SOLR 'more like this' URL
     */
    public void setMoreLikeThisURL(String moreLikeThisURL) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting moreLikeThisURL to: " + moreLikeThisURL);
        }
        this.moreLikeThisURL = moreLikeThisURL;
    }

    /**
     * Gets the factory for creating autosuggest results
     * @return the autosuggest result factory
     */
    @Override
    public MoreLikeThisResultFactory getMoreLikeThisResultFactory() {
        return moreLikeThisResultFactory;
    }

    /**
     * Sets the factory for creating autosuggest results
     * @param factory the autosuggest result factory
     */
    public void setMoreLikeThisResultFactory(MoreLikeThisResultFactory factory) {
        this.moreLikeThisResultFactory = factory;
    }

    /**
     * Gets the minimum term frequency used for MoreLikeThis
     * @return the minimum term frequency
     */
    @Override
    public int getMoreLikeThisMinimumTermFrequency() {
        return moreLikeThisMininumTermFrequency;
    }
    /**
     * Gets the minimum term frequency used for MoreLikeThis
     * @param freq the minimum term frequency
     */
    public void setMoreLikeThisMinimumTermFrequency(int freq) {
        this.moreLikeThisMininumTermFrequency = freq;
    }

    /**
     * Gets the minimum document frequency used for MoreLikeThis
     * @return the minimum document frequency
     */
    @Override
    public int getMoreLikeThisMinimumDocumentFrequency() {
        return moreLikeThisMininumDocumentFrequency;
    }
    /**
     * Gets the minimum term frequency used for MoreLikeThis
     * @param freq the minimum term frequency
     */
    public void setMoreLikeThisMinimumDocumentFrequency(int freq) {
        this.moreLikeThisMininumDocumentFrequency = freq;
    }

    /**
     * Gets the name of the field used for the web id in the SOLR schema
     * @return the web id field name
     */
    @Override
    public String getWebIdField() {
        return this.webIdField;
    }
    /**
     * Sets the name of the field used for the web id in the SOLR schema
     * @param webIdField the web id field name
     */
    public void setWebIdField(String webIdField) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting webIdField to: " + webIdField);
        }
        this.webIdField = webIdField;
    }

    /**
     * Gets the Shards list of SOLR servers to which the request needs to be forwarded.
     * @return Shards List.
     */
    @Override
    public String getShardsList() {
        return shardsList;
    }

    /**
     * Sets the Shards list of SOLR servers to which the request needs to be forwarded.
     * @param shardsList list of SOLR servers to which the request needs to be forwarded.
     */
    public void setShardsList(String shardsList) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Setting Shards List to: " + shardsList);
        }
        this.shardsList = shardsList;
    }
}