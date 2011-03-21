package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social;

import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;

public class Person {
    
    private Attribute attribute;
    private Entity entity;
    
    public Person(Entity entity, Attribute attribute) {
        super();
        this.attribute = attribute;
        this.entity = entity;
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void addProperty( String name, Object value) {
        attribute.addProperty( name, value);
        entity.addProperty( name, value);
    }
   
}
