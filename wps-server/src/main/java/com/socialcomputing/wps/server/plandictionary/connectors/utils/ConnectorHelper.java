package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public abstract class ConnectorHelper {

    public abstract void readObject(Element element);
    
    /**
     * Open connections to read data from the backend
     * 
     * @param planType
     * @param wpsparams
     * @throws WPSConnectorException
     */
    public abstract void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException;

    /**
     * Close all opened connections
     * 
     * @throws WPSConnectorException
     */
    public abstract void closeConnections() throws WPSConnectorException;
    
    public static String ReplaceParameter(String value, Hashtable<String, Object> wpsparams) throws WPSConnectorException {
        StringBuilder result = new StringBuilder();
        StringTokenizer st = new StringTokenizer( value, "{}", false);
        while( st.hasMoreTokens()) {
            String token = st.nextToken();
            if( token.startsWith( "$")) {
                String param = token.substring( 1);
                String val = ( String)wpsparams.get( param);
                if( val == null)
                    val = ( String)wpsparams.get( token);
                if( val == null)
                    throw new WPSConnectorException( "Parameter " + token + " is unknown");
                result.append( val);
            }
            else
                result.append( token);
        }
        return result.toString();
    }

}
