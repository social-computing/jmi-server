package com.socialcomputing.wps.server.webservices.maker;

import java.rmi.RemoteException;

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
	public java.util.Hashtable<String, Object> createPlan( java.util.Hashtable<String, String> params) throws RemoteException;
}