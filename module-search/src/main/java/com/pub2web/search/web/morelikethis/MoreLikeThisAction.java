/*
 * MoreLikeThisAction
 * 
 * Copyright 2011 Publishing Technology PLC.
 */
package com.pub2web.search.web.morelikethis;

import static com.pub2web.search.web.action.ActionOutcomes.ERROR;
import static com.pub2web.search.web.action.ActionOutcomes.SUCCESS;

import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.pub2web.search.api.SearchService;
import com.pub2web.search.api.morelikethis.MoreLikeThisResult;

/**
 * Action for performing a 'more like this' search query.
 * Essentially it takes the web ID of an item and finds the IDs of 'similar' items.
 * The similarity is judged according to specified fields.
 * The results can also be filtered via a set of restrictions (e.g. only books)
 * 
 * The web id of the original item is specified via <code>setWebId()</code>.
 * The fields on which to evaluate similarity are specified via <code>setFields()</code>.
 * Any restrictions on results can be specified via <code>setRestrictions()</code>.
 * The maximum number of results to return can be specified via <code>setNumber()</code>.
 * 
 * After <code>execute()</code> has been called, a list of the result ids can be obtained
 * via <code>getResultIds()</code>.
 * 
 * @author jbeard
 */
public class MoreLikeThisAction {

    private static final Logger LOG = Logger.getLogger(MoreLikeThisAction.class);

    /**
     * Default number of results returned when not specified via <code>setNumber()</code>
     */
    public static final int DEFAULT_NUMBER = 6;

    // Inputs
    private String webid;
    private String fields;
    private String restrictions;
    private int number = DEFAULT_NUMBER;

    // Outputs
    private List<String> resultIds;

    private SearchService searchService;

    /**
     * Sets the web id of the item for which we should find 'similar' items
     * @param webid web id of original item
     */
    public void setWebid(String webid) {
        LOG.log(Level.DEBUG, MessageFormat.format("setWebId({0})", webid));
        this.webid = webid;
    }

    /**
     * Sets the search fields which will be used to judge similarity.
     * @param fields string containing comma-separated list of fields
     */
    public void setFields(String fields) {
        LOG.log(Level.DEBUG, MessageFormat.format("setFields({0})", fields));
        this.fields = fields;
    }

    /**
     * Sets the restrictions that will be applied to the results
     * @param restrictions restrictions in query format
     */
    public void setRestrictions(String restrictions) {
        LOG.log(Level.DEBUG, MessageFormat.format("setRestrictions({0})", restrictions));
        this.restrictions = restrictions;
    }

    /**
     * Sets the number of results to return (as a maximum)
     * @param number max number of results to return
     */
    public void setNumber(int number) {
        LOG.log(Level.DEBUG, MessageFormat.format("setNumber({0})", number));
        this.number = number;
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
     * Gets the list of result IDs (IDs of the similar items)
     * @return list of result IDs
     */
    public List<String> getResultIds() {
        LOG.log(Level.DEBUG, MessageFormat.format("getResultIds() returns {0}", resultIds));
        return resultIds;
    }

    /**
     * Executes the action which performs the 'more like this' search.
     * After this method is called, the results will be available via <code>getResultIds()</code>
     * @return <code>ActionOutcomes.SUCCESS</code> on success,
     * or <code>ActionOutcomes.ERROR</code> on error.
     */
    public String execute() {
        LOG.log(Level.DEBUG, "execute()");

        try {
            if (webid == null) {
                LOG.error("No web id specified for which to do More Like This check");
                return ERROR;
            }
            if (fields == null) {
                LOG.error("No fields specified for More Like This similarity checking");
                return ERROR;
            }
            MoreLikeThisResult result = searchService.moreLikeThis(webid, fields, restrictions, number);
            resultIds = result.getIds();
        }
        catch (RemoteException ex) {
            return ERROR;
        }

        return SUCCESS;
    }
}