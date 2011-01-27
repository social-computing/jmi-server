package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
//import java.io.*;
//import java.lang.reflect.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public abstract class XMLBaseList extends XMLBase
{
	protected ArrayList     m_list  = new ArrayList();

	protected void add( Object obj )
	{
		m_list.add( obj );
	}

	protected void addElemObj( Element elem, Element root, Hashtable refs )
	{
		Object  obj = getElement( elem, root, refs );

		m_list.add( obj );
	}

	protected void addElemRef( Class[] classes, Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		String  refNam = getElementRef( classes, elem, root, refs );

		m_list.add( new RefValue( refNam ));
	}

	public void addPropsToList( ArrayList list )
	{
		super.addPropsToList( list );

		int             i, n = m_list.size();
		Object          obj;
		Propable    container;

		for ( i = 0; i < n; i ++ )
		{
			obj = m_list.get( i );

			if ( obj instanceof Propable )
			{
				container = (Propable)obj;
				container.addPropsToList( list );
			}
		}
	}

	protected Object toClient( int i, Hashtable refs )
	{
		Object      obj = m_list.get( i );

		if ( obj instanceof Clientable )
		{
			return ((Clientable)obj ).toClient( refs );
		}
		else
		{
			return obj != null ? obj : null;
		}
	}

	protected VContainer toClientCont( int i, Hashtable refs )
	{
		Object      obj = m_list.get( i );

		if ( obj instanceof Clientable )
		{
			return ((Clientable)obj ).toClientCont( refs );
		}
		else
		{
			return obj != null ? new VContainer( obj, false ) : null;
		}
	}
}