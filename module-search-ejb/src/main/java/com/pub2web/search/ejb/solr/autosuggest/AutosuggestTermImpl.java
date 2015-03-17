/*
 * AutosuggestTermImpl
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.autosuggest;

import com.pub2web.search.api.autosuggest.AutosuggestTerm;

/**
 * Simple implementation of an <code>AutosuggestTerm</code>.
 * @author jbeard
 *
 */
class AutosuggestTermImpl implements AutosuggestTerm {
    
    private final String name;
    private final int count;
    
    /**
     * Constructs the autosuggest term, using given name and count
     * @param name name of term
     * @param count frequency count of term
     */
    public AutosuggestTermImpl(String name, int count) {
        this.name = name;
        this.count = count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("%s:%s", name, count);
    }
    
    /**
     * Gets the name of this autosuggest term - this is the search term itself
     * @return term name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the count of this autosuggest term within all the results.
     * @return frequency count of term
     */
    @Override
    public int getCount() {
        return count;
    }
}