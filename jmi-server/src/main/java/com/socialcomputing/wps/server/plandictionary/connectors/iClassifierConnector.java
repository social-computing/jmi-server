package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

public interface iClassifierConnector
{
   public abstract  String getName();
   public abstract  String getDescription();
   public abstract  Collection<iClassifierRuleConnector> getRules() throws JMIException;
   public abstract  iClassifierRuleConnector getRule( String id) throws JMIException;
   public abstract  String getClassification( String id) throws JMIException;
}
