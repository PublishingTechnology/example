/*
 * SearchElement
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.domain;

import com.ingenta.search.xml.XmlSerializable;

import java.io.Serializable;

/**
 * An API for elements from which searches are constructed.
 * A given search is likely to contain many search elements.
 * 
 * @author Mike Bell
 */
public interface SearchElement extends Serializable, XmlSerializable{

   /**
    * Gets a String representation of this <code>SearchElement</code>.
    * This method is intended to be used to construct a string representation
    * of an entire Search and is not to be confused with the standard 
    * <code>toString()</code> method.  
    * @return A String representation of this <code>SearchElement</code>.
    */
   String getAsString();
}
