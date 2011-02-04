package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.utils.StringAndFloat;

public class XmlAffinityGroupReader implements iAffinityGroupReader {
	protected String m_EntityId = null;
	protected List<Element> m_Entities = null;
	
	static XmlAffinityGroupReader readObject( Element element)
	{
		XmlAffinityGroupReader grp = new XmlAffinityGroupReader();
		return grp;
	}
	
	public void openConnections(Hashtable<String, Object> wpsparams, String id, List<Element> entities)  {
		m_EntityId = id;
		m_Entities = entities;
	}

	public void closeConnections() {
	}
	
	@Override
	public StringAndFloat[] retrieveAffinityGroup(String id, int affinityThreshold, int max) throws WPSConnectorException {
		StringAndFloat[] result = new StringAndFloat[ m_Entities.size()];
		int i = 0;
		for( Element el : m_Entities) {
			result[i++] = new StringAndFloat( el.getAttributeValue( m_EntityId), 1);
		}
		return result;
	}

}
