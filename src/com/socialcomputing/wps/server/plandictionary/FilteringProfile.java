package com.socialcomputing.wps.server.plandictionary;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
/**
 * Title:        FilteringProfile
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      MAPSTAN
 * @author Franck Valetas
 * @version 1.0
 */

public class FilteringProfile  implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7118300192810843226L;
	public String m_Name = null;
	public String m_Description = null;

	public String m_AttributesRef = null;
	public int m_AffinityThreshold = 60; // between 0 et 100  %
	public int m_AffProfileMaxAttrNb = 1000;
	public String m_TmpDir = null;

	static public FilteringProfile readObject( org.jdom.Element element) throws org.jdom.JDOMException
	{
		FilteringProfile profile = new FilteringProfile( element.getAttributeValue( "name"));
		profile.m_Description = element.getChildText( "comment");
		profile.m_AttributesRef = element.getAttributeValue( "attributes-ref");
		profile.m_AffinityThreshold = Integer.parseInt( element.getAttributeValue( "threshold"));
		profile.m_AffProfileMaxAttrNb = Integer.parseInt( element.getAttributeValue( "max-attribute"));
		profile.m_TmpDir = element.getAttributeValue( "tmp-dir");
		return profile;
	}

	public FilteringProfile( String name)
	{
		m_Name = name;
	}

	public iProfileConnector getConnector( WPSDictionary dico)  throws WPSConnectorException
	{
		return dico.m_EntitiesConnector.getProfile( m_AttributesRef);
	}


	public void checkIntegrity( String m, iEntityConnector entities) throws org.jdom.JDOMException,  WPSConnectorException
	{
		if( entities.getProfile( m_AttributesRef) == null)
			throw new org.jdom.JDOMException( m + ", Filtering Profile '" + m_Name + "', Unknown Attributes '" + m_AttributesRef + "'");
	}

}