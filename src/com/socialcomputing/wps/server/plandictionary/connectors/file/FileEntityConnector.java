package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.util.Collection;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.file.xml.XmlEntityConnector;

public abstract class FileEntityConnector implements iEntityConnector 
{
	public String m_Name = null;
	public String m_Description = null;

	public FileEntityConnector( String name) {
		m_Name = name;
	}
	
	public void _readObject( org.jdom.Element element) {
		m_Description = element.getChildText( "comment");
		
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
	public void openConnections(Hashtable<String, Object> wpsparams)
			throws WPSConnectorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeConnections() throws WPSConnectorException {
		// TODO Auto-generated method stub

	}

	@Override
	public Hashtable getProperties(String entityId)
			throws WPSConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iIdEnumerator getEnumerator() throws WPSConnectorException {
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
