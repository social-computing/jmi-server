package com.socialcomputing.wps.server.plandictionary;


/**
 * Title:        Plan Dictionary
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public class WPSSelection implements java.io.Serializable
{
	static final long serialVersionUID = 8478465783138888307L;

	public String m_SelectionName = null;
	public String m_SelectionRef = null;
	public boolean m_FreeSelection = false;
	public int m_Index = 0;

	static public WPSSelection readObject( org.jdom.Element element, int index)
	{
		WPSSelection sel = null;
		sel = new WPSSelection( element.getAttributeValue( "name"));
		sel.m_SelectionRef = element.getAttributeValue( "selection-ref");
		if( sel.m_SelectionRef == null)
		{
			sel.m_FreeSelection = true;
		}
		sel.m_Index = index;
		return sel;
	}

	public WPSSelection( String name)
	{
		m_SelectionName = name;
	}
}