/*
 * AutosuggestException
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.pub2web.search.api.autosuggest;

/**
 * Exception class for errors in Autosuggest
 * 
 * @author James Beard
 */
public class AutosuggestException extends Exception {

    /**
     * Constructs a new Exception with <code>null</code> as its detail message.
     */
    public AutosuggestException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * @param message the detail message
     */
    public AutosuggestException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the exception that caused this exception
     */
    public AutosuggestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and <code>null</code> as its detail message.
     * @param cause the exception that caused this exception
     */
    public AutosuggestException(Throwable cause) {
        super(cause);
    }
}