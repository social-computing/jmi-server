package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.jdom.Element;
import org.json.simple.JSONValue;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.file.FileEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.OAuth2Helper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper;

public class PortableContactsEntityConnector extends SocialEntityConnector {

    protected UrlHelper urlHelper;
    protected OAuth2Helper oAuth2Helper;

    static PortableContactsEntityConnector readObject(org.jdom.Element element) {
        PortableContactsEntityConnector connector = new PortableContactsEntityConnector( element.getAttributeValue("name"));
        connector._readObject( element);
        return connector;
    }
    
    public PortableContactsEntityConnector(String name) {
        super(name);
        urlHelper = new UrlHelper();
        oAuth2Helper = new OAuth2Helper();
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
        oAuth2Helper.readObject(element);
        urlHelper.readObject(element);
    }


    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections( planType, wpsparams);
        oAuth2Helper.openConnections( planType, wpsparams);
        urlHelper.addParameter( "access_token", oAuth2Helper.getToken());
        urlHelper.openConnections( planType, wpsparams);
        
        Object obj = JSONValue.parse( new InputStreamReader( urlHelper.getStream()));
        
        addPerson( "Franck").addProperty( "name", "Franck");
        addPerson( "Pierre").addProperty( "name", "Pierre");
        addPerson( "Louis").addProperty( "name", "Louis");
        addPerson( "Chantal").addProperty( "name", "Chantal");
        setFriendShip( "Franck", "Pierre");
        //setFriendShip( "Franck", "Louis");
        setFriendShip( "Chantal", "Louis");
        setFriendShip( "Pierre", "Louis");
    }


    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
        urlHelper.closeConnections();
        oAuth2Helper.closeConnections();
    }
    
}
