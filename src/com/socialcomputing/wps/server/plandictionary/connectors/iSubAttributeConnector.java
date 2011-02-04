package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Hashtable;

public interface iSubAttributeConnector
{
   public abstract  String getName(  );

   public abstract  String getDescription(  );

   public abstract  iEnumerator<SubAttributeEnumeratorItem> getEnumerator( String entity, String attribute ) throws WPSConnectorException;

   public abstract  Hashtable<String, Object> getProperties( String subAttributeId, String attributeId, String entityId ) throws WPSConnectorException;
}
