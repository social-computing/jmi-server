package com.socialcomputing.wps.server.plandictionary.connectors;

import java.util.Collection;
import java.util.Hashtable;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

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
     * @throws JMIException
     */
    void openConnections(int planType, Hashtable<String, Object> wpsparams) throws JMIException;

    /**
     * Close all opened connections
     * 
     * @throws JMIException
     */
    void closeConnections() throws JMIException;

    /**
     * Load the entity properties (image, age, income, ...).
     * 
     * @param entityId
     * @return
     * @throws JMIException
     */
    Hashtable<String, Object> getProperties(String entityId) throws JMIException;

    /**
     * 
     * @return
     * @throws JMIException
     */
    iEnumerator<String> getEnumerator() throws JMIException;

    /**
     * 
     * @return
     * @throws JMIException
     */
    Collection<iAffinityGroupReader> getAffinityGroupReaders() throws JMIException;

    /**
     * 
     * 
     * @param affGrpReader
     * @return
     * @throws JMIException
     */
    iAffinityGroupReader getAffinityGroupReader(String affGrpReader) throws JMIException;

    /**
     * 
     * 
     * @return
     * @throws JMIException
     */
    Collection<iProfileConnector> getProfiles() throws JMIException;

    /**
     * 
     * @param profile
     * @return
     * @throws JMIException
     */
    iProfileConnector getProfile(String profile) throws JMIException;

    /**
     * 
     * @return
     * @throws JMIException
     */
    Collection<iClassifierConnector> getClassifiers() throws JMIException;

    /**
     * 
     * @param classifierId
     * @return
     * @throws JMIException
     */
    iClassifierConnector getClassifier(String classifierId) throws JMIException;

    /**
     * 
     * @return
     * @throws JMIException
     */
    Collection<iSelectionConnector> getSelections() throws JMIException;

    /**
     * 
     * @param selectionId
     * @return
     * @throws JMIException
     */
    iSelectionConnector getSelection(String selectionId) throws JMIException;
}