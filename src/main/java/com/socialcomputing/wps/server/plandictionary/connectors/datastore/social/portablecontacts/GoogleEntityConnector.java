package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.Attribute;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.OAuth2Helper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper.Type;

public class GoogleEntityConnector extends SocialEntityConnector {

    protected UrlHelper urlHelper;

    static GoogleEntityConnector readObject(org.jdom.Element element) {
        GoogleEntityConnector connector = new GoogleEntityConnector( element.getAttributeValue("name"));
        connector._readObject( element);
        return connector;
    }
    
    public GoogleEntityConnector(String name) {
        super(name);
        urlHelper = new UrlHelper();
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
        urlHelper.readObject(element);
        for( Element property: (List<Element>)element.getChildren( "Google-property")) {
            attributeProperties.add( new AttributePropertyDefinition( property.getAttributeValue( "id"), property.getAttributeValue( "entity")));
        }
    }


    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections( planType, wpsparams);
        urlHelper.setType(Type.POST);
        urlHelper.openConnections( planType, wpsparams);
        JSONObject jobj = ( JSONObject)JSONValue.parse( new InputStreamReader(urlHelper.getStream()));
        System.out.println(" - access_token = " + jobj.get("access_token"));
        
        // Liste contact
        UrlHelper uh = new UrlHelper();
        uh.setUrl( "https://www.google.com/m8/feeds/contacts/default/full");
        uh.addParameter("oauth_token", (String)jobj.get("access_token"));
        uh.addParameter("alt", "json");
        
        uh.openConnections( planType, wpsparams);
        
        JSONObject all = (JSONObject)JSONValue.parse( new InputStreamReader(uh.getStream()));
        System.out.println(all.toJSONString());
        JSONObject feed = (JSONObject)all.get("feed");
        System.out.println(feed.toJSONString());
        JSONArray array=(JSONArray)feed.get( "entry");
        for (int i = 0 ; i < array.size() ; i++) {
            JSONObject user = (JSONObject) array.get(i);
            JSONObject title = (JSONObject) user.get("title");
            addPerson((String)title.get("$t")).addProperty("name", title.get("$t"));
        }
        
    }


    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
        urlHelper.closeConnections();
    }
    
}
