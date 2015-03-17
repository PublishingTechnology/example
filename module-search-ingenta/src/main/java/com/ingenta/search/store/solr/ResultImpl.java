/**
ResultImpl
Copyright: 2007 PublishingTechnology plc
*/
package com.ingenta.search.store.solr;


import com.ingenta.search.store.Result;
import com.pub2web.util.AnnotableImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ResultImpl extends AnnotableImpl implements Result {
   
   static final long serialVersionUID = 1;
   private String id;

   
   public ResultImpl(String id) {
      setIdentifier(id);
   }

   public void setIdentifier(String id) {
      this.id = id;
      setProperty("webID", getWebIdentifier());      
   }

   public String getIdentifier() {      
      return id;
   }
   
   /* <p>Set one of the properties (e.g. Title) of the article.</p> */
   public void setProperty(String propertyName, Object property)
   {
         
      super.setProperty(propertyName, property);
   }   
   
   private String getWebIdentifier(){
      
      String id = getIdentifier();
      URI uri = null;
      try {
         uri = new URI(getIdentifier());
      }
      catch (URISyntaxException e) {
         return getIdentifier();
      }
      // for now, same as identifier
      return uri.getPath();
      
   }
   
   public Map<Object, Object> getProperties(){
      Map<Object, Object> result = new HashMap<Object, Object>();
      Map annotableMap = super.getProperties();
      Set keySet = annotableMap.keySet();
      
      for (Iterator iter = keySet.iterator(); iter.hasNext();) {
          String key = (String)iter.next();
          result.put(key, annotableMap.get(key));         
      }
      
      return result;
   }
}
