/*
 * SolrClientRemote
 * 
 * Copyright 2011 Publishing Technology PLC.
 */
package com.pub2web.search.api.ejb;

import javax.ejb.EJBObject;

import com.pub2web.search.api.SearchService;

/**
 * Remote interface for the SolrClient EJB.
 * 
 * @author James Beard
 */
public interface SolrClientRemote extends EJBObject, SearchService {

}