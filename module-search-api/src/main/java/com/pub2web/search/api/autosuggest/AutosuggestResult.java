/*
 * AutosuggestResult
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.api.autosuggest;

import java.util.Collection;

/**
 * Result of an Autosuggest query - this consists of a list of terms (each one of which
 * represents a single suggestion).
 * @author jbeard
 */
public interface AutosuggestResult {
    /**
     * Gets a list of the suggested terms for an Autosuggest query.
     * @return list of suggested terms
     */
    public Collection<AutosuggestTerm> getTerms();
}