package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;
import java.util.Hashtable;

public interface iEntityConnector {
    
    /**
     * Name of the connector
     * 
     * @return the connector's name
     */
    String getName();
    
    /**
     * Description of the connector
     * 
     * @return the connector's description
     */
    String getDescription();

    /**
     * Open connections to read data from the backend
     * 
     * @param planType
     * @param wpsparams
     * @throws WPSConnectorException
     */
    void openConnections(int planType, Hashtable<String, Object> wpsparams)
            throws WPSConnectorException;

    /**
     * Close all opened connections 
     * 
     * @throws WPSConnectorException
     */
    void closeConnections() throws WPSConnectorException;

    /**
     *  Load the entity properties (image, age, income, ...).
     *  
     * @param entityId
     * @return
     * @throws WPSConnectorException
     */
    Hashtable<String, Object> getProperties(String entityId) throws WPSConnectorException;

    /**
     *  
     * @return
     * @throws WPSConnectorException
     */
    iEnumerator<String> getEnumerator() throws WPSConnectorException;

    /**
     * 
     * @return
     * @throws WPSConnectorException
     */
    Collection<iAffinityGroupReader> getAffinityGroupReaders()
            throws WPSConnectorException;

    /**
     * 
     * 
     * @param affGrpReader
     * @return
     * @throws WPSConnectorException
     */
    iAffinityGroupReader getAffinityGroupReader(String affGrpReader)
            throws WPSConnectorException;

    /**
     * 
     * 
     * @return
     * @throws WPSConnectorException
     */
    Collection<iProfileConnector> getProfiles() throws WPSConnectorException;


    /**
     * 
     * @param profile
     * @return
     * @throws WPSConnectorException
     */
    iProfileConnector getProfile(String profile) throws WPSConnectorException;

    /**
     * 
     * @return
     * @throws WPSConnectorException
     */
    Collection<iClassifierConnector> getClassifiers() throws WPSConnectorException;

    /**
     * 
     * @param classifierId
     * @return
     * @throws WPSConnectorException
     */
    iClassifierConnector getClassifier(String classifierId) throws WPSConnectorException;

    /**
     * 
     * @return
     * @throws WPSConnectorException
     */
    Collection<iSelectionConnector> getSelections() throws WPSConnectorException;
    
    /**
     * 
     * @param selectionId
     * @return
     * @throws WPSConnectorException
     */
    iSelectionConnector getSelection(String selectionId) throws WPSConnectorException;
}