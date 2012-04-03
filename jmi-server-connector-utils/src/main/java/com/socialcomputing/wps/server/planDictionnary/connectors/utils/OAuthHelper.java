package com.socialcomputing.wps.server.planDictionnary.connectors.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;

import sun.misc.BASE64Encoder;

public class OAuthHelper {
    
    protected List<NameValuePair> signatureParams = new ArrayList<NameValuePair>();
       
    public String getNonce() throws NoSuchAlgorithmException {
        // Create a secure random number generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

        // Get 1024 random bits
        byte[] bytes = new byte[1024/8];
        sr.nextBytes(bytes);


        // Create two secure number generators with the same seed
        int seedByteCount = 10;
        byte[] seed = sr.generateSeed(seedByteCount);

        sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        SecureRandom sr2 = SecureRandom.getInstance("SHA1PRNG");
        sr2.setSeed(seed);
        //return String.valueOf( sr2.nextLong());
        return "1234";
    }
    
    public void addSignatureParam( String name, String value) {
        signatureParams.add( new NameValuePair( name, value));
    }

    // oAuth GET 
    public void addOAuthParams(UrlHelper urh, String uri, String type, String secret) throws java.security.SignatureException, UnsupportedEncodingException {
        String signature = getSignature( uri, type);
        String oAuthSignature = getOAuthSignature( signature, secret);
        signatureParams.add( new NameValuePair("oauth_signature", oAuthSignature));
        Collections.sort( signatureParams, new Comparator<NameValuePair>() {
            public int compare(NameValuePair e1, NameValuePair e2) {
                return e1.getName().compareTo(e2.getName());
            }
        });
        for( NameValuePair param : signatureParams) {
            urh.addParameter(param);
        }
    }
    
    public String getOAuthHeader(String uri, String type, String secret) throws java.security.SignatureException, UnsupportedEncodingException {
        String signature = getSignature( uri, type);
        String oAuthSignature = getOAuthSignature( signature, secret);
        return getAuthHeader( oAuthSignature);
    }

    protected String getSignature(String uri, String type) throws UnsupportedEncodingException {
        StringBuilder signature = new StringBuilder( type + "&");
        signature.append( URLEncoder.encode( uri, "UTF-8").replaceAll("%7E", "~")).append( "&");
        Collections.sort( signatureParams, new Comparator<NameValuePair>() {
            public int compare(NameValuePair e1, NameValuePair e2) {
                return e1.getName().compareTo(e2.getName());
            }
        });
        boolean first = true;
        for( NameValuePair param : signatureParams) {
            if( !first)
                signature.append( "%26");       
            signature.append( URLEncoder.encode( param.getName(), "UTF-8")).append( "%3D");
            if( param.getName().equalsIgnoreCase( "oauth_callback"))
                signature.append( URLEncoder.encode( URLEncoder.encode( param.getValue(), "UTF-8"), "UTF-8"));
            else
                signature.append( URLEncoder.encode( param.getValue(), "UTF-8"));
            if( first)
                first = false;
        }
        return signature.toString();
    }

    protected String getAuthHeader( String oAuthSignature) throws UnsupportedEncodingException {
        StringBuilder header = new StringBuilder( "OAuth ");
        boolean first = true;
        for( NameValuePair param : signatureParams) {
            if( !first)
                header.append( ", ");       
            header.append( param.getName()).append( "=\"").append( URLEncoder.encode( param.getValue(), "UTF-8")).append( "\"");
            if( first)
                first = false;
        }
        header.append( ", ").append( "oauth_signature").append( "=\"").append( URLEncoder.encode( oAuthSignature, "UTF-8")).append( "\"");
        return header.toString();
    }
    
    protected String getOAuthSignature(String data, String key) throws java.security.SignatureException {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            byte[] digest = mac.doFinal(data.getBytes());
            // base64-encode the hmac
            BASE64Encoder enc = new BASE64Encoder();
            return enc.encode( digest);
        }
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
    }
    
    static public OAuthHelper GetOAuth(String key, String token) throws WPSConnectorException {
        OAuthHelper oAuth = new OAuthHelper();
        try {
            oAuth.addSignatureParam("oauth_consumer_key", key);
            oAuth.addSignatureParam("oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam("oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam("oauth_token", token);
            oAuth.addSignatureParam("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            oAuth.addSignatureParam("oauth_version", "1.0");
        }
        catch (Exception e) {
            throw new WPSConnectorException("GetOAuth: ", e);
        }
        return oAuth;
    }
    
    static public Map<String,String> GetAccessToken(String accessTokenUrl, String key, String secret, String authTokenSecret, String token, String verifier) throws WPSConnectorException {
        Map<String,String> result = new HashMap<String,String>();
        OAuthHelper oAuth = new OAuthHelper();
        try {
            oAuth.addSignatureParam("oauth_consumer_key", key);
            oAuth.addSignatureParam("oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam("oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            oAuth.addSignatureParam("oauth_token", token);
            oAuth.addSignatureParam("oauth_verifier", verifier);
            oAuth.addSignatureParam("oauth_version", "1.0");
            UrlHelper uh = new UrlHelper(UrlHelper.Type.POST, accessTokenUrl);
            uh.addHeader("Authorization", oAuth.getOAuthHeader(accessTokenUrl, "POST", secret + "&" + authTokenSecret));
            uh.openConnections();
            for( String p : uh.getResult().split("&")) {
                int pos = p.indexOf('=');
                if( pos != -1) {
                    result.put( p.substring(0, pos), p.substring(pos+1));
                }
            }
        }
        catch (Exception e) {
            throw new WPSConnectorException("GetAccessToken: ", e);
        }
        return result;
    }
    
    static public Map<String,String> GetRequestToken(String requestTokenUrl, String callback, String key, String secret) throws WPSConnectorException {
        Map<String,String> result = new HashMap<String,String>();
        OAuthHelper oAuth = new OAuthHelper();
        try {
            oAuth.addSignatureParam("oauth_callback", callback);
            oAuth.addSignatureParam("oauth_consumer_key", key);
            oAuth.addSignatureParam("oauth_nonce", oAuth.getNonce());
            oAuth.addSignatureParam("oauth_signature_method", "HMAC-SHA1");
            oAuth.addSignatureParam("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            oAuth.addSignatureParam("oauth_version", "1.0");
            UrlHelper uh = new UrlHelper(UrlHelper.Type.POST, requestTokenUrl);
            uh.addHeader("Authorization", oAuth.getOAuthHeader(requestTokenUrl, "POST", secret + "&"));
            uh.openConnections(0, new Hashtable<String, Object>());
            for( String p : uh.getResult().split("&")) {
                int pos = p.indexOf('=');
                if( pos != -1) {
                    result.put( p.substring(0, pos), p.substring(pos+1));
                }
            }
        }
        catch (Exception e) {
            throw new WPSConnectorException("GetRequestToken: ", e);
        }
        return result;
    }
    
}
