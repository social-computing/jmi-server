package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.util.Hashtable;

public abstract class Data {
	protected String m_Id;
	protected Hashtable<String, Object> m_Properties;
	
	public Data( String id) {
		m_Id = id;
		m_Properties = new Hashtable<String, Object>();
	}

	public String getId() {
		return m_Id;
	}
	
	public Hashtable<String, Object> getProperties() {
		return m_Properties;
	}
}
