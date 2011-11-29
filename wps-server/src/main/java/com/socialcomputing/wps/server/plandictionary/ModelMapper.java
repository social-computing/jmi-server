package com.socialcomputing.wps.server.plandictionary;

import java.util.Enumeration;
import java.util.Hashtable;

import com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException;

/**
 * Title:        Plan Dictionary
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class ModelMapper implements java.io.Serializable
{
	static final long serialVersionUID = 161215456465787774L;

	/**
	* The classifier association map (AnalysisProfile / LanguageMapper) */
	private Hashtable m_ModelData = null;
	private LanguageMapper m_DefaultLanguageMapper = null;

	static public ModelMapper readObject( org.jdom.Element element)
	{
		ModelMapper mm = new ModelMapper();

		// Default
		mm.m_DefaultLanguageMapper = LanguageMapper.readObject( element);

		java.util.List lst = element.getChildren( "display-analysis-mapping");
		int size = lst.size();
		for( int i = 0; i < size; ++i)
		{
			org.jdom.Element node = (org.jdom.Element )lst.get( i);
			LanguageMapper lm = LanguageMapper.readObject( node);
			mm.AddMapping( node.getAttributeValue( "analysis-ref"), lm);
		}
		return mm;
	}

	public ModelMapper()
	{
		m_ModelData = new Hashtable();
	}

	public void AddMapping( String analysisProfile, LanguageMapper mapper)
	{
		m_ModelData.put( analysisProfile, mapper);
	}

	public LanguageMapper getDefaultMapper()
	{
		return m_DefaultLanguageMapper;
	}

	public LanguageMapper getClassifier( String analysisProfile)
	{
		LanguageMapper result = ( LanguageMapper) m_ModelData.get( analysisProfile);

		if( result == null) // Error : Association not defined
			return m_DefaultLanguageMapper;

		// Ok
		return result;
	}

	// Check classifiers existence
	public void checkIntegrity( String m, WPSDictionary dico) throws org.jdom.JDOMException,  WPSConnectorException
	{
		AnalysisProfile profile = dico.getAnalysisProfile( dico.m_AnalysisMapper.getDefault());
		m_DefaultLanguageMapper.checkIntegrity( m + ", Default Analysis Profile '" + profile.m_Name + "'", dico, profile.m_AttributesRef);
		Enumeration enumvar = this.m_ModelData.keys();
		while( enumvar.hasMoreElements())
		{
			String key = ( String)enumvar.nextElement();
			profile = (AnalysisProfile) dico.m_AnalysisProfiles.get( key);
			if( profile == null)
				throw new org.jdom.JDOMException( m + " : Unknown Analysis Profile '" + key + "'");

			LanguageMapper cl = this.getClassifier( key);
			cl.checkIntegrity( m + ", Analysis Profile=" + key, dico, profile.m_AttributesRef);
		}
	}
}