package com.socialcomputing.wps.server.plandictionary.connectors.datastore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.StoreHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

public abstract class DatastoreEntityConnector extends StoreHelper implements iEntityConnector {

    private final String m_Name;

    public String m_Description = null;
    protected Hashtable<String, Object> m_WPSParams = null;
    protected int m_planType;
    
    protected boolean m_inverted = false;

    protected List<DatastoreAffinityGroupReader> affinityGroupReaders = new ArrayList<DatastoreAffinityGroupReader>();
    protected List<DatastoreProfileConnector> profileConnectors = new ArrayList<DatastoreProfileConnector>();

    public DatastoreEntityConnector(String name) {
        this.m_Name = name;
    }

    public void _readObject(org.jdom.Element element) {
        m_Description = element.getChildText("comment");
        affinityGroupReaders.add(new DatastoreAffinityGroupReader());
        profileConnectors.add(new DatastoreProfileConnector());
    }

    @Override
    public String getName() {
        return m_Name;
    }

    @Override
    public String getDescription() {
        return m_Description;
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws JMIException {
        m_planType = planType;
        m_WPSParams = wpsparams;

        for (DatastoreAffinityGroupReader affinityGroupReader : affinityGroupReaders) {
            affinityGroupReader.openConnections(this);
        }
        for (DatastoreProfileConnector profileConnector : profileConnectors) {
            profileConnector.openConnections(this);
        }
    }

    @Override
    public void closeConnections() throws JMIException {
        for (DatastoreAffinityGroupReader affinityGroupReader : affinityGroupReaders) {
            affinityGroupReader.closeConnections();
        }
        for (DatastoreProfileConnector profileConnector : profileConnectors) {
            profileConnector.closeConnections();
        }
    }

    @Override
    public Hashtable<String, Object> getProperties(String entityId) throws JMIException {
        return isInverted() ? getAttribute(entityId).getProperties() : getEntity(entityId).getProperties();
    }

    @Override
    public iEnumerator<String> getEnumerator() throws JMIException {
        return isInverted() ? new DataEnumerator<String>(m_Attributes.keySet()) : new DataEnumerator<String>(m_Entities.keySet());
    }

    @Override
    public Collection getAffinityGroupReaders() {
        return affinityGroupReaders;
    }

    @Override
    public iAffinityGroupReader getAffinityGroupReader(String affGrpReader) {
        return affinityGroupReaders.get(0);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Collection getProfiles() throws JMIException {
        return profileConnectors;
    }

    @Override
    public iProfileConnector getProfile(String profile) throws JMIException {
        return profileConnectors.get(0);
    }

    @Override
    public Collection<iClassifierConnector> getClassifiers() throws JMIException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public iClassifierConnector getClassifier(String classifierId) throws JMIException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<iSelectionConnector> getSelections() throws JMIException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public iSelectionConnector getSelection(String selectionId) throws JMIException {
        // TODO Auto-generated method stub
        return null;
    }

    protected boolean isInverted() {
        return m_inverted;
    }

}
