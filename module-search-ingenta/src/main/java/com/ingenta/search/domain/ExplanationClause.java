/*
 * ExplanationClause
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

/**
 * A class 
 * @author John West
 */

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;


public class ExplanationClause implements Serializable {

    private Operator operator;
    private String prettyOperator;
    private String prettyFieldName;
    private String searchTerm;
    private String prettyPredicate;
    private String fieldName = "no-field-name";


    public ExplanationClause(SearchExpression searchExpression){
        StringBuilder buff = new StringBuilder();
        operator = searchExpression.getOperator();
        //operator = searchCondition.getOperator();
        SearchCondition searchCondition = searchExpression.getSearchCondition();
        List<SearchTerm> termList = searchCondition.getSearchTerms();		
        for (Iterator<SearchTerm> k = termList.iterator(); k.hasNext();) {
            buff.append(k.next().getAsString()+" ");
        }    
        searchTerm = buff.toString();
        prettyPredicate = "contains";
        fieldName = searchCondition.getField();
    }

    public String getFieldName() {
        return fieldName;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getPrettyFieldName() {
        prettyFieldName = getFieldName().trim();
        return prettyFieldName;
    }

    public String getPrettyOperator() {
        prettyOperator = getOperator().toString();
        return prettyOperator;
    }

    public String getPrettyPredicate() {
        return prettyPredicate;
    }

    public String getSearchTerm() {
        return searchTerm.trim();
    }

    public String getExpressionExplanation(){
        StringBuilder buff = new StringBuilder();
        if (!(operator == null)) {
            buff.append(getPrettyOperator() + Search.SPACE + "(");
        }
        buff.append(getPrettyFieldName() + Search.SPACE + getPrettyPredicate() + Search.SPACE + getSearchTerm());
        if (!(operator == null)) {
            buff.append(")");
        }
        return buff.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder(this.getClass().getName());
        buff.append("[");
        buff.append(getExpressionExplanation());
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
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((prettyFieldName == null) ? 0 : prettyFieldName.hashCode());
        result = prime * result + ((prettyOperator == null) ? 0 : prettyOperator.hashCode());
        result = prime * result + ((prettyPredicate == null) ? 0 : prettyPredicate.hashCode());
        result = prime * result + ((searchTerm == null) ? 0 : searchTerm.hashCode());

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
        ExplanationClause other = (ExplanationClause) obj;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        }
        else if (!fieldName.equals(other.fieldName))
            return false;
        if (operator == null) {
            if (other.operator != null)
                return false;
        }
        else if (!operator.equals(other.operator))
            return false;
        if (prettyFieldName == null) {
            if (other.prettyFieldName != null)
                return false;
        }
        else if (!prettyFieldName.equals(other.prettyFieldName))
            return false;
        if (prettyOperator == null) {
            if (other.prettyOperator != null)
                return false;
        }
        else if (!prettyOperator.equals(other.prettyOperator))
            return false;
        if (prettyPredicate == null) {
            if (other.prettyPredicate != null)
                return false;
        }
        else if (!prettyPredicate.equals(other.prettyPredicate))
            return false;
        if (searchTerm == null) {
            if (other.searchTerm != null)
                return false;
        }
        else if (!searchTerm.equals(other.searchTerm))
            return false;
        return true;
    }
}


