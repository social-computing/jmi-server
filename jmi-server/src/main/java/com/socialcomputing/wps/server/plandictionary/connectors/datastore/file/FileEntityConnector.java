package com.socialcomputing.wps.server.plandictionary.connectors.datastore.file;

import java.util.Hashtable;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.planDictionnary.connectors.utils.UrlHelper;
import com.socialcomputing.wps.server.plandictionary.connectors.datastore.DatastoreEntityConnector;

public abstract class FileEntityConnector extends DatastoreEntityConnector {

    protected UrlHelper urlHelper;

    public FileEntityConnector(String name) {
        super(name);
        urlHelper = new UrlHelper();
    }

    @Override
    public void _readObject(org.jdom.Element element) {
        super._readObject(element);
        urlHelper.readObject(element.getChild(UrlHelper.DTD_DEFINITION));
    }

    @Override
    public void openConnections(int planType, Hashtable<String, Object> wpsparams) throws JMIException {
        super.openConnections(planType, wpsparams);
        urlHelper.openConnections(planType, wpsparams);
    }

    @Override
    public void closeConnections() throws JMIException {
        super.closeConnections();
        urlHelper.closeConnections();
    }
}