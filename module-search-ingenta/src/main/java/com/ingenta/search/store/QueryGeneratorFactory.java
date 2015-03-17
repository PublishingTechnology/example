/*
 * QueryGeneratorFactory
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.store;


import com.ingenta.search.store.solr.DefaultSolrQueryGenerator;

/**
 * A factory class intended to get the appropriate instance of QueryGenerator for
 * the type of store in use.
 * 
 * @author Mike Bell
 */
public class QueryGeneratorFactory {

   /**
    * Gets the QueryGenerator corresponding to the given config ID. 
    * @param configId The config ID which determines which type of 
    * QueryGenerator is in use.
    * @return The appropriate instance of QueryGenerator.
    */
   public QueryGenerator getQueryGenerator(String configId){
      // TODO MB at present this is hard-wired to return a SolrQueryGenerator
	  //
	   
	  // TSM - this loads an instance of alternatice query generator which is loaded 
	  // from the system properties and if one is not found then we use the default SolrQueryGenerator.
	   
      return DefaultSolrQueryGenerator.getInstance();
   }
}
