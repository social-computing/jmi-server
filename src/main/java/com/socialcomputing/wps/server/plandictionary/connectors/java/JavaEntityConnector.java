package com.socialcomputing.wps.server.plandictionary.connectors.java;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iAffinityGroupReader;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iSelectionConnector;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class JavaEntityConnector implements iEntityConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -116065676887017225L;
	public static final String s_Name = "JavaEntityConnector";
	public static final String s_Description = "";

	private String m_javaPlanParameter = null, m_xmlPlanParameter = null;
	private String m_xmlFileName = null;

	private transient PlanData m_PlanData = null;
	private JavaProfileConnector m_Profile = null;
	private JavaAffinityGroupReader m_AffReader = null;

	static JavaEntityConnector readObject( org.jdom.Element element)
	{
		JavaEntityConnector connector = new JavaEntityConnector();
		org.jdom.Element eleType = element.getChild( "plan-object-entities");
		if( eleType != null)
		{
			connector.m_javaPlanParameter = eleType.getAttributeValue( "javaPlanParameter");
		}
		else
		{
			eleType = element.getChild( "xml-object-entities");
			if( eleType != null)
			{
				connector.m_xmlPlanParameter = eleType.getAttributeValue( "xmlPlanParameter");
				connector.m_xmlFileName = eleType.getAttributeValue( "xmlFileName");
			}
		}
		return connector;
	}
	public JavaEntityConnector()
	{
		m_Profile = new JavaProfileConnector();
		m_AffReader = new JavaAffinityGroupReader();
	}

	// iEntityConnector interface
	public String getName()
	{
		return s_Name;
	}

	public  String getDescription()
	{
		return s_Description;
	}

	public void openConnections( Hashtable<String, Object> wpsparams) throws WPSConnectorException
	{
		if( m_javaPlanParameter != null)
		{
			m_PlanData = ( PlanData) wpsparams.get( m_javaPlanParameter);
		}
		if( m_PlanData == null && m_xmlPlanParameter != null)
		{
			try {
				org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( false);
				org.jdom.Document doc = builder.build( ( String) wpsparams.get( m_xmlPlanParameter));
				m_PlanData = PlanData.readObject( doc.getRootElement());
			}
			catch( Exception e)
			{
				throw new WPSConnectorException( "JavaEntityConnector : failed to read xml parameter '" + m_xmlPlanParameter + "'", e);
			}
		}
		if( m_PlanData == null && m_xmlFileName != null)
		{
			try {
				org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( false);
				org.jdom.Document doc = builder.build( new File( m_xmlFileName));
				m_PlanData = PlanData.readObject( doc.getRootElement());
			}
			catch( Exception e)
			{
				throw new WPSConnectorException( "JavaEntityConnector : failed to read xml file '" + m_xmlFileName + "'", e);
			}
		}
		if( m_PlanData == null)
		{
			throw new WPSConnectorException( "JavaEntityConnector : no data found");
		}
		// STD init
		m_Profile.openConnections( m_PlanData);
		m_AffReader.openConnections( m_PlanData);
	}

	public void closeConnections()
	{
	}

/**
  * Load the entity properties (image, age, income, ...). */
	public  Hashtable getProperties( String entityId ) throws WPSConnectorException
	{
		return m_PlanData.getEntity( entityId).getProperties();
	}

   public iEnumerator<String> getEnumerator()
   {
		JavaIdEnumerator enumvar = new JavaIdEnumerator( m_PlanData.getEntities());
		return enumvar;
   }

   public Collection getAffinityGroupReaders()
   {
		return null;
   }

	public iAffinityGroupReader getAffinityGroupReader( String affGrpReader)
	{
		return m_AffReader;
	}

/**
  * Retrieve a collection of interface iProfileConnector    */
	public Collection getProfiles()
	{
	  return null;
	}

	public iProfileConnector getProfile( String profile )
	{
		return m_Profile;
	}

	public Collection getClassifiers()
	{
		return null;
	}

	public iClassifierConnector getClassifier( String classifier)
	{
		return null;
	}

	public Collection getSelections()
	{
		return null;
	}

	public iSelectionConnector getSelection( String selectionId)
	{
		return null;
	}
}