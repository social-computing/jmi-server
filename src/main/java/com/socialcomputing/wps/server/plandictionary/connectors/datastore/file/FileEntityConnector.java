package com.socialcomputing.wps.server.plandictionary.connectors.datastore.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.DatastoreEntityConnector;

public abstract class FileEntityConnector extends DatastoreEntityConnector {
	public String m_fileUrl = null;
	protected InputStream m_Stream = null;
	
	public FileEntityConnector(String name) {
		super( name);
	}

	@Override
	public void _readObject(org.jdom.Element element) {
		super._readObject( element);
		Element connection = element.getChild("URL-connection");
		m_fileUrl = connection.getChildText("url");
	}

	@Override
	public void openConnections( int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
		super.openConnections( planType, wpsparams);

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
	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		super.closeConnections();
		if (m_Stream != null) {
			try {
				m_Stream.close();
			} catch (IOException e) {
			}
			m_Stream = null;
		}
	}

}
