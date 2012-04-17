package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.PropertyDefinition;
import com.socialcomputing.wps.server.planDictionnary.connectors.utils.UrlHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;

public class GoogleEntityConnector extends SocialEntityConnector {

    protected UrlHelper oAuth2Helper;

    static GoogleEntityConnector readObject(org.jdom.Element element) {
        GoogleEntityConnector connector = new GoogleEntityConnector( element.getAttributeValue("name"));
        connector._readObject( element);
        return connector;
    }
    
    public GoogleEntityConnector(String name) {
        super(name);
        oAuth2Helper = new UrlHelper();
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
        oAuth2Helper.readObject( element.getChild( UrlHelper.DTD_DEFINITION));
        for( Element property: (List<Element>)element.getChildren( "Google-property")) {
            attributeProperties.add( new PropertyDefinition( property.getAttributeValue( "id"), property.getAttributeValue( "entity")));
        }
    }


    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws JMIException {
        super.openConnections( planType, wpsparams);
        oAuth2Helper.openConnections( planType, wpsparams);
        JSONObject jobj = ( JSONObject)JSONValue.parse( new InputStreamReader(oAuth2Helper.getStream()));
        System.out.println(" - access_token = " + jobj.get("access_token"));
        
        // Liste contact
        UrlHelper uh = new UrlHelper();
        uh.setUrl( "https://www.google.com/m8/feeds/contacts/default/full");
        uh.addParameter("oauth_token", (String)jobj.get("access_token"));
        uh.addParameter("max-results", "100");
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
            socialHelper.addPerson((String)title.get("$t")).addProperty("name", title.get("$t"));
        }
        
    }


    @Override
    public void closeConnections() throws JMIException {
        super.closeConnections();
        oAuth2Helper.closeConnections();
    }
    
}
