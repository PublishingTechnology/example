/**
 * 
 */
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Field
      implements Serializable
   {
      private String name;
      private String value;
      private List<String> values;
      
      public void setName(String name) 
      {
         this.name = name;
      }
      public String getName() 
      {
         return name;
      }
      public void setValue(String value) 
      {
         this.value = value;
      }
      public String getValue() 
      {
         return value;
      }
      public void addValue(String value)
      {
         if (values == null)
            values = new ArrayList<String>();
         values.add(value);
      }
      public List<String> getValues()
      {
         return values;
      }
      public boolean isMultivalued()
      {
         return values != null;
      }
      public String toString(){
          if (isMultivalued()){
              return name + " - " + values.toString();
          }
          return name + " - " + value;
      }
   }