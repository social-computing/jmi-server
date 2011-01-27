package com.socialcomputing.wps.server.plandictionary.connectors;


public interface iAttributeEnumerator
{
   public abstract void next( AttributeEnumeratorItem item) throws WPSConnectorException;

   public abstract boolean hasNext(  ) throws WPSConnectorException;

}
