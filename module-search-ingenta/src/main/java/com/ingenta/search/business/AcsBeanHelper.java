/*
 * AcsBeanHelper
 *
 * Copyright 2009 Publishing Technology PLC.
 */
package com.ingenta.search.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ingenta.acs.core.interfaces.Acs;
import com.ingenta.acs.core.interfaces.AcsHome;
import com.ingenta.acs.core.values.ACSLicence;
import com.pub2web.containerservices.ServiceLocator;
import com.pub2web.user.AuthenticatedSubject;
import com.pub2web.user.Identity;
import com.pub2web.user.IdentityType;

/**
 * Helper-class for getting the Acs bean. Avoids repeated blocks of code.
 * 
 * @author Mike Bell
 */
public class AcsBeanHelper {
   private static final Logger LOG = Logger.getLogger(AcsBeanHelper.class);
   private static final AcsBeanHelper INSTANCE = new AcsBeanHelper();

   private Acs acs;
   
   /**
    * Private constructor - use getInstance() instead.
    */
   private AcsBeanHelper(){
      // No-op
   }
   
   /**
    * Gets an instance of the helper for use.
    * @return The AcsBeanHelper.
    */
   public static AcsBeanHelper getInstance(){
      return INSTANCE;
   }
   
   /**
    * Obtains the ACS session bean remote interface.
    * @return The ACS session bean
    * @throws EJBException if the bean cannot be located or created
    */
   public Acs getAcsBean() throws EJBException{
      LOG.debug("getAcsBean");

      // There's no real harm with this not bein synchronized - you just get two instances of the bean
      if (this.acs == null){
         ServiceLocator serviceLocator = ServiceLocator.getInstance(getInitialContext(), true);
         this.acs = (Acs)serviceLocator.createRemoteObject(AcsHome.JNDI_NAME, AcsHome.class);
      }

      LOG.debug("Successfully got AcsBean");
      return this.acs;
   }
   
   /**
    * Retrieved the subscribed licences for the logged in user.
    * 
    * @param request the request
    * @return the external ids of the licences
    */
   public List<String> getSubscribedIds(HttpServletRequest request) {

      List<String> result = new ArrayList<String>();

      Set<String> identityIds = getIdentities(request);

      Acs acsBean = getAcsBean();

      try {
         Set licences = acsBean.getLicences(identityIds);
         for (Iterator iterator = licences.iterator(); iterator.hasNext();) {
            ACSLicence licence = (ACSLicence) iterator.next();
            String externalId = acsBean.getResource(licence.getResourceId()).getExternalId();
            LOG.log(Level.DEBUG, "ExternalID is: " + externalId + " Licence is: " + licence);
            if (licence.getPropertyMap().containsKey("freeType")) {
               String freeType = (String)licence.getPropertyMap().get("freeType");
               if ("SUBSCRIPTION".equals(freeType)) {
                  result.add(externalId);
               }
            }
         }
      } catch (RemoteException e) {
         LOG.error("Problem reading subscribed titles", e);
      }

      return result;
   }

   /**
    * Retrieved the access token licences for the logged in user.
    * 
    * @param request the request
    * @return the external ids of the licences
    */
   List<ACSLicence> getAccessTokenLicences(HttpServletRequest request){
      Set<ACSLicence> result = new HashSet<ACSLicence>();

      Set<String> identityIds = getIdentities(request);

      Acs acsBean = getAcsBean();

      try {
          Set<ACSLicence> licences = acsBean.getLicences(identityIds, "ENABLED", new Date(), true);
          for (ACSLicence licence : licences) {
              String externalId = acsBean.getResource(licence.getResourceId()).getExternalId();
              LOG.log(Level.DEBUG, "ExternalID is: " + externalId + " Licence is: " + licence);
              if (licence.getType().equals("ACCTOKEN")){
                  result.add(licence);
                  LOG.log(Level.DEBUG, "Accesstoken Licence is: " + licence);
              }
          }
      } catch (RemoteException e) {
         LOG.error("Problem reading access token licences", e);
      }   

      return new ArrayList<ACSLicence>(result);
   }

   private Set<String> getIdentities(HttpServletRequest request) {
      AuthenticatedSubject subject = 
                        AuthenticatedSubject.getAuthenticatedSubject(request.getSession());
      Set<Identity> identities = subject.getIdentities();
      Set<String> identityIds = new HashSet<String>();
      for (Iterator<Identity> iterator = identities.iterator(); iterator.hasNext();) {
         Identity identity = iterator.next();
         if (! identity.getType().equals(IdentityType.GUEST))
            identityIds.add(identity.getId());
      }
      return identityIds;
   }

   
   /**
    * Gets the naming context for JNDI lookups via the ServiceLocator.
    * @return The naming context.
    */
   private InitialContext getInitialContext(){
      LOG.debug("getInitialContext");
      try {
         return new InitialContext();
      }catch(NamingException e){
         throw new EJBException(e);
      }
   }
}
