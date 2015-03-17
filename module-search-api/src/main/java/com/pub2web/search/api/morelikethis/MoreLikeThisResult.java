/*
 * MoreLikeThisResult
 * 
 * Copyright 2011 Publishing Technology PLC.
 */
package com.pub2web.search.api.morelikethis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ingenta.search.store.Document;
import com.ingenta.search.store.RawResult;

/**
 * Container for the result of the "More Like This" query.
 * It basically contains a list of the IDs of the "similar" objects returned by the query.
 * @author jbeard
 */
public class MoreLikeThisResult {

    private List<String> ids;

    /**
     * Constructs a <code>MoreLikeThisResult</code> from the given raw result object
     * @param result raw result object
     */
    public MoreLikeThisResult(RawResult result) {
        init(result.getDocuments().values());
    }

    /**
     * Gets a list of the IDs of the similar items from a 'more like this' query
     * @return list of IDs of similar items
     */
    public List<String> getIds() {
        return ids;
    }

    /**
     * Initialises this result from the collection of result documents
     * @param results collection of result documents
     */
    private void init(Collection<Document> results) {
        ids = new ArrayList<String>(results.size());
        for (Document result: results) {
            ids.add(result.getId());
        }
    }
}