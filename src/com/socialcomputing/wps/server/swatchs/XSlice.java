package com.socialcomputing.wps.server.swatchs;

import java.util.*;
import java.io.Serializable;
//import java.awt.Color;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Slice;
import com.socialcomputing.wps.client.applet.Transfo;
import com.socialcomputing.wps.client.applet.VContainer;

/**
  * **********************************************************
  *  Java Class Name : ServerSlice
  *  ---------------------------------------------------------
  *  Filetype: (SOURCE)
  *  Filepath: E:\Dvpt\src\com\voyezvous\wps\server\SwatchEditor\ServerSlice.java
  *
  *
  *  GDPro Properties
  *  ---------------------------------------------------
  *   - GD Symbol Type    : CLD_Class
  *   - GD Method         : UML ( 5.0 )
  *   - GD System Name    : WPS
  *   - GD Diagram Type   : Class Diagram
  *   - GD Diagram Name   : Server-side Swatch Classes
  *  ---------------------------------------------------
  *   Author         : flugue
  *   Creation Date  : Tues - Jan 23, 2001
  *
  *   Change Log     :
  *
  * ********************************************************** */

public class XSlice extends XMLBase implements Serializable
{
	static final long serialVersionUID = 8717559344583160600L;

	/**
	 * Identifies the fill.in member value
	 */
//	private static final String                          IN_COL         = "FILLIN_COL";

	/**
	 * Identifies the fill.out member value
	 */
//	private static final String                          OUT_COL        = "FILLOUT_COL";

	/**
	 * Identifies the transfo( ScaledTransfo )member value
	 */
//	private static final String                          SLICE_TRF      = "SLICE_TRF";

	/**
	 * Identifies the fill.bitmap member value
	 */
//	private static final String                          BMP_STR       = "FILLBMP_STR";

	/**
	 * Identifies the visibility member value
	 */
//	private static final String                          FACE_TYP       = "FACE_TYP";

	/**
	 * Identifies the mapType member value
	 */
//	private static final String                          MAP_TYP        = "MAP_TYP";

	/**
	 * Identifies the mapUrl member value
	 */
//	private static final String                          MAPURL_STR     = "MAPURL_STR";

	/**
	 * Identifies the rampType member value
	 */
//	private static final String                          RAMP_TYP       = "RAMP_TYP";

	/**
	 * Identifies the colorKeys.dir member value.
	 * As there will be many ColorKey p/Slice, each value is numbered.
	 * For exemple, the third ColorKey dir member value is accessed by COLKEY_DIR#2
	 */
//	private static final String                          COLKEY_DIR     = "COLKEY_DIR";

	/**
	 * Identifies the colorKeys.color member value.
	 * As there will be many ColorKey p/Slice, each value is numbered.
	 * For exemple, the first ColorKey color member value is accessed by COLKEY_COL#0
	 */
//	private static final String                          COLKEY_COL     = "COLKEY_COL";

	private int                                          m_flags        = 0;
//	private XTip                                   m_text;

	public static XSlice readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XSlice  slice   = new XSlice();
		boolean isValid, hasAtt, hasElm;

		hasAtt  = slice.putAttRef( "transfo", Transfo.class, elem, root, refs, IDREF );
		hasElm  = slice.putElemObj( "transfo", elem, root, refs );

		if ( hasAtt && hasElm )
		{
			throw ( new JDOMException( "<slice id=" + elem.getAttributeValue( "id" ) + "> must only contains one 'transfo' attribute OR element" ));
		}

		hasAtt  = slice.putAttRef( "tip", XTip.class, elem, root, refs, IDREF );
		hasElm  = slice.putElemObj( "tip", elem, root, refs );
		isValid = hasAtt || hasElm;

		if ( hasAtt && hasElm )
		{
			throw ( new JDOMException( "<slice id=" + elem.getAttributeValue( "id" ) + "> must only contains one 'transfo' attribute OR element" ));
		}

		isValid |= slice.putAttRef( "image", TextValue.class, elem, root, refs, IDREF | CDATA );
		isValid |= slice.putAttRef( "inCol", ColorX.class, elem, root, refs, IDREF | CDATA );
		isValid |= slice.putAttRef( "outCol", ColorX.class, elem, root, refs, IDREF | CDATA );

		if ( !isValid )
		{
			throw ( new JDOMException( "<slice id=" + elem.getAttributeValue( "id" ) + "> must contains at least one tip, image or color" ));
		}
		
		//float test = elem.getAttribute("alpha").getFloatValue();

		isValid = slice.putAttRef( "alpha", Float.class, elem, root, refs, CDATA );

		return slice;
	}

	public Object toClient( Hashtable refs )
	{
		Slice   slice = new Slice();

		slice.m_containers = new VContainer[]
		{
			new VContainer( new Integer( m_flags ), false ),
			toClientCont( "transfo", refs ),
			toClientCont( "inCol", refs ),
			toClientCont( "outCol", refs ),
			toClientCont( "image", refs ),
			toClientCont( "tip", refs ),
			toClientCont( "alpha", refs ),
			};

		return slice;
	}
}
