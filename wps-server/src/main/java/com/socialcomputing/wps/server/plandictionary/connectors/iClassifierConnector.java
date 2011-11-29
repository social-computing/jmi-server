package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;

public interface iClassifierConnector
{
   public abstract  String getName();
   public abstract  String getDescription();
   public abstract  Collection<iClassifierRuleConnector> getRules() throws WPSConnectorException;
   public abstract  iClassifierRuleConnector getRule( String id) throws WPSConnectorException;
   public abstract  String getClassification( String id) throws WPSConnectorException;
}
