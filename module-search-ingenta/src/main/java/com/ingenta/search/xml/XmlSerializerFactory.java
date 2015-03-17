/*
 * XmlSerializerFactory
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.xml;

/**
 * A simple factory class to obtain instances of <code>XmlSerializer</code>.
 * If a different implementation is required, all developers will need to do is
 * to write the new implementation and change this factory class to return it.
 * All other code should be able to stay the same.
 * 
 * @author Mike Bell
 */
public class XmlSerializerFactory {

   /**
    * Gets an instance of <code>XmlSerializer</code>.
    * @return an instance of <code>XmlSerializer</code>.
    */
   public static final XmlSerializer getSerializer(){
      return new XmlSerializerImpl();
   }
}
