package com.ingenta.search.store;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ingenta.search.store.solr.DefaultResultsFactory;
import com.ingenta.search.domain.SearchSuggestion;

/**
 * Factory class for converting RawResults into SearchResult
 * and SearchResults.
 * Call <code>getInstance</code> to get an appropriate ArticleFactory as defined
 * by the <code>search.resultsFactory</code> system property. 
 */
public abstract class AbstractResultsFactory implements ResultsFactory{
   
   public static final String PROPERTY_RESULTS_FACTORY = "search.resultsFactory";
   protected static final String ID = "id";
   
   private static final Logger log = Logger.getLogger(AbstractResultsFactory.class);
      
   private boolean highlighting = false;
   
   /**
    * Protected constructor - use <code>getInstance()</code>
    */
   protected AbstractResultsFactory(){}
   
   /**
    * Generate a Result List from a RawResult returned from the search engine.
    * @param result The RawResult to convert.
    */
   public SearchResults generateResultList(RawResult result){
      if(log.isDebugEnabled()){
         log.debug("generateResultList: " + result);
      }
      
      List<SearchResult> articles = new ArrayList<SearchResult>();
      
      for (Document doc : result.getDocuments().values()){
         articles.add(generateResult(doc));
      }
      SearchResults searchResult = new SearchResults(result.getPageSize(), (result.getStart() / result.getPageSize()) + 1, result.getHitCount(), articles, result.getFacets(), null );
      SearchSuggestion suggestion = new SearchSuggestion();
      suggestion.setSuggestions(result.getSuggestions());
      searchResult.setSearchSuggestion(suggestion);
      return searchResult;
   }
   
   /**
    * Generate a SearchResult from a Document
    * @param document a Document.
    */
   protected abstract SearchResult generateResult(Document document);
   
   /**
    * Set a Result property to the value in the Result. Only calls result.setProperty if there is a value,
    * which prevents keys with null values being created.
    */
   protected void setResultProperty(SearchResult result, Document document, String propertyName, String fieldName){
      if(log.isDebugEnabled()){
         log.debug("setResultProperty property: " + propertyName + ", field: " + fieldName);
      }
      
      if (document.getFields().containsKey(fieldName)){
         result.setProperty(rationaliseFieldName(propertyName), toStringList(document.getFields(), fieldName));
      }
   }
   
   /**
    * This just returns the name unchanged here, but is provided
    * as a means for any descendents to munge Solr field names.
    * @param fieldName The name to be rationalised.
    * @return the rationalised name. 
    */
   protected String rationaliseFieldName(String fieldName){
      return fieldName;
   }
   
   /**
    * Convert a Result Field into either a <code>String</code> value if the field is single valued,
    * or a <code>List<String></code> if it is multivalued. 
    * 
    */
   protected List<String> toStringList(Map<String,Field> data, String key){
      if(log.isDebugEnabled()){
         log.debug("toStringList for key: " + key);
      }
      
      if (!data.containsKey(key)){
         return null;
      }
      
      List<String> list = new ArrayList<String>();
      Field field = data.get(key);
      if (field.isMultivalued()){
         log.debug("field is multivalued");
         
         for (Iterator<String> iter = field.getValues().iterator(); iter.hasNext();){
            list.add(iter.next());
         }
      }else{
         log.debug("field is single-valued");

         if(log.isDebugEnabled()){
            log.debug("field value: " + field.getValue());
         }
    	   
         list.add(field.getValue());
      }

      return list;
   }   
   
   /**
    * Remove duplicates from a list of strings (e.g. from a multi valued field)
    */
   protected void deduplicate(List<String> values){
      if (values != null){
         for(int i=0; i<(values.size()-1); i++){
            values.subList(i+1, values.size()).remove(values.get(i));
         }
      }
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.ResultsFactory#isHighlighting()
    */
   public boolean isHighlighting() {
      return highlighting;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.ResultsFactory#setHighlighting(boolean)
    */
   public void setHighlighting(boolean highlighting) {
      this.highlighting = highlighting;
   }

   /**
    * Get ResultsFactory instance. Uses the com.ingenta.search.articleFactory system property.
    */
   public static ResultsFactory getInstance(String configId){
      if(log.isDebugEnabled()){
         log.debug("getInstance for config: " + configId);
      }
      
	   if (configId == null){
		   return new DefaultResultsFactory();  
	   }
	   String factoryClassName = System.getProperty(configId + PROPERTY_RESULTS_FACTORY);
   
      // no meaningful default
      if (factoryClassName == null) {
         throw new RuntimeException(configId + PROPERTY_RESULTS_FACTORY + " system property not set");
      }

      try {
         @SuppressWarnings("unchecked")
         Class<? extends ResultsFactory> c = (Class<? extends ResultsFactory>) Thread.currentThread()
               .getContextClassLoader().loadClass(factoryClassName);
         ResultsFactory instance = c.newInstance();
         log.debug("Created " + factoryClassName + " ArticleFactory instance");
         return instance;
      }
      catch (ClassNotFoundException e) {
         throw new IllegalArgumentException("ArticleFactory class " + factoryClassName + " cannot be loaded", e);
      }
      catch (InstantiationException e) {
         throw new IllegalArgumentException("ArticleFactory class " + factoryClassName
               + " cannot be instantiated (must have zero-args constructor)", e);
      }
      catch (IllegalAccessException e) {
         throw new IllegalArgumentException("ArticleFactory class " + factoryClassName
               + " constructor cannot be accessed", e);
      }
   }   
}
