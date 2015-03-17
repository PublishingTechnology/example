/*
 * ValuePhraseTerm
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;


/**
 * A search term representing a value phrase.
 * 
 * @author Mike Bell
 */
public class ValuePhraseTerm extends ValueTerm implements SearchTerm {

   private static final long serialVersionUID = 1L;

   /**
    * Constructs an instance from the given value.
    * @param value The value for this term.
    */
   public ValuePhraseTerm(String value) {
      super(value);
   }
}
