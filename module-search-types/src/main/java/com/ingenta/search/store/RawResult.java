package com.ingenta.search.store;

import java.util.LinkedHashMap;
import java.util.Map;

public interface RawResult {

	public abstract void setHitCount(int hitCount);

	public abstract void addDocument(Document document);

	public abstract void addHighlight(Highlight highlight);
	
	public abstract void addFacetElement (FacetElement facetElement);

	public abstract LinkedHashMap<String, Document> getDocuments();

    public abstract LinkedHashMap<String, FacetElement> getFacets();    

	public abstract int getHitCount();

	public abstract int getStart();

	public abstract int getPageSize();
	
   public abstract void addSuggestion(Suggestion suggestion);
   public abstract Map<String, String> getSuggestions();    



}