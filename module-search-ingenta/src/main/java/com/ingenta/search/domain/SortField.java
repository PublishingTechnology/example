/*
 * SortField
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.io.Serializable;

/**
 * A Class to encapsulate sorting by field. It includes the name of a field
 * on which to sort and the order (either descending or ascending). An ordered
 * List of <code>SortField</code>s could be used to specify 'sorts within sorts'
 * if this functionality is required.
 * 
 * @author Mike Bell
 */
public class SortField implements Serializable {

   private static final long serialVersionUID = 1L;
   private String fieldName;
   private boolean descending = true; // default setting
   
   /**
    * Creates an instance which will sort in the given direction on the
    * given field.
    * @param descending Flag indicating whether to sort descending (true) or
    * ascending (false).
    * @param fieldName The name of the field on which to sort.
    */
   public SortField(boolean descending, String fieldName) {
      this.descending = descending;
      this.fieldName = fieldName;
   }

   /**
    * Creates an instance which will sort on the given field.
    * @param fieldName The name of the field on which to sort.
    */
   public SortField(String fieldName) {
      this.fieldName = fieldName;
   }

   /**
    * Default constructor - mainly used for XML serialization/deserialization.
    */
   public SortField() {
      // default constructor
   }

   /**
    * Gets the name of the sort field.
    * @return the fieldName
    */
   public String getFieldName() {
      return fieldName;
   }

   /**
    * Sets the name of the sort field.
    * @param fieldName the fieldName to set
    */
   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   /**
    * Gets the sort direction flag.
    * @return the flag <code>true</code> if descending, <code>false</code>
    * if ascending.
    */
   public boolean isDescending() {
      return this.descending;
   }

   /**
    * Sets the sort direction flag.
    * @param descending the flag <code>true</code> if descending, <code>false</code>
    * if ascending.
    */
   public void setDescending(boolean ascending) {
      this.descending = ascending;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[fieldName: ");
      buff.append(fieldName);
      buff.append("|descending: ");
      buff.append(descending);
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
      result = prime * result + (descending ? 1231 : 1237);
      result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());

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
      SortField other = (SortField) obj;
      if (descending != other.descending)
         return false;
      if (fieldName == null) {
         if (other.fieldName != null)
            return false;
      }
      else if (!fieldName.equals(other.fieldName))
         return false;
      return true;
   }
}
