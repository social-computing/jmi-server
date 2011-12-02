package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

public class OAuthHelper {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthHelper.class);
    
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

    public String getOAuthHeader(String uri, String type, String secret) throws java.security.SignatureException, UnsupportedEncodingException {
        String signature = getSignature( uri, type);
        LOG.debug("signature = {}", signature);
        String oAuthSignature = getOAuthSignature( signature, secret);
        LOG.debug("oAuthSignature = {}", oAuthSignature);
        String header = getAuthHeader( oAuthSignature);
        LOG.debug("header = {}", header);
        return header;
    }
    
    protected String getSignature(String uri, String type) throws UnsupportedEncodingException {
        StringBuilder signature = new StringBuilder( type + "&");
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
            //String key2 = key + "&";
            SecretKeySpec signingKey = new SecretKeySpec( key.getBytes(), "HmacSHA1");
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
    
}
