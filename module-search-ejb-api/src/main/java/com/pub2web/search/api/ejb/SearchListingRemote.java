/*
 * SearchListingRemote
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.api.ejb;

import javax.ejb.EJBObject;

import com.pub2web.search.api.SearchListingService;


/** Remote interface for the SearchListing EJB.
    @author Keith Hatton
 */
public interface SearchListingRemote
      extends SearchListingService, EJBObject {

}
