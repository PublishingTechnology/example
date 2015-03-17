/*
 * SearchTermGroup
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class represents a collection of search terms which is itself a search
 * term. In a String representation, a search term group is implicitly wrapped
 * in a pair of brackets. An example would be <code>(cheese AND onion)</code>.
 * 
 * @author Mike Bell
 */
public class SearchTermGroup implements SearchTerm{

   private static final long serialVersionUID = 1L;
   private List<SearchTerm> searchTerms;
   
   /**
    * Constructs a new instance with an empty list of search terms.
    */
   public SearchTermGroup(){
      this.searchTerms = new ArrayList<SearchTerm>();
   }
   
   /**
    * Adds a search term to the group.
    * @param term The term to add.
    */
   public void addTerm(SearchTerm term){
      this.searchTerms.add(term);
   }
   
   /**
    * Adds the given search terms to any existing ones. This method
    * is not to be confused with the <code>setSearchTerms()</code> method.
    * @param terms the terms to add.
    */
   public void addSearchTerms(List<SearchTerm> terms) {
      this.searchTerms.addAll(terms);
   }
   
   /**
    * Gets the search terms in the group.
    * @return The search terms.
    */
   public List<SearchTerm> getSearchTerms(){
      return this.searchTerms;
   }
      
   /**
    * Sets the search terms for this group. This will <i>replace</i>
    * any existing ones. If you wish to add terms to the existing list,
    * then use the <code>addSearchTerms()</code> method instead.
    * @param terms the terms to set.
    */
   public void setSearchTerms(List<SearchTerm> terms) {
      this.searchTerms = terms;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.SearchElement#getAsString()
    */
   public String getAsString(){
      StringBuilder buff = new StringBuilder();
      
      if(!this.searchTerms.isEmpty()){
         buff.append("(");
         
         for (Iterator<SearchTerm> i = this.searchTerms.iterator(); i.hasNext();) {
            SearchTerm term = i.next();
            
            if(!" ".equals(buff.substring(buff.length() -1))){
               buff.append(Search.SPACE);
            }
            
            buff.append(term.getAsString());
         }
         
         buff.append(" )");
      }
      
      return buff.toString();
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[");
      buff.append(this.getAsString());
      buff.append("]");
      
      return buff.toString();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((searchTerms == null) ? 0 : searchTerms.hashCode());
      
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
      SearchTermGroup other = (SearchTermGroup) obj;
      if (searchTerms == null) {
         if (other.searchTerms != null)
            return false;
      }
      else if (!searchTerms.equals(other.searchTerms))
         return false;
      
      return true;
   }
}
