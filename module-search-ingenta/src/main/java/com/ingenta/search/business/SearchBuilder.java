/*
 * SearchBuilder
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.ingenta.search.domain.InvalidSearchTermException;
import com.ingenta.search.domain.Operator;
import com.ingenta.search.domain.Search;
import com.ingenta.search.domain.SearchCondition;
import com.ingenta.search.domain.SearchExpression;
import com.ingenta.search.domain.SearchTerm;
import com.ingenta.search.domain.SearchUnit;
import com.ingenta.search.domain.SortField;
import com.ingenta.search.parse.ParseException;
import com.ingenta.search.parse.SearchExpressionParser;
import com.ingenta.util.StringUtils;


/**
 * This class has the responsibilty to accept a HandlerRequest and to return a Search object 
 * by parsing the request parameters in the request via the business method <code>buildSearch()</code>
 * 
 * It utilises the SearchExpressionParser to parse search terms that are to be entered by the user when 
 * building the <code>List<SearchTerm></code> list per <code>SearchCondition</code>.   
 * 
 * @author ccsrak
 */
public class SearchBuilder {

    private static final String PREFIX_VALUE_FIELD = "value";    
    private static final String PREFIX_OPTION_FIELD = "option";
    private static final String PREFIX_OPERATOR_FIELD = "operator";
    private static final String PARAM_REFINE_LEVEL = "refineLevel";

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_PAGESIZE = "pageSize";

    private static final String SORT_FIELD = "sortField";
    private static final String SORT_FIELD_DEFAULT = "default";
    private static final String SORT_ORDER_DESCENDING = "sortDescending"; 

    private static final String NEW_LINE = "\n";
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGESIZE = 20;
    private static final String SOLR_PUBLISHER_ID_KEY = "solrPublisherId";
    private static final String SOLR_EXCLUDED_PUBLISHER_ID_KEY = "solrExcludedPublisherIds";

    private static final Logger LOG = Logger.getLogger(SearchBuilder.class);

    /**
     * Builds a search from the parameters contained in the given request.
     * @param request The client request.
     * @return The populated search.
     * @throws InvalidSearchTermException If the request contains invalid/malformed 
     * search parameters.
     */
    public Search buildSearch(HttpServletRequest request) throws InvalidSearchTermException{
        LOG.debug("entering buildSearch");

        Search search = new Search();
        search = populateSearch(request, search);
        configureSorting(request, search);
        request.setAttribute("currentSearch", search);

        return search;
    }

    /**
     * Adds a SearchUnit constructed from the parameters contained in the given request
     * to the given Search.
     * @param request The client request.
     * @param currentSearch The user's current search.
     * @return The populated search.
     * @throws InvalidSearchTermException If the request contains invalid/malformed 
     * search parameters.
     */
    public Search refineSearch(HttpServletRequest request, Search currentSearch)throws InvalidSearchTermException{
        LOG.debug("entering refineSearch");
        Search search = populateSearch(request, currentSearch);
        configureSorting(request, search);

        return search;
    }

    /**
     * Configures the sorting options on the given Search from the parameters in
     * the given request. It will look for a sort field name and, if one is present
     * (other than the default sort order) it will also check if the search is to be
     * in descending or ascending order. It will then add an appropriate sort field 
     * to the search. If the sort field is the default or is null, the search is 
     * unchanged. 
     * @param request The client request.
     * @param search The search to have its order configured.
     */
    public void configureSorting(HttpServletRequest request, Search search) {
        LOG.debug("entering configureSorting");
        String sortFieldName = request.getParameter(SORT_FIELD);

        if (sortFieldName != null && !sortFieldName.equals(SORT_FIELD_DEFAULT)) {
            SortField sortField;
            String sortOrderDescending = request.getParameter(SORT_ORDER_DESCENDING);

            if (sortOrderDescending != null) {
                sortField = new SortField(new Boolean(sortOrderDescending), sortFieldName);
            } else{
                sortField = new SortField(sortFieldName);
            }

            LOG.debug(sortField);

            search.addSortField(sortField);
        }
    }

    /**
     * Populates the given search from the parameters contained in the given request.
     * Takes into account the refineLevel param that is set in the request, and truncates the
     * SearchUnit(s) in the search  if the refineLevel param indicates that the user has gone back
     * in the browser to a form that holds an earlier refine level.
     * @param request The client request.
     * @param currentSearch search to be populated.
     * @throws InvalidSearchTermException If the request contains invalid/malformed 
     * search parameters.
     */
    @SuppressWarnings("unchecked")
   private Search populateSearch(HttpServletRequest request, Search currentSearch) throws InvalidSearchTermException {
        Search search = currentSearch.getCopy();
        LOG.debug("entering populateSearch");
        readPagingInfo(request, search);
        List<SearchExpression> searchExpressions = getParameters(request);
        List<SearchUnit> searchUnits = search.getSearchUnits();

        String levelParam = request.getParameter(PARAM_REFINE_LEVEL);

        refineList(searchUnits, levelParam);

        Map<String,String> params = buildParamMap(request);      
        searchUnits.add(new SearchUnit(searchExpressions));
        search.setSearchUnits(searchUnits);
        search.setParamMap(params);
        /*publisher specific search*/
        Object publisherId = request.getAttribute(SOLR_PUBLISHER_ID_KEY);
        Object excludedPublisherIds = request.getAttribute(SOLR_EXCLUDED_PUBLISHER_ID_KEY);
        if (publisherId != null && !((String)publisherId).isEmpty()) {
           search.setPublisherId((String)publisherId);
           search.setPublisherSpecific(true);
        } else if (excludedPublisherIds instanceof List<?>) {
           search.setExcludedPublisherIds(((List<String>) excludedPublisherIds).isEmpty() ? null
               : (List<String>) excludedPublisherIds);
           search.setPublisherExcluded(!((List<String>) excludedPublisherIds).isEmpty());
        }
        return search;
    }

   private void refineList(List<SearchUnit> searchUnits, String levelParam) {
      if (levelParam != null) {      
            int refineLevel = Math.max(Integer.parseInt(levelParam), 1);
            if (searchUnits.size() > refineLevel) {
                for (int i = searchUnits.size() - refineLevel; i > 0; i--) {
                    searchUnits.remove(searchUnits.size() - 1);
                }
            }
        }
   }

    /**
     * Builds a list of SearchExpressions from the parameters contained in the 
     * given request. 
     * @param request The client request.
     * @return The search expressions corresponding to the parameters in the
     * request.
     * @throws InvalidSearchTermException If the request contains invalid/malformed 
     * search parameters.
     */
    private List<SearchExpression> getParameters(HttpServletRequest request) throws InvalidSearchTermException {
        List<SearchExpression> searchExpressions = parseParameters(request);
        if (searchExpressions.size() == 0) {
            throw new InvalidSearchTermException("All", "You have not entered any Search terms", "");
        }
        return searchExpressions;
    }

    private List<SearchExpression> parseParameters(HttpServletRequest request) throws InvalidSearchTermException {
        LOG.debug("entering parseParameters");
        List<SearchExpression> searchExpressions = new ArrayList<SearchExpression>();
        @SuppressWarnings("unchecked")
        List<String> parameterNames = Collections.list(request.getParameterNames());
        Collections.sort(parameterNames); 

        for (String parameterName : parameterNames) {
            parseParameter(request, searchExpressions, parameterName);
        }
        return searchExpressions;
    }

    /**
     * Adds any SearchExpressions in the given request to the supplied search
     * @param request the client request 
     * @param search the search to be populated
     * @throws InvalidSearchTermException If the request contains invalid/malformed 
     * search parameters.
     */
    public void readAdditionalParameters(HttpServletRequest request, Search search) throws InvalidSearchTermException {
       List<SearchExpression> searchExpressions = parseParameters(request);
       
       if (searchExpressions.size() > 0) {
          search.addSearchUnit(new SearchUnit(searchExpressions));
       }
    }
    
    /**
     * Populates the given search with the paging information contained
     * in the given request.
     * @param request The client request.
     * @param search The search to be populated.
     */
    public void readPagingInfo(HttpServletRequest request, Search search) {
        search.setCurrentPage(readPage(request));
        search.setPageSize(readPageSize(request));
    }

    /**
     * Parses the parameter with the given name from the given request
     * and adds the resulting SearchExpression to the given List.
     * @param request The client request.
     * @param searchExpressions The list to which the generated search expression
     * is to be added.
     * @param parameterName The name of the parameter to be parsed.
     * @throws InvalidSearchTermException If the request contains invalid/malformed 
     * search parameters.
     */
    private void parseParameter(HttpServletRequest request, 
            List<SearchExpression> searchExpressions, 
            String parameterName) throws InvalidSearchTermException {
        LOG.debug("Entering parseParameter() name: " + parameterName);

        if (parameterName.startsWith(PREFIX_VALUE_FIELD)) {        
            //get multi valued option 
            String[] value = request.getParameterValues(parameterName);                        

            LOG.debug("parameter name: [" + parameterName + ']');

            if (value != null && value.length > 0) {
                //ignore blank values
                value[0] = value[0].trim();
                if (value[0].length() > 0) {
                    value[0] = value[0].replaceAll(" +", " ");
                    LOG.debug("parameter value: [" + value[0] + ']');
                    String index = parameterName.replaceAll(PREFIX_VALUE_FIELD, "");
                    String optionName = PREFIX_OPTION_FIELD + index;
                    String fieldName = request.getParameter(optionName);
                    Operator operator = getOperator(request, index);

                    searchExpressions.add(buildSearchExpression(value, fieldName, operator));

                }
            }
        }
    }

    /**
     * Builds a SearchExpression by parsing the given value and combining the result with
     * the given field name and operator.
     * @param value The value containing the search terms.
     * @param fieldName The name of the field to which the search terms apply.
     * @param operator The operator (eg 'EQUALS' or 'AND') for the search expression.
     * @return The completed search expression.
     * @throws InvalidSearchTermException  If the given value contains invalid/malformed 
     * search parameters.
     */
    private SearchExpression buildSearchExpression(String[] value, String fieldName, Operator operator) 
          throws InvalidSearchTermException{
        LOG.debug("entering buildSearchExpression");
        String valueStr = buildSearchString(value);
        try {
            LOG.debug("parameter value(s): " + valueStr);
            SearchExpressionParser searchExpressionParser = new SearchExpressionParser(valueStr + NEW_LINE);
            List<SearchTerm> searchTerms = searchExpressionParser.parseSearchTerms();
            
            for (Iterator<SearchTerm> iterator = searchTerms.iterator(); iterator.hasNext();) {
               SearchTerm searchTerm = iterator.next();
               if (!isValidExpression(searchTerm.getAsString())) {
                  iterator.remove();
               }
            }

            SearchCondition searchCondition = new SearchCondition(fieldName, searchTerms);
            SearchExpression searchExpression = new SearchExpression(operator, searchCondition);

            return searchExpression;
        } catch (ParseException e) {
            LOG.warn("error parsing search terms: " + valueStr);
            throw new InvalidSearchTermException(fieldName, e.getMessage(), valueStr);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("char encoding not supported: " + valueStr);
            throw new InvalidSearchTermException(fieldName, e.getMessage(), valueStr);
        }
    }

   private String buildSearchString(String[] value) throws InvalidSearchTermException {
      StringBuilder searchString = new StringBuilder();
      for (String v : value) {
          if (searchString.length() > 0) {
              searchString.append(" OR ");
          }
          searchString.append(removeApostrophes(v));
      }
      String valueStr = searchString.toString();
      
      if (valueStr.indexOf("\"")>=0) {
         valueStr = removeDoubledCharacters(valueStr, "\"");
      } else if (valueStr.indexOf("'")>=0) {
         valueStr = removeDoubledCharacters(valueStr, "'");
      }
      
      return valueStr;
   }

   private String removeDoubledCharacters(String valueStr, String quote) throws InvalidSearchTermException {
      String doubledQuote = quote + quote;
      if (valueStr.indexOf(doubledQuote)>=0) {
         valueStr = valueStr.replaceAll(doubledQuote, "");
         if (valueStr.isEmpty()) {
            throw new InvalidSearchTermException("Search", "You have entered invalid search expresion", 
                  valueStr);
         }
      }
      return handleSpecialCharacter(quote, valueStr);
   }

   private String removeApostrophes(String valueStr) {
      if (valueStr.indexOf("'") > 0) {
          //only remove it if its not a phrase search although the below is not
          //necessary since phrase search should consist of double quotations mark. 
          if (!valueStr.startsWith("'")) {
              LOG.debug("Apostrophe found - removing from value:" + valueStr);
              valueStr = valueStr.replaceAll("'s", "");
              valueStr = valueStr.replaceAll("'", ""); 
          }
      }
      return valueStr;
   }

    /**
     * Reads the operator of the given index from the given request.
     * @param request The client request.
     * @param index The index of the required operator.
     * @return The Operator or <code>null</code> if there wasn't one.
     */
    private Operator getOperator(HttpServletRequest request, String index) {
        LOG.debug("entering getOperator: " + index);
        Operator operator = null;
        String operatorValue = request.getParameter(PREFIX_OPERATOR_FIELD + index);
        LOG.debug("operator value: " + operatorValue);

        if (operatorValue!= null) {
            operator = Operator.valueOf(operatorValue);
        }

        return operator;
    }

    /**
     * Reads the 'page' parameter from the given request.
     * @param request The client request.
     * @return The read parameter or the default value if it is unreadable.
     */
    private int readPage(HttpServletRequest request) {
        int page = DEFAULT_PAGE;

        try {
            page = Integer.parseInt(request.getParameter(PARAM_PAGE));
        } catch (NumberFormatException e) {
            LOG.info("Unable to read page from request - using default value");
        }

        return page;
    }

    /**
     * Reads the 'pageSize' parameter from the given request.
     * @param request The client request.
     * @return The read parameter or the default value if it is unreadable.
     */
    private int readPageSize(HttpServletRequest request) {
        int pageSize = DEFAULT_PAGESIZE;

        try {
            pageSize = Integer.parseInt(request.getParameter(PARAM_PAGESIZE));
        } catch (NumberFormatException e) {
            LOG.info("Unable to read page size from request - using default value");
        }

        return pageSize;
    }

    /**
     * Utility methods for putting all request parameter and value in map
     * which will be used by SavedSearch when used with CombinedSearch
     * @param request
     *            The client request.
     * @return Map
     *          Parameter/value map
     */
    private Map<String,String> buildParamMap(HttpServletRequest request)
    {      
        Map<String,String> paramMap = new HashMap<String,String>();

        for (Iterator<String> iter = request.getParameterMap().keySet().iterator(); iter.hasNext();) {
            String param = iter.next();
            String value = StringUtils.toNonEmptyTrimmedString(request.getParameter(param));
            if (value == null)
                value = "";
            paramMap.put(param, value);            
        }
        return paramMap;
    }
    
    private boolean isValidExpression(String subQueryStr) {
       String subQ = subQueryStr.replaceAll("\\s", "")
                          .replaceAll("[\\\"\\~\\*?:\\+\\-!() {}\\[\\]]*", "");      
       if (subQ.equals("")) return false;
      return true;
   }
   /**
    * This method designed to handle quote(') and double quote(") in search text.
    * @param character
    * @param inputStr
    * @return the modified string
    */
   protected String handleSpecialCharacter(String character, String inputStr) {
      int count = StringUtils.countOccurences(inputStr, character);
      if (!((count % 2) == 0)) {
         if ((inputStr.length() == inputStr.lastIndexOf(character)+1)) {
            inputStr = inputStr.substring(0,inputStr.lastIndexOf(character));
         } else if (count == 1 && inputStr.indexOf(character) == 0) {
            inputStr = inputStr.substring(1);
         } else {
            inputStr = inputStr+character;
         }
      }
      return inputStr;
   }
}