package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

public class AttributePropertyDefinition {

	protected String m_Name, m_Entity;
	
	public AttributePropertyDefinition(String name, String entity) {
		super();
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
