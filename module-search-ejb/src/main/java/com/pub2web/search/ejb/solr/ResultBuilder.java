/*
 * ResultBuilder
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr;

import java.io.InputStream;

import com.ingenta.search.domain.SearchFailedException;
import com.ingenta.search.store.RawResult;

/**
 * Interface used for building a <code>RawResult</code> from the XML returned by SOLR.
 * @author jbeard
 */
public interface ResultBuilder {
    /**
     * Builds the result from the given search results XML.
     * @param solrXml stream containing the search results XML
     * @param start index of result on which to start
     * @param pageSize page size to indicate number of results to return
     * @return result object
     * @throws SearchFailedException if there was a problem building the result
     */
    public RawResult buildResult(InputStream solrXml, int start, int pageSize)
    throws SearchFailedException;
}