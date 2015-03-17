/**
Result
Copyright: 2007 PublishingTechnology plc
*/
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.Map;


import com.pub2web.util.Annotable;

public interface Result extends Serializable, Annotable {
   
   /** <p>Set one of the properties (e.g. Title) of the result.</p> */ 
   public void setProperty(String propertyName, Object property);
   
   public String getIdentifier();
   
   public Map<Object, Object> getProperties();    

}
