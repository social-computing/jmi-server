package com.socialcomputing.wps.server.plandictionary.connectors.xml;

import com.socialcomputing.wps.server.plandictionary.connectors.*;

import java.io.*;
import java.util.*;
import org.jdom.*;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class XmlEntityConnector implements iEntityConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2427315660133745314L;
	public String m_Name = null;
	public String m_Description = null;
	public Element m_Root = null;

	static XmlEntityConnector readObject( org.jdom.Element element)
	{
		XmlEntityConnector connector = new XmlEntityConnector( "");
		connector.m_Description = "";

		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( false);
			org.jdom.Document doc = builder.build( "test.xml");
			connector.m_Root = doc.getRootElement();
		}
		catch( Exception e)
		{
			connector = null;
		}
		return connector;
	}
	public XmlEntityConnector( String name)
	{
		m_Name = name;
	}

	// iEntityConnector interface
	public  String getName()
	{
		return m_Name;
	}

	public  String getDescription()
	{
		return m_Description;
	}

	public void openConnections( Hashtable wpsparams)
	{
		//return false;
	}

	public void closeConnections()
	{
	}

/**
  * Load the entity properties (image, age, income, ...). */
	public  Hashtable getProperties( String entityId )
	{
		Hashtable table = new Hashtable();
		return table;
	}

   public iIdEnumerator getEnumerator()
   {
		XmlIdEnumerator enumvar = new XmlIdEnumerator( m_Root.getChildren( "o1"), "id");
		return enumvar;
   }

   public Collection getAffinityGroupReaders()
   {
		return null;
   }

	public iAffinityGroupReader getAffinityGroupReader( String affGrpReader)
	{
		return null;
	}

/**
  * Retrieve a collection of interface iProfileConnector    */
	public  Collection getProfiles()
	{
	  return null;
	}

	public  iProfileConnector getProfile( String profile )
	{
		return null;
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