/*
 * OperatorTerm
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;


/**
 * A search term which represents an operator.
 * 
 * @author Mike Bell
 * @see com.ingenta.search.domain.Operator
 */
public class OperatorTerm implements SearchTerm{

   private static final long serialVersionUID = 1L;
   private Operator operator;

   /**
    * Constructs an instance from the given operator.
    * @param operator The Operator to be wrapped.
    */
   public OperatorTerm(Operator operator) {
      this.operator = operator;
   }
   
   /**
    * Gets the Operator this term is wrapping
    * @return The Operator.
    */
   public Operator getOperator(){
      return this.operator;
   }
   
   /**
    * Sets the Operator this term is wrapping.
    * @param operator the operator to set
    */
   public void setOperator(Operator operator) {
      this.operator = operator;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.SearchElement#getAsString()
    */
   public String getAsString() {
      return this.operator.toString();
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
      OperatorTerm other = (OperatorTerm) obj;
      if (operator == null) {
         if (other.operator != null)
            return false;
      }
      else if (!operator.equals(other.operator))
         return false;
      
      return true;
   }
}
