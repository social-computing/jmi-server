package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

public abstract class FileEntityConnector implements iEntityConnector {
	public String m_Name = null;
	public String m_Description = null;
	public String m_fileUrl = null;
	protected InputStream m_Stream = null;
	protected Hashtable<String, Object> m_WPSParams = null;
	
	protected Hashtable<String, Entity> m_Entities = new Hashtable<String, Entity>();
	protected Hashtable<String, Attribute> m_Attributes = new Hashtable<String, Attribute>();

	protected 	List<FileAffinityGroupReader> 	affinityGroupReaders = new ArrayList<FileAffinityGroupReader>();
	protected 	List<FileProfileConnector> 		profileConnectors = new ArrayList<FileProfileConnector>();
	
	public FileEntityConnector(String name) {
		m_Name = name;
	}

	public void _readObject(org.jdom.Element element) {
		m_Description = element.getChildText("comment");
		Element connection = element.getChild("URL-connection");
		m_fileUrl = connection.getChildText("url");
		
		affinityGroupReaders.add( new FileAffinityGroupReader());
		profileConnectors.add( new FileProfileConnector());
	}

	@Override
	public String getName() {
		return m_Name;
	}

	@Override
	public String getDescription() {
		return m_Description;
	}

	@Override
	public void openConnections(Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		m_WPSParams = wpsparams;
		// TODO Traitement des variables de l'url, POST ou GET des
		// paramètres....
		try {
			URL url = new URL(m_fileUrl);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setUseCaches(false);
			m_Stream = connection.getInputStream();
		} catch (IOException e) {
			throw new WPSConnectorException("", e);
		}
		
		for( FileAffinityGroupReader affinityGroupReader : affinityGroupReaders) {
			affinityGroupReader.openConnections( wpsparams, this);
		}
		for ( FileProfileConnector profileConnector : profileConnectors) {
			profileConnector.openConnections( wpsparams, this);
		}
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		if (m_Stream != null) {
			try {
				m_Stream.close();
			} catch (IOException e) {
			}
			m_Stream = null;
		}
		for( FileAffinityGroupReader affinityGroupReader : affinityGroupReaders) {
			affinityGroupReader.closeConnections();
		}
		for ( FileProfileConnector profileConnector : profileConnectors) {
			profileConnector.closeConnections();
		}
	}

	@Override
	public Hashtable<String, Object> getProperties(String entityId) throws WPSConnectorException {
		return getEntity( entityId).getProperties();
	}

	@Override
	public iEnumerator<String> getEnumerator() throws WPSConnectorException {
		return new DataEnumerator<String>( m_Entities.keySet());
	}

	@Override
	public Collection getAffinityGroupReaders() {
		return affinityGroupReaders;
	}

	@Override
	public iAffinityGroupReader getAffinityGroupReader(String affGrpReader) {
		return affinityGroupReaders.get( 0);
	}

	@Override
	public Collection getProfiles() throws WPSConnectorException {
		return profileConnectors;
	}

	@Override
	public iProfileConnector getProfile(String profile) throws WPSConnectorException {
		return profileConnectors.get( 0);
	}

	@Override
	public Collection<iClassifierConnector> getClassifiers() throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iClassifierConnector getClassifier(String classifierId) throws WPSConnectorException {
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

	protected Entity getEntity( String id) {
		return m_Entities.get( id);
	}
	protected Entity addEntity( String id) {
		Entity entity = new Entity( id);
		m_Entities.put( id, entity);
		return entity;
	}
	protected Attribute getAttribute( String id) {
		return m_Attributes.get( id);
	}
	protected Attribute addAttribute( String id) {
		Attribute attribute = new Attribute( id);
		m_Attributes.put( id, attribute);
		return attribute;
	}
}
