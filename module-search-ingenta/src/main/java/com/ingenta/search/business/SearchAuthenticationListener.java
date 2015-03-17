/*
 * SearchAuthenticationListener
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ingenta.acs.core.values.ACSLicence;
import com.pub2web.authentication.event.AuthenticationEvent;
import com.pub2web.authentication.event.AuthenticationListener;


/**
 * AuthenticationListener - keeps the list of subscribed titles within the session up to date. 
 * 
 * @author ccsrak
 */
public class SearchAuthenticationListener implements AuthenticationListener {
    /**
     * Session attribute for subscribed titles
     */
    private static final String SUBSCRIBED_TITLES = "subscribedTitles";
    /**
     * Session attribute for access token licences
     */
    private static final String ACCTOKEN_LICENCES = "accTokenLicences";

    /**
     * {@inheritDoc}
     */
    @Override
    public void identitiesChanged(AuthenticationEvent event) {
        refreshSubscribedTitles(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loggedIn(AuthenticationEvent event) {
        refreshSubscribedTitles(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loggedOut(AuthenticationEvent event) {
        refreshSubscribedTitles(event);
        clearSearchHistory(event);
    }

    private void clearSearchHistory(AuthenticationEvent event) {
        HttpServletRequest request = (HttpServletRequest)event.getSource();
        clearSearchHistory(request);
    }
    
    private void clearSearchHistory(HttpServletRequest request) {
        new SavedSearchDelegate(request.getSession()).getCurrentSearchHistory().clear();
    }

    private void refreshSubscribedTitles(AuthenticationEvent event) {
        HttpServletRequest request = (HttpServletRequest)event.getSource();
        refreshSubscribedTitles(request);
    }

    private void refreshSubscribedTitles(HttpServletRequest request ){
        AcsBeanHelper acs = getAcsBeanHelper();
        
        List<String> subscribedTitles = acs.getSubscribedIds(request);
        request.getSession().setAttribute(SUBSCRIBED_TITLES, subscribedTitles);
        
        List<ACSLicence> accTokenLicences = acs.getAccessTokenLicences(request);
        request.getSession().setAttribute(ACCTOKEN_LICENCES, accTokenLicences);
    }
    
    /**
     * Protected to allow junit testing
     * @return an AcsBeanHelper
     */
    protected AcsBeanHelper getAcsBeanHelper() {
       return AcsBeanHelper.getInstance();
    }
}