/*
 * TermsWriter
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.web.autosuggest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;

import org.json.JSONArray;

import com.pub2web.search.api.autosuggest.AutosuggestTerm;

/**
 * Class that writes the autosuggest terms in the format expected by the UI,
 * which is a JSONArray object.
 * @author jbeard
 */
class TermsWriter {

    private final Collection<AutosuggestTerm> terms;

    /**
     * Constructs a writer from the given list of autosuggest terms.
     * @param terms list of auto-suggestions (terms).
     */
    public TermsWriter(Collection<AutosuggestTerm> terms) {
        this.terms = terms;
    }

    /**
     * Converts the list of terms into a stream.
     * The terms are written in the format of a JSONArray, e.g.
     * ["term1", "term2", "term3"]
     * @return stream containing the terms
     */
    public InputStream toStream() {
        JSONArray arr = new JSONArray();
        for (AutosuggestTerm term : terms) {
            arr.put(term.getName());
        }
        byte[] bytes = arr.toString().getBytes(Charset.forName("utf-8"));
        return new ByteArrayInputStream(bytes);
    }
}