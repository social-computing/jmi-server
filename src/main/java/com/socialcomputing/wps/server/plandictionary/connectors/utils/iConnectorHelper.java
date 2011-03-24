package com.socialcomputing.wps.server.plandictionary.connectors.utils;

import java.util.Hashtable;

import org.jdom.Element;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public interface iConnectorHelper {

    public void readObject(Element element);
    
    /**
     * Open connections to read data from the backend
     * 
     * @param planType
     * @param wpsparams
     * @throws WPSConnectorException
     */
    void openConnections(int planType, Hashtable<String, Object> wpsparams) throws WPSConnectorException;

    /**
     * Close all opened connections
     * 
     * @throws WPSConnectorException
     */
    void closeConnections() throws WPSConnectorException;

}
