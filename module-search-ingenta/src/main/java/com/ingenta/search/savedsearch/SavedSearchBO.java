/*
 * SavedSearchBO
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.savedsearch;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pub2web.containerservices.ServiceLocator;

/**
 * Business logic for the SavedSearch functionality.
 * TODO MB **under construction ** Discuss with JC/RK. Issues:
 * 1) Do we need this class (is it adding anything?).
 * 
 * @author Mike Bell
 */
public class SavedSearchBO {
   
   public static final String DATASOURCE_SAVED_SEARCH = "jdbc/savedSearch";

   private Logger log = Logger.getLogger(SavedSearchBO.class);

   /**
    * Gets all the saved searches for the given identity, in their
    * respective folders. 
    * @param identityId The ID of the user for which the saved searches 
    * are sought.
    * @return The saved searches. This may be empty but will never be 
    * <code>null</code>.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public SavedSearchFolders getSavedSearches(String identityId)throws DaoRuntimeException{
      if(log.isDebugEnabled()){
         log.debug("getSavedSearches for: " + identityId);
      }
      
      SavedSearchDAO dao = getSavedSearchDAO();
      List<SavedSearch> searches = dao.getAllSearches(identityId);
      
      SavedSearchFolders folders = new SavedSearchFolders();
      folders.addSearchesToFolders(searches);
      
      return folders;
   }
   
   /**
    * Gets the saved search corresponding to the given unique ID.
    * @param searchId The identifier of the search.
    * @return The found search.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    * @throws SavedSearchNotFoundException if no search corresponding to 
    * the given ID is in the database.
    */
   public SavedSearch getSavedSearch(String searchId) 
   throws DaoRuntimeException, SavedSearchNotFoundException{
      if(log.isDebugEnabled()){
         log.debug("getSavedSearch ID: " + searchId);
      }
      
      SavedSearchDAO dao = getSavedSearchDAO();
      
      return dao.getSavedSearch(searchId);
   }
   
   /**
    * Deletes the given search.
    * @param search The search to be deleted.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public void deleteSavedSearch(SavedSearch search)throws DaoRuntimeException{
      if(log.isDebugEnabled()){
         log.debug("deleteSavedSearch ID: " + (search == null ? "null" : search.getSearchId()));
      }
      
      SavedSearchDAO dao = getSavedSearchDAO();
      dao.deleteSearch(search);
   }
   
   /**
    * Deletes the given searches.
    * @param searches The searches to be deleted.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public void deleteSavedSearches(List<SavedSearch> searches)throws DaoRuntimeException{
      if(log.isDebugEnabled()){
         log.debug("deleteSavedSearches: " + searches.size());
      }
      
      SavedSearchDAO dao = getSavedSearchDAO();
      dao.deleteSearches(searches);
   }
   
   /**
    * Saves the given search to the data store.
    * @param search The search to be saved.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public SavedSearch saveSearch(SavedSearch search)throws DaoRuntimeException{
      if(log.isDebugEnabled()){
         log.debug("saveSearch ID: " + search.getSearchId());
      }
         
      SavedSearchDAO dao = getSavedSearchDAO();
      return dao.saveSearch(search);
   }
   
   /**
    * Saves the given search to the data store.
    * @param searches The search to be saved.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public void saveSearches(List<SavedSearch> searches)throws DaoRuntimeException{
      SavedSearchDAO dao = getSavedSearchDAO();
      dao.saveSearches(searches);
   }
      
   
   /**
    * Updates the given search in the data store.
    * @param search The search to be updated.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public void updateSearch(SavedSearch search)throws DaoRuntimeException{
      if(log.isDebugEnabled()){
         log.debug("updateSearch ID: " + search.getSearchId());
      }
      
      SavedSearchDAO dao = getSavedSearchDAO();
      dao.updateSearch(search);
   }
   
   /**
    * Updates the given searches in the data store.
    * @param searches The searches to be updated.
    * @throws DaoRuntimeException if there is an error interacting 
    * with the database.
    */
   public void updateSearches(List<SavedSearch> searches)throws DaoRuntimeException{
      if(log.isDebugEnabled()){
         log.debug("updateSearches: " + searches.size());
      }
      
      SavedSearchDAO dao = getSavedSearchDAO();
      dao.updateSearches(searches);
   }
   
   /**
    * Gets an instance of the SavedSearchDAO connected to the appropriate
    * Pub2Web database. Protected to allow JUnit substitution.
    * @return A DAO instance.
    */
   protected SavedSearchDAO getSavedSearchDAO(){
      log.debug("getSavedSearchDAO returning SavedSearchDAOImpl");
      return new SavedSearchDAOImpl(getDataSource());
   }
   
   /**
    * Gets a connection to the appropriate Pub2Web database. Protected 
    * to allow JUnit substitution.
    * @return A database connection.
    * @throws DaoRuntimeException if there is an error getting the connection.
    */
   protected DataSource getDataSource(){
      ServiceLocator serviceLocator = ServiceLocator.getSynchronizedInstance(true);
      return serviceLocator.getDataSource(DATASOURCE_SAVED_SEARCH);
   }
}
