/*
 * InvalidSearchTermException
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

/**
 * This exception should be thrown when a user-input string throws a parse
 * error.
 * @author ccsrak
 */
public class InvalidSearchTermException extends SearchException {

    private static final long serialVersionUID = 1L;
    private String field;
    private String error;
    private String term;

    public InvalidSearchTermException(String field, String error, String term){
        super ("Error in field [" + field + "] searched for [" + term + "]: " + error);
        this.error = error;
        this.field = field;
        this.term = term;
    }

    public String getError() {
        return error;
    }

    public String getField() {
        return field;
    }

    public String getTerm() {
        return term;
    }
}