package com.socialcomputing.wps.server.plandictionary;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Title:        Plan Dictionary
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class AffinityReaderMapper implements java.io.Serializable
{
	static final long serialVersionUID = 161215456465787774L;

	/**
	* The classifier association map (AnalysisProfile / ClassifierMapper) */
	private Hashtable m_AnalysisData = null;
	private ClassifierMapper m_DefaultClassifier = null;

	static public AffinityReaderMapper readObject( org.jdom.Element element)
	{
		AffinityReaderMapper mm = new AffinityReaderMapper();

		// Default
		mm.m_DefaultClassifier = ClassifierMapper.readObject( element);

		java.util.List lst = element.getChildren( "affreader-analysis-mapping");
		int size = lst.size();
		for( int i = 0; i < size; ++i)
		{
			org.jdom.Element node = (org.jdom.Element )lst.get( i);
			ClassifierMapper lm = ClassifierMapper.readObject( node);
			mm.AddMapping( node.getAttributeValue( "analysis-ref"), lm);
		}
		return mm;
	}

	public AffinityReaderMapper()
	{
		m_AnalysisData = new Hashtable();
	}

	public void AddMapping( String analysisProfile, ClassifierMapper mapper)
	{
		m_AnalysisData.put( analysisProfile, mapper);
	}

	public ClassifierMapper getDefaultMapper()
	{
		return m_DefaultClassifier;
	}

	public ClassifierMapper getClassifier( String analysisProfile)
	{
		ClassifierMapper result = ( ClassifierMapper) m_AnalysisData.get( analysisProfile);

		if( result == null) // Error : Association not defined
			return m_DefaultClassifier;

		// Ok
		return result;
	}

	// Check classifiers existence
	public void checkIntegrity( String m, WPSDictionary dico) throws org.jdom.JDOMException
	{
		m_DefaultClassifier.checkIntegrityForAffinityReaderProfiles( m + ", Default Affinity Reader Profile", dico);
		Enumeration enumvar = this.m_AnalysisData.keys();
		while( enumvar.hasMoreElements())
		{
			String key = ( String)enumvar.nextElement();
			if( dico.m_AnalysisProfiles.get( key) == null)
				throw new org.jdom.JDOMException( m + " : Unknown Analysis Profile '" + key + "'");

			ClassifierMapper cl = this.getClassifier( key);
			cl.checkIntegrityForAffinityReaderProfiles( m + ", Analysis Profile=" + key, dico);
		}
	}
}