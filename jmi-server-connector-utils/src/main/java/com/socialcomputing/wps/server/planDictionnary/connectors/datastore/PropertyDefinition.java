package com.socialcomputing.wps.server.planDictionnary.connectors.datastore;

/**
 * 
 * @author Franck Valetas
 * @author Jonathan Dray
 *
 */
public class PropertyDefinition {

	private final String m_Name, m_Id, m_Default;
	
    public PropertyDefinition(String name, String id) {
        this(name, id, null);
    }
    
	public PropertyDefinition(String name, String id, String def) {
		this.m_Name = name;
		this.m_Id = id;
		this.m_Default = def;
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

    public String getDefault() {
        return m_Default;
    }
}
