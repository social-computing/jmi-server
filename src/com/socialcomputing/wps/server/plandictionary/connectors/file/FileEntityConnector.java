package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Hashtable;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

public abstract class FileEntityConnector implements iEntityConnector 
{
	public String m_Name = null;
	public String m_Description = null;
	public String m_fileUrl = null;
	protected InputStream m_Stream = null;
	protected Hashtable<String, Object> m_WPSParams = null;

	public FileEntityConnector( String name) {
		m_Name = name;
	}
	
	public void _readObject( org.jdom.Element element) {
		m_Description = element.getChildText( "comment");
		Element connection = element.getChild( "URL-connection");
		m_fileUrl = connection.getChildText( "url");
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
		// TODO Traitement des variables de l'url, POST ou GET des paramètres....
		try {
			URL url = new URL( m_fileUrl);
			URLConnection connection = url.openConnection(); 
			connection.setDoInput( true); 
			connection.setUseCaches( false);
			m_Stream = connection.getInputStream(); 
		} catch (IOException e) {
			throw new WPSConnectorException( "", e);
		}
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		if( m_Stream != null) 
		{
			try {
				m_Stream.close();
			} catch (IOException e) {
			}
			m_Stream = null;
		}
	}

	@Override
	public Hashtable getProperties(String entityId)
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iEnumerator<String> getEnumerator() throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<iAffinityGroupReader> getAffinityGroupReaders()
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iAffinityGroupReader getAffinityGroupReader(String affGrpReader)
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<iProfileConnector> getProfiles()
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iProfileConnector getProfile(String profile)
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<iClassifierConnector> getClassifiers()
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iClassifierConnector getClassifier(String classifierId)
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<iSelectionConnector> getSelections()
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iSelectionConnector getSelection(String selectionId)
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

}
