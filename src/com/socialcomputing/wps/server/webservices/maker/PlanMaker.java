package com.socialcomputing.wps.server.webservices.maker;

import java.rmi.RemoteException;
//import java.util.Hashtable;

//import javax.ejb.*;
/**
 * Title:        PlanMaker
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface PlanMaker //extends EJBObject
{
	public java.util.Hashtable createBinaryPlan( java.util.Hashtable params) throws RemoteException;

	public String createXmlPlan( java.util.Hashtable params) throws RemoteException;
}