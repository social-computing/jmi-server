package com.socialcomputing.wps.server.swatchs.loader;

import java.rmi.RemoteException;

/**
 * Title:        Swatch Loader
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface SwatchLoader
{
	public java.lang.String getName() throws RemoteException;
	public com.socialcomputing.wps.server.swatchs.XSwatch getSwatch() throws RemoteException, org.jdom.JDOMException;
	public void setSwatch(java.lang.String definition) throws RemoteException;
	public java.lang.String getSwatchDefinition() throws RemoteException;
}