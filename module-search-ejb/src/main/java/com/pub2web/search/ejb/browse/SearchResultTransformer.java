/*
 * SearchResultTransformer
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.ejb.browse;

import static com.pub2web.rdf.cci.JcaResourceHelper.close;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ingenta.search.store.SearchResult;
import com.pub2web.rdf.cci.RdfStoreConnectionFactory;
import com.pub2web.rdf.cci.RdfStoreLensInteractionSpec;
import com.pub2web.rdf.cci.facet.ContentItem;
import com.pub2web.rdf.cci.facet.FacetSerializer;


/** Helper class that transforms a SearchResult object into a ContentItem, by representing it in RDF/XML and
    calling the RDF connector (configured for an <code>input</code> graph location) to produce a ContentItem.  
    @author Keith Hatton
 */
public class SearchResultTransformer {
   private static final Logger LOG = Logger.getLogger(SearchResultTransformer.class);
   
   private static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
   private static final String NS_SEARCH = "http://search.ingenta.com/ns/";
   
   private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
   private DocumentBuilder db = null;
   private TransformerFactory tf = TransformerFactory.newInstance();
   private Transformer t = null;
   
   private RdfStoreConnectionFactory ds = null;
   private URI lensId = null;
   private URI baseURI = null;
   
   /** Default constructor. */
   public SearchResultTransformer() {
      try {
         db = dbf.newDocumentBuilder();
         t = tf.newTransformer();
      }
      catch (ParserConfigurationException e) {
         LOG.log(Level.ERROR, "unable to create document builder", e);
         Error ex = new ExceptionInInitializerError("unable to create document builder");
         ex.initCause(e);
         throw ex;
      }
      catch (TransformerException e) {
         LOG.log(Level.ERROR, "unable to create transformer", e);
         Error ex = new ExceptionInInitializerError("unable to create transformer");
         ex.initCause(e);
         throw ex;
      }
   }
   
   /** Sets the connection factory for converting SearchResult objects into ContentItem objects.
       @param f the connection factory
    */
   public void setConnectionFactory(RdfStoreConnectionFactory f) {
      LOG.log(Level.DEBUG, String.format("setConnectionFactory(%s)", f));
      ds = f;
   }
   
   /** Sets the lens that should be executed to transform the RDF into a ContentItem.
       @param id the lens identifier
    */
   public void setLensId(URI id) {
      LOG.log(Level.DEBUG, String.format("setLensId(%s)", id));
      lensId = id;
   }
   
   /** Converts the SearchResult into an RDF/XML document.
       @param res the search result
       @return the document
    */
   private Document convertToRdf(SearchResult res) {
      Document doc = db.newDocument();
      
      Element root = doc.createElementNS(NS_RDF, "RDF");
      root.setPrefix("rdf");
      doc.appendChild(root);
      
      Element obj = doc.createElementNS(NS_RDF, "Description");
      obj.setPrefix("rdf");
      
      Attr attr = doc.createAttributeNS(NS_RDF, "about");
      attr.setValue(res.getIdentifier());
      attr.setPrefix("rdf");
      obj.setAttributeNode(attr);
      
      root.appendChild(obj);
      
      for (Map.Entry<String,Object> en : res.getProperties().entrySet()) {
         String name = en.getKey();
         
         if (name.indexOf('_') == -1 || name.equals("_version_"))
            continue;
         
         Object v = en.getValue();
         if (v instanceof Collection) {
            for (Object el : (Collection<?>)v)
               addProperty(root, obj, name, el);
            
         }
         else
            addProperty(root, obj, name, v);
         
      }
      
      return doc;
   }
   
   /** Converts the SearchResult into an RDF/XML document, represented as a String.
       @param res the search result
       @return a String containing the RDF/XML
       @throws javax.xml.transform.TransformerException if it is not possible to run the XSL transformation
    */
   private String convertToRdfAsString(SearchResult res)
         throws TransformerException {
      
      Document doc = convertToRdf(res);
      
      Source src = new javax.xml.transform.dom.DOMSource(doc);
      ByteArrayOutputStream byos = new ByteArrayOutputStream();
      Result out = new javax.xml.transform.stream.StreamResult(byos);
      
      t.transform(src, out);
      
      String xml = new String(byos.toByteArray());
      
      if (LOG.isDebugEnabled()) {
         LOG.debug("Converted " + res + " to " + xml);
      }
      return xml;
   }
   
   /** Adds an item property to the RDF/XML document.
       @param nsContainer the document element in which namespaces should be declared
       @param container the document element that should contain the property
       @param name the property name
       @param value the property value
    */
   private void addProperty(Element nsContainer, Element container, String name, Object value) {
      int i = name.indexOf('_');
      String prefix = name.substring(0, i);
      String ns = NS_SEARCH + prefix + "/";
      String decl = "xmlns:" + prefix;
      
      Element property = container.getOwnerDocument().createElementNS(ns, name.substring(i + 1));
      property.setPrefix(prefix);
      container.appendChild(property);
      
      if (nsContainer.getAttribute(decl).isEmpty())
         nsContainer.setAttribute(decl, ns);

      if (appendXmlLiteral(value, property)) {
         return;
      }
      
      // TODO if value is not a String, set rdf:datatype
      if (!(value instanceof String)) {
         LOG.log(Level.INFO, name + " is not a String: " + value.getClass().getName());
      }
      property.setTextContent(String.valueOf(value));

      container.appendChild(property);
      
   }

   /**
    * Attempts to add the value as an XML literal
    * 
    * @param value the value
    * @param property the property to add it to
    * @return true if the property was a valid piece of XML
    */
   private boolean appendXmlLiteral(Object value, Element property) {
      if (value instanceof String) {
         String v = ((String) value).trim();
         // Although </ doesn't mean it will actually be XML we can try it - if we get an error we fall through to the
         // String case
         if (v.contains("</")) {
            try {
               Document doc = property.getOwnerDocument();
               // It's exceedingly unlikely we will be a well formed single element so we just stick a wrapper element
               // around the outside
               Node fragment = db.parse(new InputSource(new StringReader("<foo>" + v + "</foo>"))).getDocumentElement();
               // And then look at the children
               NodeList children = fragment.getChildNodes();
               property.setAttribute("rdf:parseType", "Literal");
               for (int c = 0 ; c < children.getLength() ; c++) {
                  property.appendChild(doc.importNode(children.item(c), true));
               }
               return true;
            } catch (SAXException e) {
               LOG.log(Level.DEBUG, "Unable to parse " + v + " as XML, treating as text", e);
            } catch (IOException e) {
               LOG.log(Level.DEBUG, "Unable to parse " + v + " as XML, treating as text", e);
            }
         }
      }
      return false;
   }
   
   /** Gets the SearchResult represented as a ContentItem.
       @param res the search result
       @return the content item
    */
   public ContentItem getContentItem(SearchResult res) {
      LOG.log(Level.DEBUG, String.format("getContentItem(%s)", (res == null ? null : res.getIdentifier())));
      
      ContentItem item = null;
      Connection conn = null;
      Interaction ix = null;
      try {
         URI itemId = new URI(res.getIdentifier());
         
         RecordFactory rf = ds.getRecordFactory();
         conn = ds.getConnection();
         
         ix = conn.createInteraction();
         RdfStoreLensInteractionSpec ixspec = new RdfStoreLensInteractionSpec();
         ixspec.setLensId(lensId);
         ixspec.setSerializerId(FacetSerializer.SERIALIZER_ID);
         
         Map<URI,Object> options = new java.util.HashMap<URI,Object>();
         if (baseURI != null)
            options.put(FacetSerializer.OPTION_BASE_URI, baseURI);
         
         options.put(FacetSerializer.OPTION_FACET_PROPERTY_MAPPING, true);
         ixspec.setSerializerOptions(options);
         
         MappedRecord in = rf.createMappedRecord(lensId.toString());
         in.put("item", itemId);
         in.put("model", convertToRdfAsString(res));
         
         MappedRecord out = (MappedRecord)ix.execute(ixspec, in);
         
         item = (ContentItem)out.get(itemId);
      }
      catch (URISyntaxException e) {
         LOG.log(Level.ERROR, "invalid URI", e);
      }
      catch (TransformerException e) {
         LOG.log(Level.ERROR, "unable to transform RDF/XML", e);
      }
      catch (ResourceException e) {
         LOG.log(Level.ERROR, "unable to transform search result", e);
      }
      finally {
         close(null, ix, conn);
      }
      
      return item;
   }
}
