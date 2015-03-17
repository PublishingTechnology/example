/*
 * SolrClientLookupDelegate
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.ingenta.search.store.solr;

import com.pub2web.search.api.ejb.SolrClientHome;
import com.pub2web.search.api.ejb.SolrClientRemote;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;

/**
 * Delegate that looks up the Solr Client EJB via JNDI
 * @author jbeard
 */
public class SolrClientLookupDelegate {
    
    private static final String SOLR_CLIENT_JNDI_NAME = "java:comp/env/ejb/search/SolrClient";
    
    /**
     * Look up the SOLR client via JNDI
     * @return the SOLR client to use for searches
     * @throws javax.naming.NamingException if there is a failure to look up the SOLR client
     * @throws javax.ejb.CreateException if there is a failure to created the SOLR client
     * @throws java.rmi.RemoteException if there is a remote communication problem
     */
    public SolrClientRemote getSolrClient() throws NamingException, CreateException, RemoteException {
        InitialContext ictx = new InitialContext();
        String jndiName = "java:comp/env/ejb/search/SolrClient";
        if (System.getProperties().containsKey("program.name")){
            String progName = System.getProperties().getProperty("program.name");
            if (progName.equals("SearchAlertRun")){
                System.out.println("*******SearchAlertRun*********");
                jndiName = "ejb/search/SolrClient";
            }
        }
        Object o = ictx.lookup(jndiName);
        SolrClientHome scHome = (SolrClientHome) PortableRemoteObject.narrow(o, SolrClientHome.class);
        return scHome.create();
    }
}