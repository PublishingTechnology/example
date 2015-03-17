package com.ingenta.search.store.solr;

import com.ingenta.search.domain.*;
import com.ingenta.search.savedsearch.SavedSearch;
import com.ingenta.search.store.Query;
import com.ingenta.search.store.QueryGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * A class to generate a solr query, passed to public variable solrQueryGeneratorString
 * 
 * @author John West
 */
public class SolrQueryGenerator implements QueryGenerator{

    private static final long serialVersionUID = 5264927571917168436L;
    public static final String SORT_DESCENDING = "desc";
    public static final String SORT_ASCENDING = "asc";
    public static final String SORT_SEPARATOR = ";";
    public static final String SPACE = " ";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";

    private static final String FIELD_LAST_RUN = "lastRunOn";

    private String[] fields;
    private Set<String> highLights = new HashSet<String>();

    /** Year range 'from' specified on the form **/
    private String yearFrom = "";

    /** Year range 'to' specified on the form **/
    private String yearTo = "";

    /** Date range 'from' specified on the form **/
    private String dateFrom = "";

    /** Date range 'to' specified on the form **/
    private String dateTo = "";


    private static Logger LOG = Logger.getLogger(SolrQueryGenerator.class);

    /* (non-Javadoc)
     * @see com.ingenta.search.store.QueryGenerator#generateQuery(com.ingenta.search.savedsearch.SavedSearch)
     */
    @Override
    public Query generateQuery(SavedSearch savedSearch){
        return generateQuery(getToDateSearch(savedSearch));
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.QueryGenerator#generateQuery(com.ingenta.search.domain.Search)
     */
    @Override
    public Query generateQuery(Search search){
        LOG.debug("generateQuery");
        Query query = (Query) new SolrQuery();
        String queryStr = getQueryString(search);
        LOG.debug(queryStr);
        query.setQuery(queryStr);
        query.setFields(fields);
        query.setHighlights(highLights.toArray(new String[]{}));
        query.setHighlighting(search.isHighlighting());
        query.setRows(search.getPageSize());
        query.setStart((search.getCurrentPage()-1) * search.getPageSize());

        LOG.debug( "solr query is " + query.getQuery());
        return query;
    }

    /**
     * Gets the 'last run on' date from the given saved search, if it is present,
     * and appends it as a <code>SearchUnit</code> to a copy of the <code>Search</code>
     * contained in the saved search. If no 'last run on' date is present, it simply
     * returns an unmodified copy of the <code>Search</code> contained in the saved
     * search.
     * @param savedSearch The saved search containing the <code>Search</code> object
     * to be modified.
     * @return The Search ready to be run.
     */
    protected Search getToDateSearch(SavedSearch savedSearch){
        Search toDateSearch = savedSearch.getSearch().getCopy();
        Date lastRunOn = savedSearch.getLastRunOn();

        if(lastRunOn != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            String dateString = dateFormat.format(lastRunOn);
            SearchTerm dateValue = new ValueTerm(dateString);
            List<SearchTerm> terms = new ArrayList<SearchTerm>();
            terms.add(dateValue);
            SearchCondition searchCondition = new SearchCondition(FIELD_LAST_RUN, terms);
            SearchExpression searchExpression = new SearchExpression(searchCondition);
            List<SearchExpression> expressions = new ArrayList<SearchExpression>();
            expressions.add(searchExpression);
            SearchUnit searchUnit = new SearchUnit(expressions);

            toDateSearch.addSearchUnit(searchUnit);
        }

        return toDateSearch;
    }

    /**
     * Gets the SolR-specific String representation of the search parameters
     * contained in the given Search.
     * @param search Search object for conversion to query string
     * @return String representation of query object
     */
    protected String getQueryString(Search search){
        StringBuilder buff = new StringBuilder();
        List<SearchUnit> searchUnitList = search.getSearchUnits();

        for (Iterator<SearchUnit> i = searchUnitList.iterator(); i.hasNext();){
            buff.append("( ");
            buff.append(getQueryUnit(i.next()));
            appendFromToYear(buff);
            appendFromToDate(buff);
            buff.append(" )");
            if (i.hasNext())            // TODO RK hardcoded for refined searches for now.
                buff.append(" AND ");    // Needs to be populated in the SearchUnit's operator field.
        }
        if (search.getSubscribedIds() != null){
            buff.insert(0, "+");
            List<String> subscribedIds = search.getSubscribedIds();
            if (subscribedIds.size() > 0){
                buff.append(" +(acs_parents:(");
                for (Iterator<String> ids = subscribedIds.iterator(); ids
                .hasNext();) {
                    String subscribedId = ids.next();
                    buff.append(" " + subscribedId + " ");
                }
                buff.append(" ))");
            }
        }
        /*this code is exclusive to publisher portal*/
        if (search.isPublisherSpecific()) {
            /*add publisher id for publisher specific search*/
            buff.append(" +(pub_publisherId:");
            buff.append(search.getPublisherId());
            buff.append(" )");
            LOG.log(Level.DEBUG, MessageFormat.format("Solr Query after " +
                    "adding publisher specific search clause {0}", buff.toString()));
        } else if (search.isPublisherExcluded()) {
            /*this exclude a publisher's id(s) to exclude content from search results.
             * an example search query will look like as below.
             * +( (dcterms_title:test^10.0 dcterms_description:test pub_keyword:test^0.5
             * pub_author:test fulltext:test prism_issn:test prism_eIssn:test
             * prism_doi:test pub_isbn:test))
             * -(pub_publisherId:content/publisher/somejunk
             * OR pub_publisherId:content/publisher/ocean
             * OR pub_publisherId:content/publisher/maney);
             * score desc,contentType asc
             * */
            buff.append(" -(pub_publisherId:");
            List<String> excludedPublisherIds = search.getExcludedPublisherIds();
            if (excludedPublisherIds != null) {
                int size = excludedPublisherIds.size();
                for (int i = 0; i < size; i++) {
                    buff.append(excludedPublisherIds.get(i));
                    if (i+1 != size) {
                        buff.append(" OR ");
                    }
                }
            }
            buff.append(" )");
            LOG.log(Level.DEBUG, MessageFormat.format("Solr Query after " +
                    "adding excluded publisher clause {0}", buff.toString()));
        }
        /*publisher portal code finish*/

        if(search.getSortFields() != null){//Sort fields are set to null for combined search
            appendSortOrder(search, buff);
        }

        return buff.toString();
    }

    /**
     * Appends the sort order String to the given buffer, if one has been
     * set in the search. Otherwise does nothing.
     * @param search The search potentially containing a sort-order.
     * @param buff The buffer to be appended.
     */
    protected void appendSortOrder(Search search, StringBuilder buff) {
        List<SortField> sortFields = search.getSortFields();
        if (sortFields.isEmpty()) {
            sortFields = getSortFields();
        }
        if(sortFields.size() > 0){
            buff.append(SORT_SEPARATOR);
            boolean firstIteration = true;

            for(SortField sortField : sortFields){
                if(!firstIteration){
                    buff.append(",");
                }

                buff.append(sortField.getFieldName());
                buff.append(SPACE);
                buff.append(getOrder(sortField));
                firstIteration = false;
            }
        }
    }

    /**
     * Gets the correct String representation for the order (ascending or descending)
     * contained in the given sort field.
     * @param sortField The field from which the order is to be got.
     * @return The order expressed as a String.
     */
    protected Object getOrder(SortField sortField) {
        if(sortField.isDescending()){
            return SORT_DESCENDING;
        }

        return SORT_ASCENDING;
    }

    /**
     * Primarily to get Book content ranked higher than chapters when searching on ISBN.
     * Kludgy but pretty harmless as the second sort only happens when the scores are exactly equal.    *
     * @return
     */
    protected List<SortField> getSortFields(){
        List<SortField> result = new ArrayList<SortField>();
        SortField scoreSortField = new SortField(true, "score");
        result.add(scoreSortField);
        return result;
    }

    /**
     * Checks if a year range was included in the search and, if so,
     * appends the appropriate values to the query String.
     * @param buff The StringBuilder to be appended.
     */
    protected void appendFromToYear(StringBuilder buff) {
        if (!(this.yearFrom.equals("") && this.yearTo.equals(""))){
            String fr = "0001";
            if (!this.yearFrom.equals("")){
                fr = this.yearFrom;
            }
            String to = "9999";
            if (!this.yearTo.equals("")){
                to = this.yearTo;
            }
            and(buff).append("prism_publicationDate:["+ fr + "0101 TO " + to + "1231] ");
        }
    }


    /**
     * Checks if a date range was included in the search and, if so,
     * appends the appropriate values to the query String.
     * @param buff The StringBuilder to be appended.
     */
    protected void appendFromToDate(StringBuilder buff) {
        if (!(this.dateFrom.equals("") && this.dateTo.equals(""))){
            String from = "";
            if (!this.dateFrom.equals("")){
                from = this.dateFrom;
                from = from.replaceAll("-", "");
            }
            String to = "";
            if (!this.dateTo.equals("")){
                to = this.dateTo;
                to = to.replaceAll("-", "");
            }
            if (!from.equals("") && !to.equals("") && (buff.indexOf(":") == -1)) {
               and(buff).append("+prism_publicationDate:["+ from + " TO " + to + "] ");
            }
            else if (!from.equals("") && !to.equals("")) {
               and(buff).append("prism_publicationDate:["+ from + " TO " + to + "] ");
            }
        }
    }

    private StringBuilder and(StringBuilder buff) {
        if (buff.indexOf(":") != -1) {
           return buff.insert(0, "( ").append(" ) AND ");
        }
        return buff;
    }

   /**
     * @param searchUnit SearchUnit object for conversion to query string
     * @return           Query string representation of searchUnit object
     */
    protected String getQueryUnit(SearchUnit searchUnit){
        StringBuilder buff = new StringBuilder();
        List<SearchExpression> searchExpressionList = searchUnit.getSearchExpressions();

        /* In the following loop we group together continuous
         * runs of SearchExpressions with AND and NOT operators between
         * them. This means that in the final query, these runs of SearchExpressions
         * will appear within a pair of parentheses. This is needed because Solr
         * does not appear to have any operator precedence amongst the AND/OR/NOT
         * operators, and if we don't group these expressions together then the resulting
         * query has undefined behaviour(as far as search results are concerned)
         * if there is another SearchExpression with an OR operator.
         */
        String subQueryStr = "";
        boolean gotSubQuery = false;
        int j = 1;
        boolean insideParentheses = false;
        for (SearchExpression expression : searchExpressionList) {
            gotSubQuery = false;
            subQueryStr = getQueryExpression(expression) + " ";
            gotSubQuery = StringUtils.isNotBlank(subQueryStr);
            if (gotSubQuery){
                if (buff.length() > 0) {
                    if(searchExpressionList.get(j-1).getOperator() ==null){
                        buff.append(" " +  Operator.AND + " ");
                    }
                    else{
                        buff.append(" " +  searchExpressionList.get(j-1).getOperator().name() + " ");
                    }
                }
                if (j != searchExpressionList.size()) {
                    Operator op = searchExpressionList.get(j).getOperator();
                    if(op == null){
                        op = Operator.AND;
                    }
                    if (op.equals(Operator.AND) || op.equals(Operator.NOT) || op.equals(Operator.OR)){

                        if (!insideParentheses) {
                            buff.append("(");
                            insideParentheses = true;
                        }
                        buff.append(subQueryStr);
                    }
                    else{
                        buff.append(subQueryStr);
                        if (insideParentheses){
                            buff.append(")");
                            insideParentheses = false;
                        }
                    }
                }

            }

            j++;
        }

        if (gotSubQuery){
            buff.append(subQueryStr);
        }
        if (insideParentheses){
            buff.append(")");
        }
        /*        if (searchUnit.getOperator() == Operator.AND)  {
            buff.insert(0,"(+");
            buff.append(")");
        }
        if (searchUnit.getOperator() == Operator.NOT)  {
            buff.insert(0,"(-");
            buff.append(")");
        }
         */
        return buff.toString();
    }

    /**
     * @param searchExpression SearchExpression object for conversion to query string
     * @return                 Query string representation of searchExpression object
     */
    protected String getQueryExpression(SearchExpression searchExpression){
        String query = getQueryCondition(searchExpression.getSearchCondition());
        if (StringUtils.isBlank(query)) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        buff.append("(");
        buff.append(query);
        buff.append(")");
        
        return buff.toString();
    }

    /**
     * @param searchCondition SearchCondition object for conversion to query string
     * @return                Query string representation of searchCondition object
     */
    protected String getQueryCondition(SearchCondition searchCondition){
        StringBuilder buff = new StringBuilder();

        if (searchCondition.getField().equals("titleabs")){
            buff.append(getMultiFieldQuery(
                    new String[]{"dcterms_title", "dcterms_description", "pub_keyword", "pub_author", "prism_issn", "prism_eIssn","prism_doi", "pub_isbn"},
                    searchCondition.getTermsAsString()
            ));
            highLights.add("dcterms_title");
            highLights.add("dcterms_description");
            highLights.add("pub_keyword");

        } else if (searchCondition.getField().equals("title")){
            buff.append(getMultiFieldQuery(new String[]{"dcterms_title"}, searchCondition.getTermsAsString()));
            highLights.add("dcterms_title");
        } else if (searchCondition.getField().equals("journalbooktitle")){
            buff.append(getMultiFieldQuery(new String[]{"pub_serialTitle"}, searchCondition.getTermsAsString()));
            highLights.add("pub_serialTitle");

        } else if (searchCondition.getField().equals("author")){
            buff.append(getMultiFieldQuery(new String[]{"pub_author"}, searchCondition.getTermsAsString()));
            highLights.add("pub_author");

        } else if (searchCondition.getField().equals("subjectarea")){
            buff.append("dcterms_subject:subject/" + searchCondition.getTermsAsString().trim());
            highLights.add("dcterms_subject");

        } else if (searchCondition.getField().equals("contenttype")){
            buff.append(getMultiFieldQuery(new String[]{"contentType"}, searchCondition.getTermsAsString()));

        } else if (searchCondition.getField().equals("all")){
            buff.append(getMultiFieldQuery(
                    new String[]{"dcterms_title", "dcterms_description", "pub_keyword", "pub_author", "prism_issn", "prism_eIssn","prism_doi", "pub_isbn"}
                    , searchCondition.getTermsAsString())
            );
            highLights.add("dcterms_title");
            //highLights.add("subTitle");
            highLights.add("pub_keyword");
            highLights.add("dcterms_description");

        } else if (searchCondition.getField().equals("issnisbndoi")){
            buff.append(getMultiFieldQuery(new String[]{"prism_issn", "prism_eIssn","prism_doi", "pub_isbn"}, searchCondition.getTermsAsString()));
            highLights.add("prism_issn");
            highLights.add("prism_eIssn");
            highLights.add("prism_doi");
            highLights.add("pub_isbn");

        } else if (searchCondition.getField().equals("fulltext")){
            buff.append(getMultiFieldQuery(
                    new String[]{"dcterms_title", "dcterms_description", "pub_keyword", "pub_author","fulltext"}
                    , searchCondition.getTermsAsString())
            );

            buff.append(" ");

            buff.append(getMultiFieldQuery(
                    new String[]{"prism_issn", "prism_eIssn","prism_doi", "pub_isbn"}
                    , searchCondition.getTermsAsString().replaceAll(" ", ""))
            );
            highLights.add("dcterms_title");
            //highLights.add("subTitle");
            highLights.add("pub_keyword");
            highLights.add("pub_author");
            highLights.add("dcterms_description");

        } else if (searchCondition.getField().equals("year_from")){
            setYearFrom(searchCondition.getTermsAsString());

        } else if (searchCondition.getField().equals("year_to")){
            setYearTo(searchCondition.getTermsAsString());

        } else if (searchCondition.getField().equals("date_from")){
            setDateFrom(searchCondition.getTermsAsString());

        } else if (searchCondition.getField().equals("date_to")){
            setDateTo(searchCondition.getTermsAsString());

        } else if(FIELD_LAST_RUN.equals(searchCondition.getField())){
            appendSearchSince(buff, searchCondition.getTermsAsString());
        } else if(searchCondition.getField().endsWith("_facet")){
            // Don't do a multifield query on a facet

            // Remove any leading spaces on the search term
            String searchTerms = searchCondition.getTermsAsString();
            if (searchTerms.startsWith(" ")) {
                searchTerms = searchTerms.replaceFirst(" ", "");
            }
            buff.append(searchCondition.getField() + ":\"" + searchTerms + "\"");
        } else {
            buff.append(getMultiFieldQuery(new String[]{searchCondition.getField()}, searchCondition.getTermsAsString()));
            highLights.add(searchCondition.getField());
        }

        return buff.toString();
    }

    /**
     * @param buff the StringBuilder to append to 
     * @param termsString the term to append
     */
    protected void appendSearchSince(StringBuilder buff, String termsString) {
        buff.append("loaded:[");
        buff.append(termsString.trim());
        buff.append(" TO *]");
    }

    /**
     * Converts the given SearchTermGroup into the Solr equivalent String representation.
     * @param searchTermGroup SearchTermGroup object for conversion to query string.
     * @return Query string representation of searchTermGroup object.
     */
    protected String getQueryString(SearchTermGroup searchTermGroup){
        StringBuilder buff = new StringBuilder();
        List<SearchTerm> searchTerms = searchTermGroup.getSearchTerms();
        for (Iterator<SearchTerm> i = searchTerms.iterator(); i.hasNext();) {
            buff.append(getQueryString(i.next()));
            if (i.hasNext()){
                buff.append(SPACE);
            }
        }
        return buff.toString();
    }

    /**
     * Converts the given SearchTerm into the Solr equivalent String representation.
     * @param searchTerm SearchTerm object for conversion to query string.
     * @return Query string representation of searchTerm object.
     */
    protected String getQueryString(SearchTerm searchTerm){
        if(searchTerm.getClass().equals(SearchTermGroup.class)){
            SearchTermGroup searchTermGroup = (SearchTermGroup) searchTerm;

            return getQueryString(searchTermGroup);
        }else if(searchTerm instanceof OperatorTerm){
            return getQueryString((OperatorTerm)searchTerm);
        }else if (searchTerm instanceof ValuePhraseTerm){

            return "\"" + searchTerm.getAsString() + "\"";
        }else{
            return searchTerm.getAsString();
        }
    }

    /**
     * Converts the given OperatorTerm into the Solr equivalent String representation.
     * @param term The term to be converted.
     * @return The Solr equivalent of the term.
     */
    protected String getQueryString(OperatorTerm term){
        return getSolrOperator(term.getOperator());
    }

    /**
     * Gets the Solr string equivalent of the given Operator.
     * @param operator The operator to be coonverted.
     * @return The Solr equivalent of the given Operator.
     */
    protected String getSolrOperator(Operator operator){
        switch(operator){
        case AND:     return "+";
        case OR:      return " ";
        case NOT:     return "-";

        default:      return null;
        }
    }


    protected static Map<String, Float> boostMap = new HashMap<String, Float>();
    static{
        boostMap.put("dcterms_title", 1000.0f);
        boostMap.put("subTitle", 10.0f);
        boostMap.put("pub_keyword", 0.5f);
        boostMap.put("dcterms_description", 1.0f);
        boostMap.put("fulltext", 1.0f);
        boostMap.put("pub_author", 1.0f);
        boostMap.put("editors", 1.0f);
        boostMap.put("prism_issn", 1.0f);
        boostMap.put("prism_eIssn", 1.0f);
        boostMap.put("prism_doi", 1.0f);
        boostMap.put("pub_issn", 1.0f);
        boostMap.put("journal", 1.0f);
    }

    protected String getMultiFieldQuery(String[] fields, String searchTermString){

        if (LOG.isDebugEnabled()) {
            LOG.debug("Got searchTermString: " + searchTermString);
            LOG.debug("boostMap is: " + boostMap.toString());
        }
        SolrQueryEscapeCharHelper helper = new SolrQueryEscapeCharHelper();

        searchTermString = helper.escapeSolrReservedChars(searchTermString);
        if (StringUtils.isBlank(searchTermString)) {
           LOG.debug("Not parsing blank search term");
           return "";
        }        
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new WhitespaceAnalyzer(), boostMap);
        queryParser = new MultiFieldQueryParser(fields, new WhitespaceAnalyzer(), boostMap);
        queryParser.setDefaultOperator(org.apache.lucene.queryParser.QueryParser.Operator.AND);

        try {
           org.apache.lucene.search.Query parsedQuery = queryParser.parse(searchTermString);
           // The results of parsedQuery.toString() are designed to be human readable and are not suitable for sending
           // to solr directly - that means we need to reescape it - I think you could probably make an argument that
           // the use of MultiFieldQueryParser causes more problems than it solves
           return helper.escapeSolrQuery(parsedQuery.toString());
        } catch(ParseException e){
            LOG.warn("Error reading multi field query: " + searchTermString, e);
            return "";
        }
    }
    
    /**
     * Get method for highlights object
     * @return highLights a set of fields that need to be highlighted
     */
    protected Set<String> getHighlights(){
        return this.highLights;
    }

    /**
     * Set method for highlights object
     * @param hl the highlights
     */
    protected void setHighlights(Set<String> hl){
        this.highLights = hl;
    }

    /**
     * Gets the "year from" string
     * @return  year from setting
     */
    protected String getYearFrom() {
        return yearFrom;
    }

    /**
     * Set method for "year from" string
     * @param yearFrom  year from
     */
    protected void setYearFrom(String yearFrom) {
        this.yearFrom = yearFrom;
    }

    /**
     * Gets the "year to" string
     * @return  year to setting
     */
    protected String getYearTo() {
        return yearTo;
    }

    /**
     * Set method for "year to" string
     * @param yearTo  year to
     */
    protected void setYearTo(String yearTo) {
        this.yearTo = yearTo;
    }

    /**
     * Set method for "date to" string
     * @param dateTo  Date to
     */
    protected void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * Set method for "date from" string
     * @param dateFrom  date from
     */
    protected void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

}
