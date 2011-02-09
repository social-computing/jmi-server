package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
//import java.io.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.ShapeX;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XShape extends XMLBase
{
//	private Point[]     m_points;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1000133891843752945L;
	private Integer     m_flags;

	public static XShape readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XShape  shape   = new XShape();
		int     flags   = 0;
//		java.util.List  pointLst    = elem.getChildren( "point" );
//		Element         pointElm;
//		int             i, n        = pointLst.size();

		shape.putAttRef( "scale", Float.class, elem, root, refs, CDATA | IDREF );

		String  anchorStr = elem.getAttributeValue( "anchor" );

		if ( anchorStr != null )
		{
			flags    = anchorStr.equals( "center" ) ?
						ShapeX.CTR_LNK_BIT :
						( anchorStr.equals( "intersect" ) ?
							ShapeX.SEC_LNK_BIT :
							ShapeX.TAN_LNK_BIT );
		}

		shape.m_flags = new Integer( flags );

//		if ( n == 0 )
//		{
//			shape.m_points      = new Point[1];
//			shape.m_points[0]   = new Point( 0, 0 );
//		}
//		else
//		{
//			shape.m_points      = new Point[n];
//
//			for ( i = 0; i < n; i ++ )
//			{
//				pointElm            = (Element)pointLst.get( i );
//				shape.m_points[i]   = (Point)getElement( pointElm, root, refs );
//			}
//		}

		return shape;
	}

	public Object toClient( Hashtable refs )
	{
		ShapeX  shape = new ShapeX();

		shape.m_containers = new VContainer[]
		{
			new VContainer( m_flags, false ),
			new VContainer( "_VERTICES", true ),
//			new VContainer( m_points, false ),
			new VContainer( "_SCALE", true ),
//			toClientCont( "scale", refs ),
		};

		return shape;
	}
}
