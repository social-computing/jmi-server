package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
//import java.io.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.Base;
import com.socialcomputing.wps.client.applet.MenuX;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XItem extends XMLBase
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7111624605396330997L;

	public static XItem readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XItem   item    = new XItem();
		boolean hasURL  = item.putAttRef( "url", TextValue.class, elem, root, refs, CDATA | IDREF ),
				hasElem = item.putElemObj( "call", elem, root, refs ),
				hasCall = item.putAttRef( "call", String.class, elem, root, refs, IDREF );

		item.putAttRef( "track", TextValue.class, elem, root, refs, CDATA | IDREF );
		item.putAttRef( "label", TextValue.class, elem, root, refs, CDATA | IDREF );
		item.putAttRef( "font", XFont.class, elem, root, refs, IDREF );

		if ( hasURL &&( hasElem || hasCall ))   // Should not have URL && Call!
		{
			throw ( new JDOMException( "<item id=" + elem.getAttributeValue( "id" ) + "> choose 'call' OR 'url' attribute" ));
		}
		else if ( hasElem && hasCall )
		{
			throw ( new JDOMException( "<item id=" + elem.getAttributeValue( "id" ) + "> choose 'call' attribute OR 'call' element" ));
		}

		return item;
	}

	public void addPropsToList( ArrayList list )
	{
		super.addPropsToList( list );

		String  callStr = (String)get( "call" );

		if ( callStr != null )
		{
			TextValue   text = new TextValue( callStr );

			if ( text.hasProps())   list.add( text );
		}
	}

	public Object toClient( Hashtable refs )
	{
		MenuX   item    = new MenuX( null );
		String  label   = (String)toClient( "label", refs ) + Base.SEP;

		if ( containsKey( "url" ))
		{
			label += (String)toClient( "url", refs );

			if ( containsKey( "track" ))
			{
				label += Base.SEP + (String)toClient( "track", refs );
			}
		}
		else if ( containsKey( "call" ))
		{
			label += "javascript:" + (String)toClient( "call", refs );
		}

		item.m_containers = new VContainer[]
		{
			new VContainer( new Integer( MenuX.ITEM_BIT ), false ),
			toClientCont( "font", refs ),
			new VContainer( label, false ),
			toClientCont( "size", refs )
		};

		return item;
	}
}