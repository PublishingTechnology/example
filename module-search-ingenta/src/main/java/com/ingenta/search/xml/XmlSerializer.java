/*
 * SearchXmlSerializer
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.xml;

import com.ingenta.search.domain.Search;

/**
 * An API for classes which can serialize <code>XmlSerializable</code>s 
 * into/from XML.
 * 
 * @author Mike Bell
 */
public interface XmlSerializer {
   
   /**
    * Renders the given <code>XmlSerializable</code> as an XML String.
    * @param serializable The Object to be rendered as XML.
    * @return The XML representation of the Object.
    */
   String toXml(XmlSerializable serializable);
   
   /**
    * Constructs an <code>XmlSerializable</code> from the given XML
    * String.
    * @param xml The XML containing the Object to be de-serialized.
    * @return The reconstituted Object.
    */
   XmlSerializable fromXml(String xml);
   
   /**
    * A convenience method for creating a <code>Search</code> from XML.
    * It removes the need for casting.
    * @param xml The XML containing the Search to be de-serialized.
    * @return The reconstituted Search.
    */
   Search readSearchFromXml(String xml);
}
