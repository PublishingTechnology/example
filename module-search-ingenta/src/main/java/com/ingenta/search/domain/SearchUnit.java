/*
 * SearchUnit
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.util.Iterator;
import java.util.List;


/**
 * A class which contains a one or more search expressions each of which 
 * constitutes, in effect, a sub-search. A search will contain one or more
 * Search Units.
 * 
 * @author Mike Bell
 */
public class SearchUnit implements SearchElement {

   private static final long serialVersionUID = 1L;
   private Operator operator;
   private List<SearchExpression> searchExpressions;
   
   /**
    * Constructs an instance from the given parameters.
    * @param operator The Operator which applies to this search unit. The
    * first search unit in a search will not have an operator. All subsequent
    * ones will.
    * @param searchExpressions The search expressions which apply to this 
    * search unit.
    */
   public SearchUnit(Operator operator, List<SearchExpression> searchExpressions) {
      this.operator = operator;
      this.searchExpressions = searchExpressions;
   }
   
   /**
    * Constructs an instance from the given parameters. The first search unit in 
    * a search will not have an operator and so should be created using this
    * constructor. 
    * @param searchExpressions The search expressions which apply to this 
    * search unit.
    */
   public SearchUnit(List<SearchExpression> searchExpressions) {
      this.searchExpressions = searchExpressions;
   }
   
   /**
    * Sets the Operator to the given value.
    * @param operator the operator to set.
    */
   public void setOperator(Operator operator) {
      this.operator = operator;
   }

   /**
    * Sets the search expressions for this unit. This will <i>replace</i>
    * any existing ones. If you wish to add expressions to the existing list,
    * then use the <code>addSearchExpressions()</code> method instead.
    * @param searchExpressions the searchExpressions to set.
    */
   public void setSearchExpressions(List<SearchExpression> searchExpressions) {
      this.searchExpressions = searchExpressions;
   }
   
   /**te
    * Adds the given search expressions to any existing ones. This method
    * is not to be confused with the <code>setSearchExpressions()</code> method.
    * @param searchExpressions the searchExpressions to add.
    */
   public void addSearchExpressions(List<SearchExpression> searchExpressions) {
      this.searchExpressions.addAll(searchExpressions);
   }

   /**
    * Gets the Operator for this unit.
    * @return the operator. May be <code>null</code>.
    */
   public Operator getOperator() {
      return operator;
   }

   /**
    * Gets the search Expressions for this unit.
    * @return the searchExpressions
    */
   public List<SearchExpression> getSearchExpressions() {
      return searchExpressions;
   }
   
   /**
    * Adds a search expression to this unit's list. This works the same
    * regardless of whether the list is empty or not.
    * @param searchExpression The expression to add.
    */
   public void addSearchExpression(SearchExpression searchExpression){
      searchExpressions.add(searchExpression);
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.SearchElement#getAsString()
    */

   public String getAsString() {
      StringBuilder buff = new StringBuilder();
      
      if(this.operator != null){
         buff.append(this.operator);
         buff.append(Search.SPACE);
      }
      
      for (Iterator<SearchExpression> i = this.searchExpressions.iterator(); i.hasNext();) {
         if(buff.length() > 0 && !" ".equals(buff.substring(buff.length() -1))){
            buff.append(Search.SPACE);
         }
         
         buff.append(i.next().getAsString());
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
      result = prime * result + ((operator == null) ? 0 : operator.hashCode());
      result = prime * result + ((searchExpressions == null) ? 0 : searchExpressions.hashCode());
      
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
      SearchUnit other = (SearchUnit) obj;
      if (operator == null) {
         if (other.operator != null)
            return false;
      }
      else if (!operator.equals(other.operator))
         return false;
      if (searchExpressions == null) {
         if (other.searchExpressions != null)
            return false;
      }
      else if (!searchExpressions.equals(other.searchExpressions))
         return false;
      
      return true;
   }
}
