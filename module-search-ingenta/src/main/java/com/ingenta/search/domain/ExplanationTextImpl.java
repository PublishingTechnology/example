/*
 * ExplanationTextImpl
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.util.*;


/**
 * Implementation of ExplanationText.
 * 
 * @author Mike Bell
 */
public class ExplanationTextImpl implements ExplanationText{

    private static final String YEAR_FROM = "year_from";
    private static final String YEAR_TO = "year_to";
    private static final String AND = " AND ";
    private static final String SPACE = " ";
    Map<String, String> friendlyTerms;
    private List<List<ExplanationClause>> explanation;

    /**
     * Constructs an instance from the given Search.
     * @param search The search to be 'explained'.
     */
    public ExplanationTextImpl(Search search){
        initialize(search);
        this.friendlyTerms = getFriendlyTermsMap();
    }

    /**
     * Reads the given search into a List of sub-Lists of <code>ExplanationClause</code>
     * Objects.
     * @param search The search to be read. 
     */
    private void initialize(Search search){
        List<SearchUnit> searchUnitList = search.getSearchUnits();
        List<List<ExplanationClause>> outerList = new ArrayList<List<ExplanationClause>>(searchUnitList.size());

        for(Iterator<SearchUnit> i = searchUnitList.iterator(); i.hasNext();) {
            SearchUnit currUnit = i.next();
            List<SearchExpression> searchExpressionList = currUnit.getSearchExpressions();
            List<ExplanationClause> explanationClauses = new ArrayList<ExplanationClause>(searchExpressionList.size());

            for (Iterator<SearchExpression> j = searchExpressionList.iterator(); j.hasNext();) {
                SearchExpression currExpression = j.next();
                explanationClauses.add(new ExplanationClause(currExpression));
            }

            outerList.add(explanationClauses);
        }

        this.explanation = outerList;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.ExplanationText#getSearchExplanation(com.ingenta.search.domain.Search)
     */
    public List<List<ExplanationClause>> getSearchExplanation() {
        return this.explanation;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.ExplanationText#getSearchExplanationString()
     */
    public String getSearchExplanationString() {
        StringBuilder buff = new StringBuilder();
        boolean firstIteration = true;

        for (List<ExplanationClause> explanationClauses : this.explanation){
            if(!firstIteration){
                buff.append(AND);
            }

            String yearFrom = null;
            String yearTo = null;

            for (ExplanationClause explanationClause : explanationClauses) {
                if(YEAR_FROM.equals(explanationClause.getPrettyFieldName())){
                    yearFrom = explanationClause.getSearchTerm();
                }else if(YEAR_TO.equals(explanationClause.getPrettyFieldName())){
                    yearTo = explanationClause.getSearchTerm();
                }else{
                    appendSearchTerm(buff, explanationClause);
                }
            }

            appendYearRange(buff, yearFrom, yearTo);

            firstIteration = false;
        }

        return buff.toString().trim();
    }

    /**
     * Appends the given start and end years to the given buffer, unless they are
     * <code>null</code>, in which case it does nothing.
     * @param buff The buffer in which the explanation string is being assembled.
     * @param yearFrom The start year.
     * @param yearTo The end year.
     */
    private void appendYearRange(StringBuilder buff, String yearFrom, String yearTo) {
        if(yearFrom != null && yearTo != null){
            buff.append("published between ");
            buff.append(yearFrom);
            buff.append(" and ");
            buff.append(yearTo);
        }
    }

    /**
     * Appends the search terms in the given explanation clause to the given buffer.
     * @param buff The buffer in which the explanation string is being assembled.
     * @param explanationClause The clause to be rendered as a String.
     */
    private void appendSearchTerm(StringBuilder buff, ExplanationClause explanationClause) {
        if(explanationClause.getOperator() != null){
            buff.append(explanationClause.getPrettyOperator());
            buff.append(SPACE);
        }

        String field = getFriendlyTerm(explanationClause.getPrettyFieldName());
        String predicate = getFriendlyTerm(explanationClause.getPrettyPredicate());
        buff.append(field);
        buff.append(SPACE);
        buff.append(predicate);
        buff.append(SPACE);
        buff.append("'");
        buff.append(explanationClause.getSearchTerm());
        buff.append("'");
        buff.append(SPACE);
    }

    /**
     * Gets the user-friendly equivalent of the given term.
     * @param term The term to be converted.
     * @return The friendly term.
     */
    private String getFriendlyTerm(String term){
        String friendlyTerm;

        if(this.friendlyTerms.get(term) != null){
            friendlyTerm = friendlyTerms.get(term);
        }else{
            friendlyTerm = term;
        }

        return friendlyTerm;
    }

    /**
     * Gets the Map of terms to friendly terms.
     * TODO MB should this be configurable?
     * @return The Map of terms to friendly terms.
     */
    private Map<String, String> getFriendlyTermsMap() {
        Map<String, String> niceNames = new HashMap<String, String>();

        niceNames.put("titleabs", "Title, Keywords or Abstract");
        niceNames.put("title", "Title");
        niceNames.put("author", "Authors");
        niceNames.put("all", "All Fields (excluding fulltext)");
        niceNames.put("issnisbndoi", "ISSN/ISBN/DOI");
        niceNames.put("fulltext", "Full Text");
        niceNames.put("journalbooktitle", "Journal or Book title");
        niceNames.put("contains", "contains");
        niceNames.put("titleabstract", "All Fields (including fulltext)");
        niceNames.put("all", "All Fields (excluding fulltext)");
        niceNames.put("journalexact", "Series Name/Journal Name");
        niceNames.put("subject", "Subject");
        niceNames.put("region", "Region");
        niceNames.put("contains", "contains");
        niceNames.put("doctype", "Content Type");
        niceNames.put("tka", "Quick Search");
        niceNames.put("wpnumber", "Working Paper");
        niceNames.put("abstract", "Abstract");
        niceNames.put("issue", "Journal Issue");
        niceNames.put("volume", "Journal Volume");
        niceNames.put("keywords", "Keyword");
        niceNames.put("country", "Country");

        return niceNames;
    }
}
