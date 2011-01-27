package com.socialcomputing.wps.server.plandictionary.connectors;


public interface iSubAttributeEnumerator
{
   public abstract  boolean hasNext(  ) throws WPSConnectorException;

   public abstract  void next( SubAttributeEnumeratorItem item) throws WPSConnectorException;

}
