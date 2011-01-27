package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;
import java.util.Hashtable;

public interface iEntityConnector
{
   public abstract  String getName(  );

   public abstract  String getDescription(  );

   public abstract  void openConnections( Hashtable wpsparams) throws WPSConnectorException;

   public abstract  void closeConnections(  ) throws WPSConnectorException;

/**
  * Load the entity properties (image, age, income, ...). */
   public abstract  Hashtable getProperties( String entityId ) throws WPSConnectorException;

   public abstract  iIdEnumerator getEnumerator() throws WPSConnectorException;

/**
  * Retrieve a collection of interface iAffinityGroupReader    */
   public abstract  Collection getAffinityGroupReaders(  ) throws WPSConnectorException;

   public abstract  iAffinityGroupReader getAffinityGroupReader( String affGrpReader ) throws WPSConnectorException;

/**
  * Retrieve a collection of interface iProfileConnector    */
   public abstract  Collection getProfiles(  ) throws WPSConnectorException;

   public abstract  iProfileConnector getProfile( String profile ) throws WPSConnectorException;

   public abstract  Collection getClassifiers(  ) throws WPSConnectorException;

   public abstract  iClassifierConnector getClassifier( String classifierId ) throws WPSConnectorException;

   public abstract Collection getSelections() throws WPSConnectorException;

   public abstract  iSelectionConnector getSelection( String selectionId) throws WPSConnectorException;
}
