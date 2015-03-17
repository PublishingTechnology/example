/*
 * SearchHandler
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ingenta.search.domain.InvalidSearchTermException;
import com.ingenta.search.domain.Search;
import com.ingenta.search.store.SearchResults;
import com.ingenta.servlet.ForwardTarget;
import com.ingenta.servlet.HandlerException;
import com.ingenta.servlet.HandlerRequest;
import com.ingenta.servlet.HandlerResponse;
import com.ingenta.servlet.SimpleHandler;
import com.ingenta.servlet.Target;
import com.ingenta.search.domain.SearchSuggestion;

/**@deprecated use SearchAction instead.
 * Handler for executing new and refined searches.
 * 
 * @author Mike Bell
 */
public class SearchHandler extends SimpleHandler {

   /**
    * Request attribute containing search results
    */
   static final String ATTR_RESULTS = "searchResult";
   
   // These two are used in the sunmedia subclass
   /**
    * Is subscribed only?
    */
   protected static final String PARAM_SUBSCRIBED = "subscribed";
   
   /**
    * Is a refined search?
    */
   protected static final String PARAM_REFINED = "refined";
   
   private static final String ATTR_SUGGESTED_SEARCH ="suggestedSearch";
   private static final String ATTR_ERROR = "searchError";
   private static final String VALUE_1 = "value1";

   private static final Logger LOG = Logger.getLogger(SearchHandler.class);

   /**
    * {@inheritDoc}
    */
   public Target handle(HandlerRequest request, HandlerResponse response) throws HandlerException {
      LOG.debug("entering handle()");

      try {
         Search search = getSearch(request);

         boolean isSubscribedOnly = Boolean.valueOf(request.getParameter(PARAM_SUBSCRIBED));
         if (isSubscribedOnly) {
            List<String> subscribedIds = getSubscribedIds(request);
            search.setSubscribedIds(subscribedIds);
         }

         SearchDelegate delegate = getSearchDelegate();
         SearchResults results = delegate.doSearch(search);
         request.setAttribute(ATTR_RESULTS, results);

         request.setAttribute(ATTR_SUGGESTED_SEARCH, getSuggestedSearch(search,results));

         return getSuccessTarget() ;
      } catch(InvalidSearchTermException e) {
         LOG.error("A search term was not recognised", e);
         request.setAttribute(ATTR_ERROR, e.getMessage());
         return getErrorTarget();
      } catch(RuntimeException e) {
         LOG.error("Unexpected system error", e);
         return getErrorTarget();
      }
   }

   /**
    * Protected method which can be overridden by subclasses to get a test
    * impl for use by JUnit.
    * @return A SearchDelegate instance.
    */
   protected SearchDelegate getSearchDelegate() {
      return new SearchDelegate();
   }

   /**
    * Gets a search from the given request by calling the SearchBuilder. If 
    * the request contains the 'refinedSearch' parameter, the returned search
    * will be a refinement of the user's current search. Otherwise it will be
    * a new search coonstructed from the request params. The method also
    * 'refreshes' the current search in the user's session.
    * @param request The client request containing the search parameters.
    * @return The requested search.
    * @throws InvalidSearchTermException If the search parameters are unreadable.
    */
   protected Search getSearch(HandlerRequest request)throws InvalidSearchTermException {
      Search search = null;
      SearchBuilder searchBuilder = new SearchBuilder();
      HttpSession session = request.getSession();
      boolean isRefinedSearch = Boolean.valueOf(request.getParameter(PARAM_REFINED));

      if (isRefinedSearch) {
         Search currentSearch = getCurrentSearch(session);
         search = searchBuilder.refineSearch(request, currentSearch);
      } else{
         search = searchBuilder.buildSearch(request);
      }

      rememberCurrentSearch(search, session);

      return search;
   }

   /**
    * Saves the current search in the session.
    * @param search The search to be saved.
    * @see SavedSearchDelegate
    */
   private void rememberCurrentSearch(Search search, HttpSession session) {
      SavedSearchDelegate savedSearchDelegate = getSavedSearchDelegate(session);
      savedSearchDelegate.setCurrentSearch(search);
   }

   /**
    * Gets the current (ie last-executed) search, via the <code>SavedSearchDelegate</code>.
    * @param session The current user-session.
    * @return The current search or <code>null</code> if no searches have 
    * been run in this session.
    * @see SavedSearchDelegate
    */
   private Search getCurrentSearch(HttpSession session) {
      SavedSearchDelegate savedSearchDelegate = getSavedSearchDelegate(session);

      return savedSearchDelegate.getCurrentSearch().getSearch();
   }

   /**
    * Retrieved the subscribed licences for the logged in users.
    * 
    * @param request the request
    * @return the external ids of the licences
    */
   protected List<String> getSubscribedIds(HandlerRequest request) {
      return AcsBeanHelper.getInstance().getSubscribedIds(request);
   }

   /**
    * Protected method which can be overridden by subclasses to get a test
    * impl for use by JUnit.
    * @param session The client Session.
    * @return A SavedSearchDelegate instance.
    */
   protected SavedSearchDelegate getSavedSearchDelegate(HttpSession session) {
      return new SavedSearchDelegate(session);
   }

   /**
    * @return search_results.jsp
    */
   protected Target getSuccessTarget() {
      return new ForwardTarget("/search/search_results.jsp");
   }

   /**
    * @return advancedsearch.jsp
    */
   protected Target getErrorTarget() {
      return new ForwardTarget("/search/advancedsearch.jsp");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String getViewId(HandlerRequest arg0) {
      return null;
   }
   
   protected Search getSuggestedSearch(Search search,SearchResults results) {
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
                  LOG.log(Level.DEBUG, MessageFormat.format("Old value of {0} in the search query is replaced by {1}",
                        lValue, replaced));
                  ///break;
               }
            }
         }
         return suggestedSearch;
      }
      return null;

   }

}
