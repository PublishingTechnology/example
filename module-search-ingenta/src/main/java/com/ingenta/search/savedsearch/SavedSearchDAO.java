package com.ingenta.search.savedsearch;

import java.util.List;

/**
 * API for CRUD operations on saved searches. Implementations must
 * contain NO business logic.
 * 
 * @author Mike Bell
 */
public interface SavedSearchDAO {

   /**
    * Creates a new saved search in the database.
    * @param search The search to be saved.
    * @return The saved search, which should now have its identifier
    * populated.
    * @throws DaoRuntimeException If there is an error saving the data.
    */
   SavedSearch saveSearch(SavedSearch search)throws DaoRuntimeException;
   
   /**
    * Creates saved searches in the database.
    * @param searches The searches to be saved.
    * @throws DaoRuntimeException If there is an error saving the data.
    */   
   public void saveSearches(List<SavedSearch> searches) throws DaoRuntimeException;   
   
   /**
    * Deletes the given search from the database.
    * @param search The search to be deleted.
    * @throws DaoRuntimeException If there is an error deleting the data.
    */
   void deleteSearch(SavedSearch search)throws DaoRuntimeException;
   
   /**
    * Deletes the given searches from the database.
    * @param searches The searches to be deleted.
    * @throws DaoRuntimeException If there is an error deleting the data.
    */
   void deleteSearches(List<SavedSearch> searches)throws DaoRuntimeException;
   
   /**
    * Deletes all the searches in the given folder (this also removes the folder).
    * @param folder The folder to be deleted.
    * @throws DaoRuntimeException If there is an error deleting the folder.
    */
   public void deleteFolder(SavedSearchFolder folder)throws DaoRuntimeException;

   /**
    * Updates the given search in the database.
    * @param search The search to be updated.
    * @throws DaoRuntimeException If there is an error updating the data.
    */
   void updateSearch(SavedSearch search)throws DaoRuntimeException;
   
   /**
    * Updates the given search in the database.
    * @param savedSearch The search to be updated.
    * @throws DaoRuntimeException If there is an error updating the data.
    */
   void updateLastRun(SavedSearch savedSearch) throws DaoRuntimeException;
   
   /**
    * Updates the given searches in the database.
    * @param searches The searches to be updated.
    * @throws DaoRuntimeException If there is an error updating the data.
    */
   void updateSearches(List<SavedSearch> searches)throws DaoRuntimeException;

   /**
    * Gets the saved search corresponding to the given unique ID.
    * @param searchId The identifier of the search.
    * @return The found search.
    * @throws DaoRuntimeException If there is an error getting the data.
    * @throws SavedSearchNotFoundException if no search corresponding to 
    * the given ID is in the database.
    * @deprecated Use the version which takes a Long
    */
   SavedSearch getSavedSearch(String searchId)throws DaoRuntimeException, SavedSearchNotFoundException;

   /**
    * Gets the saved search corresponding to the given unique ID.
    * @param searchId The identifier of the search.
    * @return The found search.
    * @throws DaoRuntimeException If there is an error getting the data.
    * @throws SavedSearchNotFoundException if no search corresponding to 
    * the given ID is in the database.
    */
   SavedSearch getSavedSearch(Long searchId)throws DaoRuntimeException, SavedSearchNotFoundException;
   
   /**
    * Gets all the searches belonging to the user corresponding to the given
    * identity ID. 
    * @param identityId The ID of the user for whom the searches are sought.
    * @return The saved searches for the identity. This may be empty but will 
    * never be <code>null</code>.
    * @throws DaoRuntimeException If there is an error getting the data.
    */
   List<SavedSearch> getAllSearches(String identityId)throws DaoRuntimeException;
}
