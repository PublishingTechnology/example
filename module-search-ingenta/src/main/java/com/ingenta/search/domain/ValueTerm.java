/*
 * ValueTerm
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;


/**
 * A search term representing a single value.
 * 
 * @author Mike Bell
 */
public class ValueTerm implements SearchTerm{

   private static final long serialVersionUID = 1L;
   private String value;

   /**
    * Constructs an instance from the given value.
    * @param value The value for this term.
    */
   public ValueTerm(String value) {
      this.value = value;
   }
   
   /**
    * Sets the value for this term.
    * @param value the value to set
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * Gets the value for this term.
    * @return the value.
    */
   public String getValue() {
      return this.value;
   }
   
   /* (non-Javadoc)
    * @see com.ingenta.search.SearchElement#getAsString()
    */

   public String getAsString() {
      return getValue();
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
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      
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
      ValueTerm other = (ValueTerm) obj;
      if (value == null) {
         if (other.value != null)
            return false;
      }
      else if (!value.equals(other.value))
         return false;
      
      return true;
   }
}
