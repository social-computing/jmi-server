package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.util.Hashtable;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;

public class PortableContactsEntityConnector extends SocialEntityConnector {

    static PortableContactsEntityConnector readObject(org.jdom.Element element) {
        PortableContactsEntityConnector connector = new PortableContactsEntityConnector( element.getAttributeValue("name"));
        connector._readObject( element);
        return connector;
    }
    
    public PortableContactsEntityConnector(String name) {
        super(name);
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
    }


    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections(planType, wpsparams);
        
        addPerson( "Franck").addProperty( "name", "Franck");
        addPerson( "Pierre").addProperty( "name", "Pierre");
        addPerson( "Louis").addProperty( "name", "Louis");
        addPerson( "Chantal").addProperty( "name", "Chantal");
        setFriendShip( "Franck", "Pierre");
        setFriendShip( "Franck", "Louis");
        setFriendShip( "Chantal", "Louis");
        //setFriendShip( "Pierre", "Louis");
    }


    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
    }
    
}
