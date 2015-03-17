/*
 * AutosuggestAction
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.web.autosuggest;

import static com.pub2web.search.web.action.ActionOutcomes.ERROR;
import static com.pub2web.search.web.action.ActionOutcomes.SUCCESS;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.pub2web.search.api.SearchService;
import com.pub2web.search.api.autosuggest.AutosuggestResult;
import com.pub2web.search.api.autosuggest.AutosuggestTerm;

/**
 * Action for providing auto-suggestions for a given search prefix.
 * This is used to suggest valid search results based on what the user has typed in so far.
 * 
 * The prefix (term) is set via <code>setTerm()</code>.
 * The limit on results is set via <code>setLimit()</code> (defaults to <code>DEFAULT_LIMIT</code>).
 * The search service (to perform autosuggest) is set via <code>setSearchService()</code>.
 * 
 * Once the action has been executed, the results are provided in a stream obtained
 * via the <code>getInputStream()</code> method.
 * The results are in the format of a JSON array (bizarrely this is what the UI requires),
 * e.g. ["result1", "result2", "result3"]
 * @author jbeard
 */
public class AutosuggestAction {

    private static final Logger LOG = Logger.getLogger(AutosuggestAction.class);

    /**
     * Default number of results returned when not specified via <code>setNumber()</code>
     */
    public static final int DEFAULT_LIMIT = 10;

    // Inputs
    private String term;
    private int limit = DEFAULT_LIMIT;

    // Outputs
    private InputStream inputStream;

    private boolean forceLowerCase = true;
    private SearchService searchService;

    /**
     * Sets the search term that will be used as the prefix for auto-suggestions
     * @param term term to use as the prefix
     */
    public void setTerm(String term) {
        LOG.log(Level.DEBUG, MessageFormat.format("setTerm({0})", term));
        this.term = term;
    }

    /**
     * @return the term, potentially forced into lower case
     */
    public String getTerm() {
        return term != null && forceLowerCase ? term.toLowerCase() : term;
    }
    
    /**
     * Sets the limit on the number of results to return
     * @param limit max number of results to return
     */
    public void setLimit(int limit) {
        LOG.log(Level.DEBUG, MessageFormat.format("setLimit({0})", limit));
        this.limit = limit;
    }

    /**
     * Sets the search service to use to do the query
     * @param searchService search service to use to do 'more like this' query
     */
    public void setSearchService(SearchService searchService) {
        LOG.log(Level.DEBUG, MessageFormat.format("setSearchService({0})", searchService));
        this.searchService = searchService;
    }

    /**
     * @param forceLowerCase indicates whether autosuggest queries should be converted to lower case before
     *   passing them to the back end - this is true by default as 'autosuggest' has a type of 'lowercase' in 
     *   the solr schema.xml of all instances
     */
    public void setForceLowerCase(boolean forceLowerCase) {
       this.forceLowerCase = forceLowerCase;
    }
    
    /**
     * Gets the stream that contains the autosuggets results
     * @return stream of autosuggest results in appropriate format
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Executes the action which performs the autosuggestion.
     * After this action is called, get the results via the <code>getInputStream()</code> method.
     * @return <code>ActionOutcomes.SUCCESS</code> on success,
     * or <code>ActionOutcomes.ERROR</code> on error.
     */
    public String execute() {
        LOG.log(Level.DEBUG, "execute()");

        try {
            if (term == null) {
                LOG.error("No term specified for Autosuggest");
                return ERROR;
            }
            AutosuggestResult result = searchService.autosuggest(getTerm(), limit);
            Collection<AutosuggestTerm> terms = result.getTerms();
            TermsWriter writer = new TermsWriter(terms);
            inputStream = writer.toStream();
        }
        catch (RemoteException ex) {
            return ERROR;
        }

        return SUCCESS;
    }
}
