/*
 * SearchResult
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.Map;

/**
 * A generic abstraction encapsulating a single result obtained in
 * response to executing a search.
 * 
 * @author Mike Bell
 */
public interface SearchResult extends Serializable {
   
   /**
    * Gets the unique identifier of the search result.
    * @return The identifier.
    */
   String getIdentifier();
   
   /**
    * Gets the properties of the search result.
    * @return The properties.
    */
   Map<String, Object> getProperties();
   
   /**
    * Sets the property of the given name to the given value.
    * @param propertyName The property name.
    * @param property The property value to be set..
    */
   void setProperty(String propertyName, Object property);
   
   /**
    * Gets the property value corresponding to the given property name.
    * @param propertyName The name of the sought property.
    * @return The property value or <code>null</code> if the property does 
    * not exist.
    */
   Object getProperty(String propertyName);
}
