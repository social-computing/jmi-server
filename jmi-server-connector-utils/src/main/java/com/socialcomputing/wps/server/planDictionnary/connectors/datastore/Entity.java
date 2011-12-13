package com.socialcomputing.wps.server.planDictionnary.connectors.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.socialcomputing.wps.server.planDictionnary.connectors.AttributeEnumeratorItem;

public class Entity extends Data {

	protected List<AttributeEnumeratorItem> m_Attributes = new ArrayList<AttributeEnumeratorItem>();
	
	public Entity(String id) {
		super(id);
	}
	
    public List<AttributeEnumeratorItem> getAttributes() {
        return m_Attributes;
    }
    
	public void addAttribute( Attribute attribute, float ponderation) {
	    AttributeEnumeratorItem item = new AttributeEnumeratorItem( attribute.getId(), ponderation);
	    if( !m_Attributes.contains( item)) {
    		m_Attributes.add( item);
    		attribute.addEntity( this); // reverse pour le groupe d'affinités
	    }
	}
	
	public boolean containsAttribute( Attribute attribute) {
		return containsAttribute( attribute.getId());
	}
	
	public boolean containsAttribute( String attribute) {
		for( AttributeEnumeratorItem item :  m_Attributes) {
			if( item.m_Id.equalsIgnoreCase( attribute))
				return true;
		}
		return false;
	}
	
   @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity: ")
          .append(super.toString());
        for(AttributeEnumeratorItem attribute: this.m_Attributes) {
            sb.append("\n").append("id=").append(attribute.m_Id);
        }
        return sb.toString();
    }
   
   public StringBuilder toJson( StringBuilder sb) {
       sb.append( "{");
       super.toJson(sb);
       sb.append( ",\"attributes\": [");
       boolean first = true;
       for (AttributeEnumeratorItem item : this.m_Attributes) {
           if( first) first = false;
           else sb.append(',');
           sb.append( "{\"id\":\"").append(item.m_Id).append("\"}");
       }
       sb.append("]}");
       return sb;
   }
}
