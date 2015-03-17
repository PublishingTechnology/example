/*
 * ExportHandler
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ingenta.search.domain.Search;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.store.SearchResults;
import com.ingenta.servlet.ForwardTarget;
import com.ingenta.servlet.HandlerException;
import com.ingenta.servlet.HandlerRequest;
import com.ingenta.servlet.HandlerResponse;
import com.ingenta.servlet.NavigationTarget;
import com.ingenta.servlet.SimpleHandler;
import com.ingenta.servlet.Target;

/**
 * A Handler for exporting search results. It re-executes the current search, specifying the 
 * correct number of results for export. It also removes hit-highlighting HTML markup from
 * the results.
 * 
 * @author Mike Bell
 */
public class ExportHandler extends SimpleHandler {

   private static final String PARAM_FORMAT = "format";
   private static final String ATTR_FORMAT = PARAM_FORMAT;
   private static final String TARGET_ERROR = "error"; 

   private static final Logger LOG = Logger.getLogger(ExportHandler.class);
   
   /**
    * {@inheritDoc}
    */
   public Target handle(HandlerRequest request, HandlerResponse response) throws HandlerException{
      LOG.debug("entering handle()");
      String exportFormat = request.getParameter(PARAM_FORMAT);
      
      try {
         request.setAttribute(ATTR_FORMAT, exportFormat);
         SearchResults exportResults = reExecuteSearch(request);
         request.setAttribute(SearchHandler.ATTR_RESULTS, exportResults);      
         return getTarget(request, NavigationTarget.VIEW);
      } catch (Exception e) {
         LOG.error("Problem exporting", e);
         return getTarget(request, NavigationTarget.ERROR);
         
      }
   }
   
   /**
    * Gets the current search from the session and re-executes it with the configured
    * number of results for export and without hit-highlighting.
    * @param request The client request.
    * @return The results of re-executing the search.
    */
   public SearchResults reExecuteSearch(HandlerRequest request){
      LOG.debug("entering reExecuteSearch()");
      HttpSession session = request.getSession();
      SavedSearchDelegate savedSearchDelegate = getSavedSearchDelegate(session);
      SavedSearch currentSearch = savedSearchDelegate.getCurrentSearch();
      
      configureSearchForExport(currentSearch.getSearch());
      SearchDelegate delegate = getSearchDelegate();
      
      return delegate.doSearch(currentSearch.getSearch());
   }
   
   /**
    * Sets highlighting to false and the max required number of results to the
    * configured value in the given search.
    * @param search The search to be configured. 
    */
   private void configureSearchForExport(Search search){
      LOG.debug("entering configureSearchForExport()");
      search.setHighlighting(false);
      search.setPageSize(getExportSearchSize());
   }
   
   /**
    * Gets the configured size of searches for exporting.
    * @return The max required number of results to be retrieved.
    */
   private int getExportSearchSize(){
      // TODO MB we need to get this from config.
      return 300;
   }

   /**
    * Protected method which can be overridden by subclasses to get a test
    * impl for use by JUnit.
    * @return A SearchDelegate instance.
    */
   protected SearchDelegate getSearchDelegate(){
      return new SearchDelegate();
   }
   
   /**
    * Protected method which can be overridden by subclasses to get a test
    * impl for use by JUnit.
    * @param session the session
    * @return A SavedSearchDelegate instance.
    */
   protected SavedSearchDelegate getSavedSearchDelegate(HttpSession session){
      return new SavedSearchDelegate(session);
   }
   
   /**
    * @return "search_results.jsp"
    */
   protected Target getSuccessTarget(){
      return new ForwardTarget("/search/search_results.jsp");
   }
   
   /**
    * @return "error"
    */
   protected Target getErrorTarget(){
      return new ForwardTarget(TARGET_ERROR);
   }
   
   /**
    * @param request unused
    * @return "view"
    */
   @Override
   protected String getViewId(HandlerRequest request) {
      return NavigationTarget.VIEW;
   }

}
