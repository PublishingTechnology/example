/*
 * CombinedSearchHandler
 *
 * Copyright 2010 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.ingenta.search.business.SearchDelegate;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.store.SearchResult;
import com.ingenta.search.store.SearchResults;
import com.ingenta.search.domain.CombinedSearch;
import com.ingenta.servlet.ForwardTarget;
import com.ingenta.servlet.HandlerException;
import com.ingenta.servlet.HandlerRequest;
import com.ingenta.servlet.HandlerResponse;
import com.ingenta.servlet.RedirectTarget;
import com.ingenta.servlet.Target;
import com.ingenta.util.StringUtils;


/**
 * This class is used for Combined Search functionality. This functionality was initially developed for OECD
 * and later move to search component as additional feature.
 * 
 * As of November 2012 it is not used in any pub2webs.
 * 
 * @author Sachin Gharat
 */
public class CombinedSearchHandler extends RunSavedSearchHandler {
    private static final Logger LOG = Logger.getLogger(CombinedSearchHandler.class);

    /**Array of SavedSearch objects for processing the user friendly query explanation string*/
    private static final String COMBINED_SEARCH_ARRAY = "searches";

    /**Operator specified in Combined search query used in user friendly query explanation string*/
    private static final String COMBINED_OPERATORS_ARRAY = "operators";

    /**
     * {@inheritDoc}
     */
    @Override
    public Target handle(HandlerRequest request, HandlerResponse response)   throws HandlerException {

        //If combined search run single query by clicking on the query link,call RunSavedSearchHandler.handle()
        if (StringUtils.isNonEmpty(request.getParameter("searchIndex"))) {
            super.handle(request, response);
            SearchResults searchResults = (SearchResults)request.getAttribute(ATTR_RESULTS);
            return processTargetFromResults(searchResults,request);
        }

        // If combined search is used in typical format of 1 or 2 , 2 and 3 or 5      
        String page = request.getParameter("page");
        int pageNumber=0;
        if (StringUtils.isNonEmpty(page)) {
            pageNumber = (Integer.parseInt(page)- 1);
        }
        CombinedSearch combinedSearch = createCombinedSearch(request);

        SearchDelegate delegate = getSearchDelegate();

        SavedSearch[] savedSearches = (SavedSearch[])combinedSearch.getData("CombinedSearches");
        String[] operators = (String[])combinedSearch.getData("CombinedOperators");        

        SearchResults results = delegate.doSearch(savedSearches,operators,pageNumber);
        request.setAttribute(ATTR_RESULTS, results);
        request.setAttribute(COMBINED_SEARCH_ARRAY,  Arrays.asList(savedSearches));
        request.setAttribute(COMBINED_OPERATORS_ARRAY, Arrays.asList(operators));

        return processTargetFromResults(results,request);

    }

    /**
     * Utility methods return proper target depending upon the SearchResults
     * @param results SearchResults
     * @param request HandlerRequest
     * @return Target
     */
    private Target processTargetFromResults(SearchResults results,HandlerRequest request) {
        if (results.getTotalCount() == 0) {
            return new ForwardTarget("/search/advanced_search.jsp");
        }
        else if ((results.getTotalCount() == 1)
                && (request.getParameter("noRedirect") == null)) {

            SearchResult searchResult = results.getResults().get(0);

            if (!((List<?>) searchResult.getProperty("resourceType")).equals("Summary")) {
                int contentPos = searchResult.getIdentifier().indexOf("content");
                return (RedirectTarget.create("/"+ searchResult.getIdentifier().substring(contentPos)));
            }

        }
        return getSuccessTarget() ;
    }


    /**
     * 
     * @param request
     * @return
     */
    private CombinedSearch createCombinedSearch(HandlerRequest request) {
        @SuppressWarnings("unchecked")
        List<SavedSearch> history = (List<SavedSearch>)request.getSession().getAttribute("searchHistory");
        if (history == null) {
            return null;
        }
        int number = history.size();

        String value = request.getParameter("combinedSearch");
        StringBuilder error = new StringBuilder();
        if (value.trim().equals("")) {
            error.append("You entered nothing to search for");
        }

        Reader r = new StringReader(value);
        StreamTokenizer st = new StreamTokenizer(r);
        st.parseNumbers();

        CombinedSearch combSearch = new CombinedSearch();

        List<String> result = new ArrayList<String>();

        SavedSearch[] searches = new SavedSearch[10];
        String[] combinedSearchOperators = new String[9];

        int i = 1;
        int j = 1;
        boolean isNumber = false;
        boolean isWord = false;
        try {
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_NUMBER) {

                    if (i > 1) {
                        if (! isWord) {
                            error.append("Found a number that is not preceeded by a valid boolean " +
                            		"operator (AND/OR/NOT). ");
                        }
                    }
                    Double nVal = st.nval;                    

                    if ( (nVal > number) || (nVal < 1) ) {
                        error.append("Found invalid search history number " + nVal + ". Only numbers from 1 to " + 
                              number + " are valid. ");
                    }
                    else {
                        int in = nVal.intValue();

                        searches[i-1] =  history.get(in-1); 
                    }
                    result.add( (new Long (nVal.longValue())).toString());
                    isNumber = true;
                    i++;
                }
                if (st.ttype == StreamTokenizer.TT_WORD) {
                    String word = st.sval.toUpperCase();
                    if (i == 1) {
                        error.append("The first term must be a number representing a search in your search history. ");                                              
                    }
                    else {
                        if (! isNumber) {
                            error.append("Found a possible boolean operator not preceded by a number. ");
                        }
                    }
                    if (! (word.equals("OR") || word.equals("AND") || word.equals("NOT")) ) {
                        error.append("Encountered invalid term " + st.sval + 
                              ". Expected one of boolean operators OR, AND, or NOT. ");
                    }
                    else {
                        // opMap.put(j-1, word);
                        combinedSearchOperators[j-1] = word;
                    }                    
                    result.add(st.sval);
                    isWord = true;
                    j++;
                }            

            }
        }
        catch (IOException e) {
           // This shouldn't happen
           LOG.error(e);
        }
        if ("".equals(error.toString()) && isNumber) {
            combSearch.setData("CombinedSearches", searches);
            combSearch.setData("CombinedOperators", combinedSearchOperators);

            return combSearch;
        }
        return null;
    }


}
