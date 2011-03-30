package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
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
        if( oAuth2Helper.getToken() != null)
            urlHelper.addParameter( "access_token", oAuth2Helper.getToken());
        urlHelper.openConnections( planType, wpsparams);
        
        Object obj = JSONValue.parse( new InputStreamReader(urlHelper.getStream()));
        
        JSONObject jobj=(JSONObject)obj;
        
        // Liste amis
        Iterator it = jobj.entrySet().iterator();
        List<String> friendslist = new ArrayList<String>();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            JSONArray array=(JSONArray)entry.getValue();
            for (int i = 0 ; i < array.size() ; i++) {
            //for (int i = 0 ; i < 112 ; i++) {
                JSONObject user = (JSONObject) array.get(i);
                System.out.println(user.get("id") + "=>" + user.get("name"));
                addPerson((String)user.get("id")).addProperty("name", user.get("name"));
                friendslist.add((String)user.get("id"));
            }
        }
        
        // Mes infos
        String url = "https://graph.facebook.com/me";
        UrlHelper uh = new UrlHelper();
        uh.setUrl(url);
        uh.addParameter("access_token", oAuth2Helper.getToken());
        uh.openConnections( planType, wpsparams);
        JSONObject me =  (JSONObject)JSONValue.parse(new InputStreamReader(uh.getStream()));
        addPerson((String)me.get("id")).addProperty("name", me.get("name"));
        
        // Amis d'amis
        for (int i = 0 ; i < friendslist.size() -1 ; i++) {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (int j = i + 1 ; j < friendslist.size() ; j++) {
                sb1.append(friendslist.get(i)).append(",");
                sb2.append(friendslist.get(j)).append(",");
            }
            String areFriends = "https://api.facebook.com/method/friends.areFriends";
            UrlHelper uh1 = new UrlHelper();
            uh1.setUrl(areFriends);
            uh1.addParameter("uids1", sb1.toString());
            uh1.addParameter("uids2", sb2.toString());
            uh1.addParameter("access_token", oAuth2Helper.getToken());
            uh1.addParameter("format", "json");
            uh1.openConnections( planType, wpsparams);
            JSONArray r =  (JSONArray)JSONValue.parse(new InputStreamReader(uh1.getStream()));
            for (int k = 0 ; k < r.size() ; k++) {
                JSONObject rs = (JSONObject) r.get(k);
                System.out.println(rs.get("uid1") + "=>" + rs.get("uid2") + "=>" + rs.get("are_friends"));
                if (rs.get("are_friends") == "true")
                    setFriendShip((String)rs.get("uid1"), (String)rs.get("uid2"));
            }
        }
        
        // Je suis amis avec tous mes amis
        setFriendShip((String)me.get("id"), friendslist);
    }


    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
        urlHelper.closeConnections();
        oAuth2Helper.closeConnections();
    }
    
}
