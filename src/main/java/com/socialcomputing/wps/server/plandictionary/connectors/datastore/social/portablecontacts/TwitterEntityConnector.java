package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.AttributePropertyDefinition;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.SocialEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.OAuthHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper.Type;

public class TwitterEntityConnector extends SocialEntityConnector {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterEntityConnector.class);

    private static final String AccessTokenUrl = "https://api.twitter.com/oauth/access_token";
    private static final String RequestTokenUrl = "https://api.twitter.com/oauth/request_token";
    private static final String FriendsIds = "http://api.twitter.com/1/friends/ids.json";
    private static final String FriendsName = "http://api.twitter.com/1/users/show.json";
    private static final String Friendship = "http://api.twitter.com/1/friendships/exists.json";

    protected UrlHelper oAuth2Helper;
    protected String oauthConsumerKey, oauthConsumerSecret, callback;

    static TwitterEntityConnector readObject(org.jdom.Element element) {
        TwitterEntityConnector connector = new TwitterEntityConnector(element.getAttributeValue("name"));
        connector._readObject(element);
        return connector;
    }

    public TwitterEntityConnector(String name) {
        super(name);
        oAuth2Helper = new UrlHelper();
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
        oauthConsumerKey = element.getAttributeValue("consumer-key");
        oauthConsumerSecret = element.getAttributeValue("consumer-secret");
        callback = element.getAttributeValue("callback");
        oAuth2Helper.readObject(element);
        for (Element property : (List<Element>) element.getChildren("Twitter-property")) {
            attributeProperties.add(new AttributePropertyDefinition(property.getAttributeValue("id"), property
                    .getAttributeValue("entity")));
        }
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        super.openConnections(planType, wpsparams);
        String access_token = getTwitterAccessToken(planType, wpsparams);

        String oauth_token = null;
        String oauth_token_secret = null;
        String user_id = null;
        String screen_name = null;
        for (String s : access_token.split("&")) {
            if (s.startsWith("oauth_token=")) {
                oauth_token = s.substring(s.indexOf('=') + 1);
            }
            else if (s.startsWith("oauth_token_secret=")) {
                oauth_token_secret = s.substring(s.indexOf('=') + 1);
            }
            else if (s.startsWith("user_id=")) {
                user_id = s.substring(s.indexOf('=') + 1);
            }
            else if (s.startsWith("screen_name=")) {
                screen_name = s.substring(s.indexOf('=') + 1);
            }
        }
        addPerson(user_id).addProperty("name", screen_name);

        try {
            OAuthHelper oAuth = new OAuthHelper();
            oAuth.addSignatureParam("oauth_consumer_key", oauthConsumerKey);
            oAuth.addSignatureParam("oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam("oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam("oauth_token", oauth_token);
            oAuth.addSignatureParam("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            oAuth.addSignatureParam("oauth_version", "1.0");
            oAuth.addSignatureParam("user_id", user_id);
            oAuth.addSignatureParam("screen_name", screen_name);
            String secret = oauthConsumerSecret + "&" + oauth_token_secret;
            UrlHelper uh = new UrlHelper(Type.GET, FriendsIds);
            uh.addHeader("Authorization", oAuth.getOAuthHeader(FriendsIds, "POST", secret));
            uh.openConnections(planType, wpsparams);
            JSONArray jobj = (JSONArray) JSONValue.parse(uh.getResult());
            List<String> friendsids = new ArrayList<String>();
            for (int i = 0; i < jobj.size(); i++) {
                friendsids.add(jobj.get(i).toString());
                setFriendShip(user_id, jobj.get(i).toString());
            }
            
            for (int j = 0 ; j < friendsids.size() - 1 ; j++) {
                String id = friendsids.get(j);
                UrlHelper uh1 = new UrlHelper();
                uh1.setUrl(FriendsName);
                uh1.addParameter("user_id", id);
                uh1.openConnections(planType, wpsparams);
                JSONObject user = (JSONObject) JSONValue.parse(uh1.getResult());
                addPerson(id).addProperty("name", user.get("screen_name"));
                
                for (int k = j + 1 ; k < friendsids.size() ; k++) {
                    UrlHelper uh2 = new UrlHelper();
                    uh2.setUrl(Friendship);
                    uh2.addParameter("user_a", id);
                    uh2.addParameter("user_b", friendsids.get(k));
                    uh2.openConnections(planType, wpsparams);
                    if (uh2.getResult().equals("true")) {
                        setFriendShip(id, friendsids.get(k));
                    }
                }
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeConnections() throws WPSConnectorException {
        super.closeConnections();
        oAuth2Helper.closeConnections();
    }

    static public String GetTwitterRequestToken(String oauthConsumerKey, String oauthConsumerSecret, String callback)
            throws WPSConnectorException {
        OAuthHelper oAuth = new OAuthHelper();
        try {
            oAuth.addSignatureParam("oauth_callback", callback);
            oAuth.addSignatureParam("oauth_consumer_key", oauthConsumerKey);
            oAuth.addSignatureParam("oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam("oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            oAuth.addSignatureParam("oauth_version", "1.0");
            UrlHelper uh = new UrlHelper(Type.POST, RequestTokenUrl);
            uh.addHeader("Authorization", oAuth.getOAuthHeader(RequestTokenUrl, "POST", oauthConsumerSecret));
            uh.openConnections(0, new Hashtable<String, Object>());
            return uh.getResult();
        }
        catch (Exception e) {
            throw new WPSConnectorException("getTwitterAccessToken: ", e);
        }
    }

    public String getTwitterAccessToken(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        OAuthHelper oAuth = new OAuthHelper();
        try {
            String secret = oauthConsumerSecret + "&" + (String) wpsparams.get("oauth_token_secret");
            LOG.debug("secret = {}", secret);
            oAuth.addSignatureParam("oauth_consumer_key", oauthConsumerKey);
            oAuth.addSignatureParam("oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam("oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam("oauth_token", (String) wpsparams.get("oauth_token"));
            oAuth.addSignatureParam("oauth_verifier", (String) wpsparams.get("oauth_verifier"));
            oAuth.addSignatureParam("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            oAuth.addSignatureParam("oauth_version", "1.0");
            UrlHelper uh = new UrlHelper(Type.POST, AccessTokenUrl);
            uh.addHeader("Authorization", oAuth.getOAuthHeader(AccessTokenUrl, "POST", secret));
            uh.openConnections(planType, wpsparams);
            return uh.getResult();
        }
        catch (Exception e) {
            throw new WPSConnectorException("getTwitterAccessToken: ", e);
        }
    }
}
