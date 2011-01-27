package com.socialcomputing.wps.server.affinityengine.scheduler;

import java.rmi.RemoteException;

/**
 * Title:        Affinity Scheduler
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface AffinityScheduler //extends EJBObject
{
	public void deleteNow(java.lang.String plan, java.lang.String entity) throws RemoteException;
	public void updateNow(java.lang.String plan, java.util.HashSet entities) throws RemoteException;
	public void updateAllNow(java.lang.String plan) throws RemoteException;
	public void requestUpdate(java.lang.String plan, java.lang.String entity) throws RemoteException;
	public void requestUpdateAll(java.lang.String plan) throws RemoteException;
}