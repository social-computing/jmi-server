package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

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

public class DatastoreProfileConnector implements iProfileConnector {
	protected String m_Name = WPSDictionary.DEFAULT_NAME;
	protected DatastoreEntityConnector m_entityConnector = null;
	
	static DatastoreProfileConnector readObject( Element element)
	{
		DatastoreProfileConnector profile = new DatastoreProfileConnector();
		return profile;
	}
	
	public void openConnections( DatastoreEntityConnector entityConnector)  {
		m_entityConnector = entityConnector;
	}

	public void closeConnections() {
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
        if( m_entityConnector.isInverted()) {
            DataEnumerator<AttributeEnumeratorItem> e = new DataEnumerator<AttributeEnumeratorItem>();
            for( String id : m_entityConnector.getAttribute(entityId).m_Entities) {
                e.m_Collection.add( new AttributeEnumeratorItem( id, 1));
            }
            return e;
        }
        else {
            return new DataEnumerator<AttributeEnumeratorItem>( m_entityConnector.getEntity(entityId).m_Attributes);
        }
	}

	@Override
	public iEnumerator<String> getExclusionEnumerator(String entityId) throws WPSConnectorException {
		return new DataEnumerator<String>();
	}

	@Override
	public Hashtable<String, Object> getAnalysisProperties(String attributeId, String entityId) throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable<String, Object> getProperties(String attributeId, boolean bInBase, String entityId) throws WPSConnectorException {
		return m_entityConnector.isInverted() ? 
                m_entityConnector.getEntity( attributeId).getProperties() 
		        : m_entityConnector.getAttribute( attributeId).getProperties();
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
