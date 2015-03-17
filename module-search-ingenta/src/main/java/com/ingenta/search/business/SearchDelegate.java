/*
 * SearchDelegate
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import org.apache.log4j.Logger;

import com.ingenta.search.domain.ExplanationText;
import com.ingenta.search.domain.ExplanationTextImpl;
import com.ingenta.search.domain.Search;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.store.AbstractResultsFactory;
import com.ingenta.search.store.Query;
import com.ingenta.search.store.QueryGenerator;
import com.ingenta.search.store.QueryGeneratorFactory;
import com.ingenta.search.store.ResultsFactory;
import com.ingenta.search.store.SearchResults;


/**
 * Business delegate for the search component.
 * 
 * @author Mike Bell
 */
public class SearchDelegate {
    private static final Logger LOG = Logger.getLogger(SearchDelegate.class);

    private ResultsFactory resultsFactory;
    private QueryGenerator queryGenerator;
    private boolean highlight = true;    

    /**
     * Executes the given Search.
     * @param search The search to execute.
     * @return The search results.
     */
    public SearchResults doSearch(Search search) {
        LOG.debug("doing standard search");
        this.queryGenerator = getQueryGenerator();
        Query query = getQuery(search);
        if (search.getProperty("pageNumber") != null) {
            int pageNumber = (Integer)search.getProperty("pageNumber");   
            setSearchQueryStartIndex(query,pageNumber);
        }
        SearchResults results = runQuery(search, query, getConfigId());
        search.setTotalCount(results.getTotalCount());
        return results;
    }

    /**
     * Executes the given Search and returns the XML.
     * @param search The search to execute.
     * @return The search results.
     */
    public String doSearchForXml(Search search) {
        LOG.debug("doing standard search to return XML");
        this.queryGenerator = getQueryGenerator();
        Query query = getQuery(search);
        if (search.getProperty("pageNumber") != null) {
            int pageNumber = (Integer)search.getProperty("pageNumber");   
            setSearchQueryStartIndex(query,pageNumber);
        }
        String results = executeQueryForXmlResults(query, getConfigId());
        return results;
    }

    /**
     * Executes the given saved search, taking account of its 'last run' date,
     * and using the system config ID.
     * @param search The search to be run.
     * @return The search results.
     */
    public SearchResults doSearch(SavedSearch search) {
        LOG.debug("doing saved search");

        return doSearch(search, getConfigId());
    }

    /**
     * Executes the given saved search, taking account of its 'last run' date,
     * and using the given config ID. This is especially for use as a standalone
     * application.
     * @param search The search to be run.
     * @param configId The ID of the config to use.
     * @return The search results.
     */
    public SearchResults doSearch(SavedSearch search, String configId) {
        LOG.debug("doing saved search for config: " + configId);
        this.queryGenerator = getQueryGenerator();
        Query query = getQuery(search);

        return runQuery(search.getSearch(), query, configId);
    }

    /**
     * Combined search query
     * @param savedSearch the searches
     * @param operators the operators
     * @param pageNumber the pagenumber
     * @return the search results
     */
    public SearchResults doSearch(SavedSearch[] savedSearch , String[] operators,int pageNumber) {
        this.queryGenerator = getQueryGenerator();
        StringBuilder strQuery = new StringBuilder();
        strQuery.append("+( ");
        Query query=null;

        for (int i = 0; i < savedSearch.length; i++) {
            if (savedSearch[i] != null) {
                savedSearch[i].getSearch().removeSortFields(); //remove sort fields from query

                query = getQuery(savedSearch[i].getSearch());      
                strQuery.append(query.getQuery().replaceAll("\\+", ""));

                if (operators[i] != null) {   
                    strQuery.append(" " + operators[i].toUpperCase() + " ");
                }
            }
        }
        strQuery.append(") ");
        query.setQuery(strQuery.toString());

        setSearchQueryStartIndex(query,pageNumber);


        SearchResults combinedSearchResults = executeQuery(query, getConfigId());
        return combinedSearchResults;
    }

    /**
     * Runs the given query in the search engine and populates the
     * given search and the returned results with the search explanation.
     * @param search The search to have its explanation added.
     * @param query The query to be run.
     * @param configId the configId
     * @return The search results.
     */
    private SearchResults runQuery(Search search, Query query, String configId) {
        SearchResults results = executeQuery(query, configId);
        ExplanationText explanation = getSearchExplanation(search); 
        results.setSearchExplanation(explanation);
        search.setSearchExplanation(explanation);

        LOG.debug("Ran search (" + search + ") got " + results.getTotalCount() + " results");
        
        return results;
    }

    /**
     * Gets the config ID for the current instance of the search component.
     * Protected so that it may be overridden for testing purposes.
     * @return The config ID.
     */
    protected String getConfigId() {
        // TODO where do we get the config ID from?
        return null;
    }

    /**
     * Executes the given query on the results factory
     * @param query The query to be executed.
     * @param configId The config id
     * @return The results generated from the query.
     */
    private SearchResults executeQuery(Query query, String configId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executeQuery: " + query);
            LOG.debug("executeQuery: " + configId);
        }

        ResultsFactory resultsFactory = getResultsFactory(configId);
        resultsFactory.setHighlighting(query.isHighlighting());
        resultsFactory.setHighlighting(isHighlighting());

        return resultsFactory.executeQuery(query);
    }

    /**
     * Executes the given query on the results factory
     * @param query The query to be executed.
     * @param configId the config id
     * @return The results generated from the query.
     */
    private String executeQueryForXmlResults(Query query, String configId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("executeQuery: " + query);
            LOG.debug("executeQuery: " + configId);
        }

        ResultsFactory resultsFactory = getResultsFactory(configId);
        resultsFactory.setHighlighting(query.isHighlighting());
        resultsFactory.setHighlighting(isHighlighting());

        return resultsFactory.executeQueryForXml(query);
    }

    private void setSearchQueryStartIndex(Query query, int pageNumber) {
        //to set query start index so that combined search pagination works.
        if (pageNumber != 0) {
            query.setStart(((query.getRows() * pageNumber)+ 1));   
        }   
    }

    /**
     * Gets the appropriate query generator instance.
     * @return The query generator.
     */
    private QueryGenerator getQueryGenerator() {
        QueryGeneratorFactory factory = new QueryGeneratorFactory();

        return factory.getQueryGenerator(getConfigId());
    }

    /**
     * Gets the query corresponding to the given Search Object.
     * @param search The search from which to construct the query.
     * @return The generated query.
     */
    private Query getQuery(Search search) {
        return this.queryGenerator.generateQuery(search);
    }

    /**
     * Gets the query corresponding to the given Search Object.
     * @param search The search from which to construct the query.
     * @return The generated query.
     */
    private Query getQuery(SavedSearch search) {
        return this.queryGenerator.generateQuery(search);
    }

    /**
     * Get the search explanation corresponding to the given Search.
     * @param search The search.
     * @return The explanation.
     */
    private ExplanationText getSearchExplanation(Search search) {
        return new ExplanationTextImpl(search);
    }
    
    /**
     * @param highlight highlighting required?
     */
    public void setHighlighting(boolean highlight) {
       this.highlight = highlight;
    }
    
    /**
     * @return highlighting required
     */
    public boolean isHighlighting() {
       return this.highlight ;
    }
    
    /**
     * This is provided as an alternative to searches usual configuration, allowing it to be handled within spring if
     * required.
     * 
     * @param resultsFactory the results factory to use
     */
    public void setResultsFactory(ResultsFactory resultsFactory) {
       this.resultsFactory = resultsFactory;
    }
    

    /**
     * @param configId if a results factory has been explicitly set this is ignored, otherwise 
     *   AbstractResultsFactory.getInstance(configId) is called
     * @return an instance of ResultsFactory
     */
    private ResultsFactory getResultsFactory(String configId) {
       return resultsFactory != null ? resultsFactory : AbstractResultsFactory.getInstance(configId);
    }

}