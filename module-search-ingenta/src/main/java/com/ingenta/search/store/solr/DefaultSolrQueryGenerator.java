package com.ingenta.search.store.solr;

import com.ingenta.search.store.QueryGenerator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A class to load the appropriate QueryGenerator from system property. 
 * If it does not exist then default to using SolrQueryGenerator
 * 
 * @author Talvinder Matharu
 */
public abstract class DefaultSolrQueryGenerator  {

    private static final String PROPERTY_NAME = "pub2web.search.queryGenerator";

    private static Logger logger = Logger.getLogger(QueryGenerator.class);

    public static QueryGenerator getInstance() 	{


        QueryGenerator instance = null;
        if (instance == null) 		{
            // no meaningful default
            String generatorClass = System.getProperty(PROPERTY_NAME);
            if (generatorClass == null) {
                logger.log(Level.DEBUG, PROPERTY_NAME+" system property not set");
                instance = new SolrQueryGenerator();
                return instance;
            }

            try {
                Class c = Thread.currentThread().getContextClassLoader().loadClass(generatorClass);
                instance = (QueryGenerator)c.newInstance();
                logger.log(Level.DEBUG, "Created "+generatorClass+" QueryGenerator instance");
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("QueryGenerator class "+generatorClass+" cannot be loaded", e);
            } catch (InstantiationException e)  {
                throw new IllegalArgumentException("QueryGenerator class "+generatorClass+" cannot be instantiated (must have zero-args constructor)", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("QueryGenerator class "+generatorClass+" constructor cannot be accessed", e);
            }

        }

        return instance;
    }

}