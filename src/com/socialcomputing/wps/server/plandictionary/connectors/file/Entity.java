package com.socialcomputing.wps.server.plandictionary.connectors.file;

import java.util.ArrayList;
import java.util.List;

import com.socialcomputing.wps.server.plandictionary.connectors.AttributeEnumeratorItem;

public class Entity extends Data {

	protected List<AttributeEnumeratorItem> m_Attributes = new ArrayList<AttributeEnumeratorItem>();
	
	public Entity(String id) {
		super(id);
	}
	
	public void addAttribute( Attribute attribute, float ponderation) {
		m_Attributes.add( new AttributeEnumeratorItem( attribute.getId(), ponderation));
	}
	
	public boolean containsAttribute( Attribute attribute) {
		for( AttributeEnumeratorItem item :  m_Attributes) {
			if( item.m_Id.equalsIgnoreCase( attribute.getId()))
				return true;
		}
		return false;
	}
	
}
