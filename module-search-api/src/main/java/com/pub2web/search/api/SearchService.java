/*
 * SearchService
 * 
 * Copyright 2011 Publishing Technology PLC.
 */
package com.pub2web.search.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.ingenta.search.store.RawResult;
import com.pub2web.search.api.autosuggest.AutosuggestResult;
import com.pub2web.search.api.morelikethis.MoreLikeThisResult;

/**
 * Service interface for connecting to a remote search client.
 * 
 * @author James Beard
 */
public interface SearchService extends Remote {
    /**
     * Perform a search, specifying the lucene query, start and page size values,
     * and optionally the names of the fields to retrieve.
     * @param query The Solr query, which may include a sort clause
     * @param start The first result to display (zero-indexed)
     * @param pageSize The number of results on a page
     * @param fields Array of field names that should be returned in the Solr response.
     * @throws java.rmi.RemoteException
     */
    RawResult search(String query, int start, int pageSize, String[] fields) throws RemoteException;

    /**
     * Perform a search, specifying the lucene query, start and page size values,
     * and optionally the names of the fields to retrieve.
     * @param query The Solr query, which may include a sort clause
     * @param start The first result to display (zero-indexed)
     * @param pageSize The number of results on a page
     * @param fields Array of field names that should be returned in the Solr response.
     * @param highlight Array of field names that Solr should attempt to highlight
     * @throws java.rmi.RemoteException
     */
    RawResult search(String query, int start, int pageSize, String[] fields, String[] highlight)
    throws RemoteException;

    /**
     * Perform a search, specifying the lucene query, start and page size values,
     * and optionally the names of the fields to retrieve.
     * @param query The Solr query, which may include a sort clause
     * @param start The first result to display (zero-indexed)
     * @param pageSize The number of results on a page
     * @param fields Array of field names that should be returned in the Solr response.
     * @return String value which is the search results XML.
     * @throws java.rmi.RemoteException
     */
    String searchAsXml(String query, int start, int pageSize, String[] fields) throws RemoteException;

    /**
     * Performs an autosuggest, given a user-specified search prefix, plus a limit on the number
     * of results. Highest-frequency results will be returned, in descending order of frequency.
     * @param prefix the search prefix entered by the user, from which suggestions will be generated.
     * @param limit limit on number of results returned
     * @return result object containing the autosuggestions
     * @throws java.rmi.RemoteException
     */
    public AutosuggestResult autosuggest(String prefix, int limit) throws RemoteException;

    /**
     * Performs a 'more like this' request on the item with given web id. It will use the given
     * SOLR fields to work out similar results, applying any given restrictions to these results,
     * and limiting the number of results to a maximum of the given limit.
     * @param webId web id of item for which we find similar items
     * @param fields comma-separated list of fields to use to judge 'similarity'
     * @param restrictions restrictions on result set in SOLR-query format (e.g. "contentType:Book")
     * @param limit limit on number of results returned
     * @return result object containing the similar results
     * @throws java.rmi.RemoteException
     */
    public MoreLikeThisResult moreLikeThis(String webId, String fields, String restrictions, int limit)
    throws RemoteException;
}