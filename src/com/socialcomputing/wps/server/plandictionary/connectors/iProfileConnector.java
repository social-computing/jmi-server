package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;
import java.util.Hashtable;

public interface iProfileConnector
{
   public abstract  String getName(  );

   public abstract  String getDescription(  );

   public abstract  iAttributeEnumerator getEnumerator( String entityId ) throws WPSConnectorException;

   public abstract iIdEnumerator getExclusionEnumerator( String entityId) throws WPSConnectorException;

   public abstract  Hashtable getAnalysisProperties( String attributeId, String entityId ) throws WPSConnectorException;

   public abstract  Hashtable getProperties( String attributeId, boolean bInBase, String entityId ) throws WPSConnectorException;

   public abstract  iSubAttributeConnector getSubAttribute() throws WPSConnectorException;

   public abstract Collection<iSelectionConnector> getSelections() throws WPSConnectorException;

   public abstract  iSelectionConnector getSelection( String selectionId) throws WPSConnectorException;
}
