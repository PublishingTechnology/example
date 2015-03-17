/*
 * ExplanationText
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.io.Serializable;
import java.util.List;


/**
 * API for the generic Explanation text object. Different implementations may
 * be created to provide specialist functionality.
 * 
 * @author Mike Bell
 */
public interface ExplanationText extends Serializable {

   /**
    * Gets the explanation (a simple, layman's terms expression) of the
    * given Search object.
    * @param search The search for which the explanation is sought.
    * @return The explanation clauses which make up the explanation.
    */
   List<List<ExplanationClause>> getSearchExplanation();
   
   /**
    * Gets the explanation (a simple, layman's terms expression) of the
    * given Search object as a String.
    * @param search The search for which the explanation is sought.
    * @return The explanation as a String.
    */
   String getSearchExplanationString();
}
