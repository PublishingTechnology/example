/*
 * RunSavedSearchHandler
 * 
 * Copyright 2009 Publishing Technology plc
 */
package com.ingenta.search.business;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ingenta.search.savedsearch.DaoRuntimeException;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.savedsearch.SavedSearchBO;
import com.ingenta.search.savedsearch.SavedSearchNotFoundException;
import com.ingenta.search.store.SearchResults;
import com.ingenta.servlet.ForwardTarget;
import com.ingenta.servlet.HandlerException;
import com.ingenta.servlet.HandlerRequest;
import com.ingenta.servlet.HandlerResponse;
import com.ingenta.servlet.SimpleHandler;
import com.ingenta.servlet.Target;

/**@deprecated use SearchAction and its subclasses instead.
 * Handler for running saved searches
 * @author ccsrak
 */
public class RunSavedSearchHandler extends SimpleHandler {
    /**
     * Request attribute for search results
     */
    static final String ATTR_RESULTS = "searchResult";

    private static final Logger LOG = Logger.getLogger(RunSavedSearchHandler.class);
    private static final String SSID_PARAMETER = "ssid";
    private static final String SEARCH_UPDATES_PARAMETER = "updates";
    private static final String SEARCH_INDEX_PARAMETER = "searchIndex";
    private static final String TARGET_ERROR = "error";
    private static final String ATTR_SAVEDSEARCH = "savedSearch";
    private static final String SAVED_SEARCH = "search";

    private SavedSearchDelegate savedSearchDelegate;

    /**
     * {@inheritDoc}
     */
    @Override
    public Target handle(HandlerRequest request, HandlerResponse response) throws HandlerException {
        LOG.debug("entering handle()");
        this.savedSearchDelegate = getSavedSearchDelegate(request.getSession());
        String ssid = request.getParameter(SSID_PARAMETER);
        String updates = request.getParameter(SEARCH_UPDATES_PARAMETER);
        String historyIndex = request.getParameter(SEARCH_INDEX_PARAMETER);

        if (isEmpty(ssid) && isEmpty(historyIndex)) {
            return getErrorTarget();
        }

        SearchDelegate delegate = getSearchDelegate();
        SavedSearch savedSearch = null;
        boolean searchUpdates = false;
        if (!isEmpty(updates)) {
            searchUpdates = true;
        }

        SavedSearchBO ssBO = new SavedSearchBO();
        if (!isEmpty(ssid)) {
            try {
                savedSearch = ssBO.getSavedSearch(ssid);

            } catch (DaoRuntimeException e) {
                LOG.error("DaoRuntimeException : " + e);
                return getErrorTarget();

            } catch (SavedSearchNotFoundException e) {
                LOG.error("SavedSearchNotFoundException : " + e);
                return getErrorTarget();

            }
        }

        if (!isEmpty(historyIndex)) {
            savedSearch = savedSearchDelegate.getCurrentSearchHistory().get(Integer.valueOf(historyIndex));
        }
        //fix for bug#29054 - Search history pagination issue
        String page = request.getParameter("page");
        int pageNumber=0;
        if (!isEmpty(page)){
            pageNumber = (Integer.parseInt(page)- 1);
        }
        savedSearch.getSearch().setProperty("pageNumber", pageNumber);

        SearchResults results = null;

        if (searchUpdates){
            results = delegate.doSearch(savedSearch);
        }
        else {
            results = delegate.doSearch(savedSearch.getSearch());
        }
        request.setAttribute(ATTR_RESULTS, results);
        request.setAttribute(SAVED_SEARCH,  savedSearch.getSearch());
        request.setAttribute(ATTR_SAVEDSEARCH, "true");
        return getSuccessTarget() ;

    }


    private boolean isEmpty(String string) {
        return string == null || "".equals(string);
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
     * @return search_results.jsp
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
     * @return null
     */
    @Override
    protected String getViewId(HandlerRequest request) {
        return null;
    }

    /**
     * Protected method which can be overridden by subclasses to get a test
     * impl for use by JUnit.
     * @return A SearchDelegate instance.
     */
    protected SearchDelegate getSearchDelegate(){
        return new SearchDelegate();
    }

}
