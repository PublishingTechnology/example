/*
 * BrowseSearchListingService
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.ejb.browse;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ingenta.search.store.SearchResult;
import com.pub2web.browse.api.ContentViewService;
import com.pub2web.browse.api.value.ContentViewException;
import com.pub2web.rdf.cci.facet.ContentItem;
import com.pub2web.search.api.SearchListingService;


/** SearchListingService implementation that combines the search results with data from the browse service.
    @author Keith Hatton
 */
public class BrowseSearchListingService
      implements SearchListingService {
   
   private static final Logger LOG = Logger.getLogger(BrowseSearchListingService.class);
   
   private ContentViewService vsrv = null;
   private SearchResultTransformer resultTransformer = null;
   
   /** Sets the content view service that will be used by this instance.
       @param viewer the content view service
    */
   public void setContentViewService(ContentViewService viewer) {
      if (LOG.isDebugEnabled())
         LOG.log(Level.DEBUG, String.format("setContentViewService(%s)", viewer));
      this.vsrv = viewer;
   }
   
   /** Sets the search result transformer that constructs RDF/XML from the search results.
       @param t the transformer
    */
   public void setSearchResultTransformer(SearchResultTransformer t) {
      if (LOG.isDebugEnabled())
         LOG.log(Level.DEBUG, String.format("setSearchResultTransformer(%s)", t));
      resultTransformer = t;
   }
   
   @Override
   public List<ContentItem> getContentItems(List<SearchResult> results, String view)
         throws RemoteException {
      if (LOG.isDebugEnabled())    
         LOG.log(Level.DEBUG, String.format("getContentItems(%s,%s)", results, view));
      
      Map<URI,ContentItem> searchItems = new java.util.LinkedHashMap<URI,ContentItem>();
      for (SearchResult res : results) {
         try {
            URI id = new URI(res.getIdentifier());
            ContentItem searchItem = null;
            if (resultTransformer != null)
               searchItem = resultTransformer.getContentItem(res);
            
            searchItems.put(id, searchItem);
         }
         catch (URISyntaxException e) {
            LOG.log(Level.WARN, "search results contain invalid content item", e);
            throw new IllegalArgumentException(String.format("invalid search result identifier: %s", res.getIdentifier()), e);
         }
      }
      
      Map<URI,ContentItem> items = new java.util.HashMap<URI,ContentItem>();
      try {
         Map<String,Object> empty = Collections.emptyMap();
         Set<ContentItem> v = vsrv.getViews(searchItems.keySet(), view, empty);
         for (ContentItem browseItem : v) {
            URI id = browseItem.getId();
            ContentItem searchItem = searchItems.get(id);
            ContentItem item = merge(browseItem, searchItem);
            
            items.put(id, item);
         }
      }
      catch (ContentViewException e) {
         LOG.log(Level.ERROR, "unable to obtain content item for search results", e);
         throw new RemoteException("unable to obtain content item for search results", e);
      }
      
      List<ContentItem> listings = new java.util.ArrayList<ContentItem>();
      for (URI u : searchItems.keySet()) {
         ContentItem item = items.get(u);
         if (item != null)
            listings.add(item);
         
      }
      
      return Collections.unmodifiableList(listings);
   }
   
   /** Merge the properties of two content items.
       @param base the base version of the content item
       @param item the content item containing replacement property values
       @return a new content item containing the result of the merge
    */
   private ContentItem merge(ContentItem base, ContentItem item) {
      if (base == null)
         return item;
      
      if (item == null)
         return base;
      
      if (!base.getId().equals(item.getId())) {
         LOG.log(Level.WARN, String.format("content item identifiers (%s,%s) are not equal - not merging",
               base.getId(), item.getId()));
         
         return base;
      }
      
      Map<String,Object> properties = new java.util.HashMap<String,Object>(base.getProperties());
      for (Map.Entry<String,Object> en : item.getProperties().entrySet()) {
         String key = en.getKey();
         Object value = en.getValue();
         
         if (value instanceof ContentItem) {
            Object baseValue = properties.get(key);
            if (baseValue instanceof ContentItem) {
               value = merge((ContentItem)baseValue, (ContentItem)value);
            }
         }
         
         properties.put(key, value);
      }
      
      return new ContentItem(base.getId(), base.getWebId(), properties);
   }
}
