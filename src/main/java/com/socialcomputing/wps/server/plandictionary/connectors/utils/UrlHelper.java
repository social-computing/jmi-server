package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

import com.socialcomputing.utils.servlet.HtmlEncoder;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public class UrlHelper extends ConnectorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(UrlHelper.class);

    public enum Type {
        POST, GET;
    }
    
    protected Type type = Type.GET;
    protected String url = null;
    protected List<NameValuePair> defParams;
    protected List<NameValuePair> curParams;
    protected boolean basicAuth = false;
    protected String user, password;

    protected InputStream stream = null;

    @Override
    public void readObject(Element element) {
        Element connection = element.getChild("URL-connection");
        url = connection.getChildText("url");
        if( connection.getAttributeValue( "type") != null && connection.getAttributeValue( "type").equalsIgnoreCase( "POST"))
            type = Type.POST;
        defParams = new ArrayList<NameValuePair>();
        for( Element elem : (List<Element>)connection.getChildren( "url-parameter")) {
            defParams.add( new NameValuePair( elem.getAttributeValue( "name"), elem.getText()));
        }
        curParams = new ArrayList<NameValuePair>();
        Element basic = connection.getChild( "basic-authentication");
        if(  basic != null) {
            basicAuth = true;
            user = basic.getAttributeValue( "username");
            password = basic.getAttributeValue( "password");
        }
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        StringBuilder parameters = new StringBuilder();
        boolean first = true;
        if (defParams != null) {
            for( NameValuePair param : defParams) {
                if( !first) parameters.append( '&');
                parameters.append( param.getName()).append( '=').append( HtmlEncoder.encode( super.ReplaceParameter( param.getValue(), wpsparams)));
                first = false;
            }
        }
        if (curParams != null) { 
            for( NameValuePair param : curParams) {
                if( !first) parameters.append( '&');
                parameters.append( param.getName()).append( '=').append( HtmlEncoder.encode( super.ReplaceParameter( param.getValue(), wpsparams)));
                first = false;
            }
        }
        try {
            LOG.debug("  - url = {}, parmas = {}", url, parameters.toString());
            URL u = new URL( type == Type.GET ? url + "?" + parameters.toString() : url);
            URLConnection connection = u.openConnection();
            connection.setUseCaches(false);
            connection.setDoInput(true);
            if( basicAuth) {
                BASE64Encoder enc = new BASE64Encoder();
                String userpassword = user + ":" + password;
                String encodedAuthorization = enc.encode( userpassword.getBytes());
                connection.setRequestProperty( "Authorization", "Basic " + encodedAuthorization);
            }
            if( type == Type.POST && connection instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = ( HttpURLConnection) connection;
                httpConnection.setDoOutput(true);
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpConnection.setRequestProperty("Content-Length", "" + Integer.toString( parameters.toString().getBytes().length));
                DataOutputStream wr = new DataOutputStream( httpConnection.getOutputStream ());
                wr.writeBytes( parameters.toString());
                wr.flush ();
                wr.close ();
           }
           stream = connection.getInputStream();
            
        } catch (IOException e) {
            throw new WPSConnectorException("openConnections: ", e);
        }
    }

    @Override
    public void closeConnections() throws WPSConnectorException {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
            stream = null;
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public InputStream getStream() {
        return stream;
    }

    public void addParameter( String name, String value) {
        if (curParams == null)
            curParams = new ArrayList<NameValuePair>();
        curParams.add( new NameValuePair( name, value));
    }
}
