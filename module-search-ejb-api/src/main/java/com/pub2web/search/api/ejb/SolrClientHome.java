/*
 * SolrClientHome
 *
 * Copyright 2011 Publishing Technology PLC.
 */
package com.pub2web.search.api.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * The SOLR Client EJB Home Interface.
 * 
 * @author James Beard
 */
public interface SolrClientHome extends EJBHome {

    /** The Constant COMP_NAME. Component environment JNDI name */
    public static final String COMP_NAME = "java:comp/env/ejb/SolrClient";

    /** The Constant JNDI_NAME. */
    public static final String JNDI_NAME = "ejb/search/SolrClient";

    /**
     * Creates the remote EJB object.
     * 
     * @return the SOLR Client remote object
     * 
     * @throws CreateException
     *             the create exception
     * @throws java.rmi.RemoteException
     *             the remote exception
     */
    public SolrClientRemote create()throws CreateException, RemoteException;
}
