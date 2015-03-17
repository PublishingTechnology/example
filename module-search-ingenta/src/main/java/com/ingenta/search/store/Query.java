/*
 * Query
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.store;

/**
 * An API for a generic query to a search store. Implementations of this will
 * be created for specific stores (eg SolR).
 * 
 * @author Mike Bell
 */
public interface Query {
	
   /**
    * Gets the fields to which the query applies.
    * @return The fields.
    */
   public String[] getFields();
	
   /**
    * Sets the fields to which the query applies.
    * @param fields The fields to set.
    */
   public void setFields(String[] fields);
	
   /**
    * Gets the fields to which highlights apply.
    * @return The highlighted fields.
    */
   public String[] getHighlights();             
	
   /**
    * Sets the fields to which highlights apply.
    * @param highLights the fields to highlight.
    */
   public void setHighlights(String[] highLights);
	
   /**
    * Gets the actual query string which will be submitted to the store.
    * @return The query String.
    */
   public String getQuery();
	
   /**
    * Sets the actual query string which will be submitted to the store.
    * @param query The query String to set.
    */
   public void setQuery(String query);
	
   /**
    * Gets the number of rows (ie search results) to fetch.
    * @return The number of rows.
    */
   public int getRows();
	
   /**
    * Sets the number of rows (ie search results) to fetch.
    * @param rows The number of rows to set.
    */
   public void setRows(int rows);
	
   /**
    * Gets the row number of the result at which to start.
    * @return the row number of the result at which to start.
    */
   public int getStart();
	
   /**
    * Sets the row number of the result at which to start.
    * @param start the row number of the result at which to start.
    */
   public void setStart(int start);
   
   /**
    * Gets the flag indicating if results are to be highlighted.
    * @return true if Highlighting is switched on.
    */
   public boolean isHighlighting();

   /**
    * Sets the flag indicating if results are to be highlighted. This defaults 
    * to <code>true</code>.
    * @param isHighlighting the isHighlighting to set
    */
   public void setHighlighting(boolean isHighlighting);
}
