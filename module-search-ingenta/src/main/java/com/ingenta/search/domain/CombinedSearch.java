/*
 * CombinedSearch
 *
 * Copyright 2010 Publishing Technology PLC.
 */

package com.ingenta.search.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Combined Search value object
 * Combined search is new functionality added to Search component
 * @author Sachin Gharat
 *
 */
public class CombinedSearch implements Serializable, Cloneable {

	private Map additionalData;

	/**
	 * 
	 */
	public CombinedSearch() {
		additionalData = new HashMap();
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void setData(String name, Object value) {
		additionalData.put(name, value);
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Object getData(String name) {
		return additionalData.get(name);
	}

	/**
	 * 
	 * @param searches
	 */
	public void setCombinedSearches(Search[] searches) {
		setData("CombinedSearches", searches);
	}

	/**
	 * 
	 * @return
	 */
	public Search[] getCombinedSearches() {
		return (Search[]) getData("CombinedSearches");
	}

	/**
	 * 
	 * @param ops
	 */
	public void setCombinedOperators(String[] ops) {
		setData("CombinedOperators", ops);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getCombinedOperators() {
		return (String[]) getData("CombinedOperators");
	}

}
