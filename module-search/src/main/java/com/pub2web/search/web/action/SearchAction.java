/*
 * SearchAction
 * 
 * Copyright 2012 Publishing Technology plc
 */
package com.pub2web.search.web.action;

import static com.pub2web.search.web.action.ActionOutcomes.ERROR;
import static com.pub2web.search.web.action.ActionOutcomes.SUCCESS;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.ingenta.search.business.AcsBeanHelper;
import com.ingenta.search.business.SavedSearchDelegate;
import com.ingenta.search.business.SearchBuilder;
import com.ingenta.search.business.SearchDelegate;
import com.ingenta.search.domain.InvalidSearchTermException;
import com.ingenta.search.domain.Search;
import com.ingenta.search.store.SearchResults;
import com.pub2web.rdf.cci.facet.ContentItem;
import com.pub2web.search.api.SearchListingService;
import com.ingenta.search.domain.SearchSuggestion;


/** Struts action for executing a search.
    This action reuses a significant amount of code from the Framework SearchHandler implementation and
    should be prioritised for rework.
    @author Keith Hatton
 */
public class SearchAction
      implements ServletRequestAware {
   
   private static final String INVALID = "invalid";
   
   private static final String VALUE_1 = "value1";
   
   private final Logger LOG = Logger.getLogger(getClass());
   
   private SearchDelegate delegate = null;
   private SavedSearchDelegate history = null;
   private SearchListingService lsrv = null;
   private String view = "";
   private boolean requireNew = true;
   private HttpServletRequest request = null;
   private boolean refined = false;
   private boolean subscribed = false;
   private boolean invalid = false;
   private boolean shouldSave = true;
   private SearchResults results = null;
   private Search suggested = null;
   private List<ContentItem> listings = new java.util.ArrayList<ContentItem>();
   
   
   /** Sets the search service that will be used by this action.
       @param srv the search service
    */
   public void setSearchService(SearchDelegate srv) {
      LOG.log(Level.DEBUG, String.format("setSearchService(%s)", srv));
      delegate = srv;
   }

   /** Gets the search service that can be accessed by sub classes.
      @return a SearchDelegate instance
    */
   public SearchDelegate getSearchService() {
      return delegate;
   }
   
   /** Sets the search history service that will be used by this action.
       @param srv the search history service
    */
   public void setSearchHistoryService(SavedSearchDelegate srv) {
      LOG.log(Level.DEBUG, String.format("setSearchHistoryService(%s)", srv));
      history = srv;
   }
   
   /** Gets the saved search delegate for this session.
       @return a SavedSearchDelegate instance
    */
   protected SavedSearchDelegate getSearchHistoryService() {
      return history;
   }
   
   /** Sets the search listing service that will be used by this action.
       @param srv the search listing service
    */
   public void setSearchListingService(SearchListingService srv) {
      LOG.log(Level.DEBUG, String.format("setSearchListingService(%s)", srv));
      lsrv = srv;
   }
   
   
   /** Gets the search listing service
      @return lsrv the search listing service
   */
   public SearchListingService getSearchListingService() {
     return lsrv;
   }

   /** Sets the view that should be returned for each item in the search results.
       @param view the view name
    */
   public void setView(String view) {
      LOG.log(Level.DEBUG, String.format("setView(%s)", view));
      this.view = view;
   }
   
   public String getView() {
      return view;
    }
   
   /** Sets whether this action must construct a new search from the request.
       If this value is false, the action will attempt to re-execute the most recent search if none can be
       constructed from the request.
       @param value true to require a new search, false otherwise
    */
   public void setNewSearch(boolean value) {
      LOG.log(Level.DEBUG, String.format("setNewSearch(%s)", value));
      requireNew = value;
   }
   
   /** Sets whether this action should refine a previous search.
       @param refined true to refine a previous search, false otherwise
    */
   public void setRefined(boolean refined) {
      LOG.log(Level.DEBUG, String.format("setRefined(%s)", refined));
      this.refined = refined;
   }
   
   /** Determines whether this action should refine a previous search.
       @return true to refine a previous search, false otherwise
    */
   public boolean isRefined() {
      return refined;
   }
   
   /**
    * @param shouldSave should this search be added to the history
    */
   public void setShouldSave(boolean shouldSave) {
      this.shouldSave = shouldSave;
   }
   
   /** Sets whether this action should search only in subscribed titles.
       @param subscribed true to search only in subscribed titles, false otherwise
    */
   public void setSubscribed(boolean subscribed) {
      LOG.log(Level.DEBUG, String.format("setSubscribed(%s)", subscribed));
      this.subscribed = subscribed;
   }
   
   /** Determines whether this action should search only in subscribed titles.
       @return true to search only in subscribed titles, false otherwise
    */
   public boolean isSubscribed() {
      return subscribed;
   }
   
   /**
    * @return true indicates that the previous search terms were invalid.
    */
   public boolean isInvalid() {
      return invalid;
   }
   
   @Override
   public void setServletRequest(HttpServletRequest request) {
      LOG.log(Level.DEBUG, "setServletRequest(...)");
      this.request = request;
   }
   
   public HttpServletRequest getServletRequest() {
      LOG.log(Level.DEBUG, "getServletRequest(...)");
      return request;
   }   
   
   /** Executes the search.
       @return "success" if the search is executed successfully;
        "error" if an error occurs
    */
   public String execute() {
      LOG.log(Level.DEBUG, "execute()");
      
      try {
         Search search = getSearch();
         
         if (isSubscribed()) {
            List<String> subscribedIds = getSubscribedIds(request);
            search.setSubscribedIds(subscribedIds);
         }
         
         results = delegate.doSearch(search);
         suggested = getSuggestedSearch(search, results);
         
         listings.clear();
         
         // TODO what if non-content items (e.g. blurb) are returned by search?
         listings = lsrv.getContentItems(results.getResults(), view);
      }
      catch (InvalidSearchTermException e) {
         LOG.log(Level.ERROR, "unable to assemble search", e);
         
         this.invalid = true;
         return INVALID;
      }
      catch (RemoteException e) {
         LOG.log(Level.ERROR, "unable to obtain search listings", e);
         return ERROR;
      }
      
      return SUCCESS;
   }
   
   /** Gets a search from the given request by calling the SearchBuilder.
       If the request contains the 'refinedSearch' parameter, the returned search will be a refinement
       of the user's current search. Otherwise it will be a new search coonstructed from the request params.
       The method also 'refreshes' the current search in the user's session.
       @param request the client request containing the search parameters
       @return the requested search
       @throws InvalidSearchTermException if the search parameters are unreadable
    */
   public Search getSearch()
         throws InvalidSearchTermException {
      
      Search search = null;
      SearchBuilder searchBuilder = new SearchBuilder();
      
      if (isRefined()) {
         Search currentSearch = getCurrentSearch();
         search = searchBuilder.refineSearch(request, currentSearch);
      }
      else {
         try {
            search = searchBuilder.buildSearch(request);
         }
         catch (InvalidSearchTermException e) {
            if (!requireNew)
               search = getCurrentSearch();
            
            if (search == null)
               throw e;
            
         }
      }
      
      if (shouldSave) {
         rememberCurrentSearch(search);
      }
      
      return search;
   }

   /** Retrieves the subscribed licences for the logged in users.
       @param request the request
       @return the external ids of the licences
    */
   protected List<String> getSubscribedIds(HttpServletRequest request) {
      return AcsBeanHelper.getInstance().getSubscribedIds(request);
   }
   
   /** Saves the current search in the session.
       @param search The search to be saved.
       @see SavedSearchDelegate
    */
   private void rememberCurrentSearch(Search search) {
      SavedSearchDelegate savedSearchDelegate = getSearchHistoryService();
      savedSearchDelegate.setCurrentSearch(search);
   }
   
   /** Gets the current (ie last-executed) search, via the <code>SavedSearchDelegate</code>.
       @return The current search or <code>null</code> if no searches have been run in this session
       @see SavedSearchDelegate
    */
   private Search getCurrentSearch() {
      SavedSearchDelegate savedSearchDelegate = getSearchHistoryService();

      return savedSearchDelegate.getCurrentSearch().getSearch();
   }
   
   public Search getSuggestedSearch(Search search, SearchResults results) {
      SearchSuggestion searchSuggestion = results.getSearchSuggestion();
      Search suggestedSearch = new Search();
      
      Map<String,String> suggestedParams = new HashMap<String, String>();
      suggestedParams.putAll(search.getParamMap());
      suggestedSearch.setParamMap(suggestedParams);
      
      if (searchSuggestion.getSuggestions() != null && !searchSuggestion.getSuggestions().isEmpty()) {
         Map<String,String> params = suggestedSearch.getParamMap();
         /*now put suggested word one by one in suggested params map*/
         Map<String,String> suggestions = searchSuggestion.getSuggestions();
         for (Map.Entry<String, String> suggestionEntry : suggestions.entrySet()) {
            Set<String> paramKeys = params.keySet();
            if (paramKeys.contains(VALUE_1)) {
               String value = params.get(VALUE_1);
               LOG.log(Level.DEBUG,"getSuggestedSearch parameter key-" + VALUE_1);
               /*case neutral value for replacement*/
               String lValue = value.toLowerCase();
               /* In future this can be extended to suggest values for every field. as we have suggestion for 
                * everything */
               if (lValue.indexOf(suggestionEntry.getKey().toLowerCase()) >= 0) {
                  String replaced = lValue.replace(suggestionEntry.getKey(), suggestionEntry.getValue());
                  params.put(VALUE_1, replaced);
                  LOG.log(Level.DEBUG, String.format("Old value of %s in the search query is replaced by %s",
                        lValue, replaced));
                  ///break;
               }
            }
         }
         return suggestedSearch;
      }
      return null;

   }
   
   /** Gets the search result.
       @return the search result
    */
   public SearchResults getSearchResult() {
      return results;
   }
   
   /** Sets the search result 
      @param the search result
   */
   public void setSearchResult(SearchResults results) {
     this.results = results;
   }

   /** Gets the suggested search.
       @return the suggested search
    */
   public Search getSuggestedSearch() {
      return suggested;
   }

   /** Sets the suggested search  
      @param the suggested search
   */
   public void setSuggestedSearch(Search suggested) {
     this.suggested = suggested;
   }


   /** Gets the search result listings.
       @return a list of content items
    */
   public List<ContentItem> getListings() {
      return Collections.unmodifiableList(listings);
   }
   
   /** Sets the search result listings.
      @param a list of content items
   */
   public void setListings(List<ContentItem> listings) {
     this.listings = listings;
   }
   
}
