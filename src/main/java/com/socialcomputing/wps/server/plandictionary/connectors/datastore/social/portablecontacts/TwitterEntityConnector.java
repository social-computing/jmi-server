package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.io.InputStreamReader;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jdom.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import sun.misc.BASE64Encoder;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.OAuthHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper.Type;

public class TwitterEntityConnector extends SocialEntityConnector {

    private static final String FriendsUrl = "http://api.twitter.com/1/friends/ids.json";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    protected UrlHelper oAuth2Helper;

    static TwitterEntityConnector readObject(org.jdom.Element element) {
        TwitterEntityConnector connector = new TwitterEntityConnector( element.getAttributeValue("name"));
        connector._readObject( element);
        return connector;
    }
    
    public TwitterEntityConnector(String name) {
        super(name);
        oAuth2Helper = new UrlHelper();
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
        oAuth2Helper.readObject( element);
        for( Element property: (List<Element>)element.getChildren( "Twitter-property")) {
            attributeProperties.add( new AttributePropertyDefinition( property.getAttributeValue( "id"), property.getAttributeValue( "entity")));
        }
    }


    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections( planType, wpsparams);
        String access_token = oAuth2Helper.openConnectionsTwitter( planType, wpsparams);
        System.out.println("%%%%%% " + access_token);
        
        String oauth_token = null;
        String oauth_token_secret = null;
        String user_id = null;
        String screen_name = null;
        for (String s : access_token.split("&")) {
            if (s.startsWith("oauth_token=")) {
                oauth_token = s.substring(s.indexOf( '=') + 1);                
            } else if (s.startsWith("oauth_token_secret=")) {
                oauth_token_secret = s.substring(s.indexOf( '=') + 1);                
            } else if (s.startsWith("user_id=")) {
                user_id = s.substring(s.indexOf( '=') + 1);                
            } else if (s.startsWith("screen_name=")) {
                screen_name = s.substring(s.indexOf( '=') + 1);                
            }
        }
        
        try {
            OAuthHelper oAuth = new OAuthHelper();
            oAuth.addSignatureParam( "oauth_consumer_key", (String) wpsparams.get("oauth_consumer_key"));
            oAuth.addSignatureParam( "oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam( "oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam( "oauth_token", oauth_token);
            oAuth.addSignatureParam( "oauth_timestamp", String.valueOf( System.currentTimeMillis()/1000));
            oAuth.addSignatureParam( "oauth_version", "1.0");
            oAuth.addSignatureParam( "user_id", user_id);
            oAuth.addSignatureParam( "screen_name", screen_name);
            String signature = oAuth.getSignature(FriendsUrl, "GET");
            
            String secret = (String) wpsparams.get("oauth_consumer_secret") + "&" + oauth_token_secret;
            System.out.println("secret = " + secret);
            String oAuthSignature = oAuth.getOAuthSignature( signature, secret);
            UrlHelper uh = new UrlHelper();
            uh.setUrl(FriendsUrl);
            uh.setType( Type.GET);
            String header = oAuth.getAuthHeader( oAuthSignature);
            uh.addHeader( "Authorization", header);
            uh.openConnections( 0, new Hashtable<String, Object>());
            //System.out.println(uh.getResult());
            JSONArray jobj = (JSONArray) JSONValue.parse(uh.getResult());
            for (int i = 0; i < jobj.size() ; i++) {
                System.out.println(jobj.get(i));
                setFriendShip(user_id, jobj.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
        oAuth2Helper.closeConnections();
    }
       
}
