/**
 * 
 */
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Suggestion
      implements Serializable
   {
      /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private String id;
   private List<String> suggestionText = new ArrayList<String>();
   /**
    * @return the id
    */
   public String getId() {
      return id;
   }
   /**
    * @param id the id to set
    */
   public void setName(String id) {
      this.id = id;
   }
   /**
    * @return the suggestionText
    */
   public List<String> getSuggestionText() {
      return suggestionText;
   }
   public void addField(Field field)
   {
       if (field.getName().equals("id") || field.getName().equals("name"))
       {
           this.id = field.getValue();
       }
       /*as we are only showing only one suggestion hence add only one
        * otherwise solr support 1+ suggestions*/
       suggestionText.add(field.getValues().get(0));
   }
   }