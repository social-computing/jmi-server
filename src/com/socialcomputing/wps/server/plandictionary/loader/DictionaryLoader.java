package com.socialcomputing.wps.server.plandictionary.loader;

import java.rmi.RemoteException;

/**
 * Title:        DictionaryLoader
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface DictionaryLoader
{
	public java.lang.String getName() throws RemoteException;
	public com.socialcomputing.wps.server.plandictionary.WPSDictionary getDictionary() throws RemoteException, org.jdom.JDOMException;
	public void setDictionaryDefinition( java.lang.String definition) throws RemoteException, org.jdom.JDOMException;
	public void setDictionaryDefinitionApplyDTD( java.lang.String definition) throws RemoteException, org.jdom.JDOMException;
	public java.lang.String getDictionaryDefinition() throws RemoteException;
	public java.util.Date getNextFilteringDate() throws RemoteException;
	public java.util.Date computeNextFilteringDate() throws RemoteException;
}