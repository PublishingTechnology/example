/*
 * DefaultResultsFactory
 * 
 * Copyright 2011 Publishing Technology plc
 */
package com.ingenta.search.store.solr;

import com.ingenta.search.domain.SearchFailedException;
import com.ingenta.search.store.*;
import com.pub2web.search.api.SearchService;
import org.apache.log4j.Logger;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.util.*;

public class DefaultResultsFactory extends AbstractResultsFactory {
    protected static final String ID = "id";// TODO MB this is masking field from superclass - NLR?

    private static Logger log = Logger.getLogger(DefaultResultsFactory.class);

    /* (non-Javadoc)
     * @see com.ingenta.search.store.AbstractResultsFactory#executeQuery(com.ingenta.search.store.Query)
     */
    @Override
    public SearchResults executeQuery(Query query) throws SearchFailedException {
        if (log.isDebugEnabled()){
            log.debug("executeQuery: " + query);
        }

        RawResult rawResult = doQuery(query);

        return generateResultList(rawResult);
    }


    /* (non-Javadoc)
     * @see com.ingenta.search.store.ResultsFactory#executeQueryForXml(com.ingenta.search.store.Query)
     */
    @Override
    public String executeQueryForXml(Query query) throws SearchFailedException {
        // TODO Auto-generated method stub
        if (log.isDebugEnabled()){
            log.debug("executeQueryForXml: " + query);
        }

        String xmlResult = doQueryForXml(query);

        return xmlResult;

    }

    /* (non-Javadoc)
     * @see com.ingenta.search.store.AbstractResultsFactory#generateResult(com.ingenta.search.store.solr.Document)
     */
    public SearchResult generateResult(Document document) {
        if (log.isDebugEnabled()){
            log.debug("generateResult: " + document);
        }

        Map<String, Field> fields = document.getFields();
        Map<String,Field> highlights = document.getHighlights();
        String id = getField(fields, ID).get(0);
        SearchResult result = new SearchResultImpl(id);

        for (String fieldName : fields.keySet()) {
            setResultProperty(result, document, fieldName, fieldName);
        }

        if ((highlights != null) && isHighlighting()) {
            // TODO MB This is the same loop as above. Could we do it in the same iteration?
            for (String fieldName : fields.keySet()) {
                setHighlights(result, highlights, fieldName);
            }
        }      

        // TODO MB why do we need to set a result property when we've already got it?
        result.setProperty("properties", result.getProperties());

        return result;
    }

    /**
     * @param highlights
     * @param key
     * @return
     */
    protected List<String> getHighlight(Map<String, Field> highlights, String key) {
        return toStringList(highlights, key);
    }

    /**
     * Create List<String> from a field
     * @param fields
     * @param key
     * @return
     */
    protected List<String> getField(Map<String, Field> fields, String key){
        return toStringList(fields, key);
    }

    /**
     * @param fields
     * @param fieldName
     * @return
     */
    private Set<String> getSimilarFieldNames(Map<String, Field> fields, String fieldName ){
        if (log.isDebugEnabled()){
            log.debug("getSimilarFieldNames: " + fieldName);
        }

        Set<String> fieldSet = fields.keySet();
        Set<String> matchedSet = new HashSet<String>();
        for (Iterator<String> iter = fieldSet.iterator(); iter.hasNext();) {
            String element =  iter.next();
            if (element.matches(fieldName)){
                matchedSet.add(element);
            }         
        }
        return matchedSet;     
    }

    /**
     * @param result
     * @param highlights
     * @param fieldName
     */
    private void setHighlights (SearchResult result, Map<String, Field> highlights, String fieldName){
        if (log.isDebugEnabled()){
            log.debug("setHighlights: " + fieldName);
        }

        Set<String> similarFields = getSimilarFieldNames(highlights, fieldName);
        for (Iterator<String> iter = similarFields.iterator(); iter.hasNext();) {
            String name =  iter.next();

            result.setProperty(rationaliseFieldName(name), getHighlight(highlights, name));
            if(log.isDebugEnabled()){
                log.debug("setHighlights setting fieldname " 
                        + rationaliseFieldName(name) + " to :" 
                        + getHighlight(highlights, name));
            }
        }
    }

    /**
     * Executes the given query.
     * @param query The query to execute.
     * @return The results of executing the query.
     */
    private RawResult doQuery(Query query) throws SearchFailedException{
        if (log.isDebugEnabled()){
            log.debug("doQuery");
        }

        try {
            return getSolrClient().search(
                    query.getQuery(), 
                    query.getStart(), 
                    query.getRows(), 
                    query.getFields(), 
                    query.getHighlights());
        }
        catch (RemoteException ex) {
            throw new SearchFailedException("RemoteException calling search on SolrClient", ex);
        }
    }

    /**
     * Executes the given query and return Xml result.
     * @param query The query to execute.
     * @return The results of executing the query.
     */
    private String doQueryForXml(Query query) throws SearchFailedException {
        if (log.isDebugEnabled()){
            log.debug("doQueryForXml");
        }

        try {
            return getSolrClient().searchAsXml(
                    query.getQuery(), 
                    query.getStart(), 
                    query.getRows(), 
                    query.getFields());
        }
        catch (RemoteException ex) {
            throw new SearchFailedException("RemoteException calling searchAsXml on SolrClient", ex);
        }
    }


   /**
    * Looks up the SolrClient in JNDI.
    * 
    * @return the client
    */
   protected SearchService getSolrClient() {
      try {
         return new SolrClientLookupDelegate().getSolrClient();
      }
      catch (NamingException ex) {
         throw new SearchFailedException("NamingException on jndi lookup of SolrClient", ex);
      }
      catch (CreateException ex) {
         throw new SearchFailedException("CreateException on jndi lookup of SolrClient", ex);
      }
      catch (RemoteException ex) {
         throw new SearchFailedException("RemoteException on jndi lookup of SolrClient", ex);
      }
   }
}