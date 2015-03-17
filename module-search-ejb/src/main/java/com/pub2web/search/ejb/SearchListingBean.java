/*
 * SearchListingBean
 * 
 * Copyright 2013 Publishing Technology plc
 */
package com.pub2web.search.ejb;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextJndiBeanFactoryLocator;

import com.pub2web.search.api.SearchListingService;
import com.pub2web.search.api.util.DelegatingSearchListingService;


/** EJB implementation of the SearchListingService.
    @author Keith Hatton
 */
public class SearchListingBean
      extends DelegatingSearchListingService
      implements SessionBean {
   
   private static final Logger LOG = Logger.getLogger(SearchListingBean.class);
   
   public void ejbCreate() {
      LOG.log(Level.DEBUG, "ejbCreate()");
      
      ContextJndiBeanFactoryLocator b = new ContextJndiBeanFactoryLocator();
      BeanFactoryReference r = b.useBeanFactory("java:comp/env/BeanFactoryPath");
      BeanFactory f = r.getFactory();
      SearchListingService srv = (SearchListingService)f.getBean("delegate");
      setDelegate(srv);
   }
   
   @Override
   public void ejbActivate() {}

   @Override
   public void ejbPassivate() {}

   @Override
   public void ejbRemove() {}

   @Override
   public void setSessionContext(SessionContext ctx) {}
}
