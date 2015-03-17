package com.ingenta.search.domain;

/**
 * @author Rishi
 *
 */
public class SearchFailedException extends RuntimeException {
	
	static final long serialVersionUID = 1;
	
	/**
	 * Constructs a new exception with the specified detail message. The 
	 * cause is not initialized, and may subsequently be initialized by 
	 * a call to Throwable.initCause(java.lang.Throwable). 
	 * @param message the detail message. The detail message is saved for 
	 * later retrieval by the Throwable.getMessage() method.
	 */
	public SearchFailedException(String message){
		super(message);
	}

   /**
    * Constructs a new exception with null as its detail message. The 
    * cause is not initialized, and may subsequently be initialized by 
    * a call to Throwable.initCause(java.lang.Throwable). 
    */
   public SearchFailedException() {
      super();
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
   public SearchFailedException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * Constructs a new runtime exception with the specified cause and 
    * a detail message of <code>(cause==null ? null : cause.toString())</code>  
    * (which typically contains the class and detail message of cause). 
    * This constructor is useful for runtime exceptions that are little more 
    * than wrappers for other throwables. 
    * @param cause the cause (which is saved for later retrieval by the 
    * Throwable.getCause() method). (A null value is permitted, and indicates 
    * that the cause is nonexistent or unknown.)
    */
   public SearchFailedException(Throwable cause) {
      super(cause);
   }
}
