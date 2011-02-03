package com.socialcomputing.wps.server.plandictionary.connectors.file.xml;

import com.socialcomputing.wps.server.plandictionary.connectors.*;
import com.socialcomputing.wps.server.plandictionary.connectors.file.FileEntityConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.jdbc.JDBCEntityConnector;

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

public class XmlEntityConnector extends FileEntityConnector
{
	public Element m_Root = null;

	static XmlEntityConnector readObject( org.jdom.Element element)
	{
		XmlEntityConnector connector = new XmlEntityConnector( element.getAttributeValue( "name"));
		connector._readObject( element);
		
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
		super( name);
	}

	public void openConnections( Hashtable<String, Object> wpsparams)
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

   @Override
   public Collection<iAffinityGroupReader> getAffinityGroupReaders()
   {
		return null;
   }

	@Override
	public iAffinityGroupReader getAffinityGroupReader( String affGrpReader)
	{
		return null;
	}

	@Override
	public Collection<iProfileConnector> getProfiles()
	{
	  return null;
	}

	@Override
	public iProfileConnector getProfile( String profile )
	{
		return null;
	}

	@Override
	public Collection getClassifiers()
	{
		return null;
	}

	@Override
	public iClassifierConnector getClassifier( String classifier)
	{
		return null;
	}

	@Override
	public Collection<iSelectionConnector> getSelections()
	{
		return null;
	}

	@Override
	public iSelectionConnector getSelection( String selectionId)
	{
		return null;
	}
}