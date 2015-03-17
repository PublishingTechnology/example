/*
 * JSONSavedSearchHelper
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.savedsearch.SavedSearchFolder;
import com.ingenta.search.savedsearch.SavedSearchFolders;

/**
 * A class for reconciling a modified saved-search model, represented as a JSON
 * String, with the user's existing saved searches. Differences between the two
 * are returned to the calling class.
 * 
 * @author Mike Bell
 */
public class JSONSavedSearchHelper {
   
   /**
    * Prefix for deleted folders
    */
   public static final String PREFIX_DELETED = "deleted_";
   /**
    * The search id
    */
   public static final String SEARCH_ID = "id";
   /**
    * Deleted flag
    */
   public static final String FLAG_DELETED = "deleted";
   /**
    * Key for deleted searches
    */
   static final String DELETED_SEARCHES = "deleted-searches";
   /**
    * Key for modified searches
    */
   static final String MODIFIED_SEARCHES = "modified-searches";
   
   private static final String JSON_SAVED_SEARCHES = "savedSearches";
   private static final String SEARCH_NAME = "title";
   
   private static final Logger LOG = Logger.getLogger(JSONSavedSearchHelper.class);
   
   /**
    * Checks the saved searches from the session against the saved searches created from
    * the given String representation of a JSON Object. It produces a Map containing lists
    * of Searches to be added, deleted and updated from the session and the database.
    * Any of the lists contained in the Map may be empty but none will ever be null.
    * The keys to the Map are this class's declared constants <code>DELETED_SEARCHES</code>
    * <code>ADDED_SEARCHES</code>, and <code>MODIFIED_SEARCHES</code>.
    * @param savedSearches The user's current saved searches retrieved from their session.
    * @param jsonString The user's updated saved searches received from the client.
    * @return A Map of any changes. This will never be null and will always contain
    * each of the 3 Lists, even though these may be empty. Thus, if the user had made no 
    * changes, the Map would contain 3 empty lists.
    * @throws JSONException If the given String cannot be read as a JSON model.
    */
   public Map<String, List<SavedSearch>> reconcileSavedSearches(SavedSearchFolders savedSearches, String jsonString) 
   throws JSONException {
      LOG.debug("reconcileSavedSearches");
      JSONObject jsonFolders = readSavedSearchFoldersFromJson(jsonString);
      List<String> folderNames = getFolderNames(jsonFolders);
      
      List<SavedSearch> updatedSearches = discoverUpdatedSearches(jsonFolders, savedSearches, folderNames);
      // IMPORTANT - discover deletions AFTER updates
      List<SavedSearch> searchesToDelete = discoverDeletedSearches(jsonFolders, savedSearches, folderNames);
      List<String> deletedFolderNames = getDeletedFolderNames(jsonFolders);
      identifySearchesInDeletedFolders(deletedFolderNames, savedSearches, searchesToDelete);
      
      Map<String, List<SavedSearch>> changesToPersist = new HashMap<String, List<SavedSearch>>();
      changesToPersist.put(DELETED_SEARCHES, searchesToDelete);
      changesToPersist.put(MODIFIED_SEARCHES, updatedSearches);
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Changes size: " + changesToPersist.size());
      }
      
      return changesToPersist;
   }
   
   /**
    * Gets the search folder names from the given JSON object which represents all the
    * saved search folders, omitting any which begin with the prefix <code>deleted_</code>.
    * @param jsonFolders The folders to have their names read.
    * @return The non-deleted folder names.
    */
   public List<String> getFolderNames(JSONObject jsonFolders) {
      String[] allFolderNames = JSONObject.getNames(jsonFolders);
      List<String> folderNames = new ArrayList<String>(allFolderNames.length);
      
      for (String folderName : allFolderNames) {
         if (!folderName.startsWith(PREFIX_DELETED)) {
            folderNames.add(folderName);
         }
      }
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Folder names: " + folderNames);
      }
      
      return folderNames;
   }
   
   /**
    * Gets the deleted search folder names from the given JSON object which represents all the
    * saved search folders, omitting any which do not begin with the prefix <code>deleted_</code>.
    * @param jsonFolders The folders to have their names read.
    * @return The deleted folder names.
    */
   public List<String> getDeletedFolderNames(JSONObject jsonFolders) {
      String[] allFolderNames = JSONObject.getNames(jsonFolders);
      List<String> deletedFolderNames = new ArrayList<String>(allFolderNames.length);
      
      for (String folderName : allFolderNames) {
         if (folderName.startsWith(PREFIX_DELETED)) {
            deletedFolderNames.add(folderName.substring(PREFIX_DELETED.length()));
         }
      }
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Deleted folder names: " + deletedFolderNames);
      }
      
      return deletedFolderNames;
   }
   
   /**
    * Gets a representation of all the saved search folders from the given String.
    * @param jsonString The JSON returned from the client.
    * @return A JSONObject representing all the saved search folders.
    * @throws JSONException If the given String cannot be read as a JSON model.
    */
   public JSONObject readSavedSearchFoldersFromJson(String jsonString)throws JSONException {
      LOG.debug("readSavedSearchFoldersFromJson");
      JSONObject json = new JSONObject(jsonString);
      JSONArray savedSearches = json.getJSONArray(JSON_SAVED_SEARCHES);
      
      return savedSearches.getJSONObject(0);
   }
   
   /**
    * Compares the given JSON Object to the saved searches in the session and identifies
    * deleted searches.
    * @param jsonFolders The saved searches as modified by the user.
    * @param savedSearches The saved searches as stored in the session.
    * @param folderNames The names of the non-deleted saved search folders, as modified 
    * by the user.
    * @return The searches to be deleted.
    * @throws JSONException If there is an error reading the JSON.
    */
   private List<SavedSearch> discoverDeletedSearches(JSONObject jsonFolders, 
                     SavedSearchFolders savedSearches, List<String> folderNames)throws JSONException {
      LOG.debug("discoverDeletedSearches");
      List<JSONObject> deletedSearches = getDeletedSearches(jsonFolders, folderNames);
      List<SavedSearch> searchesToDelete = new ArrayList<SavedSearch>(deletedSearches.size());
      
      for (JSONObject jsonSearch : deletedSearches) {
         if (jsonSearch.has(SEARCH_ID)) {
            Long searchId = jsonSearch.getLong(SEARCH_ID);
            searchesToDelete.add(savedSearches.getSearch(searchId));
         }
      }
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Deleting " + searchesToDelete.size() + " searches");
      }
      
      return searchesToDelete;
   }
   
   /**
    * Gets all searches in folders with names in the given list and adds them
    * to the given List. <i>Ensure this method is only called <b>after</b> updates have
    * been identified</i>. 
    * @param deletedFolderNames The list of names of deleted folders.
    * @param savedSearches The user's existing saved searches in the session.
    * @param searchesToDelete The list of searches to be deleted. May not be null.
    */
   private void identifySearchesInDeletedFolders(List<String> deletedFolderNames, 
                                               SavedSearchFolders savedSearches, 
                                               List<SavedSearch> searchesToDelete) {
      LOG.debug("identifySearchesInDeletedFolders");
      
      for (String folderName : deletedFolderNames) {
         SavedSearchFolder savedSearchFolder = savedSearches.getFolder(folderName);
         searchesToDelete.addAll(savedSearchFolder.getSearches());
      }
   }

   /**
    * Identifies the searches to be deleted.
    * @param jsonFolders The JSON Object representing the updated folders. 
    * @param folderNames The names of the non-deleted folders.
    * @return A list containing JSON representations of searches which are to be deleted.
    * @throws JSONException If there is an error reading the JSON.
    */
   private List<JSONObject> getDeletedSearches(JSONObject jsonFolders, List<String> folderNames)throws JSONException {
      LOG.debug("getDeletedSearches");
      List<JSONObject> deletedSearches = new ArrayList<JSONObject>();

      for (String folderName : folderNames) {
         JSONArray folder = jsonFolders.getJSONArray(folderName);

         for (int i = 0; i < folder.length(); i++) {
            JSONObject search = folder.getJSONObject(i);

            if (search.has(FLAG_DELETED) && (search.has(SEARCH_ID))) {
               deletedSearches.add(search);
            }
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Deleted searches: " + deletedSearches);
      }
      
      return deletedSearches;
   }
   
   /**
    * Compares the given JSON Object to the saved searches in the session and identifies
    * modified searches.
    * @param jsonFolders The saved searches as modified by the user.
    * @param savedSearches The saved searches as stored in the session.
    * @param folderNames The names of the non-deleted saved search folders, as modified 
    * by the user.
    * @return The searches to be modified.
    * @throws JSONException If there is an error reading the JSON.
    */
   private List<SavedSearch> discoverUpdatedSearches(JSONObject jsonFolders, 
                     SavedSearchFolders savedSearches, List<String> folderNames)throws JSONException {
      LOG.debug("discoverUpdatedSearches");
      List<SavedSearch> updatedSearches = new ArrayList<SavedSearch>();
      
      for (String folderName : folderNames) {
         JSONArray folder = jsonFolders.getJSONArray(folderName);
         
         for (int i = 0; i < folder.length(); i++) {
            JSONObject jsonSearch = folder.getJSONObject(i);
            
            if (!jsonSearch.has(FLAG_DELETED) && jsonSearch.has(SEARCH_ID)) {
               SavedSearch savedSearch = savedSearches.getSearch(jsonSearch.getLong(SEARCH_ID));
               
               if (savedSearch == null) {
                  diagnoseSearchIdsMismatch(savedSearches, jsonSearch);
               }
               
               if (isSearchNameChanged(jsonSearch, savedSearch) || !folderName.equals(savedSearch.getFolderName())) {
                  updateSearchName(jsonSearch, savedSearch);
                  updateFolderName(folderName, savedSearch);
                  updatedSearches.add(savedSearch);
               }
            }
         }
      }
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Updated searches: " + updatedSearches);
      }
      
      return updatedSearches;
   }

   /**
    * A method purely for logging in development. MB
    * In a properly-configured 'live' system, this method should never be called.
    * @param savedSearches
    * @param jsonSearch
    * @throws JSONException
    */
   private void diagnoseSearchIdsMismatch(SavedSearchFolders savedSearches, JSONObject jsonSearch)
         throws JSONException {
      LOG.warn("Cannot find saved search in session with ID: " + jsonSearch.getString(SEARCH_ID));
      List<SavedSearchFolder> allFolders = savedSearches.getAllFolders();
      List<Long> savedSearchIds = new ArrayList<Long>();
      List<SavedSearch> allSavedSearches = new ArrayList<SavedSearch>();
      
      for (SavedSearchFolder savedFolder : allFolders) {
         List<SavedSearch> searches = savedFolder.getSearches();
         
         for (SavedSearch search : searches) {
            savedSearchIds.add(search.getSearchId());
            allSavedSearches.add(search);
         }
      }
      
      LOG.warn("Saved searche IDs in session are: " + savedSearchIds);
      LOG.warn("Saved searches in session are: " + allSavedSearches);
   }
   
   /**
    * Updates the folder name in the given saved search to the given value, unless it is 
    * already that value.
    * @param newFolderName The folder name to update to.
    * @param savedSearch The saved search to have its folder name updated.
    */
   private void updateFolderName(String newFolderName, SavedSearch savedSearch) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Update folder name from: " + savedSearch.getFolderName() + " to: " + newFolderName);
      }
      
      if (!newFolderName.equals(savedSearch.getFolderName())) {
         savedSearch.setFolderName(newFolderName);
      }
   }

   /**
    * Updates the search name in the given saved search to the value in the given JSON
    * representation of a search, unless it is null or already the same.
    * @param jsonSearch A JSON model of a search.
    * @param savedSearch The saved search from the user session.
    * @throws JSONException If there is an error reading the JSON.
    */
   private void updateSearchName(JSONObject jsonSearch, SavedSearch savedSearch)throws JSONException {
      if (isSearchNameChanged(jsonSearch, savedSearch)) {
         if (jsonSearch.has(SEARCH_NAME)) {
            savedSearch.setSearchName(jsonSearch.getString(SEARCH_NAME));
            
            if (LOG.isDebugEnabled()) {
               LOG.debug("Changed search name from: " + savedSearch.getSearchName() + 
                                 " to: " + jsonSearch.getString(SEARCH_NAME));
            }
         }
      }
   }
   
   /**
    * Checks if the name in the given JSON representation of a search is the same as that 
    * in the given saved search from the user's session.
    * @param jsonSearch A JSON model of a search.
    * @param savedSearch The saved search from the user session.
    * @return true if the name has changed.
    * @throws JSONException If there is an error reading the JSON.
    */
   private boolean isSearchNameChanged(JSONObject jsonSearch, SavedSearch savedSearch)throws JSONException {
      boolean hasChanged = false;
      
      if (jsonSearch.has(SEARCH_NAME)) {
         hasChanged = !jsonSearch.getString(SEARCH_NAME).equals(savedSearch.getSearchName());
      }
      
      return hasChanged;
   }
}
