/*
 * FacetElement
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.ingenta.search.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that holds the facet information for a single facet within a search result.
 * It contains a list of fields - each field is one of the facet's constraints along
 * with the count for that constraint within the facet.
 * @author ccsrak / jbeard
 */
public class FacetElement implements Serializable
{
    private String id;
    private List<Field> facetItems = new ArrayList<Field>();

    /**
     * Adds a field to the facet. A field contains the name of the constraint (facet value)
     * and the count for that constraint.
     * @param field field to add to element
     */
    public void addField(Field field)
    {
        if (field.getName().equals("id") || field.getName().equals("name"))
        {
            id = field.getValue();
        }
        facetItems.add(field);
    }

    /**
     * Gets the ID of this facet element (e.g. dcterms_subject)
     * @return facet ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the facet
     * @param name id of facet
     */
    public void setName(String name) {
        id = name;
    }

    /**
     * Gets the list of facet items, ordered on insertion order (typically by count)
     * Each facet item is a field, with name = constraint name, value = count for that constraint
     * @return list of facet items
     */
    public List<Field> getFacetItems(){
        return facetItems; 
    }

    /**
     * Standard toString() method
     */
    public String toString(){
        return facetItems.toString();         
    }
}