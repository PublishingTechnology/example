/*
 * SearchFolders
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.savedsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * A class which holds a number of <code>SavedSearchFolder</code>s, mapped
 * against their names. It provides methods to access all the folders in order
 * and to retrieve individual folders and searches.
 * 
 * @author Mike Bell
 */
public class SavedSearchFolders implements Serializable{
   private static final long serialVersionUID = 1L;
   private static final Logger LOGGER = Logger.getLogger(SavedSearchFolder.class);
   private SortedMap<String, SavedSearchFolder> folders;
   
   /**
    * Constructs an empty instance.
    */
   public SavedSearchFolders(){
      // Sort alphabetically but case insensitively
      folders = new TreeMap<String, SavedSearchFolder>(new Comparator<String>() {
         @Override
         public int compare(String o1, String o2) {
            int comparison = String.CASE_INSENSITIVE_ORDER.compare(o1,  o2);
            // To ensure backwards compatibility allow words which differ only in case - in these cases use the
            // default sort order
            if (comparison == 0) {
               comparison = o1.compareTo(o2);
            }
            return comparison;
         }
      });
   }
   
   /**
    * Constructs an instance populated from the contents of the
    * given List. 
    * @param searchFolders A list of SearchFolders. If this is empty
    * or null, this <code>SearchFolders</code> will be initialized empty.
    */
   public SavedSearchFolders(List<SavedSearchFolder> searchFolders){
      this();
      
      if(searchFolders != null){
         for (SavedSearchFolder folder : searchFolders) {
            addFolder(folder);
         }
      }
   }
   
   /**
    * Adds the given folder to this instance. If the folder is <code>null</code>
    * this method does nothing.
    * @param folder The folder to be added.
    */
   public void addFolder(SavedSearchFolder folder){
      if(folder != null){
         this.folders.put(folder.getName(), folder);
      }
   }
   
   /**
    * Gets the folder with the given name, if it is present.
    * @param folderName The name of the folder to be got.
    * @return The requested folder, or <code>null</code> if it
    * is not present.
    */
   public SavedSearchFolder getFolder(String folderName){
      return folders.get(folderName);
   }
   
   /**
    * Gets all the folders in the correct alphabetical order by name.
    * @return All the folders. This List may be empty but it will never be <code>null</code>.
    */
   public List<SavedSearchFolder> getAllFolders(){
      List<SavedSearchFolder> allFolders = new ArrayList<SavedSearchFolder>();
      allFolders.addAll(folders.values());
      
      return allFolders;
   }
   
   /**
    * Gets a List containing the names of all the folders contained
    * in this Object.
    * @return The folder names.
    */
   public List<String> getAllFolderNames(){
      List<SavedSearchFolder> folders = getAllFolders();
      List<String> folderNames = new ArrayList<String>(folders.size());
      
      for (SavedSearchFolder folder : folders) {
         folderNames.add(folder.getName());
      }
      
      return folderNames;
   }
   
   /**
    * Removes the folder with the given name. If the folder does not exist,
    * this method does nothing.
    * @param folderName The name of the folder to be removed.
    */
   public void removeFolder(String folderName){
      this.folders.remove(folderName);
   }
   
   /**
    * Removes the given search from its folder. If the folder is then
    * empty, it is removed as well. This method works even if the given
    * search has had its foldername updated.
    * @param search The search to be removed.
    */
   public void removeSearch(SavedSearch search){
      List<SavedSearchFolder> folders = getAllFolders();
      
      for (SavedSearchFolder folder : folders) {
         SavedSearch savedsearch = folder.getSearch(search.getSearchId());
         
         if(savedsearch != null){
            folder.removeSearch(savedsearch);
         if (folder.isEmpty()) removeFolder(folder.getName());            
            break;
         }
      }
   }
   
   /**
    * Removes the search corresponding to the given ID from its folder. If the 
    * folder is then empty, it is removed as well.
    * @param searchIdentifier The ID of the search to be removed.
    */
   public void removeSearch(Long searchIdentifier){
      removeSearch(getSearch(searchIdentifier));
   }
   
   /**
    * Updates the given search (it removes the old version and inserts the new.
    * @param search The search to be updated.
    */
   public void updateSearch(SavedSearch search){
      removeSearch(search);
      addSearchToFolder(search);
   }
   
   /**
    * Adds the contents of the given list to folders, creating new ones as required.
    * Empty or <code>null</code> lists are ignored.
    * @param searches The searches to be added.
    */
   public void addSearchesToFolders(List<SavedSearch> searches){
      if(searches != null){
         for (SavedSearch search : searches) {
            addSearchToFolder(search);
         }
      }
   }
   
   /**
    * Adds the given search to the correct folder in the given folders. New folders
    * are automatically created as required.
    * @param search The search to be put in its folder
    */
   public void addSearchToFolder(SavedSearch search){
      String folderName = search.getFolderName();
      
      if(folderName == null || folderName.equals("")){
         folderName = SavedSearchFolder.DEFAULT_NAME;  
      }
      
      SavedSearchFolder folder = getFolder(folderName);
      
      if(folder == null){
         folder = new SavedSearchFolder(folderName);
         addFolder(folder);
      }
      
      folder.addSearch(search);
   }

   /**
    * Gets the search with the given identifier.
    * @param searchIdentifier The ID of the search to be found.
    * @return The search or <code>null</code> if it is not found.
    * @deprecated user the version which takes a Long
    */
   public SavedSearch getSearch(String searchIdentifier){
      try {
         return getSearch(Long.parseLong(searchIdentifier));
      } catch (NumberFormatException e) {
         LOGGER.error("Unable to parse searchIdentifier " + searchIdentifier, e);
         return null;
      }
   }
   /**
    * Gets the search with the given identifier.
    * @param searchIdentifier The ID of the search to be found.
    * @return The search or <code>null</code> if it is not found.
    */
   public SavedSearch getSearch(Long searchIdentifier){
      List<SavedSearchFolder> allFolders = getAllFolders();
      SavedSearch search = null;
      
      for (SavedSearchFolder folder : allFolders) {
         search = folder.getSearch(searchIdentifier);
         if(search != null){
            break;
         }
      }
      
      return search;
   }

   /**
    * Gets the search with the given identifier from the folder with the given
    * name. If the folder is not found, all folders will be searched for a search
    * with the given identifier.
    * @param folderName The name of the folder the search is expected to be in.
    * @param searchIdentifier The ID of the search to be found.
    * @return The search or <code>null</code> if it is not found.
    * @deprecated use the version which takes a Long
    */
   public SavedSearch getSearch(String folderName, String searchIdentifier){
      try {
         return getSearch(folderName, Long.parseLong(searchIdentifier));
      } catch (NumberFormatException e) {
         LOGGER.error("Unable to parse searchIdentifier " + searchIdentifier, e);
         return null;
      }
   }
   /**
    * Gets the search with the given identifier from the folder with the given
    * name. If the folder is not found, all folders will be searched for a search
    * with the given identifier.
    * @param folderName The name of the folder the search is expected to be in.
    * @param searchIdentifier The ID of the search to be found.
    * @return The search or <code>null</code> if it is not found.
    */
   public SavedSearch getSearch(String folderName, Long searchIdentifier){
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("getSearch(" + folderName + ", " + searchIdentifier + ")");
      }
      SavedSearch search = null;
      SavedSearchFolder folder = null;
      
      if(folderName != null){
         folder = this.getFolder(folderName);
      }
      
      if(folder != null){
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Searching within " + folder);
         }
         search = folder.getSearch(searchIdentifier);
      }
      
      if(search == null){
         search = getSearch(searchIdentifier);
      }
      
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("Returning " + search);
      }

      return search;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((folders == null) ? 0 : folders.hashCode());
      
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SavedSearchFolders other = (SavedSearchFolders) obj;
      if (folders == null) {
         if (other.folders != null)
            return false;
      }
      else if (!folders.equals(other.folders))
         return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[");
      Set<String> keys = this.folders.keySet();
      boolean firstIteration = true;
      
      for (String key : keys) {
         if(!firstIteration){
            buff.append(", ");
         }
         
         buff.append(key);
         firstIteration = false;
      }
      
      buff.append("]");
      
      return buff.toString();
   }
}
