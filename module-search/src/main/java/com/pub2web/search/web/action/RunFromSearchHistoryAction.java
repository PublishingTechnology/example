/*
 * RunFromSearchHistoryAction
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.web.action;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ingenta.search.business.SavedSearchDelegate;
import com.ingenta.search.domain.InvalidSearchTermException;
import com.ingenta.search.domain.Search;
import com.ingenta.search.savedsearch.SavedSearch;


/** Subclass for re-running a search from the search history.
    @author Keith Hatton
 */
public class RunFromSearchHistoryAction
      extends SearchAction {
   
   private static final Logger LOG = Logger.getLogger(RunFromSearchHistoryAction.class);
   
   private int historyIndex = 0;
   
   /** Sets the index in the search history.
       @param index the index
    */
   public void setSearchIndex(int index) {
      LOG.log(Level.DEBUG, String.format("setSearchIndex(%s)", index));
      historyIndex = index;
   }
   
   @Override
   public Search getSearch()
         throws InvalidSearchTermException {
      
      LOG.log(Level.DEBUG, "getSearch()");
      
      SavedSearchDelegate history = getSearchHistoryService();
      SavedSearch search = history.getCurrentSearchHistory().get(historyIndex);
      
      return search.getSearch();
   }
}
