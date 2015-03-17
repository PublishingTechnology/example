/*
 * Pageable
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import java.io.Serializable;

/**
 * API for classes which implement the concept of <i>paging</i>. An example 
 * of this is search results, where we may need to define the number of results
 * in one page, and to be able to query the class as to which page we are 
 * currently on.
 * 
 * @author Mike Bell
 */
public interface Pageable extends Serializable {

   /**
    * Sets the page size (ie the maximum number of things) to be
    * displayed on one page.
    * @param size The page size to use.
    */
   void setPageSize(int size);
   
   /**
    * Gets the page size (ie the maximum number of things) to be
    * displayed on one page.
    * @return The page size.
    */
   int getPageSize();
   
   /**
    * Gets the number of the current page.
    * @return The current page number.
    */
   int getCurrentPage();
   
   /**
    * Gets the number of total items.
    * @return The total count number.
    */
   int getTotalCount();
   
   /**
    * Sets the total number of items.
    */
   void setTotalCount(int totalCount);   
   
}
