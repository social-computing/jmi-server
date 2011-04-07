package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.NameValuePair;

import sun.misc.BASE64Encoder;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper.Type;

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
        return String.valueOf( sr2.nextLong());
    }
    
    public void addSignatureParam( String name, String value) {
        signatureParams.add( new NameValuePair( name, value));
    }

    public String getSignature( String uri) {
        StringBuilder signature = new StringBuilder( "POST&");
        signature.append( URLEncoder.encode( uri)).append( "&");
        Collections.sort( signatureParams, new Comparator<NameValuePair>() {
            public int compare(NameValuePair e1, NameValuePair e2) {
                return e1.getName().compareTo(e2.getName());
            }
        });
        boolean first = true;
        for( NameValuePair param : signatureParams) {
            if( !first)
                signature.append( "%26");       
            signature.append( URLEncoder.encode( param.getName())).append( "%3D");
            if( param.getName().equalsIgnoreCase( "oauth_callback"))
                signature.append( URLEncoder.encode( URLEncoder.encode( param.getValue())));
            else
                signature.append( URLEncoder.encode( param.getValue()));
            if( first)
                first = false;
        }
        return signature.toString();
    }

    public String getAuthHeader( String oAuthSignature) {
        StringBuilder header = new StringBuilder( "OAuth ");
        boolean first = true;
        for( NameValuePair param : signatureParams) {
            if( !first)
                header.append( ", ");       
            header.append( param.getName()).append( "=\"").append( URLEncoder.encode( param.getValue())).append( "\"");
            if( first)
                first = false;
        }
        header.append( ", ").append( "oauth_signature").append( "=\"").append( URLEncoder.encode( oAuthSignature)).append( "\"");
        return header.toString();
    }
    
    public String getOAuthSignature(String data, String key) throws java.security.SignatureException {
        try {
            String key2 = key + "&";
            SecretKeySpec signingKey = new SecretKeySpec( key2.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance( "HmacSHA1");
            mac.init( signingKey);

            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            BASE64Encoder enc = new BASE64Encoder();
            return enc.encode( rawHmac);
        }
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
    }
    
    
    public static void main( String[] args ) throws NoSuchAlgorithmException, SignatureException, WPSConnectorException
    {
        String consumer = "7v0Vnjoe1yWD7H40yXp2NA";
        String secret = "sWg9k8F8AFLcKPJ70O76aw7hGj8zmpLVcDz4LD0m4";
        String callback = "http://denis.social-computing.org:8080/wps/social/twitter.jsp";
//        String consumer = "GDdmIQH6jhtmLUypg82g";
//        String secret = "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98";
//        String callback = "http://localhost:3005/the_dance/process_callback?service_provider_id=11";
        OAuthHelper oAuth = new OAuthHelper();
        oAuth.addSignatureParam( "oauth_callback", callback);
        oAuth.addSignatureParam( "oauth_consumer_key", consumer);
        oAuth.addSignatureParam( "oauth_nonce", oAuth.getNonce()); //"QP70eNmVz8jvdPevU3oJD2AfF7R7odC2XJcn4XlZJqk");
        oAuth.addSignatureParam( "oauth_signature_method", "HMAC-SHA1");
        oAuth.addSignatureParam( "oauth_timestamp", String.valueOf( System.currentTimeMillis()/1000));
        oAuth.addSignatureParam( "oauth_version", "1.0");
        String signature = oAuth.getSignature( "https://api.twitter.com/oauth/request_token");
        System.out.println( signature);
        String oAuthSignature = oAuth.getOAuthSignature( signature, secret);
        System.out.println( oAuthSignature);
        
        UrlHelper uh = new UrlHelper();
        uh.setUrl( "https://api.twitter.com/oauth/request_token");
        uh.setType( Type.POST);
        String header = oAuth.getAuthHeader( oAuthSignature);
        System.out.println( header);
        uh.addHeader( "Authorization", header);
        uh.openConnections( 0, new Hashtable<String, Object>());
        String key = uh.getResult();
        System.out.println( key);
    }        
}
