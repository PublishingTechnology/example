/*
 * SearchExpression
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;


/**
 * A class to express a single 'line' of a search. It will encapsulate
 * a search element such as <code>AND someField EQUALS someValue AND 
 * (anotherValue OR aThirdValue)</code>. It wraps a <code>SearchCondition</code>.
 * Its operator parameter may be <code>null</code>, to reflect the case where
 * this is the first or only search element; such as when the example above becomes:
 * <code>someField EQUALS someValue AND (anotherValue OR aThirdValue)</code>.
 * 
 * @author Mike Bell
 */
public class SearchExpression implements SearchElement {

   private static final long serialVersionUID = 1L;
   private Operator operator = Operator.AND; // default value
   private SearchCondition searchCondition;
   
   /**
    * Constructor to be used when no Operator is required, ie
    * when this is the first or only expression in the search.
    * @param searchCondition The search condition to be wrapped.
    */
   public SearchExpression(SearchCondition searchCondition) {
      this.searchCondition = searchCondition;
   }
   
   /**
    * The 'standard' constructor.
    * @param operator The Operator for this search expression.
    * @param searchCondition The search condition to be wrapped.
    */
   public SearchExpression(Operator operator, SearchCondition searchCondition) {
      this.operator = operator;
      this.searchCondition = searchCondition;
   }
   
   /**
    * Gets the Operator.
    * @return the operator
    */
   public Operator getOperator() {
      return operator;
   }

   /**
    * The Wrapped search condition.
    * @return the searchCondition
    */
   public SearchCondition getSearchCondition() {
      return searchCondition;
   }

   /**
    * Sets the given Operator.
    * @param operator the operator to set
    */
   public void setOperator(Operator operator) {
      this.operator = operator;
   }

   /**
    * Sets the given search condition.
    * @param searchCondition the searchCondition to set
    */
   public void setSearchCondition(SearchCondition searchCondition) {
      this.searchCondition = searchCondition;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.SearchElement#getAsString()
    */
   public String getAsString(){
      StringBuilder buff = new StringBuilder();
      
      if(this.operator != null){
         buff.append(this.operator);
         buff.append(Search.SPACE);
      }
      
      if(buff.length() > 0 && !" ".equals(buff.substring(buff.length() -1))){
         buff.append(Search.SPACE);
      }
      
      buff.append(this.searchCondition.getAsString());
      
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
      result = prime * result + ((searchCondition == null) ? 0 : searchCondition.hashCode());

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
      SearchExpression other = (SearchExpression) obj;
      if (operator == null) {
         if (other.operator != null)
            return false;
      }
      else if (!operator.equals(other.operator))
         return false;
      if (searchCondition == null) {
         if (other.searchCondition != null)
            return false;
      }
      else if (!searchCondition.equals(other.searchCondition))
         return false;
      
      return true;
   }
}
