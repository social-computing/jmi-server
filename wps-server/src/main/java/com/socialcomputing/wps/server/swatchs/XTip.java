package com.socialcomputing.wps.server.swatchs;

import java.io.Serializable;
import java.util.*;
//import java.awt.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.HTMLText;
import com.socialcomputing.wps.client.applet.VContainer;

/**
 * Title:        ServerText
 * Description:  Server-Side TextX.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XTip extends XMLBase implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -199418080708539681L;
	private static Hashtable s_dirIds  = new Hashtable();
	private Integer m_flags;
	private boolean m_hasUrl;

	public XTip( ){}

	static
	{
		s_dirIds.put( "N", new Integer( HTMLText.NORTH ));
		s_dirIds.put( "NE", new Integer( HTMLText.NORTH_EAST ));
		s_dirIds.put( "E", new Integer( HTMLText.EAST ));
		s_dirIds.put( "SE", new Integer( HTMLText.SOUTH_EAST ));
		s_dirIds.put( "S", new Integer( HTMLText.SOUTH ));
		s_dirIds.put( "SW", new Integer( HTMLText.SOUTH_WEST ));
		s_dirIds.put( "W", new Integer( HTMLText.WEST ));
		s_dirIds.put( "NW", new Integer( HTMLText.NORTH_WEST ));
	}

	public static XTip readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XTip    tip         = new XTip();
		String  alignStr    = elem.getAttributeValue( "align" );
		int     flags       = alignStr.equals( "center" ) ? HTMLText.CENTER_BIT :( alignStr.equals( "right" ) ? HTMLText.RIGHT_BIT : 0 );
		boolean hasLabel    = tip.putAttRef( "label", TextValue.class, elem, root, refs, CDATA | IDREF ),
				hasTextCol  = tip.putAttRef( "textCol", ColorX.class, elem, root, refs, CDATA | IDREF ),
				hasFont     = tip.putAttRef( "font", XFont.class, elem, root, refs, IDREF );

		tip.m_hasUrl    = tip.putAttRef( "url", TextValue.class, elem, root, refs, CDATA | IDREF );

		if (( tip.m_hasUrl && hasLabel )||( !tip.m_hasUrl && !hasLabel ))
			throw ( new ExclusivAttException( elem, "label", "url" ));

		tip.putAttRef( "inCol", ColorX.class, elem, root, refs, CDATA | IDREF );
		tip.putAttRef( "outCol", ColorX.class, elem, root, refs, CDATA | IDREF );
        tip.putAttRef( "blur", Integer.class, elem, root, refs, CDATA | IDREF );
        tip.putAttRef( "rounded", Integer.class, elem, root, refs, CDATA | IDREF );

		if ( hasLabel ) // Label based Tip
		{
			if ( !hasTextCol )
				throw ( new MissingAttException( elem, "textCol" ));
			if ( !hasFont )
				throw ( new MissingAttException( elem, "font" ));

			if ( elem.getAttribute( "isCorner" ).getBooleanValue())     flags |= HTMLText.CORNER_BIT;
			if ( elem.getAttribute( "isFloating" ).getBooleanValue())   flags |= HTMLText.FLOAT_BIT;
		}
		else            // URL based Tip
		{
			String  dirStr  = elem.getAttributeValue( "dir" );

			flags = HTMLText.URL_BIT |(((Integer)s_dirIds.get( dirStr )).intValue()<< 16 );
		}

		tip.m_flags = new Integer( flags );

		return tip;
	}

	public Object toClient( Hashtable refs )
	{
		HTMLText    tip = new HTMLText();

		tip.m_containers = new VContainer[]
		{
			new VContainer( m_flags, false ),
			toClientCont( "font", refs ),
			toClientCont( m_hasUrl ? "url" : "label", refs ),
			toClientCont( "inCol", refs ),
			toClientCont( "outCol", refs ),
			toClientCont( "textCol", refs ),
            toClientCont( "blur", refs ),
            toClientCont( "rounded", refs )
		};

		return tip;
	}
}