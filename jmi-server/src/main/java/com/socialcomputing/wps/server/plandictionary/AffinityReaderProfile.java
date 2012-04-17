package com.socialcomputing.wps.server.plandictionary;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      MApStan
 * @author Franck Valetas
 * @version 1.0
 */

public class AffinityReaderProfile implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7642036228464769915L;
	public String m_Name = null;
	public String m_Description = null;

	public String m_ReaderRef = null;
	public int m_AffinityThreshold = 60; // between 0 et 100  %
	public int m_AffinityMaxNb = 1000;

	public DefaultAffinityGroupReader m_defaultConnector = null; // For default only

	static public AffinityReaderProfile readObject( org.jdom.Element element) throws org.jdom.JDOMException
	{
		AffinityReaderProfile profile = new AffinityReaderProfile( element.getAttributeValue( "name"));
		profile.m_Description = element.getChildText( "comment");
		profile.m_ReaderRef = element.getAttributeValue( "affinity-reader-ref");
		if( profile.m_ReaderRef == null)
		{
			profile.m_ReaderRef =  WPSDictionary.DEFAULT_NAME; // On utilise le built-in
			profile.m_defaultConnector = new DefaultAffinityGroupReader();
		}
		profile.m_AffinityThreshold = Integer.parseInt( element.getAttributeValue( "threshold"));
		profile.m_AffinityMaxNb = Integer.parseInt( element.getAttributeValue( "max-entities"));
		return profile;
	}

	public AffinityReaderProfile( String name)
	{
		m_Name = name;
	}

	public iAffinityGroupReader getConnector( WPSDictionary dico)  throws JMIException
	{
		if( m_ReaderRef.equalsIgnoreCase(  WPSDictionary.DEFAULT_NAME))
			return m_defaultConnector;
		return dico.m_EntitiesConnector.getAffinityGroupReader( m_ReaderRef);
	}

	public void checkIntegrity( String m, iEntityConnector entities) throws org.jdom.JDOMException, JMIException
	{
		if( m_ReaderRef.equalsIgnoreCase(  WPSDictionary.DEFAULT_NAME))
			return;
		if( entities.getAffinityGroupReader( m_ReaderRef) == null)
			throw new org.jdom.JDOMException( m + ", Affinity Reader Profile '" + m_Name + "', Unknown Affinity Reader '" + m_ReaderRef + "'");
	}
}