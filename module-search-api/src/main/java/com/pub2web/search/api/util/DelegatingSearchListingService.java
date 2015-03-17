/*
 * DelegatingSearchListingService
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.api.util;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ingenta.search.store.SearchResult;
import com.pub2web.rdf.cci.facet.ContentItem;
import com.pub2web.search.api.SearchListingService;


/** Implementation of SearchListingService that delegates to another implementation.
    @author Keith Hatton
 */
public class DelegatingSearchListingService
      implements SearchListingService {
   
   private static final Logger LOG = Logger.getLogger(DelegatingSearchListingService.class);
   
   private SearchListingService delegate = null;
   
   /** Sets the SearchListingService to which this instance will delegate calls.
       @param delegate the delegate
    */
   public void setDelegate(SearchListingService delegate) {
      LOG.log(Level.DEBUG, String.format("setDelegate(%s)", delegate));
      this.delegate = delegate;
   }
   
   /** Gets the SearchListingService to which this instance will delegate calls.
       @return the delegate
    */
   public SearchListingService getDelegate() {
      return delegate;
   }
   
   /** Gets the SearchListingService to which this instance will delegate calls, and
       throws an IllegalStateException if no delegate is set.
       @return the delegate
    */
   private SearchListingService getNonNullDelegate() {
      SearchListingService d = getDelegate();
      if (d == null) {
         LOG.log(Level.ERROR, "DelegatingSearchListingService has no delegate");
         throw new IllegalStateException("DelegatingSearchListingService has no delegate");
      }
      
      return d;
   }
   
   @Override
   public List<ContentItem> getContentItems(List<SearchResult> results, String view)
         throws RemoteException {
      
      LOG.log(Level.DEBUG, String.format("getContentItems(%s,%s)", results, view));
      
      return getNonNullDelegate().getContentItems(results, view);
   }
}
