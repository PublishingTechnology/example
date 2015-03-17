/*
 * ResultsFactory Copyright 2008 Publishing Technology PLC.
 */

package com.ingenta.search.store;

import com.ingenta.search.domain.SearchFailedException;

/**
 * An API for Results factories. Different implementations may be required for
 * different search store implementations. Instances are to be obtained via the
 * <code>getInstance()</code> method on AbstractResultsFactory. TODO MB don't like 
 * that - we should have a separate factory class rather than depending on 
 * AbstractResultsFactory.
 * 
 * @author Mike Bell
 */
public interface ResultsFactory {

   /**
    * Executes the given query on the store.
    * @param query The query to be run.
    * @return the results of running the query.
    * @throws SearchFailedException If there is an error executing the search.
    */
   SearchResults executeQuery(Query query) throws SearchFailedException;

   /**
    * Executes the given query on the store as an Xml string.
    * @param query The query to be run.
    * @return the results of running the query as the raw Xml string.
    * @throws SearchFailedException If there is an error executing the search.
    */
   String executeQueryForXml(Query query) throws SearchFailedException;

   /**
    * Gets the flag indicating if this resultsFactory instance is highlighting 
    * search criteria or not.
    * @return true if highlighting is switched on.
    */
   boolean isHighlighting();

   /**
    * Sets the flag indicating if this resultsFactory instance is highlighting 
    * search criteria or not.
    * @param highlighting
    */
   void setHighlighting(boolean highlighting);

}