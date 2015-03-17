/*
 * StoredSearchRequest
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.savedsearch;

import java.io.Serializable;
import java.util.Date;

/**
 * A simple TO to encapsulate a request to run a stored search.
 * 
 * @author Mike Bell
 */
public class StoredSearchRequest implements Serializable {

   private static final long serialVersionUID = 1L;
   private String searchId;
   private Date searchSince;
   
   /**
    * Constructs an instance from the given parameters.
    * @param searchId The unique ID of the search to be run.
    * @param searchSince The date from which the search results are
    * required. If this is null, the search's existing 'lastRunOn'
    * date will be used.
    */
   public StoredSearchRequest(String searchId, Date searchSince){
      if(searchId == null || searchId.equals("")){
         throw new IllegalArgumentException("Null/empty search ID not allowed!");
      }
      
      this.searchId = searchId;
      this.searchSince = searchSince;
   }

   /**
    * Gets the unique ID of the search to be run.
    * @return The search ID.
    */
   public String getSearchId(){
      return this.searchId;
   }
   
   /**
    * Gets the date from which the search results are required.
    * @return The date from which search results will be sought.
    */
   public Date getSearchSince(){
      return this.searchSince;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((searchId == null) ? 0 : searchId.hashCode());
      result = prime * result + ((searchSince == null) ? 0 : searchSince.hashCode());
      
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
      StoredSearchRequest other = (StoredSearchRequest) obj;
      if (searchId == null) {
         if (other.searchId != null)
            return false;
      }
      else if (!searchId.equals(other.searchId))
         return false;
      if (searchSince == null) {
         if (other.searchSince != null)
            return false;
      }
      else if (!searchSince.equals(other.searchSince))
         return false;
      
      return true;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[searchId: ");
      buff.append(searchId);
      buff.append("|searchSince: ");
      buff.append(searchSince);
      buff.append("]");
      
      return buff.toString();
   }
}
