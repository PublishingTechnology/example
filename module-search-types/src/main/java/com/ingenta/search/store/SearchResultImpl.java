/*
 * SearchResultImpl
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.store;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of <code>SearchResult</code>. This is,
 * essentially, a transfer-object.
 * 
 * @author Mike Bell
 */
public class SearchResultImpl implements SearchResult {

   private static final long serialVersionUID = 1L;
   private String identifier;
   private Map<String, Object> properties;
   
   /**
    * Constructs an instance from the give parameters, neither of which
    * may be <code>null</code>.
    * @param identifier The identifier.
    * @param properties The properties.
    */
   public SearchResultImpl(String identifier, Map<String, Object> properties) {
      if(identifier == null || properties == null){
         throw new IllegalArgumentException("Null parameters not valid");
      }
      
      this.identifier = identifier;
      this.properties = properties;
   }
   
   /**
    * Constructs an instance from the give identifier (which may not 
    * be <code>null</code>) with an empty properties Map.
    * @param identifier The identifier.
    */
   public SearchResultImpl(String identifier) {
      this(identifier, new HashMap<String, Object>());
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.SearchResult#getIdentifier()
    */
   public String getIdentifier() {
      return this.identifier;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.SearchResult#getProperties()
    */
   public Map<String, Object> getProperties() {
      return this.properties;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.SearchResult#getProperty(java.lang.String)
    */
   public Object getProperty(String propertyName) {
      return this.properties.get(propertyName);
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.SearchResult#setProperty(java.lang.String, java.lang.Object)
    */
   public void setProperty(String propertyName, Object property) {
      this.properties.put(propertyName, property);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder buff = new StringBuilder(this.getClass().getName());
      buff.append("[identifier: ");
      buff.append(this.identifier);
      buff.append("|properties: ");
      buff.append(this.properties);
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
      result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());

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
      SearchResultImpl other = (SearchResultImpl) obj;
      if (identifier == null) {
         if (other.identifier != null)
            return false;
      }
      else if (!identifier.equals(other.identifier))
         return false;
      if (properties == null) {
         if (other.properties != null)
            return false;
      }
      else if (!properties.equals(other.properties))
         return false;
      return true;
   }
}
