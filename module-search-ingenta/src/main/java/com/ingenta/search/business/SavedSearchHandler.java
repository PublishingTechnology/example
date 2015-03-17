/*
 * SavedSearchHandler
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.ingenta.facet.update.handler.JSONResponseWriter;
import com.ingenta.search.savedsearch.InvalidSavedSearchException;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.savedsearch.SavedSearchFolders;
import com.ingenta.servlet.ForwardTarget;
import com.ingenta.servlet.HandlerException;
import com.ingenta.servlet.HandlerRequest;
import com.ingenta.servlet.HandlerResponse;
import com.ingenta.servlet.NavigationTarget;
import com.ingenta.servlet.SimpleHandler;
import com.ingenta.servlet.Target;
import com.ingenta.servlet.UnknownTargetException;
import com.ingenta.servlet.WriterTarget;

/**
 * Handler for the saved search functionality.
 * 
 * @author Mike Bell
 */
public class SavedSearchHandler extends SimpleHandler {

    /**
     * Save all searches
     */
    static final String ACTION_SAVE = "save";
    /**
     * Save current search
     */
    static final String ACTION_SAVE_CURRENT = "saveCurrent";
    /**
     * Move search
     */
    static final String ACTION_MOVE = "move";
    /**
     * Delete search
     */
    static final String ACTION_DELETE = "delete";
    /**
     * Show searches
     */
    static final String ACTION_SAVED = "savedSearches";
    /**
     * Show search history
     */
    static final String ACTION_HISTORY = "history";
    /**
     * Show current search
     */
    static final String ACTION_CURRENT_SEARCH = "currentSearch";
    private static final String ACTION_MANAGE = "manageChanges";

    /**
     * Indicates action choice
     */
    static final String PARAM_ACTION = "action";
    /**
     * Desired folder name
     */
    static final String PARAM_FOLDER_NAME = "folderName";
    /**
     * Search id
     */
    static final String PARAM_SEARCH_ID = "searchId";
    
    /**
     * JSON request?
     */
    protected static final String PARAM_JSON = "json";

    private static final String PARAM_FOLDER_TO_NAME = "folderToName";
    private static final String PARAM_SEARCH_NAME = "name";
    private static final String PARAM_SEARCH_INDEX = "saveSearchIndex";
    private static final String PARAM_AJAX = "ajax";

    /**
     * Request attribute where search history is stored
     */
    static final String ATTR_HISTORY = "history";
    /**
     * Request attribute where current search is stored
     */
    static final String ATTR_CURRENT_SEARCH = "currentSearch";
    /**
     * Request attribute where saved searches are stored
     */
    protected static final String ATTR_SAVED = "savedSearches";

    private static final String TARGET_ERROR = "error";
    private static final String TARGET_SUCCESS = "/search/savedsearches.jsp";

    private static final String ATTR_ERROR = "error";

    private SavedSearchDelegate savedSearchDelegate;

    /**
     * Logger - keyed against the runtime class - subclasses will log against themselves
     */
    protected final Logger log = Logger.getLogger(this.getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public Target handle(HandlerRequest request, HandlerResponse response) throws HandlerException {
        log.debug("entering handle()");
        this.savedSearchDelegate = getSavedSearchDelegate(request.getSession());

        try{
            return executeAction(request);
        } catch(RuntimeException e) {
            log.error("Unexpected error", e);
            throw new HandlerException(this, "Unexpected error", e);
        }
    }

    /**
     * Extracts the <code>action=</code> parameter from the given request
     * and executes accordingly.
     * @param request The client request.
     * @return A success target if the action was correctly executed or
     * an error target if the action parameter is unrecognised.
     * @throws HandlerException
     */
    private Target executeAction(HandlerRequest request)throws HandlerException {
        String action = request.getParameter(PARAM_ACTION);
        if (log.isDebugEnabled()) {
           log.debug("executeAction: " + action);
        }

        try {
           if (ACTION_SAVE.equals(action)) {
               saveSearches(request);
               return redirectToView(request);
           } else if (ACTION_SAVE_CURRENT.equals(action)) {
               SavedSearch search = saveCurrentSearch(request);
               if (log.isDebugEnabled()) {
                  log.debug("Saved search " + search);
               }
               if (request.getParameter(PARAM_AJAX) != null) {
                  return new WriterTarget(new JSONResponseWriter(createJSONResponse(search)));
               }
               return redirectToView(request);
           } else if (ACTION_MOVE.equals(action)) {
               String folderTo = request.getParameter(PARAM_FOLDER_TO_NAME);
               SavedSearch search = getSelectedSearch(request);
               moveSavedSearch(search, folderTo);
               return redirectToView(request);
           } else if (ACTION_DELETE.equals(action)) {
               SavedSearch search = deleteSavedSearch(request);
               if (log.isDebugEnabled()) {
                  log.debug("Deleted search " + search);
               }
               if (request.getParameter(PARAM_AJAX) != null) {
                  return new WriterTarget(new JSONResponseWriter(createJSONResponse(search)));
               }
               return redirectToView(request);
           } else if (ACTION_SAVED.equals(action)) {
               // Just shows saved searches which are set below
           } else if (ACTION_HISTORY.equals(action)) {
               List<SavedSearch> searchHistory = getCurrentSearchHistory();
               request.setAttribute(ATTR_HISTORY, searchHistory);
           } else if (ACTION_CURRENT_SEARCH.equals(action)) {
               SavedSearch currentSearch = getCurrentSearch();
               request.setAttribute(ATTR_CURRENT_SEARCH, currentSearch);
           } else if (ACTION_MANAGE.equals(action)) {
               manageSavedSearchUpdates(request);
               setSavedSearchesInRequest(request);
               Map<String, String> jsonResponse = new HashMap<String, String>();
               jsonResponse.put("status", "success");
               return new WriterTarget(new JSONResponseWriter(jsonResponse));
   
           } else{
               log.error("Unrecognised action: " + action);
               return getErrorTarget();
           }
        }
        catch (InvalidSavedSearchException e) {
           log.error("Search can not be saved with guest or institution identity", e);
           request.setAttribute(ATTR_ERROR, e.getMessage());
           //return getErrorTarget();//lets continue to success target with a error message.
        }
        setSavedSearchesInRequest(request);
        return getSuccessTarget();
    }

   private Target redirectToView(HandlerRequest request) {
      // Where the sitemap.xml isn't configured to do PRG return to the previous behaviour of just returning the
      // jsp page directly and risking multiple submits.
      try {
         return getTarget(request, NavigationTarget.VIEW);
      } catch (UnknownTargetException e) {
         log.debug("Consider setting up a view target with a redirect to '/savedsearches?action=savedSearches' to " +
             "guard against multiple submits");
         return getSuccessTarget();
      }
   }

   private Map<String, String> createJSONResponse(SavedSearch search) {
      Map<String, String> jsonResponse = new HashMap<String, String>();
      if (search != null) {
         jsonResponse.put("status", "success");
         jsonResponse.put("id", Long.toString(search.getSearchId()));
      } else {
         jsonResponse.put("status", "failed");
      }
      return jsonResponse;
   }

    /**
     * Gets the user's saved searches and sets them in the user request with the
     * correct attribute name (<code>savedSearches</code>).
     * @param request The client request.
     */
    protected void setSavedSearchesInRequest(HandlerRequest request) {
        SavedSearchFolders savedSearches = getSavedSearches();
        request.setAttribute(ATTR_SAVED, savedSearches);
    }

    /**
     * Reads the JSON string from the given client request and passes it to the delegate to be
     * processed unless the String is null (in which case it does nothing).
     * @param request The client request.
     * @throws HandlerException If there is an error reading the JSON String into a
     * JSONObject.
     */
    protected void manageSavedSearchUpdates(HandlerRequest request)throws HandlerException {
        String json = request.getParameter(PARAM_JSON);

        if (log.isDebugEnabled()) {
           log.debug("JSON = " + json);
        }

        try{
            if (json != null) {
                this.savedSearchDelegate.saveManageSearchChanges(json);
            }
        } catch(JSONException e) {
            String errorMessage = "Error parsing JSON String: " + json;
            log.error(errorMessage, e);
            throw new HandlerException(this, errorMessage, e);
        }
    }

    /**
     * Gets the search designated by the values of the folder name and search ID
     * parameters in the given request.
     * @param request The client request.
     * @return The designated search, or <code>null</code> if there isn't one.
     */
    private SavedSearch getSelectedSearch(HandlerRequest request) {
       try {
          SavedSearchFolders savedSearches = getSavedSearches();
          String folderName = request.getParameter(PARAM_FOLDER_NAME);
          Long searchIdentifier = Long.valueOf(request.getParameter(PARAM_SEARCH_ID));
         
          return savedSearches.getSearch(folderName, searchIdentifier);
       } catch (NumberFormatException e) {
          log.error("Unable to parse searchId " + request.getParameter(PARAM_SEARCH_ID), e);
          return null;
       }
    }

    /**
     * Saves the current search, adding the given folder name and search
     * name from the client request, if they are present. <code>protected</code>
     * so that it may be overridden for JUnit testing.
     * @param request The client request.
     * @return the updated search 
     * @throws InvalidSavedSearchException
     */
    protected SavedSearch saveCurrentSearch(HandlerRequest request) throws InvalidSavedSearchException {
        SavedSearch search = getCurrentSearch();
        if (search != null) {
           search.setFolderName(request.getParameter(PARAM_FOLDER_NAME));
           search.setSearchName(request.getParameter(PARAM_SEARCH_NAME));
           
           search = saveSearch(search);
        }
        return search;
    }

    /**
     * Saves the current search, adding the given folder name and search
     * name from the client request, if they are present. <code>protected</code>
     * so that it may be overridden for JUnit testing.
     * @param request The client request.
     * @throws InvalidSavedSearchException 
     */
    private void saveSearches(HandlerRequest request) throws InvalidSavedSearchException {
        List<SavedSearch> searches = getCurrentSearchHistory();
        String[] searchIndexes = request.getParameterValues(PARAM_SEARCH_INDEX);
        List<SavedSearch> searchesToSave = new ArrayList<SavedSearch>();
        if (searchIndexes != null) {
           for (int i = 0; i < searchIndexes.length; i++) {
               Integer in = null;
               try {
                   in = Integer.valueOf(searchIndexes[i]);
                   
                   SavedSearch srch = searches.get(in);
                   srch.setFolderName(request.getParameter(PARAM_FOLDER_NAME));
                   srch.setSearchName(request.getParameter(PARAM_SEARCH_NAME));
                   searchesToSave.add(srch);
               } catch (NumberFormatException e) {
                   // do nothing
               }
           }
        } else {
           log.warn("Call to saveSearches without a saveSearchIndex");
        }
        saveSearches(searchesToSave);
    }

    /**
     * Gets the searches run in this session. These are in the order
     * most-recently-run first. <code>protected</code> so that it may
     * be overridden for JUnit testing.
     * @return The searches. This may be empty but will never be <code>null</code>.
     */
    protected List<SavedSearch> getCurrentSearchHistory() {
        log.debug("getCurrentSearchHistory");

        return this.savedSearchDelegate.getCurrentSearchHistory();
    }

    /**
     * Gets the current (ie last executed) search, if there is one.
     * <code>protected</code> so that it may be overridden for JUnit testing.
     * @return The last-run search in this session, or <code>null</code>
     * if no searches have been run in this session.
     */
    protected SavedSearch getCurrentSearch() {
        log.debug("getCurrentSearch");

        return this.savedSearchDelegate.getCurrentSearch();
    }

    /**
     * Gets all the saved searches for the current user (as distinct from the current
     * search history). <code>protected</code> so that it may  be overridden for JUnit
     * testing.
     * @return The saved searches. This may be empty but will never be <code>null</code>.
     */
    protected SavedSearchFolders getSavedSearches() {
        log.debug("getSavedSearches");

        return this.savedSearchDelegate.getSavedSearches();
    }

    /**
     * Deletes the given search from both the cached saved searches and
     * from the persisted searches in the database. <code>protected</code>
     * so that it may be overridden for JUnit testing.
     * @param request the servlet request
     * @return the deleted search
     */
    protected SavedSearch deleteSavedSearch(HandlerRequest request) {
        log.debug("deleteSavedSearch");

        SavedSearch search = getSelectedSearch(request);
        if (search != null) {
           this.savedSearchDelegate.deleteSavedSearch(search);
        }
        return search;
    }

    /**
     * Adds the given search to both the cached saved searches and
     * to the persisted searches in the database. <code>protected</code>
     * so that it may be overridden for JUnit testing.
     * @param search The search to be saved.
     * @return the updated search 
     * @throws InvalidSavedSearchException
     */
    protected SavedSearch saveSearch(SavedSearch search) throws InvalidSavedSearchException {
        log.debug("saveSearch");
        return this.savedSearchDelegate.saveSearch(search);
    }

    /**
     * Adds the given search to both the cached saved searches and
     * to the persisted searches in the database. <code>protected</code>
     * so that it may be overridden for JUnit testing.
     * @param searches The search to be saved.
    * @throws InvalidSavedSearchException 
     */
    protected void saveSearches(List<SavedSearch> searches) throws InvalidSavedSearchException {
        log.debug("saveSearch");
        this.savedSearchDelegate.saveSearches(searches);
    }


    /**
     * Moves the given search to a folder of the given name. A new
     * folder will be created if required and any empty folder will
     * be automatically deleted.  <code>protected</code> so that it may
     * be overridden for JUnit testing.
     * @param search The search to be moved.
     * @param folderTo The name of the folder to move to.
     */
    protected void moveSavedSearch(SavedSearch search, String folderTo) {
        log.debug("moveSavedSearch");
        this.savedSearchDelegate.moveSavedSearch(search, folderTo);
    }

    /**
     * Protected method which can be overridden by subclasses to get a test
     * impl for use by JUnit.
     * @param session the session
     * @return A SavedSearchDelegate instance.
     */
    protected SavedSearchDelegate getSavedSearchDelegate(HttpSession session) {
        return new SavedSearchDelegate(session);
    }

    /**
     * Gets the navigation target indicating a successful operation.
     * @return The 'success' target.
     */
    protected Target getSuccessTarget() {
        return new ForwardTarget(TARGET_SUCCESS);
    }

    /**
     * Gets the navigation target indicating that an error occurred.
     * @return The 'error' target.
     */
    protected Target getErrorTarget() {
        return new ForwardTarget(TARGET_ERROR);
    }

    /**
     * @param request unused
     * @return an UnsupportedOperationException!
     */
    @Override
    protected String getViewId(HandlerRequest request) {
        String action = request.getParameter(PARAM_ACTION);
        if (action != null) {
            return "savedsearch." + action;
        }
        return "savedsearch";
    }
}
