package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.file.FileEntityConnector;
import com.socialcomputing.wps.server.utils.StringAndFloat;

public class DatastoreAffinityGroupReader implements iAffinityGroupReader {
	
    private final static Logger LOG = LoggerFactory.getLogger(DatastoreAffinityGroupReader.class);
    
    protected DatastoreEntityConnector m_entityConnector = null;
	
	static DatastoreAffinityGroupReader readObject( Element element)
	{
		DatastoreAffinityGroupReader grp = new DatastoreAffinityGroupReader();
		return grp;
	}
	
	public void openConnections( DatastoreEntityConnector entityConnector)  {
		m_entityConnector = entityConnector;
	}

	public void closeConnections() {
	}
	
	@Override
	public StringAndFloat[] retrieveAffinityGroup(String id, int affinityThreshold, int max) throws WPSConnectorException {
		StringAndFloat[] result = null;
		float maxPond = Float.MIN_VALUE;
		int i = 0;
		switch( m_entityConnector.m_planType)
		{
			case AnalysisProfile.GLOBAL_PLAN:
			    LOG.debug("global plan");
				result = new StringAndFloat[  m_entityConnector.m_Entities.size()];
				for( String id2 : m_entityConnector.m_Entities.keySet()) {
					result[i++] = new StringAndFloat( id2, 1);
				}
				break;
				
			case AnalysisProfile.PERSONAL_PLAN:
			    LOG.debug("Personal plan with id = {}", id);
				Map<String, Integer> set = new HashMap<String, Integer> ();
				for(AttributeEnumeratorItem attributeItem : m_entityConnector.m_Entities.get(id).m_Attributes) {
					for( String entityId2 : m_entityConnector.m_Attributes.get( attributeItem.m_Id).m_Entities) {
						if( set.containsKey( entityId2)) {
							int pond = set.get( entityId2) + 1;
							set.put( entityId2, pond);
							if( pond > maxPond)
								maxPond = pond;
						}
						else {
							set.put( entityId2, 1);
							if( maxPond < 1)
								maxPond = 1;
						}
					}
				}
				result = new StringAndFloat[ set.size()];
				for( Entry<String, Integer> entry : set.entrySet()) {
					result[i++] = new StringAndFloat( entry.getKey(), (maxPond - entry.getValue()) / maxPond);
				}
				break;
				
			case AnalysisProfile.DISCOVERY_PLAN:
			    LOG.debug("discovery plan with id = {}", id);
				Map<String,Integer> set2 = new HashMap<String,Integer>();
				for( String entityId : m_entityConnector.m_Attributes.get( id).m_Entities) {
					if( set2.containsKey( entityId)) {
						int pond = set2.get( entityId) + 1;
						set2.put( entityId, pond);
						if( pond > maxPond)
							maxPond = pond;
					}
					else {
						set2.put( entityId, 1);
						if( maxPond < 1)
							maxPond = 1;
					}
				}
				result = new StringAndFloat[ set2.size()];
				for( Entry<String, Integer> entry : set2.entrySet()) {
					result[i++] = new StringAndFloat( entry.getKey(), (maxPond - entry.getValue()) / maxPond);
				}
				break;
		}
		return result;
	}
}
