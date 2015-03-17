/*
 * StoredSearchDelegate
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.ingenta.search.savedsearch.DaoRuntimeException;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.savedsearch.SavedSearchBO;
import com.ingenta.search.savedsearch.SavedSearchDAO;
import com.ingenta.search.savedsearch.SavedSearchDAOImpl;
import com.ingenta.search.savedsearch.SavedSearchNotFoundException;
import com.ingenta.search.savedsearch.StoredSearchRequest;
import com.ingenta.search.store.SearchResult;
import com.ingenta.search.store.SearchResults;
import com.pub2web.containerservices.ServiceLocator;

/**
 * A business delegate providing an API for stored searches to be retrieved
 * and run. This is initially provided for Search alerts but is sufficiently 
 * generic to be used in other contexts.
 * 
 * @author Mike Bell
 */
public class StoredSearchHelper {
   
   private static final String CONFIG_STANDALONE = "standalone.";
   private static final Logger LOG = Logger.getLogger(StoredSearchHelper.class);

   /**
    * Gets the results of running a stored search as defined in the given stored
    * search request (which contains the ID of the search and the effective date 
    * for the search results).  
    * @param request The specification of the search to be run.
    * @return The content IDs of the found results.
    * @throws SavedSearchNotFoundException if no stored search corresponding to the 
    * given primary key was found. 
    */
   public List<String> getSearchResultsSince(StoredSearchRequest request)throws SavedSearchNotFoundException{
      LOG.debug("getSearchResultsSince: " + request);
      
      SavedSearchDAO dao = getSavedSearchDAO();
      SavedSearch savedSearch = dao.getSavedSearch(request.getSearchId());
      savedSearch.setLastRunOn(request.getSearchSince());
      SearchDelegate searchDelegate = getSearchDelegate();
      SearchResults results = searchDelegate.doSearch(savedSearch, CONFIG_STANDALONE);
      
      return getContentIds(results);
   }
   
   /**
    * Gets the content IDs from the given searchResults.
    * @param results The results to have their IDs read.
    * @return The content IDs.
    */
   private List<String> getContentIds(SearchResults results){
      List<SearchResult> resultsList = results.getResults();
      List<String> contentIds = new ArrayList<String>(resultsList.size());
      
      for (SearchResult searchResult : resultsList) {
         contentIds.add(searchResult.getIdentifier());
      }
      
      return contentIds;
   }
     
   /**
    * Gets an instance of the SavedSearchDAO connected to the appropriate
    * Pub2Web database. Protected to allow JUnit substitution.
    * @return A DAO instance.
    */
   protected SavedSearchDAO getSavedSearchDAO(){
      LOG.debug("getSavedSearchDAO returning SavedSearchDAOImpl");
      return new SavedSearchDAOImpl(getDataSource());
   }
   
   /**
    * Gets a connection to the appropriate Pub2Web database. Protected 
    * to allow JUnit substitution.
    * @return A database connection.
    * @throws DaoRuntimeException if there is an error getting the connection.
    */
   protected DataSource getDataSource(){
      ServiceLocator serviceLocator = ServiceLocator.getSynchronizedInstance(true);
      return serviceLocator.getDataSource(SavedSearchBO.DATASOURCE_SAVED_SEARCH);
   }

   /**
    * Gets an instance of the SearchDelegate. Protected 
    * to allow JUnit substitution.
    * @return a search delegate.
    */
   protected SearchDelegate getSearchDelegate(){
      return new SearchDelegate();
   }
}
