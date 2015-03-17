/*
 * RunSavedSearchAction
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.web.action;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ingenta.search.business.SavedSearchDelegate;
import com.ingenta.search.business.SearchBuilder;
import com.ingenta.search.domain.InvalidSearchTermException;
import com.ingenta.search.domain.Search;
import com.ingenta.search.savedsearch.SavedSearch;


/** Subclass that runs a saved search.
    @author Keith Hatton
 */
public class RunSavedSearchAction
      extends SearchAction {
   
   private static final Logger LOG = Logger.getLogger(RunFromSearchHistoryAction.class);
   
   private long searchId = 0;
   
   /** Sets the saved search identifier.
       @param id the identifier
    */
   public void setSsid(long id) {
      LOG.log(Level.DEBUG, String.format("setSsid(%s)", id));
      searchId = id;
   }
   
   @Override
   public Search getSearch() throws InvalidSearchTermException {
      
      LOG.log(Level.DEBUG, "getSearch()");
      
      SavedSearchDelegate history = getSearchHistoryService();
      SavedSearch search = history.getSavedSearches().getSearch(searchId);
      
      // Bug 45241 - paging, etc doesn't work for saved search
      SearchBuilder searchBuilder = new SearchBuilder();
      searchBuilder.configureSorting(getServletRequest(), search.getSearch());
      searchBuilder.readPagingInfo(getServletRequest(), search.getSearch());
      searchBuilder.readAdditionalParameters(getServletRequest(), search.getSearch());
      
      return search.getSearch();
   }
}
