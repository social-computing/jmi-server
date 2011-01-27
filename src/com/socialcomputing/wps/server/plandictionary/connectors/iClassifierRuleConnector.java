package com.socialcomputing.wps.server.plandictionary.connectors;

public interface iClassifierRuleConnector
{
   public abstract  String getName(  );
   public abstract  String getDescription(  );
   public abstract  com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator getEnumerator() throws WPSConnectorException;
}
