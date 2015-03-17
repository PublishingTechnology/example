/*
 * SavedSearch
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.savedsearch;

import java.io.Serializable;
import java.util.Date;

import com.ingenta.search.domain.Search;
import com.ingenta.search.xml.XmlSerializer;
import com.ingenta.search.xml.XmlSerializerFactory;

/**
 * A class representing a saved search. It contains a search and adds
 * the additional information needed for database storage and folder
 * management.
 *  
 * @author Mike Bell
 */
public class SavedSearch implements Comparable<SavedSearch>, Serializable{

   private static final long serialVersionUID = 1L;
   public static final String DEFAULT_SEARCH_NAME = "My Search";
   private String folderName = SavedSearchFolder.DEFAULT_NAME;
   private String identityId;
   private Long searchId;
   private Search search;
   private String searchName = DEFAULT_SEARCH_NAME;
   private Date lastRunOn;
   private boolean alerted;
   
   /**
    * Constructs an instance from the given parameters.
    * @param folderName The name of the folder this search belongs in. If the 
    * given value is <code>null</code> or empty the default will be set.
    * @param identityId The ID of the search 'owner'. Empty or <code>null</code>
    * values are not permitted.
    * @param searchId The unique ID of the saved search. If it has not yet been saved to
    * the database, this will be <code>null</code>.
    * @param lastRunOn The date this saved search was last run. If <code>null</code>
    * this defaults to the current date/time.
    * @param search The actual saved search. 
    * @param name The name of the saved search. If the given value is <code>null</code> 
    * or empty the default will be set.
    */
   public SavedSearch(String folderName, String identityId, Long searchId, Date lastRunOn, Search search, String name) {
      setSearch(search);
      setSearchName(name);
      setFolderName(folderName);
      setIdentityId(identityId);
      setSearchId(searchId);
      
      if(lastRunOn != null){
         setLastRunOn(lastRunOn);
      }else{
         setLastRunOnToNow();
      }
   }
   /**
    * Constructs an instance from the given parameters.
    * @param folderName The name of the folder this search belongs in. If the 
    * given value is <code>null</code> or empty the default will be set.
    * @param identityId The ID of the search 'owner'. Empty or <code>null</code>
    * values are not permitted.
    * @param identifier The unique ID of the saved search. If it has not yet been saved to
    * the database, this will be <code>null</code>.
    * @param lastRunOn The date this saved search was last run. If <code>null</code>
    * this defaults to the current date/time.
    * @param search The actual saved search. 
    * @param name The name of the saved search. If the given value is <code>null</code> 
    * or empty the default will be set.
    */
   public SavedSearch(String folderName, String identityId, String identifier, Date lastRunOn, Search search, String name) {
      this(folderName, identityId, Long.parseLong(identifier), lastRunOn, search, name);
   }
   /**
    * Minimal constructor which sets only the mandatory fields. All others will be set to
    * default values.
    * @param identityId The ID of the search 'owner'. Empty or <code>null</code>
    * values are not permitted.
    * @param search The actual saved search. 
    */
   public SavedSearch(String identityId, Search search){
      this(null, identityId, (Long)null, null, search, null);
   }

   /**
    * Sets the search to the given value.
    * @param search The search to be set. May not be <code>null</code>.
    */
   private void setSearch(Search search){
      if(search == null){
         throw new IllegalArgumentException("Search may not be null!");
      }

      this.search = search;
   }

   /**
    * Get the Search that this saved search is wrapping.
    * @return the search
    */
   public Search getSearch() {
      return this.search;
   }

   /**
    * Sets the search name to the given value.
    * @param name the search name to set. If this is empty or <code>null</code> it will
    * be ignored and the existing name will continue to be used instead.
    */
   public void setSearchName(String name) {
      if(name != null && !name.equals("")){
         this.searchName = name;
      }
   }

   /**
    * Gets the search name.
    * @return the search name.
    */
   public String getSearchName() {
      return this.searchName;
   }

   /**
    * Gets the FolderName for this search .
    * @return folderName.
    */
   public String getFolderName() {
      return folderName;
   }

   /**
    * Sets the FolderName for this search. If the given value is
    * <code>null</code> or empty, it will be ignored.
    * @param folderName the folderName to set
    */
   public void setFolderName(String folderName) {
      if(folderName != null && !folderName.equals("")){
         this.folderName = folderName;
      }
   }

   /**
    * Gets the IdentityId for the search.
    * @return identityId
    */
   public String getIdentityId() {
      return identityId;
   }

   /**
    * Sets the identityId for the search.
    * @param identityId the identityId to set. This may not be null or
    * empty.
    */
   public void setIdentityId(String identityId){
      if(identityId == null || identityId.equals("")){
         throw new IllegalArgumentException("IdentityId may not be null or empty");
      }

      this.identityId = identityId;
   }

   /**
    * Gets the unique identifier of this search.
    * @return The search identifier.
    * @deprecated use getSearchId
    */
   public String getIdentifier(){
      return searchId == null ? null : Long.toString(searchId);
   }

   
   /**
    * Gets the unique identifier of this search.
    * @return The search identifier.
    */
   public Long getSearchId() {
      return searchId;
   }
   
   public int getTotalCount(){
	      return this.search.getTotalCount();
	   }   
   
   /**
    * Sets the given value as this search's id.
    * @param searchId The identifier to set.
    */
   public void setSearchId(Long searchId){
      this.searchId = searchId;
   }

   /**
    * Gets the LastRunOn for the search
    * @return lastRunOn.
    */
   public Date getLastRunOn() {
      return lastRunOn;
   }

   /**
    * Sets the LastRunOn date/time for the Search. This will be the given value,
    * unless it is null, in which case it will be ignored.
    * @param lastRunOn the lastRunOn to set
    */
   public void setLastRunOn(Date lastRunOn){
      if(lastRunOn != null){
         this.lastRunOn = lastRunOn;
      }
   }

   /**
    * Sets the LastRunOn date/time for the Search to the current date/time.
    */
   public void setLastRunOnToNow() {
      this.lastRunOn = new Date();
   }

   /**
    * Gets the Search and converts it into xml String.
    * @return The search as xml.
    */
   public String getSearchAsXml() {
      XmlSerializer xmlSerializer = XmlSerializerFactory.getSerializer();

      return  xmlSerializer.toXml(this.search);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   
   /**
    * returns boolean indicating whether the user has subscribed to an alert for this search.
    * @return The boolean value.
    */
   public boolean getAlerted() {
	   return this.alerted;
   }

   /**
    * Sets boolean indicating whether the user has subscribed to an alert for this search.
    */
   public void setAlerted(boolean alerted) {
	   this.alerted = alerted;
   }
   
   
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((folderName == null) ? 0 : folderName.hashCode());
      result = prime * result + ((searchId == null) ? 0 : searchId.hashCode());
      result = prime * result + ((identityId == null) ? 0 : identityId.hashCode());
      result = prime * result + ((search == null) ? 0 : search.hashCode());
      result = prime * result + ((searchName == null) ? 0 : searchName.hashCode());

      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      SavedSearch other = (SavedSearch) obj;
      if (folderName == null) {
         if (other.folderName != null)
            return false;
      }
      else if (!folderName.equals(other.folderName))
         return false;
      if (searchId == null) {
         if (other.searchId != null)
            return false;
      }
      else if (!searchId.equals(other.searchId))
         return false;
      if (identityId == null) {
         if (other.identityId != null)
            return false;
      }
      else if (!identityId.equals(other.identityId))
         return false;
      if (search == null) {
         if (other.search != null)
            return false;
      }
      else if (!search.equals(other.search))
         return false;
      if (searchName == null) {
         if (other.searchName != null)
            return false;
      }
      else if (!searchName.equals(other.searchName))
         return false;
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[identityId: ");
      buff.append(identityId);
      buff.append("|folderName: ");
      buff.append(folderName);
      buff.append("|searchId: ");
      buff.append(searchId);
      buff.append("|lastRunOn: ");
      buff.append(lastRunOn);
      buff.append("| ");
      buff.append(search);

      return buff.toString();
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(SavedSearch otherSearch){
      // Sort with most recently ran at the top, null is logically yet to be allocated an id so even nearer the top
      return this.searchId == null 
            ? (otherSearch.searchId == null ? 0 : 1) 
            : (otherSearch.searchId == null ? -1 : -this.searchId.compareTo(otherSearch.searchId));
   }
}
