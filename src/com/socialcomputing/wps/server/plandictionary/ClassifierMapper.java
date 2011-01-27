package com.socialcomputing.wps.server.plandictionary;

import java.util.Hashtable;
import java.util.Iterator;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.webservices.RequestingClassifyId;

/**
  * Maps a classifier with a string.
  * The string may be the name of :
  * - a profile (used by anlyzing or filtering)
  * - a recommandation engine
  * - an advising engine
  */

public class ClassifierMapper implements java.io.Serializable
{
	static final long serialVersionUID = 161215646546417774L;

	public String m_ClassifierName = "<default>";
	public String m_Description = null;

	/**
	* The classifier association map (for ech rule of iClassifierConnector) */
	private Hashtable m_ClassifierData = null;
	private String m_DefaultClassifierData = "<default>";

	static public ClassifierMapper readObject( org.jdom.Element element)
	{
		ClassifierMapper cm = new ClassifierMapper();
		cm.setDefault( element.getAttributeValue( "default-ref"));
		cm.m_Description = element.getChildText( "comment");

		org.jdom.Element clref = element.getChild( "classifier-ref");
		if( clref != null)
		{
			cm.setClassifier( clref.getAttributeValue( "classifier"));
			java.util.List lst = clref.getChildren( "mapping");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				org.jdom.Element node = ( org.jdom.Element)lst.get( i);
				cm.AddMapping( node.getAttributeValue( "key"), node.getAttributeValue( "value"));
			}
		}
		return cm;
	}

	static public ClassifierMapper readSimpleMapping( org.jdom.Element element)
	{
		ClassifierMapper cm = new ClassifierMapper();
		java.util.List lst = element.getChildren( "mapping");
		int size = lst.size();
		for( int i = 0; i < size; ++i)
		{
			org.jdom.Element node = ( org.jdom.Element)lst.get( i);
			cm.AddMapping( node.getAttributeValue( "key"), node.getAttributeValue( "value"));
		}
		return cm;
	}

	// classifier / default rule
	public ClassifierMapper()
	{
		m_ClassifierData = new Hashtable();
	}

	public void setClassifier( String classifier)
	{
		m_ClassifierName = classifier;
	}

	public void setDefault( String classifier)
	{
		if( classifier != null)
			m_DefaultClassifierData = classifier;
	}

	public String getDefault()
	{
		return m_DefaultClassifierData;
	}

	public void AddMapping( String key, String value)
	{
		m_ClassifierData.put( key, value);
	}

	public String getAssociatedName( iEntityConnector entityConnector, RequestingClassifyId classifyId)  throws WPSConnectorException
	{
		if( m_ClassifierName.equals( "<default>")) // No segmentation
			return m_DefaultClassifierData;

		String result = ( String) classifyId.m_ClassifiersResults.get( m_ClassifierName);
		if( result == null)
		{   // New segmentation
			result = this.classify( entityConnector, classifyId.m_Id);
			classifyId.m_ClassifiersResults.put( m_ClassifierName, result);
		}
		result = ( String) m_ClassifierData.get( result);

		if( result == null) // Error : Association not defined
			return m_DefaultClassifierData;
		return result;
	}

	public String getAssociatedName( String classifyName )
	{
		if( classifyName.equals( "<default>")) // No segmentation
			return m_DefaultClassifierData;

		String result;
		result = ( String) m_ClassifierData.get( classifyName);

		if( result == null) // Error : Association not defined
			return m_DefaultClassifierData;

		// Ok
		return result;
	}

	// Apply segmentation rules for an entity
	public String classify( iEntityConnector entityConnector, String entityId) throws WPSConnectorException
	{
		iClassifierConnector cl = entityConnector.getClassifier( m_ClassifierName);
		return cl.getClassification( entityId);
	}

	public void checkIntegrityForFilteringProfiles( String m, WPSDictionary dico) throws org.jdom.JDOMException
	{
		String value = this.getDefault();
		if( dico.m_FilteringProfiles.get( value ) == null)
			throw new org.jdom.JDOMException( m + " : Unknown Filtering Profile '" + value + "'");
		Iterator it = this.m_ClassifierData.values().iterator();
		while( it.hasNext())
		{
			value = (String )it.next();
			if( dico.m_FilteringProfiles.get( value ) == null)
				throw new org.jdom.JDOMException( m + " : Unknown Filtering Profile '" + value + "'");
		}
	}

	// Check classifiers and attributes integrity
	public void checkIntegrityForAnalysisProfiles( String m, WPSDictionary dico) throws org.jdom.JDOMException
	{
		String value = this.getDefault();
		if( dico.m_AnalysisProfiles.get( value ) == null)
			throw new org.jdom.JDOMException( m + " : Unknown Analysis Profile '" + value + "'");
		Iterator it = this.m_ClassifierData.values().iterator();
		while( it.hasNext())
		{
			value = (String )it.next();
			if( dico.m_AnalysisProfiles.get( value ) == null)
				throw new org.jdom.JDOMException( m + " : Unknown Analysis Profile '" + value + "'");
		}
	}

	// Check classifiers and attributes integrity
	public void checkIntegrityForAffinityReaderProfiles( String m, WPSDictionary dico) throws org.jdom.JDOMException
	{
		String value = this.getDefault();
		if( dico.m_AffinityReaderProfiles.get( value ) == null)
			throw new org.jdom.JDOMException( m + " : Unknown Affinity Reader Profile '" + value + "'");
		Iterator it = this.m_ClassifierData.values().iterator();
		while( it.hasNext())
		{
			value = (String )it.next();
			if( dico.m_AffinityReaderProfiles.get( value ) == null)
				throw new org.jdom.JDOMException( m + " : Unknown Affinity Reader Profile '" + value + "'");
		}
	}

	// Check classifiers integrity
	private void checkIntegrity( String m, iEntityConnector entities) throws org.jdom.JDOMException, WPSConnectorException
	{
		if( m_ClassifierName.equals( "<default>")) // No segmentation
			return;
		if(  entities.getClassifier( m_ClassifierName) == null)
			throw new org.jdom.JDOMException( m + "Unknown Classifier '" + m_ClassifierName + "'");
		// Don't check rules at this time !!!!
	}

	// Check classifiers and models integrity
	public void checkIntegrityForModels( String m, WPSDictionary dico, String attributes) throws org.jdom.JDOMException, WPSConnectorException
	{
		this.checkIntegrity( m, dico.m_EntitiesConnector);

		String value = this.getDefault();
		Model model = dico.getModel( value );
		if( model == null)
			throw new org.jdom.JDOMException( m + " : Unknown Display Profile '" + value + "'");
		model.checkIntegrity( m + " : Default Model '" + model.m_Name + "'", dico.getEntityConnector(), attributes);
		Iterator it = this.m_ClassifierData.values().iterator();
		while( it.hasNext())
		{
			value = (String )it.next();
			model = dico.getModel( value );
			if( model == null)
				throw new org.jdom.JDOMException( m + " : Unknown Display Profile '" + value + "'");
			model.checkIntegrity( m + " : Model '" + model.m_Name + "'", dico.getEntityConnector(), attributes);
		}
	}

}
