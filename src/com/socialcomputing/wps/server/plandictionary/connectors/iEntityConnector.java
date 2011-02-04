package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;
import java.util.Hashtable;

public interface iEntityConnector
{
   public abstract  String getName(  );

   public abstract  String getDescription(  );

   public abstract  void openConnections( Hashtable<String, Object> wpsparams) throws WPSConnectorException;

   public abstract  void closeConnections(  ) throws WPSConnectorException;

/**
  * Load the entity properties (image, age, income, ...). */
   public abstract  Hashtable<String, Object> getProperties( String entityId ) throws WPSConnectorException;

   public abstract  iIdEnumerator getEnumerator() throws WPSConnectorException;

/**
  * Retrieve a collection of interface iAffinityGroupReader    */
   public abstract  Collection<iAffinityGroupReader> getAffinityGroupReaders(  ) throws WPSConnectorException;

   public abstract  iAffinityGroupReader getAffinityGroupReader( String affGrpReader ) throws WPSConnectorException;

/**
  * Retrieve a collection of interface iProfileConnector    */
   public abstract  Collection<iProfileConnector> getProfiles(  ) throws WPSConnectorException;

   public abstract  iProfileConnector getProfile( String profile ) throws WPSConnectorException;

   public abstract  Collection<iClassifierConnector> getClassifiers(  ) throws WPSConnectorException;

   public abstract  iClassifierConnector getClassifier( String classifierId ) throws WPSConnectorException;

   public abstract Collection<iSelectionConnector> getSelections() throws WPSConnectorException;

   public abstract  iSelectionConnector getSelection( String selectionId) throws WPSConnectorException;
}
