package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

/**
 * 
 * @author Franck Valetas
 * @author Jonathan Dray
 *
 */
public class AttributePropertyDefinition {

	private final String m_Name, m_Entity;
	
	public AttributePropertyDefinition(String name, 
	                                   String entity) {
		this.m_Name = name;
		this.m_Entity = entity;
	}
	
	public boolean isSimple() {
		return m_Entity == null;
	}
	
	public String getName() {
		return m_Name;
	}
	
	public String getEntity() {
		return m_Entity;
	}
}
