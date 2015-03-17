package com.ingenta.search.store.solr;

import com.ingenta.search.store.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to return results from a Solr query.
 * A result has a hitCount, details of the start and pageSize, and an ordered Map (LinkedHashMap)
 * of Documents.
 * 
 * Each Document contains fields returned from Solr as a Map. The keys are String values,
 * and the values are either String values or Lists of Strings depending upon whether this is a multi-valued
 * field or not (something the ArticleFactory needs to be aware of when processing this).
 * 
 * Documents may also include highlights. The highlights are also in a String keyed Map,
 * but here the values are always Lists of Strings regardless of whether the field is multi-valued or not.
 * This is because depending upon the mode in which highlighting is working there may be multiple
 * fragments from a single valued field (e.g. the abstract).
 * 
 * @author Andrew D. May
 */
public class SolrResult implements RawResult, Serializable {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private LinkedHashMap<String,Document> documents;
   private int hitCount;
   private int start;
   private int pageSize;
   private LinkedHashMap<String, FacetElement> facets;
   private Map<String, String> suggestions;
   public SolrResult(int start, int pageSize){
      this.start = start;
      this.pageSize = pageSize;
      documents = new LinkedHashMap<String,Document>(pageSize);
      facets = new LinkedHashMap<String,FacetElement>();
      suggestions = new HashMap<String, String>();
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#setHitCount(int)
    */
   public void setHitCount(int hitCount){
      this.hitCount = hitCount;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#addDocument(com.ingenta.search.store.solr.Document)
    */
   public void addDocument(Document document){
      documents.put(document.getId(), document);
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#addHighlight(com.ingenta.search.store.solr.Highlight)
    */
   public void addHighlight(Highlight highlight){
      Document doc = documents.get(highlight.getId());
      Map<String,Field> fields = doc.getFields();
      
      Map<String,Field> highlights = highlight.getHighlights();
      
      for (String highlightField : highlights.keySet()){
         if (fields.get(highlightField).isMultivalued()){
            Field high = highlights.get(highlightField);
            Field replacementHighlight = new Field();
            replacementHighlight.setName(high.getName());
            
            Field field = fields.get(highlightField);
            
            for (String value: field.getValues()){
               String h = getHighlight(value,high);
               if (h != null ){
                  replacementHighlight.addValue(h);
               }
               else{
                  replacementHighlight.addValue(value);
               }
            }
            
            highlights.put(highlightField, replacementHighlight);
            
         }
      }
      if (doc != null){ // it should never be null, but just in case
         doc.setHighlights(highlights);
      }
   }
   
   /**
    * Method to go through the highlighted list for a multi-valued doc field
    * for a particular value, that returns the highlighted value if it exists
    * or else returns null. A bit of a hack, as the markup to check for 
    * is hard coded as  '<em class="highlight">' 
    * 
    **/   
   private String getHighlight(String value, Field highlights){
      for(String highlight: highlights.getValues()){
         String stripped = highlight.replace("<em class=\"highlight\">","");
         stripped = stripped.replace("</em>","");
         if (stripped.equals(value)){
            return highlight;
         }
      }
      return null;
   }
   
   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#addSugestion
    */
   public void addSuggestion(Suggestion suggestion){
      List<String> suggestionTexts = suggestion.getSuggestionText();
      String suggestionText = suggestionTexts.get(0);
      suggestions.put(suggestion.getId(), suggestionText);
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#getDocuments()
    */
   public LinkedHashMap<String,Document> getDocuments(){
      return documents;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#getHitCount()
    */
   public int getHitCount(){
      return hitCount;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#getStart()
    */
   public int getStart(){
      return start;
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#getPageSize()
    */
   public int getPageSize(){
      return pageSize;
   }

   public String toString(){
      return "Hits: "+hitCount+", Start: "+start+", PageSize: "+pageSize+", Documents:\n"+documents;
   }
   
   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#addFacetElement(com.ingenta.search.store.FacetElement)
    */
   public void addFacetElement(FacetElement facetElement) {
       facets.put(facetElement.getId(), facetElement);
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.store.solr.RawResult#getFacets()
    */
   public LinkedHashMap<String, FacetElement> getFacets() {    
       return facets;
   }
   
   public Map<String, String> getSuggestions() {    
      return suggestions;
  }

}
