/*
 * SearchListingService
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.api;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.ingenta.search.store.SearchResult;
import com.pub2web.rdf.cci.facet.ContentItem;


/** Service interface for obtaining search listings.
    @author Keith Hatton
 */
public interface SearchListingService
      extends Remote {
   
   /**@deprecated this method is a workaround for executing the search in the web tier.
       Transform search results into content items.
       @param results the search results
       @param view the view name
       @return a corresponding list of content items
       @throws java.rmi.RemoteException if there is an unhandled server exception
    */
   public List<ContentItem> getContentItems(List<SearchResult> results, String view) throws RemoteException;
}
