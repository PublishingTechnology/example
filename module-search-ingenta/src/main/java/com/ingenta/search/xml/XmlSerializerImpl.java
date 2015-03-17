/*
 * XmlSerializerImpl
 *
 * Copyright 2008 Publishing Technology PLC.
 */
package com.ingenta.search.xml;

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.ingenta.search.domain.OperatorTerm;
import com.ingenta.search.domain.Search;
import com.ingenta.search.domain.SearchCondition;
import com.ingenta.search.domain.SearchExpression;
import com.ingenta.search.domain.SearchUnit;
import com.ingenta.search.domain.ValuePhraseTerm;
import com.ingenta.search.domain.ValueTerm;

/**
 * An implementation which uses the JDK's built in <code>XMLDecoder</code> and
 * <code>XMLEncoder</code> classes to serialize the XML.
 * 
 * @author Mike Bell
 */
public class XmlSerializerImpl implements XmlSerializer{
   
   /**
    * Default constructor made package visible because instances of this class
    * should only be got via the <code>XmlSerializerFactory</code>.
    * @see XmlSerializerFactory
    */
   XmlSerializerImpl(){
      // Default constructor
   }
   
   /* (non-Javadoc)
    * @see com.ingenta.search.XmlSerializer#fromXml(java.lang.String)
    */
   @Override
   public XmlSerializable fromXml(String xml) {
      XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
      try {
         return (XmlSerializable)xmlDecoder.readObject();
      } finally {
         xmlDecoder.close();
      }
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.XmlSerializer#readSearchFromXml(java.lang.String)
    */
   @Override
   public Search readSearchFromXml(String xml) {
      return (Search)this.fromXml(xml);
   }

   /* (non-Javadoc)
    * @see com.ingenta.search.XmlSerializer#toXml(com.ingenta.search.XmlSerializable)
    */
   @Override
   public String toXml(XmlSerializable serializable) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(output));
      try {
         configureXmlEncoder(xmlEncoder);
         xmlEncoder.writeObject(serializable);
         xmlEncoder.close();
         xmlEncoder = null;
         return output.toString();
      } finally {
         if (xmlEncoder != null) {
            xmlEncoder.close();
         }
      }
   }

   /**
    * Configures the <code>DefaultPersistenceDelegate</code> instances for the
    * classes to be encoded. This obviates the need for them to have no-arg constructors.
    * @param xmlEncoder The <code>XMLEncoder</code> to be configured.
    */
   private void configureXmlEncoder(XMLEncoder xmlEncoder) {
      xmlEncoder.setPersistenceDelegate(OperatorTerm.class, 
                        new DefaultPersistenceDelegate(new String[]{"operator"}));
      xmlEncoder.setPersistenceDelegate(SearchCondition.class, 
                        new DefaultPersistenceDelegate(new String[]{"field", "searchTerms"}));
      xmlEncoder.setPersistenceDelegate(SearchExpression.class, 
                        new DefaultPersistenceDelegate(new String[]{"operator", "searchCondition"}));
      xmlEncoder.setPersistenceDelegate(SearchUnit.class, 
                        new DefaultPersistenceDelegate(new String[]{"operator", "searchExpressions"}));
      xmlEncoder.setPersistenceDelegate(ValueTerm.class, 
                        new DefaultPersistenceDelegate(new String[]{"value"}));
      xmlEncoder.setPersistenceDelegate(ValuePhraseTerm.class, 
                        new DefaultPersistenceDelegate(new String[]{"value"}));
   }
}
