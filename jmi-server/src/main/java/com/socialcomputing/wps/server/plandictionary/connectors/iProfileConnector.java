package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;
import java.util.Hashtable;

import com.socialcomputing.wps.server.planDictionnary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

public interface iProfileConnector {
	public abstract String getName();

	public abstract String getDescription();

	public abstract iEnumerator<AttributeEnumeratorItem> getEnumerator(String entityId) throws JMIException;

	public abstract iEnumerator<String> getExclusionEnumerator(String entityId) throws JMIException;

	public abstract Hashtable getAnalysisProperties(String attributeId, String entityId) throws JMIException;

	public abstract Hashtable<String, Object> getProperties(String attributeId, boolean bInBase, String entityId) throws JMIException;

	public abstract iSubAttributeConnector getSubAttribute() throws JMIException;

	public abstract Collection<iSelectionConnector> getSelections() throws JMIException;

	public abstract iSelectionConnector getSelection(String selectionId) throws JMIException;
}
