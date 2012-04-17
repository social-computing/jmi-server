package com.socialcomputing.wps.server.plandictionary.connectors.datastore.social;

import java.util.Hashtable;

import org.jdom.Element;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.planDictionnary.connectors.datastore.social.SocialHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.DatastoreEntityConnector;

public abstract class SocialEntityConnector extends DatastoreEntityConnector {

    public SocialHelper socialHelper;
    
    public SocialEntityConnector(String name) {
        super(name);
    }

    @Override
    public void _readObject(Element element) {
        super._readObject(element);
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws JMIException {
        super.openConnections(planType, wpsparams);
        socialHelper = new SocialHelper( this);
    }

    @Override
    public void closeConnections() throws JMIException {
        super.closeConnections();
    }
}
