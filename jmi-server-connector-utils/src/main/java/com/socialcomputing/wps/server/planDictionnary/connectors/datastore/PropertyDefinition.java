package com.socialcomputing.wps.server.planDictionnary.connectors.datastore;

/**
 * 
 * @author Franck Valetas
 * @author Jonathan Dray
 *
 */
public class PropertyDefinition {

	private final String m_Name, m_Id;
	
	public PropertyDefinition(String name, String id) {
		this.m_Name = name;
		this.m_Id = id;
	}
	
	public boolean isSimple() {
		return m_Id == null;
	}
	
	public String getName() {
		return m_Name;
	}
	
	public String getId() {
		return m_Id;
	}
}
