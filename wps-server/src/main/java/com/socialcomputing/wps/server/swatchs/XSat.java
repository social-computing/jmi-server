package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
//import java.io.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Satellite;
import com.socialcomputing.wps.client.applet.ShapeX;
import com.socialcomputing.wps.client.applet.Slice;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XSat extends XMLBaseList implements Clientable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7499787751894436058L;
	private static final    Class[]    SLICE_CLASS   = { XSlice.class };
	//private     Object[]    m_slices;
	protected   Integer     m_flags;

	public static XSat readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException, Exception
	{
		XSat            sat         = new XSat();
		java.util.List  sliceLst    = elem.getChildren( "slice" ),
						refLst      = elem.getChildren( "ref" );
		//Element         sliceElm;
		ListIterator    li          = refLst.listIterator();
		boolean         hasTransfo  = sat.putElemObj( "transfo", elem, root, refs );

		while ( li.hasNext())
		{
			sat.addElemRef( SLICE_CLASS, (Element)li.next(), root, refs );
		}

		li  = sliceLst.listIterator();

		while ( li.hasNext())
		{
			sat.addElemObj((Element)li.next(), root, refs );
		}

		if ( !sat.m_list.isEmpty())
		{
			int flags   = 0;

			if ( sat.putAttRef( "transfo", Transfo.class, elem, root, refs, IDREF )&& hasTransfo )
			{
				throw ( new JDOMException( "<sat id=" + elem.getAttributeValue( "id" ) + "> must only contains one attribute or ref 'transfo'" ));
			}

			sat.putAttRef( "shape", XShape.class, elem, root, refs, IDREF );
			sat.putAttRef( "hover", XEvent.class, elem, root, refs, IDREF );
			sat.putAttRef( "click", XEvent.class, elem, root, refs, IDREF );
			sat.putAttRef( "dblClk", XEvent.class, elem, root, refs, IDREF );
			sat.putAttRef( "type", Integer.class, elem, root, refs, CDATA );

			if ( sat.putAttRef( "selection", TextValue.class, elem, root, refs, CDATA | IDREF ))    flags  |= Satellite.SEL_BIT;
			if ( sat.putAttRef( "linkDarkCol", ColorX.class, elem, root, refs, CDATA | IDREF ))     flags  |= Satellite.LINK_BIT;
			if ( sat.putAttRef( "linkNormCol", ColorX.class, elem, root, refs, CDATA | IDREF ))     flags  |= Satellite.LINK_BIT;
			if ( sat.putAttRef( "linkLitCol", ColorX.class, elem, root, refs, CDATA | IDREF ))      flags  |= Satellite.LINK_BIT;

			if ( elem.getAttribute( "isVisible" ).getBooleanValue())    flags |= Satellite.VISIBLE_BIT;
			if ( elem.getAttribute( "isSuper" ).getBooleanValue())      flags |= Satellite.SUPER_BIT;
			if ( elem.getAttribute( "isSub" ).getBooleanValue())        flags |= Satellite.SUB_BIT;
			if ( elem.getAttribute( "isCur" ).getBooleanValue())        flags |= Satellite.CUR_BIT;
			if ( elem.getAttribute( "isRest" ).getBooleanValue())       flags |= Satellite.REST_BIT;
			if ( elem.getAttribute( "isBack" ).getBooleanValue())       flags |= Satellite.BACK_BIT;
			if ( elem.getAttribute( "isTip" ).getBooleanValue())        flags |= Satellite.TIP_BIT;
			if ( elem.getAttribute( "isNoSided" ) != null && elem.getAttribute( "isNoSided" ).getBooleanValue())
				flags |= Satellite.NOSIDED_BIT;

			sat.m_flags = new Integer( flags );

			return sat;
		}
		else
		{
			throw ( new JDOMException( "<sat id=" + elem.getAttributeValue( "id" ) + "> must contains at least one <slice>" ));
		}
	}

	public Object toClient( Hashtable refs )
	{
		ShapeX      shape   = (ShapeX)toClient( "shape", refs );
		int         i, n    = m_list.size();
//					type    = ((Integer)get( "type" )).intValue();
		Slice[]     slices  = new Slice[n];

		for ( i = 0; i < n; i ++ )
		{
			slices[i] = (Slice)toClient( i, refs );
		}

		Satellite   sat     = new Satellite( shape, slices );//, type );

		sat.m_containers = new VContainer[]
		{
			new VContainer( m_flags, false ),
			toClientCont( "transfo", refs ),
			toClientCont( "hover", refs ),
			toClientCont( "click", refs ),
			toClientCont( "dblClk", refs ),
			toClientCont( "selection", refs ),
			toClientCont( "linkDarkCol", refs ),
			toClientCont( "linkNormCol", refs ),
			toClientCont( "linkLitCol", refs )
		};

		return sat;
	}
}