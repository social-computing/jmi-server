package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public class UrlHelper extends ConnectorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(UrlHelper.class);

    public enum Type {
        POST, GET;
    }
    
    protected Type type = Type.GET;
    protected String url = null;
    protected List<NameValuePair> defParams = new ArrayList<NameValuePair>();
    protected List<NameValuePair> curParams = new ArrayList<NameValuePair>();
    protected List<NameValuePair> headerParams = new ArrayList<NameValuePair>();
    protected boolean basicAuth = false;
    protected String user, password;

    protected InputStream stream = null;
    protected String contentType = null;
    
    public UrlHelper() {
        super();
    }
    
    public UrlHelper(Type type, String url) {
        super();
        this.type = type;
        this.url = url;
    }

    @Override
    public void readObject(Element element) {
        Element connection = element.getChild( "URL-connection");
        url = connection.getChildText("url");
        if( connection.getAttributeValue( "type") != null && connection.getAttributeValue( "type").equalsIgnoreCase( "POST"))
            type = Type.POST;
        for( Element elem : (List<Element>)connection.getChildren( "url-parameter")) {
            defParams.add( new NameValuePair( elem.getAttributeValue( "name"), elem.getText()));
        }
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
        try {
            String realUrl = super.ReplaceParameter( url, wpsparams);
            boolean first = true;
            if (defParams != null) {
                for( NameValuePair param : defParams) {
                    if( !first) parameters.append( '&');
                    parameters.append( param.getName()).append( '=').append( URLEncoder.encode( super.ReplaceParameter( param.getValue(), wpsparams), "UTF-8"));
                    first = false;
                }
            }
            if (curParams != null) { 
                for( NameValuePair param : curParams) {
                    if( !first) parameters.append( '&');
                    parameters.append( param.getName()).append( '=').append( URLEncoder.encode( super.ReplaceParameter( param.getValue(), wpsparams), "UTF-8"));
                    first = false;
                }
            }
            
            LOG.debug(" url = {}, params = {}", url, parameters.toString());
            URL u = new URL( type == Type.POST || (type == Type.GET && parameters.length() == 0) ? realUrl : realUrl + "?" + parameters.toString());
            URLConnection connection = u.openConnection();
            connection.setUseCaches(false);
            connection.setDoInput(true);
            if( basicAuth) {
                BASE64Encoder enc = new BASE64Encoder();
                String userpassword = user + ":" + password;
                String encodedAuthorization = enc.encode( userpassword.getBytes());
                connection.setRequestProperty( "Authorization", "Basic " + encodedAuthorization);
            }
            for( NameValuePair header : headerParams) {
                connection.setRequestProperty( header.getName(), header.getValue());
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
           contentType = connection.getContentType();
            
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
    
    public List<NameValuePair> getDefParams() {
        return defParams;
    }
    
    public InputStream getStream() {
        return stream;
    }

    public String getContentType() {
        return contentType;
    }
    
    public String getResult() throws WPSConnectorException {
        Writer writer = new StringWriter();
        Reader reader = new BufferedReader( new InputStreamReader( stream));
        char[] buffer = new char[1024];
        int n;
        try {
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }
        catch (IOException e) {
            throw new WPSConnectorException( "UrlHelper read failed",  e);
        }
        return writer.toString();
    }
    
    public void addParameter( String name, String value) {
        curParams.add( new NameValuePair( name, value));
    }
    
    public void addHeader( String name, String value) {
        headerParams.add( new NameValuePair( name, value));
    }
}
