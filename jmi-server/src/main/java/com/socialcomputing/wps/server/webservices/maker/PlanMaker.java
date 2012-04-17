package com.socialcomputing.wps.server.webservices.maker;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

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
    public static final String PLAN_MIME    = "mime";
    public static final String PLAN_NAME    = "name";
    public static final String PLAN         = "map";
    public static final String TYPE         = "type";
    public static final String DURATION     = "duration";
    
	public java.util.Hashtable<String, Object> createPlan( java.util.Hashtable<String, Object> params) throws JMIException;
}