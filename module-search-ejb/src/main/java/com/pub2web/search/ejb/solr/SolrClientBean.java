/*
 * SolrClientBean
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextJndiBeanFactoryLocator;

/**
 * EJB (session bean) used for SOLR searches
 * @author jbeard
 */
public class SolrClientBean extends SolrClientService implements SessionBean {

    private static final Logger LOG = Logger.getLogger(SolrClientBean.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The session context. */
    @SuppressWarnings("unused")
    private SessionContext sessionContext;

    /*
     * The following are the boilerplate EJB 2.1 mandatory methods.
     */

    /**
     * Ejb create.
     */
    public void ejbCreate() {
        LOG.log(Level.INFO, "ejbCreate");
        setConfig(getSolrClientConfig());
        initHttp();
    }

    /*
     * (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    @Override
    public void ejbActivate() {
    }

    /*
     * (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    @Override
    public void ejbPassivate() {
    }

    /*
     * (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    @Override
    public void ejbRemove() {
        shutdownHttp();
    }

    /*
     * (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    @Override
    public void setSessionContext(SessionContext context) throws EJBException, RemoteException {
        this.sessionContext = context;
    }

    /**
     * Gets the config used for setting up the SOLR client.
     * This can be overridden, e.g. for JUnits
     * @return the config object
     */
    protected SolrClientConfig getSolrClientConfig() {
        ContextJndiBeanFactoryLocator b = new ContextJndiBeanFactoryLocator();
        BeanFactoryReference r = b.useBeanFactory("java:comp/env/BeanFactoryPath");
        BeanFactory f = r.getFactory();
        return (SolrClientConfig) f.getBean("search.solrClientConfig");
    }
}