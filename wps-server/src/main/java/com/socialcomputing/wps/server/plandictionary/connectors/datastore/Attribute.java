package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

import java.util.ArrayList;
import java.util.List;

public class Attribute extends Data {

	protected List<String> m_Entities = new ArrayList<String>();
	
	public Attribute(String id) {
		super(id);
	}

	public void addProperty(AttributePropertyDefinition definition, Object value) {
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
