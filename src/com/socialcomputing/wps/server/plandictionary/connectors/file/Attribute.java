package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.util.Set;

public class Attribute extends Data {

	public Attribute(String id) {
		super(id);
	}

	public void addProperty( AttributePropertyDefinition definition, Object value) {
		addProperty( definition.m_Name, value);
	}
}
