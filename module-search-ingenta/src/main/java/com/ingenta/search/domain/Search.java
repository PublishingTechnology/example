/*
 * Search
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import com.ingenta.search.xml.XmlSerializable;

import java.util.*;

/**
 * A class representing a complete search. It must contain at least one
 * <code>SearchUnit</code> to be usable.
 * 
 * @author Mike Bell
 */
public class Search implements Pageable, XmlSerializable {

    private static final long serialVersionUID = 1L;
    public static final String SPACE = " ";
    public static final String DEFAULT_SORT_FIELD = "score";
    public static final int DEFAULT_PAGE_SIZE = 50;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int currentPage = 1;
    private int totalCount;
    private List<SearchUnit> searchUnits;
    private List<SortField> sortFields = new ArrayList<SortField>();
    private List<String> subscribedIds = new ArrayList<String>();
    private boolean isHighlighting = true;
    private ExplanationText searchExplanation;
    private Map<String,String> paramMap;
    private Map<String, Object> properties = new HashMap<String, Object>();//Additional attribute to keep properties associated with Search
    private boolean isPublisherSpecific = false;
    private String publisherId;
    private boolean isPublisherExcluded = false;
    private List<String> excludedPublisherIds;

    /**
     * Constructs an instance from the given parameters.
     * @param searchUnits The search units which make up this search.
     */
    public Search(List<SearchUnit> searchUnits){
        this.searchUnits = searchUnits;
    }

    /**
     * Constructs an instance with just a boost map but no search units. 
     */
    public Search(){
        this.searchUnits = new ArrayList<SearchUnit>();
    }

    /**
     * Gets a String representation of this <code>Search</code>.
     * This method is intended to be used to construct a string representation
     * of the entire Search and is not to be confused with the standard 
     * <code>toString()</code> method.  
     * @return A String representation of this <code>Search</code>.
     */
    public String getAsString(){
        StringBuilder buff = new StringBuilder();

        for (Iterator<SearchUnit> i = this.searchUnits.iterator(); i.hasNext();) {
            if(buff.length() > 0 && !" ".equals(buff.substring(buff.length() -1))){
                buff.append(Search.SPACE);
            }

            buff.append(i.next().getAsString());
        }

        return buff.toString();
    }

    /**
     * Sets the given search units in this search. These will <i>replace</i> the
     * existing ones, so if you want to add them to the ones that are already there
     * you should call <code>addSearchUnit()</code> or <code>addSearchUnits</code> 
     * instead.
     * @param searchUnits the searchUnits to set
     */
    public void setSearchUnits(List<SearchUnit> searchUnits) {
        this.searchUnits = searchUnits;
    }

    /**
     * Gets the Search Expressions for this search.
     * @return the searchExpressions
     */
    public List<SearchUnit> getSearchUnits() {
        return searchUnits;
    }

    /**
     * Adds a search unit to this search's existing search units (works the
     * same regardless of whether the search already has units or not).
     * @param searchUnit The search unit to add.
     */
    public void addSearchUnit(SearchUnit searchUnit){
        this.searchUnits.add(searchUnit);
    }

    /**
     * Adds a list of search units to this search's existing search units 
     * (works the same regardless of whether the search already has units 
     * or not). This method is not to be confused with <code>setSearchUnits()</code>.
     * @param searchUnits The search units to add.
     */
    public void addSearchUnits(List<SearchUnit> searchUnits){
        this.searchUnits.addAll(searchUnits);
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#getCurrentPage()
     */
    @Override
    public int getCurrentPage() {
        return this.currentPage;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#setCurrentPage()
     */

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }


    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#getPageSize()
     */
    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#setPageSize(int)
     */
    @Override
    public void setPageSize(int size) {
        this.pageSize = size;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#getTotalCount(int)
     */
    @Override
    public int getTotalCount() {
        return totalCount;
    }

    /* (non-Javadoc)
     * @see com.ingenta.search.Pageable#setTotalCount(int)
     */
    @Override
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Sets the propertyName and property for this search condition.
     * @param propertyName String
     * @param property Object
     */
    public void setProperty(String propertyName, Object property) {
        this.properties.put(propertyName, property);
    }

    /**
     * Get the property value for given property
     * @param propertyName
     * @return Object
     */
    public Object getProperty(String propertyName) {
        return this.properties.get(propertyName);
    }

    /**
     * Get all properties associated with the Search Condition 
     * @return Map
     */
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * Gets the sort fields for this search. If any have been set, the 
     * default sort field (<code>score</code>) in descending order will
     * be added as the last field in the list.
     * @return the sortFields. This may be empty but will never be 
     * <code>null</code>.
     */
    public List<SortField> getSortFields() {
        if(this.sortFields != null && this.sortFields.size() > 0){
            List<SortField> fields = new ArrayList<SortField>(this.sortFields.size() + 1);
            fields.addAll(this.sortFields);
            fields.add(new SortField(DEFAULT_SORT_FIELD));

            return fields;
        }

        return this.sortFields;
    }

    /**
     * Adds the given sort field to this search's sort fields.
     * @param field The field to be added.
     */
    public void addSortField(SortField field){
        this.sortFields.add(field);
    }

    /**
     * Sets the sort fields for this search.
     * @param fields the sortFields to set. May not be <code>null</code>.
     */
    public void setSortFields(List<SortField> fields) {
        if(fields == null){
            throw new IllegalArgumentException("null sort fields not allowed!");
        }

        this.sortFields = fields;
    }

    /**
     * Removes the sort fields for combined search. 
     */
    public void removeSortFields() {
        this.sortFields = null;
    }

    /**
     * Gets the flag indicating if results are to be highlighted.
     * @return true if Highlighting is switched on.
     */
    public boolean isHighlighting() {
        return isHighlighting;
    }

    /**
     * Sets the flag indicating if results are to be highlighted. This defaults 
     * to <code>true</code>.
     * @param isHighlighting the isHighlighting to set
     */
    public void setHighlighting(boolean isHighlighting) {
        this.isHighlighting = isHighlighting;
    }

    /**
     * Gets the 'user-friendly' expression of the search parameters. 
     * This is a list of lists of <code>ExplanationClause</code>
     * Objects. The outer List will contain one entry for each <code>SearchUnit</code>
     * contained in the original Search. This is intended to be converted into its
     * displayable for by the UI templates.
     * @return the searchExplanation.
     */
    public List<List<ExplanationClause>> getSearchExplanation(){
        if (searchExplanation == null) {
            searchExplanation = new ExplanationTextImpl(this);
        }
        return searchExplanation.getSearchExplanation();
    }

    /**
     * Gets the 'user-friendly' expression of the search parameters as a String.
     * @return the searchExplanation.
     */
    public String getSearchExplanationString(){
        if (searchExplanation == null) {
            searchExplanation = new ExplanationTextImpl(this);
        }
        return searchExplanation.getSearchExplanationString();
    }


    /**
     * @return the paramMap
     */
    public Map<String, String> getParamMap() {
        return paramMap;
    }

    /**
     * @param paramMap the paramMap to set
     */
    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * Sets the 'user-friendly' expression of the search parameters. 
     * This is a list of lists of <code>ExplanationClause</code>
     * Objects. The outer List will contain one entry for each <code>SearchUnit</code>
     * contained in the original Search. This is intended to be converted into its
     * displayable for by the UI templates.
     * @param searchExplanation the searchExplanation to set
     */
    public void setSearchExplanation(ExplanationText searchExplanation) {
        this.searchExplanation = searchExplanation;
    }

    public void setSubscribedIds(List<String> subscribedIds) {
        this.subscribedIds = subscribedIds;
    }

    public List<String> getSubscribedIds() {
        return subscribedIds;
    }

    /**
     * Gets a copy of this search. This is just like a clone
     * but it avoids the need to cast and to handle the silly
     * <code>CloneNotSupportedException</code>.
     * @return A copy of this search.
     */
    public Search getCopy(){
        Search copy = new Search(this.getSearchUnits());
        copy.setCurrentPage(this.getCurrentPage());
        copy.setPageSize(this.getPageSize());
        copy.setTotalCount(this.getTotalCount());

        List<SortField> sortFields = this.getSortFields();
        if(sortFields != null){
            copy.setSortFields(sortFields);
        }

        copy.setHighlighting(this.isHighlighting());
        copy.setSearchExplanation(this.searchExplanation);

        return copy;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder(this.getClass().getName());
        buff.append("[currentPage: ");
        buff.append(currentPage);
        buff.append("|totalCount: ");
        buff.append(totalCount);
        buff.append("|sortFields: ");
        buff.append(sortFields);
        buff.append("|isHighlighting: ");
        buff.append(isHighlighting);
        buff.append(this.getAsString());
        buff.append("]");

        return buff.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + currentPage;
        result = prime * result + (isHighlighting ? 1231 : 1237);
        result = prime * result + pageSize;
        result = prime * result + ((searchUnits == null) ? 0 : searchUnits.hashCode());
        result = prime * result + ((sortFields == null) ? 0 : sortFields.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + totalCount;

        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Search other = (Search) obj;
        if (currentPage != other.currentPage)
            return false;
        if (isHighlighting != other.isHighlighting)
            return false;
        if (pageSize != other.pageSize)
            return false;
        if (searchUnits == null) {
            if (other.searchUnits != null)
                return false;
        }
        else if (!searchUnits.equals(other.searchUnits))
            return false;
        if (sortFields == null) {
            if (other.sortFields != null)
                return false;
        }
        else if (!properties.equals(other.properties))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        }
        else if (!sortFields.equals(other.sortFields))
            return false;
        if (totalCount != other.totalCount)
            return false;
        return true;
    }
    
    /* (non-Javadoc)
     *this method is to check newness of search before storing in the session.
     *It only checks values which are required to check newness per user session.
     *if you want to compare to two search please @see Search#equals(java.lang.Object)
     *
     **/
    public boolean isNew(Search obj) {
        if (this == obj)
            return false;
        if (obj == null)
            return true;
        if (getClass() != obj.getClass())
            return true;
        Search other = obj;
        if (searchUnits == null) {
            if (other.searchUnits != null)
                return true;
        } else if (!searchUnits.equals(other.searchUnits)) {
           return true;
        }
        return false;
    }

   /**
    * @return the isPublisherSpecific
    */
   public boolean isPublisherSpecific() {
      return isPublisherSpecific;
   }

   /**
    * @param isPublisherSpecific the isPublisherSpecific to set
    */
   public void setPublisherSpecific(boolean isPublisherSpecific) {
      this.isPublisherSpecific = isPublisherSpecific;
   }

   /**
    * @return the publisherId
    */
   public String getPublisherId() {
      return publisherId;
   }

   /**
    * @param publisherId the publisherId to set
    */
   public void setPublisherId(String publisherId) {
      this.publisherId = publisherId;
   }

   /**
    * @return the isPublisherExcluded
    */
   public boolean isPublisherExcluded() {
      return isPublisherExcluded;
   }

   /**
    * @param isPublisherExcluded the isPublisherExcluded to set
    */
   public void setPublisherExcluded(boolean isPublisherExcluded) {
      this.isPublisherExcluded = isPublisherExcluded;
   }

   /**
    * @return the excludedPublisherIds
    */
   public List<String> getExcludedPublisherIds() {
      return excludedPublisherIds;
   }

   /**
    * @param excludedPublisherIds the excludedPublisherIds to set
    */
   public void setExcludedPublisherIds(List<String> excludedPublisherIds) {
      this.excludedPublisherIds = excludedPublisherIds;
   }

}
