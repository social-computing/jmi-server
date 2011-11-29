package com.socialcomputing.wps.server.plandictionary.connectors;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.utils.StringAndFloat;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface iAffinityGroupReader
{
	public  StringAndFloat [] retrieveAffinityGroup( String id, int affinityThreshold, int max) throws WPSConnectorException;
}