package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.Collection;
import java.util.Hashtable;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSubAttributeConnector;

public class XmlProfileConnector implements iProfileConnector {
	protected String m_Name = WPSDictionary.DEFAULT_NAME;
	
	static XmlProfileConnector readObject( Element element)
	{
		XmlProfileConnector profile = new XmlProfileConnector();
		return profile;
	}
	
	public XmlProfileConnector() {
	}
	
	@Override
	public String getName() {
		return m_Name;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iEnumerator<AttributeEnumeratorItem> getEnumerator(String entityId) throws WPSConnectorException {
		return new XmlAttributeEnumerator( null, "");
	}

	@Override
	public iEnumerator<String> getExclusionEnumerator(String entityId) throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable getAnalysisProperties(String attributeId, String entityId) throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable getProperties(String attributeId, boolean bInBase, String entityId) throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iSubAttributeConnector getSubAttribute() throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<iSelectionConnector> getSelections() throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iSelectionConnector getSelection(String selectionId) throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

}
