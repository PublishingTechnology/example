/*
 * AutosuggestTerm
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.api.autosuggest;

/**
 * Represents a single term in a set of auto-suggestions.
 * Each term has a name (the suggested search result itself), 
 * and a count (frequency of term within results).
 * @author jbeard
 */
public interface AutosuggestTerm {
    /**
     * Gets the name of this autosuggest term - this is the search term itself
     * @return term name
     */
    public String getName();
    /**
     * Gets the count of this autosuggest term within all the results.
     * @return frequency count of term
     */
    public int getCount();
}