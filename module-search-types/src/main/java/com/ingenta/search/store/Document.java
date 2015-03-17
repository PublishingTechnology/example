/**
 * 
 */
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Document
      implements Serializable
   {
      private String id;
      private Map<String,Field> fields = new HashMap<String,Field>();
      private Map<String,Field> highlights;
      public void addField(Field field)
      {
         if (field.getName().equals("id") || field.getName().equals("url"))
         {
            id = field.getValue();
         }
         fields.put(field.getName(), field);
      }
      public String getId() {return id;}
      public Map<String,Field> getFields() {return fields;}
      public void setHighlights(Map<String,Field> highlights) {this.highlights = highlights;}
      public Map<String,Field> getHighlights() {return highlights;}
   }