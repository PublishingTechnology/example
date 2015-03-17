/*
 * MoreLikeThisResultFactory
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.morelikethis;

import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;

import com.ingenta.search.domain.SearchFailedException;
import com.ingenta.search.store.RawResult;
import com.pub2web.search.api.morelikethis.MoreLikeThisResult;
import com.pub2web.search.ejb.solr.ResultBuilder;
import com.pub2web.search.ejb.solr.digester.ResultBuilderDigesterImpl;

/**
 * Factory for creating a <code>MoreLikeThisResult</code> out of an HTTP
 * request to a suitable URL.
 * @author jbeard
 */
public class MoreLikeThisResultFactory {

    /**
     * Gets a <code>MoreLikeThisResult</code> using an HTTP GET method.
     * @param get HTTP GET request for 'more like this' results
     * @param limit limit on number of results
     * @return result of the 'more like this' query
     * @throws SearchFailedException if there is a problem getting 'more like this' results using
     * the given GET method.
     */
    public MoreLikeThisResult getResult(GetMethod get, int limit) throws SearchFailedException {
        ResultBuilder rb = new ResultBuilderDigesterImpl();
        try {
            RawResult result = rb.buildResult(get.getResponseBodyAsStream(), 0, limit);
            return new MoreLikeThisResult(result);
        }
        catch (IOException ex) {
            throw new SearchFailedException("IO Error getting SOLR response: " + ex.getMessage());
        }
    }
}