package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.utils.StringAndFloat;

public class FileAffinityGroupReader implements iAffinityGroupReader {
	protected FileEntityConnector m_FileEntityConnector = null;
	
	static FileAffinityGroupReader readObject( Element element)
	{
		FileAffinityGroupReader grp = new FileAffinityGroupReader();
		return grp;
	}
	
	public void openConnections(Hashtable<String, Object> wpsparams, FileEntityConnector fileEntityConnector)  {
		m_FileEntityConnector = fileEntityConnector;
	}

	public void closeConnections() {
	}
	
	@Override
	public StringAndFloat[] retrieveAffinityGroup(String id, int affinityThreshold, int max) throws WPSConnectorException {
		StringAndFloat[] ret = new StringAndFloat[m_FileEntityConnector.m_Entities.size()]; 
		int i = 0;
		for( String id2 : m_FileEntityConnector.m_Entities.keySet())
			ret[ i++] = new StringAndFloat( id2, 1);
		return ret;
	}

}
