package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.util.Hashtable;

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
    
    public static String ReplaceParameter(String value, Hashtable<String, Object> wpsparams) {
        String newValue = value;
        // TODO code nul à améliorer
        if( value.startsWith( "{") && value.endsWith( "}")) {
            if( value.startsWith( "{$")) {
                String param = value.substring( 2, value.length()-1);
                newValue = ( String)wpsparams.get( param);
            }
        }
        return newValue;
    }

}
