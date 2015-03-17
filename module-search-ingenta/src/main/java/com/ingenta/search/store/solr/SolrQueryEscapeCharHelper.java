/*
 * SolrQueryEscapeCharHelper
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.store.solr;

import java.util.Arrays;
import java.util.List;

/**
 * A utility class to escape reserved chars in a query String being passed to the Solr
 * search engine. Escaped chars are preceded by a '\' char.
 * 
 * @author Mike Bell
 */
public class SolrQueryEscapeCharHelper {
   
   public static final String ESCAPE_CHAR = "\\";
   public static final List<Character> RESERVED_CHARS;
   
   static{
      //RESERVED_CHARS = Arrays.asList('+', '-', '!', '(', ')', '{', '}', '[', ']', '^', '"', '~', '*', '?', ':', '\\');
      
      RESERVED_CHARS = Arrays.asList(':', '\\', '~');
      
   }
   
   /**
    * Escapes the reserved chars in the given input String.
    * @param input The unescaped String.
    * @return The String with reserved chars escaped.
    */
   public String escapeSolrReservedChars(String input){
      StringBuilder buff = new StringBuilder();

      boolean previousWhitespace = true;
      for(int i = 0; i < input.length(); i++){
         char next = input.charAt(i);
         
         if(RESERVED_CHARS.contains(next)){
        	   buff.append(ESCAPE_CHAR);
         }
         if (previousWhitespace && (next == '?' || next == '*')) {
            // We could append the escape character and then the character - but this seems unlikely to have been the
            // users intention - they can always self escape if they really mean it
         } else {
            buff.append(next);
         }
         previousWhitespace = Character.isWhitespace(next);
      }
      
      return buff.toString();
   }
   
   /**
    * Escapes the human-readable results from org.apache.lucene.search.Query.toString in such a way as they can be
    * passed to solr. 
    * 
    * @param in the unescaped query
    * @return the escaped query
    */
   public String escapeSolrQuery(String in) {
      // One thing we can rely on is that we have no querys against the default field which means we can say with
      // some confidence that we are only interested in the characters between a ':' and a '^', ' ', or ')'

      // If a field is quoted then the same end characters do not apply - but we can also not escape            
      StringBuilder results = new StringBuilder();
      StringBuilder searchTerm = new StringBuilder();
      boolean inSearchTerm = false;
      boolean quoted = false;
      for (char c : in.toCharArray()) {
         if (!inSearchTerm) {
            // In this case we are just iterating through until we hit a search term 
            results.append(c);
            if (c == ':') {
               inSearchTerm = true;
            }
         } else {
            if (quoted) {
               if (c == '"') {
                  quoted = false;
                  inSearchTerm = false;
                  searchTerm.append(c);
                  results.append(searchTerm);
                  searchTerm.setLength(0);
               } else {
                  searchTerm.append(c);
               }
            } else {
               if (c == '^' || c == ' ' || c == ')') {
                  inSearchTerm = false;
                  results.append(escape(searchTerm));
                  searchTerm.setLength(0);
                  results.append(c);
               } else if (searchTerm.length() == 0 && c == '"') {
                  quoted = true;
                  searchTerm.append(c);
               } else {
                  searchTerm.append(c);
               }
            }
         }
      }
      if (inSearchTerm) {
         results.append(escape(searchTerm));
      }
      return results.toString();
   }

   private String escape(StringBuilder searchTerm) {
      return escape(searchTerm.toString());
   }
   
   private String escape(String searchTerm) {
      StringBuilder sb = new StringBuilder();
      // We make the assumption that if a user types a * or ? then they mean the wildcard
      for (char c : searchTerm.toCharArray()) {
        if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
          || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
          || /*c == '*' || c == '?' ||*/ c == '|' || c == '&') {
          sb.append('\\');
        }
        sb.append(c);
      }
      // Escape regular expressions
      if (searchTerm.startsWith("/")) {
         return '"' + searchTerm + '"';
      }
      return sb.toString();
   }

}