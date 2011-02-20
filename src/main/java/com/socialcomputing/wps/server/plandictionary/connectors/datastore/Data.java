package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

import java.util.Hashtable;
import java.util.Map.Entry;

public abstract class Data {
	protected String m_Id;
	protected Hashtable<String, Object> m_Properties;
	
	public Data( String id) {
		m_Id = id;
		m_Properties = new Hashtable<String, Object>();
		addProperty( "id", id);
	}

	public String getId() {
		return m_Id;
	}
	
	public Hashtable<String, Object> getProperties() {
		return m_Properties;
	}
	
	public void addProperty( String name, Object value) {
		m_Properties.put( name, value);
	}
	
   @Override
    public String toString() {
        StringBuilder sb = 
            new StringBuilder().append(this.m_Id)
                               .append(" {");
        
        // Adding list of properties to the list of displayed values
        for(Entry<String, Object> property : this.m_Properties.entrySet()) {
            sb.append(property.getKey()).append(" => ")
              .append(property.getValue()).append(", ");
        }
        
        sb.append("}");
        return sb.toString();
    }
}
