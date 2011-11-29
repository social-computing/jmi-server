package com.socialcomputing.wps.server.planDictionnary.connectors.datastore;

import java.util.ArrayList;
import java.util.List;

public class Attribute extends Data {

	protected List<String> m_Entities = new ArrayList<String>();
	
	public Attribute(String id) {
		super(id);
	}

	public List<String> getEntities() {
	    return m_Entities;
	}
	
	public void addProperty(PropertyDefinition definition, Object value) {
		addProperty(definition.getName(), value);
	}

	public void addEntity(Entity entity) {
		m_Entities.add(entity.getId());
	}
	
	@Override
	public String toString() {
	    return "Attribute: " + super.toString();
	}
}
