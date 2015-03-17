/*
 * AutosuggestTermCountComparator
 *
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.ejb.solr.autosuggest;

import java.util.Comparator;

import com.pub2web.search.api.autosuggest.AutosuggestTerm;

/**
 * Compare autosuggest term results by their counts
 * 
 * @author mstephenson
 */
public class AutosuggestTermCountComparator implements Comparator<AutosuggestTerm> {

   @Override
   public int compare(AutosuggestTerm o1, AutosuggestTerm o2) {
      if (o1 == null) {
         if (o2 == null) {
            return 0;
         }
         return 1;
      }
      if (o2 == null) {
         return -1;
      }
      int c1 = o1.getCount();
      int c2 = o2.getCount();
      
      if (c1 == c2) {
         return String.valueOf(o1.getName()).compareTo(String.valueOf(o2.getName()));
      }
      return c2 - c1;
   }

}
