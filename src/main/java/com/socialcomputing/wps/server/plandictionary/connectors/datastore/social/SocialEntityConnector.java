package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.DatastoreEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;

public class SocialEntityConnector extends DatastoreEntityConnector {
    
    protected HashMap<String, Person> persons;

    public SocialEntityConnector(String name) {
        super(name);
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections(planType, wpsparams);
        persons = new HashMap<String, Person>();
    }

    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
    }
    
    public Person addPerson( String id) {
        Person person = persons.get( id);
        if( person == null) {
            person = new Person( addEntity(id), addAttribute(id));
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

    protected void setFriendShip( Person person1, Person person2, float ponderation) {
        person1.getEntity().addAttribute( person2.getAttribute(), ponderation);
        person2.getEntity().addAttribute( person1.getAttribute(), ponderation);
    }
}
