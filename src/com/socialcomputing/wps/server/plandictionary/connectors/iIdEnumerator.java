package com.socialcomputing.wps.server.plandictionary.connectors;

public interface iIdEnumerator
{
   public abstract  void next( IdEnumeratorItem item ) throws WPSConnectorException;

   public abstract  boolean hasNext(  ) throws WPSConnectorException;

}
