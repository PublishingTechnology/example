package com.ingenta.search.store;

import java.io.Serializable;

import com.ingenta.search.domain.Search;
import com.ingenta.search.savedsearch.SavedSearch;

/**
 * An API for a class which creates store-specific queries from a generic 
 * Search Object.
 * 
 * @author Mike Bell
 */
public interface QueryGenerator extends Serializable {
	
   /**
    * Creates the appropriate instance of Query from the given Search.
    * The generated query will include the given saved search's 'last run on' 
    * date if it is present.
    * @param savedSearch The search which is to be the source of the query.
    * @return A Query ready to be submitted to the store.
    */
   Query generateQuery(SavedSearch savedSearch);
   
	/**
	 * Creates the appropriate instance of Query from the given Search.
	 * @param search The search which is to be the source of the query.
	 * @return A Query ready to be submitted to the store.
	 */
	Query generateQuery (Search search);
}
