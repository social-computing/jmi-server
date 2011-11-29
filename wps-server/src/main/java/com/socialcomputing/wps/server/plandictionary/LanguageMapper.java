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

public class LanguageMapper implements java.io.Serializable
{
	static final long serialVersionUID = 161215456465787774L;

	/**
	* The classifier association map (language / ClassifierMapper) */
	private Hashtable m_LanguageData = null;
	private ClassifierMapper m_DefaultClassifier = null;

	static public LanguageMapper readObject( org.jdom.Element element)
	{
		LanguageMapper lm = new LanguageMapper();

		// Default pour l'attribut
		lm.m_DefaultClassifier = ClassifierMapper.readObject( element);

		// Valeurs pour la segmentation
		java.util.List lst = element.getChildren( "display-language-mapping");
		int size = lst.size();
		for( int i = 0; i < size; ++i)
		{
			org.jdom.Element node = (org.jdom.Element )lst.get( i);
			ClassifierMapper cm = ClassifierMapper.readObject( node);
			lm.AddMapping( node.getAttributeValue( "language"), cm);
		}
		return lm;
	}

	public LanguageMapper()
	{
		m_LanguageData = new Hashtable();
	}

	public void AddMapping( String language, ClassifierMapper classifier)
	{
		m_LanguageData.put( language, classifier);
	}

	public ClassifierMapper getDefaultClassifier()
	{
		return m_DefaultClassifier;
	}

	public ClassifierMapper getClassifier( String language)
	{
		ClassifierMapper result = ( ClassifierMapper) m_LanguageData.get( language);

		if( result == null) // Error : Association not defined
			return m_DefaultClassifier;

		// Ok
		return result;
	}

	// Check classifiers existence
	public void checkIntegrity( String m, WPSDictionary dico, String attributes) throws org.jdom.JDOMException, WPSConnectorException
	{
		m_DefaultClassifier.checkIntegrityForModels( m + ", Default Language", dico, attributes);
		Enumeration enumvar = this.m_LanguageData.keys();
		while( enumvar.hasMoreElements())
		{
			String key = ( String)enumvar.nextElement();
			ClassifierMapper cl = this.getClassifier( key);
			cl.checkIntegrityForModels( m + ", Language=" + key, dico, attributes);
		}
	}
}