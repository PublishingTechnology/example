/*
 * SavedSearchFolder
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.savedsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple class representing a folder of saved searches. The order of
 * the searches in the folder is automatically maintained as the correct 
 * one according to each search's index.
 * 
 * @author Mike Bell
 */
public class SavedSearchFolder implements Serializable/*, Comparable<SavedSearchFolder>*/{
   
   private static final long serialVersionUID = 1L;
   public static final String DEFAULT_NAME = "Saved Searches"; 
   private String name = DEFAULT_NAME;
   private List<SavedSearch> searches;
   
   /**
    * Constructs an instance with the given name, containing the given saved searches.
    * @param name The name of the folder. If this is empty or <code>null</code> it will
    * be ignored and the default name ("<code>Saved Searches</code>") used instead.
    * @param searches The searches representing the folder's contents. This may be empty 
    * but it may not be <code>null</code>.
    */
   public SavedSearchFolder(String name, List<SavedSearch> searches){
      if(searches == null){
         throw new IllegalArgumentException("Search list may not be null!");
      }
      
      this.setName(name);
      this.searches = searches;
      sortSearches();
   }
   
   /**
    * Constructs an instance with the given name, containing an empty saved searches
    * list.
    * @param name The name of the folder. If this is empty or <code>null</code> it will
    * be ignored and the default name ("<code>Saved Searches</code>") used instead.
    */
   public SavedSearchFolder(String name) {
      this.setName(name);
      this.searches = new ArrayList<SavedSearch>();
   }

   /**
    * Sets the folder name to the given value.
    * @param name the folder name to set. If this is empty or <code>null</code> it will
    * be ignored and the existing name will continue to be used instead.
    */
   public void setName(String name) {
      if(name != null && !name.equals("")){
         this.name = name;
      }
   }

   /**
    * Gets the folder name.
    * @return the folder name.
    */
   public String getName() {
      return name;
   }

   /**
    * Gets the searches in the folder. These will always be sorted into the correct
    * order (by index number).
    * @return the searches. This may be empty but will never be <code>null</code>.
    */
   public List<SavedSearch> getSearches() {
      return searches;
   }
   
   /**
    * Adds the given search to the folder.
    * @param search The search to add.
    */
   public void addSearch(SavedSearch search){
      this.searches.add(search);
      sortSearches();
   }
   
   /**
    * Removes the given search from the folder.
    * @param search The search to be removed.
    */
   public void removeSearch(SavedSearch search){
      this.searches.remove(search);
   }
   
   /**
    * Gets the search with the given identifier, if there is one.
    * @param identifier The identifier of the sought search.
    * @return The search, or <code>null</code> if it was not found.
    */
   public SavedSearch getSearch(Long identifier){
      SavedSearch soughtSearch = null;
      
      for (SavedSearch search : this.searches) {
         if(search.getSearchId().equals(identifier)){
            soughtSearch = search;
            break;
         }
      }
      
      return soughtSearch;
   }
   
   /**
    * Sorts the searches in this folder into their index order.
    */
   private void sortSearches(){
      Collections.sort(searches);
   }
   
   /**
    * Returns <code>true</code> if this folder contains no searches.
    * @return <code>true</code> if this folder contains no searches.
    */
   public boolean isEmpty(){
      return this.searches.isEmpty();
   }
   
//   /* (non-Javadoc)
//    * @see java.lang.Comparable#compareTo(java.lang.Object)
//    */
//   @Override
//   public int compareTo(SavedSearchFolder other){
//      return this.name.compareTo(other.name);
//   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((searches == null) ? 0 : searches.hashCode());
      
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
      SavedSearchFolder other = (SavedSearchFolder) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      }
      else if (!name.equals(other.name))
         return false;
      if (searches == null) {
         if (other.searches != null)
            return false;
      }
      else if (!searches.equals(other.searches))
         return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[name: ");
      buff.append(name);
      buff.append("|searches: ");
      buff.append(searches);
      buff.append("]");
      
      return buff.toString();
   }
}
