/*
 * SearchNotFoundException
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.savedsearch;

import com.ingenta.search.domain.SearchException;

/**
 * An exception to be thrown when an attempt to get a saved search from the
 * database by its primary key fails to find a search corresponding to the
 * key.
 * 
 * @author Mike Bell
 */
public class SavedSearchNotFoundException extends SearchException {

   static final long serialVersionUID = 1;
   
   /**
    * Constructs a new exception with the specified detail message. The 
    * cause is not initialized, and may subsequently be initialized by 
    * a call to Throwable.initCause(java.lang.Throwable). 
    * @param message the detail message. The detail message is saved for 
    * later retrieval by the Throwable.getMessage() method.
    */
   public SavedSearchNotFoundException(String message) {
      super(message);
   }

   /**
    * Constructs a new exception with null as its detail message. The 
    * cause is not initialized, and may subsequently be initialized by 
    * a call to Throwable.initCause(java.lang.Throwable). 
    */
   public SavedSearchNotFoundException() {
   }

   /**
    * Constructs a new exception with the specified detail message and cause.
    * <p>Note that the detail message associated with cause is not automatically 
    * incorporated in this exception's detail message.</p> 
    * @param message the detail message. The detail message is saved for 
    * later retrieval by the Throwable.getMessage() method.
    * @param cause the cause (which is saved for later retrieval by the 
    * Throwable.getCause() method). (A null value is permitted, and indicates 
    * that the cause is nonexistent or unknown.)
    */
   public SavedSearchNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Constructs a new runtime exception with the specified cause and 
    * a detail message of <code>(cause==null ? null : cause.toString())</code>  
    * (which typically contains the class and detail message of cause). 
    * This constructor is useful for exceptions that are little more 
    * than wrappers for other throwables. 
    * @param cause the cause (which is saved for later retrieval by the 
    * Throwable.getCause() method). (A null value is permitted, and indicates 
    * that the cause is nonexistent or unknown.)
    */
   public SavedSearchNotFoundException(Throwable cause) {
      super(cause);
   }
}
