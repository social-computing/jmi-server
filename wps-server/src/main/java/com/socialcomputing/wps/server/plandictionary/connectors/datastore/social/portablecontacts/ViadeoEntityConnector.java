package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.eclipse.jetty.util.log.Log;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Entity;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper;

public class ViadeoEntityConnector extends SocialEntityConnector {

    private final static Logger LOG = LoggerFactory.getLogger(ViadeoEntityConnector.class);
    
    private static final ObjectMapper mapper = new ObjectMapper();

    protected UrlHelper oAuth2Helper;

    static ViadeoEntityConnector readObject(org.jdom.Element element) {
        ViadeoEntityConnector connector = new ViadeoEntityConnector( element.getAttributeValue("name"));
        connector._readObject( element);
        return connector;
    }
    
    public ViadeoEntityConnector(String name) {
        super(name);
        oAuth2Helper = new UrlHelper();
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
        oAuth2Helper.readObject( element);
        for( Element property: (List<Element>)element.getChildren( "Viadeo-property")) {
            attributeProperties.add( new AttributePropertyDefinition( property.getAttributeValue( "id"), property.getAttributeValue( "entity")));
        }
    }


    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections( planType, wpsparams);
        oAuth2Helper.openConnections( planType, wpsparams);
        try {
            JsonNode node = mapper.readTree( oAuth2Helper.getStream());
            String token = node.get( "access_token").getTextValue();
            wpsparams.put( "$access_token", token);
            
            String kind = ( String)wpsparams.get("kind");
            if( kind == null || kind.equalsIgnoreCase( "friends")) {
                // Liste amis
                UrlHelper urlHelper = new UrlHelper();
                urlHelper.setUrl( "https://api.viadeo.com/me/contacts.json");
                urlHelper.addParameter( "limit", "50");
                urlHelper.addParameter( "user_detail", "none");
                urlHelper.addParameter( "access_token", token);
                urlHelper.openConnections( planType, wpsparams);
                node = mapper.readTree(urlHelper.getStream());
                List<String> friendslist = new ArrayList<String>();
                ArrayNode friends = (ArrayNode)node.get( "data");
                do {
                    for( JsonNode friend : friends) {
                        addPerson(friend.get("id").getTextValue()).addProperty("name", friend.get("name").getTextValue());
                        friendslist.add(friend.get("id").getTextValue());
                    }
                    friends = null;
                    String next = node.get( "paging").get( "next").getTextValue();
                    if( next != "") {
                        UrlHelper nextHelper = new UrlHelper();
                        nextHelper.setUrl( next);
                        nextHelper.openConnections( planType, wpsparams);
                        node = mapper.readTree( nextHelper.getStream());
                        friends = (ArrayNode)node.get( "data");
                    }
                } while (friends != null);  
                
                // My self
                UrlHelper uh = new UrlHelper();
                uh.setUrl( "https://api.viadeo.com/me.json");
                uh.addParameter("access_token", token);
                uh.openConnections( planType, wpsparams);
                JsonNode me = mapper.readTree(uh.getStream());
                wpsparams.put( "$MY_VIADEO_ID", me.get("id").getTextValue());
                
                // Les amis entre eux 
                for (int i = 0 ; i < friendslist.size() -1 ; i++) {
                    UrlHelper uh1 = new UrlHelper();
                    uh1.setUrl( "https://api.viadeo.com/" + friendslist.get(i) + "/mutual_contacts.json");
                    uh1.addParameter("access_token", token);
                    uh1.openConnections( planType, wpsparams);
                    node = mapper.readTree( uh1.getStream());
                    friends = (ArrayNode)node.get( "data");
                    while( friends != null) {
                        for( JsonNode friend : friends) {
                            setFriendShip( friendslist.get(i), friend.get("id").getTextValue());
                        }
                        friends = null;
                        String next = node.get( "paging").get( "next").getTextValue();
                        if( next != "") {
                            UrlHelper nextHelper = new UrlHelper();
                            nextHelper.setUrl( next);
                            nextHelper.openConnections( planType, wpsparams);
                            node = mapper.readTree( nextHelper.getStream());
                            friends = (ArrayNode)node.get( "data");
                        }
                    }
                }
                
                // Je suis ami avec tous mes amis
                //setFriendShip((String)me.get("id"), friendslist);

                // Delete entities with only one attribute 
                Set<String> toRemove = new HashSet<String>();
                for( Entity entity : m_Entities.values()) {
                    if( entity.getAttributes().size() == 1) {
                        toRemove.add( entity.getId());
                    }
                }
                for( String id : toRemove) {
                    removeEntity( id);
                }
                
                // AJout des propriétés d'entités sur les attributs
                setEntityProperities();
            }
            else {
                UrlHelper urlHelper = new UrlHelper();
                urlHelper.setUrl( "https://graph.facebook.com/me/friends");
                urlHelper.addParameter( "access_token", token);
                urlHelper.openConnections( planType, wpsparams);
                
                node = mapper.readTree(urlHelper.getStream());
                ArrayNode friends = (ArrayNode)node.get( "data");

                // My self
                UrlHelper uh = new UrlHelper();
                uh.setUrl( "https://graph.facebook.com/me");
                uh.addParameter("access_token", token);
                uh.openConnections( planType, wpsparams);
                JsonNode me = mapper.readTree(uh.getStream());
                wpsparams.put( "$MY_FB_ID", me.get("id").getTextValue());
                friends.add( me);
                
                for( JsonNode friend : friends) {
                    Attribute attribute = addAttribute( friend.get("id").getTextValue());
                    attribute.addProperty( "name", friend.get("name").getTextValue());

                    UrlHelper urlHelper2 = new UrlHelper();
                    urlHelper2.setUrl( "https://graph.facebook.com/" + friend.get("id").getTextValue() + "/" + kind);
                    urlHelper2.addParameter( "access_token", token);
                    urlHelper2.openConnections( planType, wpsparams);
                    
                    JsonNode node2 = mapper.readTree(urlHelper2.getStream());
                    ArrayNode kinds = (ArrayNode)node2.get( "data");
                    for( JsonNode curkind : kinds) {
                        if (curkind.get("id") != null && curkind.get("name") != null) {
                            Entity entity = addEntity( curkind.get("id").getTextValue());
                            entity.addProperty( "name", curkind.get("name").getTextValue());
                            entity.addAttribute( attribute, 1);
                        }
                    }
                }
                // Delete entities with only one attribute 
                Set<String> toRemove = new HashSet<String>();
                for( Entity entity : m_Entities.values()) {
                    if( entity.getAttributes().size() == 1) {
                        toRemove.add( entity.getId());
                    }
                }
                for( String id : toRemove) {
                    removeEntity( id);
                }
                for( Attribute attribute : m_Attributes.values()) {
                    addEntityProperties( attribute);
                }
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new WPSConnectorException( "openConnections", e);
        }
    }


    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
        oAuth2Helper.closeConnections();
    }
    
}