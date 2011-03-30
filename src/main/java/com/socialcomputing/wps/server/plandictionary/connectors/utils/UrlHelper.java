package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.servlet.HtmlEncoder;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public class UrlHelper extends ConnectorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(UrlHelper.class);

    protected String url = null;
    protected List<NameValuePair> defParams;
    protected List<NameValuePair> curParams;

    protected InputStream stream = null;

    @Override
    public void readObject(Element element) {
        Element connection = element.getChild("URL-connection");
        url = connection.getChildText("url");
        defParams = new ArrayList<NameValuePair>();
        for( Element elem : (List<Element>)connection.getChildren( "url-parameter")) {
            defParams.add( new NameValuePair( elem.getAttributeValue( "name"), elem.getText()));
        }
        curParams = new ArrayList<NameValuePair>();
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        // TODO POST ou GET 
        StringBuilder sb = new StringBuilder( url);
        boolean first = true;
        if (defParams != null) {
            for( NameValuePair param : defParams) {
                sb.append( first ? '?' : '&').append( param.getName()).append( '=').append( HtmlEncoder.encode( super.ReplaceParameter( param.getValue(), wpsparams)));
                first = false;
            }
        }
        if (curParams != null) { 
            for( NameValuePair param : curParams) {
                sb.append( first ? '?' : '&').append( param.getName()).append( '=').append( HtmlEncoder.encode( super.ReplaceParameter( param.getValue(), wpsparams)));
                first = false;
            }
        }
        try {
            LOG.debug("  - url = {}", sb.toString());
            URL u = new URL( sb.toString());
            URLConnection connection = u.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
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

    public InputStream getStream() {
        return stream;
    }

    public void addParameter( String name, String value) {
        if (curParams == null)
            curParams = new ArrayList<NameValuePair>();
        curParams.add( new NameValuePair( name, value));
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
}
