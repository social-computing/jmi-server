package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
//import java.io.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.HTMLText;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XEvent extends XMLBaseList
{
//	protected   String[]    m_actions;

/**
	 * 
	 */
	private static final long serialVersionUID = -5368423196504976264L;

	//  open | play | popup | show | dump
	public static XEvent readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XEvent          event       = new XEvent();
		java.util.List  eventLst    = elem.getChildren();
		Element         eventElm;
		ListIterator    eventItr    = eventLst.listIterator();

		while ( eventItr.hasNext())
		{
			eventElm    = (Element)eventItr.next();
			event.addElemObj( eventElm, root, refs );
		}

		return event;
	}

	public void addPropsToList( ArrayList list )
	{
		super.addPropsToList( list );

		int         i, pos, n = m_list.size();
		String      actionStr, eventStr = "";
		//Propable    container;

		for ( i = 0; i < n; i ++ )
		{
			actionStr   = (String)m_list.get( i );
			pos         = actionStr.indexOf( ' ' );

			if ( !actionStr.startsWith( "pop" ))
			{
				eventStr += actionStr.substring( pos + 1 );
			}
		}

		if ( eventStr.length() > 0 )    // else it's full of popup and poptip!
		{
			list.add( new TextValue( eventStr ));
		}
	}

	public Object toClient( Hashtable refs )
	{
		int         i, n = m_list.size();
		String      eventStr = (String)m_list.get( 0 );

		for ( i = 1; i < n; i ++ )
		{
			eventStr += HTMLText.SEP +(String)m_list.get( i );
		}

		return eventStr;
	}
}