/*
 * SavedSearchDelegate
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.ingenta.search.domain.Search;
import com.ingenta.search.savedsearch.DaoRuntimeException;
import com.ingenta.search.savedsearch.InvalidSavedSearchException;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.savedsearch.SavedSearchBO;
import com.ingenta.search.savedsearch.SavedSearchFolders;
import com.ingenta.util.ejb.Delegate;
import com.pub2web.user.AuthenticatedSubject;
import com.pub2web.user.Identity;
import com.pub2web.user.IdentityType;


/**
 * Business delegate for the saved search functionality.
 * 
 * @author Mike Bell
 */
public class SavedSearchDelegate implements Delegate{
   
   private static final String ATTR_SEARCH_HISTORY = "searchHistory";
   private static final String ATTR_SAVED_SEARCHES = "savedSearches";
   private static final Logger LOG = Logger.getLogger(SavedSearchDelegate.class);
   
   private final HttpSession session;

   /**
    * Creates an instance wrapping the given user-session. 
    * @param session The current user session.
    */
   public SavedSearchDelegate(HttpSession session) {
      this.session = session;
   }

   /**
    * Adds the given search to the current search history in the top
    * position, so that a call to <code>getCurrentSearch()</code> will
    * retrieve it.
    * @param search The search to be added to the current history.
    */
   public void setCurrentSearch(Search search) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("setCurrentSearch: " + search);
      }
      
      List<SavedSearch> searchHistory = getCurrentSearchHistory();
      // If we're repeating the same search again don't add it to the history
      if (isNew(search, searchHistory)) {
         searchHistory.add(new SavedSearch(getIdentityId(), search));
      }
      sortIntoDateOrder(searchHistory);
   }

   private boolean isNew(Search search, List<SavedSearch> searchHistory) {
      // We are only checking if the last search is the same to make sure the save current search functionality works
      // If it's absolutely a requirement that something appears in the history at most once then these methods will
      // need to be revised to alter the dates of the searches
      if (searchHistory.isEmpty()) {
         return true;
      }
      SavedSearch lastSearch = searchHistory.get(0);
      return search.isNew(lastSearch.getSearch());
   }
   
   /**
    * Protected so it may be overridden by JUnit tests
    * 
    * @return the identity from the user's session
    */
   protected String getIdentityId() {
      AuthenticatedSubject subject = AuthenticatedSubject.getAuthenticatedSubject(session);
         
      Identity identity = subject.getPrimaryIdentity();
      if (identity != null) {
         IdentityType t = identity.getType();
         
         if (IdentityType.PERSON.equals(t)) {
            return identity.getId();
         } else if (IdentityType.GUEST.equals(t)) {
            return "guest";
         }
         return "institution";
      }
      return "guest";
   }
   
   /**
    * Gets the current (ie last executed) search, if there is one.
    * @return The last-run search in this session, or <code>null</code>
    * if no searches have been run in this session.
    */
   public SavedSearch getCurrentSearch() {
      LOG.debug("getCurrentSearch");
      List<SavedSearch> searchHistory = getCurrentSearchHistory();
      SavedSearch topSearch = null;
      
      if (!searchHistory.isEmpty()) {
         topSearch = searchHistory.get(0);
      }
      
      return topSearch;
   }
   
   /**
    * Gets the searches run in this session. These are in the order
    * most-recently-run first.
    * @return The searches. This may be empty but will never be <code>null</code>. 
    */
   public List<SavedSearch> getCurrentSearchHistory() {
      LOG.debug("getCurrentSearchHistory");
      List<SavedSearch> searchHistory = (List<SavedSearch>)this.session.getAttribute(ATTR_SEARCH_HISTORY);
      
      if (searchHistory == null) {
         searchHistory = new ArrayList<SavedSearch>();
         this.session.setAttribute(ATTR_SEARCH_HISTORY, searchHistory);
      }
      
      return searchHistory;
   }
   
   /**
    * Gets all the saved searches for the current user (as distinct from the current
    * search history).
    * @return The saved searches. This may be empty but will never be <code>null</code>. 
    */
   public SavedSearchFolders getSavedSearches() {
      String identityId = getIdentityId();
      if (LOG.isDebugEnabled()) {
         LOG.debug("getSavedSearches: " + identityId);
      }
      SavedSearchFolders savedSearches = (SavedSearchFolders)this.session.getAttribute(ATTR_SAVED_SEARCHES);
      
      //TODO uncomment the condition below once 
      // we figure out why the savedsearches in session
      // are not reflecting updates!
      //if (savedSearches == null) {
         SavedSearchBO searchBO = getBusinessObject();
         savedSearches = searchBO.getSavedSearches(identityId);
         this.session.setAttribute(ATTR_SAVED_SEARCHES, savedSearches);
     // }
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Saved searches are " + savedSearches);
      }
         
      return savedSearches;
   }
   
   /**
    * Deletes the given search from both the cached saved searches and
    * from the persisted searches in the database.
    * @param search The search to be deleted.
    */
   public void deleteSavedSearch(SavedSearch search) {
      LOG.debug("deleteSavedSearch: " + search);
      SavedSearchBO searchBO = getBusinessObject();
      searchBO.deleteSavedSearch(search);
      
      SavedSearchFolders savedSearches = getSavedSearches();
      savedSearches.removeSearch(search);
   }
   
   /**
    * Adds the given search to both the cached saved searches and
    * to the persisted searches in the database.
    * @param search The search to be saved.
    * @return the updated search
    * @throws if the user is logged in as a guest or institution
    */
   public SavedSearch saveSearch(SavedSearch search) throws InvalidSavedSearchException {
      LOG.debug("saveSearch: " + search);
      String identityId = verifyIdentity();
      search.setIdentityId(identityId);
      SavedSearchFolders savedSearches = getSavedSearches();
      savedSearches.addSearchToFolder(search);
      
      SavedSearchBO searchBO = getBusinessObject();
      return searchBO.saveSearch(search);
   }

   private String verifyIdentity() throws InvalidSavedSearchException {
      String identityId = getIdentityId();
      if ("guest".equalsIgnoreCase(identityId) 
                        || "institution".equalsIgnoreCase(identityId)) {
         throw new InvalidSavedSearchException("Error:Please login to save search"); 
      }
      return identityId;
   }
   
   /**
    * Adds the given searches to both the cached saved searches and
    * to the persisted searches in the database.
    * @param searches The searches to be saved.
    * @throws if the user is logged in as a guest or institution
    */
   public void saveSearches(List<SavedSearch> searches) throws InvalidSavedSearchException {
      LOG.debug("saveSearches: " + searches.size());
      SavedSearchFolders savedSearches = getSavedSearches();
      String identityId = verifyIdentity();
      List<SavedSearch> searches2Save = new ArrayList<SavedSearch>();
      for (Iterator<SavedSearch> iterator = searches.iterator(); iterator.hasNext();) {
         SavedSearch savedSearch = iterator.next();
         /*override identity id and set with current identity id */
         savedSearch.setIdentityId(identityId);
         savedSearches.addSearchToFolder(savedSearch);
         searches2Save.add(savedSearch);
      }      
      SavedSearchBO searchBO = getBusinessObject();
      searchBO.saveSearches(searches2Save);
   }    
   
   /**
    * Updates the searches in tha database and in the session from the contents of the given String.
    * @param json The JSON representation of the updates.
    * @throws JSONException If the String cannot be read as a JSON Object.
    */
   public void saveManageSearchChanges(String json) throws JSONException {
      LOG.debug("saveManageSearchChanges");
      SavedSearchFolders savedSearches = getSavedSearches();
      JSONSavedSearchHelper jsonHelper = new JSONSavedSearchHelper();
      Map<String, List<SavedSearch>> changedSearches = jsonHelper.reconcileSavedSearches(savedSearches, json);
      List<SavedSearch> updates = changedSearches.get(JSONSavedSearchHelper.MODIFIED_SEARCHES);
      List<SavedSearch> deletions = changedSearches.get(JSONSavedSearchHelper.DELETED_SEARCHES);
      
      updateSearchesInDatabase(updates, deletions);
      removeDeletedSearchesFromSession(deletions, savedSearches);
      updateSearchesInSession(updates, savedSearches);
   }

   /**
    * Removes the searches in the given List from the Session.
    * @param deletions The searches to be removed.
    * @param savedSearches The searches saved in the session.
    */
   private void removeDeletedSearchesFromSession(List<SavedSearch> deletions, SavedSearchFolders savedSearches) {
      LOG.debug("removeDeletedSearchesFromSession");
      for (SavedSearch search : deletions) {
         savedSearches.removeSearch(search);
      }
   }
   
   /**
    * Updates the searches in the given List in the Session cache.
    * @param updates The searches to be updated.
    * @param savedSearches The searches cached in the session.
    */
   private void updateSearchesInSession(List<SavedSearch> updates, SavedSearchFolders savedSearches) {
      LOG.debug("updateSearchesInSession");
      for (SavedSearch search : updates) {
         savedSearches.updateSearch(search);
      }
   }

   /**
    * Saves the given updates and deletions to the database.
    * @param updates The updates. May be empty but may not be null.
    * @param deletions The deletions. May be empty but may not be null.
    * @throws DaoRuntimeException If there is an error interacting with the database.
    */
   private void updateSearchesInDatabase(List<SavedSearch> updates, List<SavedSearch> deletions)
         throws DaoRuntimeException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("updateSearchesInDatabase");
         LOG.debug("updates = " + updates.size());
         LOG.debug("deletions = " + deletions.size());
      }
      
      if (!updates.isEmpty() || !deletions.isEmpty()) {
         SavedSearchBO searchBO = getBusinessObject();
         searchBO.updateSearches(updates);
         searchBO.deleteSavedSearches(deletions);
      }
   }
   
   /**
    * TODO MB - think this is NLR
    * Moves the given search to a folder of the given name. A new
    * folder will be created if required and any empty folder will
    * be automatically deleted.
    * @param search The search to be moved. 
    * @param folderTo The name of the folder to move to.
    */
   public void moveSavedSearch(SavedSearch search, String folderTo) {
      if (search == null) {
         throw new IllegalArgumentException("Search to move may not be null!");
      }
      
      LOG.debug("moveSavedSearch from: " + search.getFolderName() + " to: " + folderTo);
      SavedSearchFolders savedSearches = getSavedSearches();
      SavedSearchBO searchBO = getBusinessObject();
      
      searchBO.deleteSavedSearch(search);
      savedSearches.removeSearch(search);
      
      search.setFolderName(folderTo);
      searchBO.saveSearch(search);
      savedSearches.addSearchToFolder(search);
   }
   
   /**
    * Sorts the given list of Searches into date run order (most
    * recent first).
    * @param searches The searches to be sorted.
    */
   private void sortIntoDateOrder(List<SavedSearch> searches) {
      Collections.sort(searches, new SavedSearchDateComparator());
   }
   
   /**
    * Gets an instance of the business Object. Protected to allow JUnit 
    * substitution.
    * @return The business object.
    */
   protected SavedSearchBO getBusinessObject() {
      return new SavedSearchBO();
   }
   
   /**
    * A Comparator to sort saved searches into 'date last run' order.
    * That is, most recently run first.
    * This is needed because the standard sort order is by index. 
    */
   private class SavedSearchDateComparator implements Comparator<SavedSearch> {
      /**
       * {@inheritDoc}
       */
      public int compare(SavedSearch thisSearch, SavedSearch otherSearch) {
         Date thisSearchLastRun = thisSearch.getLastRunOn();
         Date otherSearchLastRun = otherSearch.getLastRunOn();
         
         return otherSearchLastRun.compareTo(thisSearchLastRun);
      }
   }
}
