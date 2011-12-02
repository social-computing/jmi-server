package com.socialcomputing.wps.server.planDictionnary.connectors.datastore.social;

import java.util.HashMap;
import java.util.List;

import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.StoreHelper;

public class SocialHelper {
    
    protected StoreHelper storeHelper;
    protected HashMap<String, Person> persons;


    public SocialHelper( StoreHelper storeHelper) {
        this.storeHelper = storeHelper;
        this.persons = new HashMap<String, Person>();
    }
    
    public Person addPerson( String id) {
        Person person = persons.get( id);
        if( person == null) {
            person = new Person( storeHelper.addEntity(id), storeHelper.addAttribute(id));
            persons.put( id, person);
            setFriendShip( person, person, 1);
        }
        return person;
    }

    public void setFriendShip( String id1, String id2) {
        setFriendShip( addPerson( id1), addPerson( id2), 1); 
    }

    public void setFriendShip( String id, List<String> friends) {
        Person person = addPerson( id);
        for( String friend : friends) {
            setFriendShip( person, addPerson( friend), 1);
        }
    }
    
    public void setEntityProperities() {
        for( Attribute attribute : storeHelper.getAttributes().values()) {
            storeHelper.addEntityProperties( attribute);
        }
    }

    protected void setFriendShip( Person person1, Person person2, float ponderation) {
        person1.getEntity().addAttribute( person2.getAttribute(), ponderation);
        person2.getEntity().addAttribute( person1.getAttribute(), ponderation);
    }
}
