/*
 * SearchListingHome
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.api.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;


/** Home interface for the SearchListing EJB.
    @author Keith Hatton
 */
public interface SearchListingHome
      extends EJBHome {
   
   /** Creates a SearchListingRemote reference to a bean instance.
       @return a SearchListingRemote reference
       @throws CreateException if there is an exception creating the bean
       @throws java.rmi.RemoteException if there is an unhandled server exception
    */
   public com.pub2web.search.api.ejb.SearchListingRemote create() throws CreateException, RemoteException;
}
