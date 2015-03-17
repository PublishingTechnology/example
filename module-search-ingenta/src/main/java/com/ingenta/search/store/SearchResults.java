/*
 * SearchResults
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ingenta.search.domain.ExplanationClause;
import com.ingenta.search.domain.ExplanationText;
import com.ingenta.search.domain.Pageable;
import com.ingenta.search.domain.SearchSuggestion;

/**
 * A class to hold a number of results from a search, together with paging
 * information.
 * 
 * @author Mike Bell
 */
public class SearchResults implements Pageable {

    private static final long serialVersionUID = 1L;

    private List<SearchResult> results;
    private ExplanationText searchExplanation;
    private int pageSize;
    private int currentPage;
    private int totalCount;
    private Map<String, FacetElement> facets;
    private SearchSuggestion searchSuggestion;

    /**
     * Constructs a new instance from the given parameters.
     * @param pageSize The number of results per page.
     * @param results The actual search results. This may be empty but
     * may not be <code>null</code>.
     * @param searchExplanation The 'user-friendly' expression of the
     * search parameters. This is a list of lists of <code>ExplanationClause</code>
     * Objects. The outer List will contain one entry for each <code>SearchUnit</code>
     * contained in the original Search. This is intended to be converted into its
     * displayable for by the UI templates.
     */
    public SearchResults(int pageSize, int currentPage, int hitCount, List<SearchResult> results, ExplanationText searchExplanation){

        if(results == null){
            throw new IllegalArgumentException("List of results may not be null");
        }

        setPageSize(pageSize);
        this.results = results;
        this.searchExplanation = searchExplanation;
        this.currentPage = currentPage;
        this.totalCount = hitCount;
    }

    public SearchResults(int pageSize, int currentPage, int hitCount, List<SearchResult> results, Map<String, FacetElement> facets, ExplanationText searchExplanation){
        this(pageSize, currentPage, hitCount, results, searchExplanation);
        this.facets = facets;
    }

    /**
     * Constructs a new instance from the given parameters.
     * @param pageSize The number of results per page.
     * @param results The actual search results. This may be empty but
     * may not be <code>null</code>.
     */
    public SearchResults(int pageSize, int currentPage, int hitCount, List<SearchResult> results){
        this(pageSize, currentPage, hitCount, results, null);
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.Pageable#getCurrentPage()
     */

    public int getCurrentPage() {
        return this.currentPage;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.Pageable#getPageSize()
     */

    public int getPageSize() {
        return this.pageSize;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.Pageable#setPageSize(int)
     */

    public void setPageSize(int size) {
        this.pageSize = size;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.Pageable#getTotalCount(int)
     */
    public int getTotalCount() {

        return totalCount;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#setTotalCount(int)
     */
    public void setTotalCount(int totalCount) {

        this.totalCount = totalCount;
    }


    /**
     * Gets the next page of results and increments the current page index.
     * If the total number of results is less than the page size, then all
     * the results will be returned. Once the results are exhausted, subsequent
     * calls to this method will return an empty list.
     * @return the list comprising the next page of results.
     */
    public List<SearchResult> getNextPage() {
        List<SearchResult> nextPage;

        if((this.pageSize >= this.results.size()) && this.currentPage == 1){
            nextPage = this.results;
        }else{
            int startIndex = getStartIndex();
            int endIndex = getEndIndex();
            nextPage = new ArrayList<SearchResult>(this.pageSize);

            for(int i = startIndex; i < endIndex; i++){
                nextPage.add(this.results.get(i));
            }
        }

        this.currentPage++;

        return nextPage;
    }

    /**
     * Calculates the index of the last result to fetch for
     * the <code>getNextPage()</code> method.
     * @return The index of the last result to fetch.
     */
    private int getEndIndex(){
        int lastResult = this.pageSize * this.currentPage;
        int index;

        if(lastResult <= this.results.size()){
            index = lastResult;
        }else{
            index = this.results.size();
        }

        return index;
    }

    /**
     * Calculates the index of the first result to fetch for
     * the <code>getNextPage()</code> method.
     * @return The index of the first result to fetch.
     */
    private int getStartIndex() {
        if(this.currentPage == 1){
            return 0;
        }

        return (currentPage - 1) * this.pageSize;
    }

    /**
     * Gets the 'user-friendly' expression of the search parameters.
     * This is a list of lists of <code>ExplanationClause</code>
     * Objects. The outer List will contain one entry for each <code>SearchUnit</code>
     * contained in the original Search. This is intended to be converted into its
     * displayable for by the UI templates.
     * @return the searchExplanation.
     */
    public List<List<ExplanationClause>> getSearchExplanation(){
        if(this.searchExplanation != null){
            return this.searchExplanation.getSearchExplanation();
        }

        return null;
    }

    /**
     * Gets the 'user-friendly' expression of the search parameters as a String.
     * @return the searchExplanation.
     */
    public String getSearchExplanationString(){
        if(this.searchExplanation != null){
            return this.searchExplanation.getSearchExplanationString();
        }

        return null;
    }

    /**
     * Sets the 'user-friendly' expression of the search parameters.
     * This is a list of lists of <code>ExplanationClause</code>
     * Objects. The outer List will contain one entry for each <code>SearchUnit</code>
     * contained in the original Search. This is intended to be converted into its
     * displayable for by the UI templates.
     * @param searchExplanation the searchExplanation to set
     */
    public void setSearchExplanation(ExplanationText searchExplanation) {
        this.searchExplanation = searchExplanation;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder(this.getClass().getName());
        buff.append("[pageSize: ");
        buff.append(pageSize);
        buff.append("|currentPage: ");
        buff.append(currentPage);
        buff.append("|searchExplanation: ");
        buff.append(searchExplanation);
        buff.append("|searchSuggestion: ");
        buff.append(searchSuggestion);
        buff.append("|results: ");
        buff.append(results);
        buff.append("]");

        return buff.toString();
    }

    /**
     * JavaBean method used in the web layer.
     * 
     * @return The list of search results.
     */
    public List<SearchResult> getResults(){
        return this.results;
    }

    /**
     * JavaBean method used in the web layer.
     * 
     * @return The map of search facets.
     */
    public Map<String, FacetElement> getFacets(){
        return this.facets;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + currentPage;
        result = prime * result + pageSize;
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        result = prime * result + ((searchExplanation == null) ? 0 : searchExplanation.hashCode());

        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchResults other = (SearchResults) obj;
        if (currentPage != other.currentPage)
            return false;
        if (pageSize != other.pageSize)
            return false;
        if (results == null) {
            if (other.results != null)
                return false;
        }else if (!results.equals(other.results))
            return false;
        if (searchExplanation == null) {
            if (other.searchExplanation != null)
                return false;
        }else if (!searchExplanation.equals(other.searchExplanation))
            return false;

        return true;
    }

   /**
    * @return the searchSuggestion
    */
   public SearchSuggestion getSearchSuggestion() {
      return searchSuggestion;
   }

   /**
    * @param searchSuggestion the searchSuggestion to set
    */
   public void setSearchSuggestion(SearchSuggestion searchSuggestion) {
      this.searchSuggestion = searchSuggestion;
   }
}