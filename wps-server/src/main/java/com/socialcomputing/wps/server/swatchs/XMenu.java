package com.socialcomputing.wps.server.swatchs;

import java.io.Serializable;
import java.util.*;
//import java.awt.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
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

public class XMenu extends XMLBaseList implements Serializable
{
	static final long serialVersionUID = 456695464622946686L;

	static final String         SEPARATOR   = "-";
	static final MenuX          s_separator = new MenuX( null );
	static final Class[]        MENU_CLASSES   = { XItem.class, XMenu.class };

	static
	{
		s_separator.m_containers    = new VContainer[]{ new VContainer( new Integer( MenuX.ITEM_BIT ), false ), null, new VContainer( SEPARATOR, false )};
	}

	public static XMenu readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XMenu           menu        = new XMenu();
		java.util.List  itemLst     = elem.getChildren();
		//ArrayList       items       = new ArrayList();
		Element         itemElm;
		//Attribute       actionAtt;
		ListIterator    itemItr     = itemLst.listIterator();
		String          itemNam;

		menu.putAttRef( "label", TextValue.class, elem, root, refs, IDREF | CDATA );
		menu.putAttRef( "font", XFont.class, elem, root, refs, IDREF );

		while ( itemItr.hasNext())
		{
			itemElm = (Element)itemItr.next();
			itemNam = itemElm.getName();

			if ( itemNam.equals( "ref" ))
			{
				menu.addElemRef( MENU_CLASSES, itemElm, root, refs );
			}
			else if ( itemNam.equals( "separator" ))
			{
				menu.add( SEPARATOR );
			}
			else    // item or menu
			{
				menu.addElemObj( itemElm, root, refs );
			}
		}

		return menu;
	}

	public Object toClient( Hashtable refs )
	{
		ArrayList       items   = new ArrayList();
		ListIterator    it      = m_list.listIterator();
		Object          item;

		while ( it.hasNext())
		{
			item = it.next();

			if ( item instanceof RefValue )
			{
				item = ((RefValue)item ).getRawValue( refs );
			}
			if ( item instanceof XMenu )
			{
				items.add(((XMenu)item ).toClient( refs ));
			}
			else if ( item instanceof XItem )
			{
				items.add(((XItem)item ).toClient( refs ));
			}
			else    // Separator
			{
				items.add( s_separator );
			}
		}

		MenuX   clientMnu   = new MenuX((MenuX[])items.toArray( new MenuX[0]));

		clientMnu.m_containers = new VContainer[]
		{
			new VContainer( new Integer( 0 ), false ),
			toClientCont( "font", refs ),
			toClientCont( "label", refs )
		};

		return clientMnu;
	}
}