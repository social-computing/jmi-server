package com.socialcomputing.wps.server.plandictionary.connectors;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

/**
 * Title:        WPS Connectors
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface iSelectionConnector
{
   public abstract  String getName(  );
   public abstract  String getDescription(  );

   public abstract  boolean isRuleVerified( String id, boolean bInBase, String refEntityId ) throws JMIException;

}