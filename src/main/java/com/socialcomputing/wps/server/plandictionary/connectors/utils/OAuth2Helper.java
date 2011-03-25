package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.hibernate.annotations.Immutable;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.servlet.HtmlEncoder;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public class OAuth2Helper extends ConnectorHelper {
    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Helper.class);

    protected String clientId, clientSecret;
    protected String url;
    protected List<NameValuePair> params;
    protected String token;
    
    @Override
    public void readObject(Element element) {
        url = null;
        token = null;
        Element oAuth2 = element.getChild("oAuth2");
        if( oAuth2 != null) {
            url = oAuth2.getChildText("url");
            clientId = oAuth2.getAttributeValue( "clientId");
            clientSecret = oAuth2.getAttributeValue( "clientSecret");
            params = new ArrayList<NameValuePair>();
            for( Element elem : (List<Element>)oAuth2.getChildren( "url-parameter")) {
                params.add( new NameValuePair( elem.getAttributeValue( "name"), elem.getText()));
            }
        }
    }
    
    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        if( url == null) return;
        StringBuilder sb = new StringBuilder( url);
        sb.append( '?').append( "client_id").append( '=').append( clientId);
        sb.append( '&').append( "client_secret").append( '=').append( clientSecret);
        for( NameValuePair param : params) {
            sb.append( '&').append( param.getName()).append( '=').append( HtmlEncoder.encode( param.getValue()));
        }
        try {
            LOG.debug("  - url = {}", sb.toString());
            URL u = new URL( sb.toString());
            URLConnection connection = u.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            InputStreamReader is = new InputStreamReader( connection.getInputStream());
            char buffer[] = new char[500];
            int n = is.read( buffer);
            token = new String( buffer, 0, n);
            LOG.debug("  - token = {}", token);
            token = token.substring( token.indexOf( '=') + 1);
        } catch (IOException e) {
            throw new WPSConnectorException("", e);
        }
    }
    
    @Override
    public void closeConnections() throws WPSConnectorException {
    }
    
    public String getToken() {
        return token;
    }

}
