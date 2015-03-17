/*
 * SearchCondition
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.util.Iterator;
import java.util.List;


/**
 * A class to express a single condition of a search. It will encapsulate
 * a search element such as <code>someField EQUALS someValue AND
 * (anotherValue OR aThirdValue)</code>. Its default operator is <code>EQUALS</code>
 * but this may be overridden if required. It will be wrapped in a
 * <code>SearchExpression</code> and contains a number of <code>SearchTerms</code>.
 * 
 * @author Mike Bell
 */
public class SearchCondition implements SearchElement{

    private static final long serialVersionUID = 1L;
    private String field;
    private Operator operator = Operator.EQUALS; //default value
    private List<SearchTerm> searchTerms;

    /**
     * Constructs an instance using the default Operator and the
     * given search terms.
     * @param field The field to which the condition refers.
     * @param searchTerms The search terms.
     */
    public SearchCondition(String field, List<SearchTerm> searchTerms) {
        this.field = field;
        this.searchTerms = searchTerms;
    }

    /**
     * Sets the field to which the condition refers.
     * @param field the field to set.
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Sets the given search terms in this search condition. These will <i>replace</i> the
     * existing ones, so if you want to add them to the ones that are already there
     * you should call <code>addSearchTerm()</code> or <code>addSearchTerms</code>
     * instead.
     * @param searchTerms the searchTerms to set
     */
    public void setSearchTerms(List<SearchTerm> searchTerms) {
        this.searchTerms = searchTerms;
    }

    /**
     * Get the field to which the condition refers.
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Gets the search terms for this search condition.
     * @return the searchTerms
     */
    public List<SearchTerm> getSearchTerms() {
        return searchTerms;
    }

    /**
     * Adds a search term to this search condition's existing search terms
     * (works the same regardless of whether the search condition already
     * has terms or not).
     * @param searchTerm The search term to be added.
     */
    public void addSearchTerm(SearchTerm searchTerm){
        this.searchTerms.add(searchTerm);
    }

    /**
     * Adds a list of search terms to this search condition's existing search
     * terms (works the same regardless of whether the search condition already
     * has terms or not). This method is not to be confused with
     * <code>setSearchTerms()</code>.
     * @param searchTerms The search terms to be added.
     */
    public void addSearchTerms(List<SearchTerm> searchTerms){
        this.searchTerms.addAll(searchTerms);
    }

    /**
     * Gets the Operator for this search condition.
     * @return The operator.
     */
    public Operator getOperator(){
        return this.operator;
    }

    /**
     * Sets the Operator for this search condition.
     * @param operator the operator to set
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.domain.SearchElement#getAsString()
     */
    public String getAsString(){
        StringBuilder buff = new StringBuilder();
        
        if (field != null)
           buff.append(field);

        if(buff.length() > 0 && !" ".equals(buff.substring(buff.length() -1))){
            buff.append(Search.SPACE);
        }

        buff.append(this.operator);

        for (Iterator<SearchTerm> i = this.getSearchTerms().iterator(); i.hasNext();){
            if(!" ".equals(buff.substring(buff.length() -1))){
                buff.append(Search.SPACE);
            }

            buff.append(i.next().getAsString());
        }

        return buff.toString();
    }

    /**
     * @return
     */
    public String getTermsAsString(){
        StringBuilder cuff = new StringBuilder(" ");

        if (cuff.length() > 0 && !" ".equals(cuff.substring(cuff.length() -1))) {
            cuff.append(Search.SPACE);
        }
        for (Iterator<SearchTerm> i = this.getSearchTerms().iterator(); i.hasNext();){
            if(!" ".equals(cuff.substring(cuff.length() -1))){
                cuff.append(Search.SPACE);
            }

            cuff.append(i.next().getAsString());
        }

        return cuff.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder(this.getClass().getName());
        buff.append("[");
        buff.append(this.getAsString());
        buff.append("]");

        return buff.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((searchTerms == null) ? 0 : searchTerms.hashCode());

        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchCondition other = (SearchCondition) obj;
        if (field == null) {
            if (other.field != null)
                return false;
        }
        else if (!field.equals(other.field))
            return false;
        if (operator == null) {
            if (other.operator != null)
                return false;
        }
        else if (!operator.equals(other.operator))
            return false;
        if (searchTerms == null) {
            if (other.searchTerms != null)
                return false;
        }
        else if (!searchTerms.equals(other.searchTerms))
            return false;

        return true;
    }
}
