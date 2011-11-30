package com.socialcomputing.wps.server.planDictionnary.connectors.datastore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;

import com.socialcomputing.wps.server.planDictionnary.connectors.AttributeEnumeratorItem;

public class StoreHelper {

    protected Hashtable<String, Entity> m_Entities = new Hashtable<String, Entity>();
    protected Hashtable<String, Attribute> m_Attributes = new Hashtable<String, Attribute>();
    
    protected Set<PropertyDefinition> entityProperties = new HashSet<PropertyDefinition>();
    protected Set<PropertyDefinition> attributeProperties = new HashSet<PropertyDefinition>();

    protected Hashtable<String, String> m_Globals = new Hashtable<String, String>();

    public void addGlobal(String id, String value) {
        m_Globals.put( id, value);
    }
    
    public Hashtable<String, Entity> getEntities() {
        return m_Entities;
    }
    public Hashtable<String, Attribute> getAttributes() {
        return m_Attributes;
    }
    public Entity getEntity(String id) {
        return m_Entities.get(id);
    }

    public Entity addEntity(String id) {
        Entity entity = getEntity(id);
        if (entity == null) {
            entity = new Entity(id);
            m_Entities.put(id, entity);
        }
        return entity;
    }

    public void removeEntity(String id) {
        if( id != null) {
            for( Attribute attribute : m_Attributes.values()) {
                if( attribute.getEntities().contains( id)) {
                    attribute.getEntities().remove( id);
                }
            }
            m_Entities.remove( id);
        }
    }
    
    public Attribute getAttribute(String id) {
        return m_Attributes.get(id);
    }

    public Attribute addAttribute(String id) {
        Attribute attribute = getAttribute(id);
        if (attribute == null) {
            attribute = new Attribute(id);
            m_Attributes.put(id, attribute);
        }
        return attribute;
    }

    public void addAttributeProperties(Entity entity) {
        for (PropertyDefinition propDefinition : entityProperties) {
            if (!propDefinition.isSimple()) {
                ArrayList<String> property = new ArrayList<String>();
                for (AttributeEnumeratorItem attributeItem : entity.getAttributes()) {
                    Attribute attribute = m_Attributes.get(attributeItem.m_Id);
                    if( attribute != null) {
                        String value = (String) attribute.getProperties().get(propDefinition.getId());
                        if (value != null)
                            property.add(value);
                    }
                }
                entity.addProperty(propDefinition.getName(), property.toArray(new String[property.size()]));
            }
        }
    }
        
   public void addEntityProperties(Attribute attribute) {
        for (PropertyDefinition propDefinition : attributeProperties) {
            if (!propDefinition.isSimple()) {
                ArrayList<String> property = new ArrayList<String>();
                for (String entityId : attribute.getEntities()) {
                    Entity entity = m_Entities.get(entityId);
                    if( entity != null) {
                        String value = (String) entity.getProperties().get(propDefinition.getId());
                        if (value != null)
                            property.add(value);
                    }
                }
                attribute.addProperty(propDefinition, property.toArray(new String[property.size()]));
            }
        }
    }

   public String toJson() {
       StringBuilder sb = new StringBuilder( "{\"entities\":[");
       boolean first = true;
       for( Entity entity : getEntities().values()) {
           if( first) first = false;
           else sb.append(',');
           entity.toJson(sb);
       }
       sb.append("],\"attributes\" : [");
       first = true;
       for( Attribute attribute : getAttributes().values()) {
           if( first) first = false;
           else sb.append(',');
           attribute.toJson(sb);
       }
       sb.append("],\"globals\" : {");
       first = true;
       for( Entry<String, String> global : m_Globals.entrySet()) {
           if( first) first = false;
           else sb.append(',');
           sb.append( "\"").append(Data.toJson(global.getKey())).append("\":\"").append(Data.toJson(global.getValue())).append("\"");
       }
       sb.append("}}");
       return sb.toString();
   }
}
