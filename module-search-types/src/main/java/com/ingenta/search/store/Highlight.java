/**
 * 
 */
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Highlight
      implements Serializable
   {
      private String id;
      private Map<String,Field> highlights = new HashMap<String,Field>();
      // the "name" of the highlight list is actually the unique id
      public void setName(String name) {id = name;}
      public String getId() {return id;}
      public void addField(Field field)
      {
         highlights.put(field.getName(), field);
      }
      public Map<String,Field> getHighlights() {return highlights;}
      public String toString() {return highlights.toString();}
   }