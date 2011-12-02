package com.socialcomputing.wps.server.swatchs;

import java.util.*;
//import java.awt.*;
//import java.io.*;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.ColorX;
import com.socialcomputing.wps.client.applet.Transfo;

/**
 * Title:        Swatchs
 * Description:  Server-Side Swatchs that makes the bridge between the Editor and the Client-Side Swatchs. This package contains polymorphic datatypes to enhance the Swatch customization.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public class XInter extends ArrayList//XMLBaseList
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6858527028849448795L;
	private static final    int         STEP_ITR    = 0;
	private static final    int         LINEAR_ITR  = 1;
	private static final    int         SPLINE_ITR  = 2;
	private static final    Hashtable   CLASSES = new Hashtable();

	static
	{
		CLASSES.put( "color",   ColorX.class );
		CLASSES.put( "integer", Integer.class );
		CLASSES.put( "float",   Float.class );
		CLASSES.put( "boolean", Boolean.class );
		CLASSES.put( "string",  String.class );
		CLASSES.put( "transfo", Transfo.class );
	}

	protected String      m_binding;
	protected Class       m_class;
	protected boolean     m_isAdaptive;
	protected boolean     m_isHSB;
	protected double      m_min;
	protected double      m_max;
	protected int         m_type;
	protected float[]     m_keysPos;

	public static InterValue readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XInter          inter           = new XInter();
		java.util.List  keyLst          = elem.getChildren( "key" );
		Element         keyElm;
		int             i, n            = keyLst.size();
		float           pos, lastPos    = -Float.MIN_VALUE;
		Object          value;

		try
		{
			Class   cls     = (Class)CLASSES.get( elem.getAttributeValue( "class" ));

			if ( cls != null )
			{
				inter.m_binding     = elem.getAttributeValue( "binding" );
				inter.m_class       = cls;
				inter.m_isAdaptive  = elem.getAttributeValue( "isAdaptive" ).equals( "true" );
				inter.m_isHSB  		= elem.getAttributeValue( "isHSB" ).equals( "true" );

				if ( inter.m_isAdaptive )
				{
					inter.m_min     = Double.POSITIVE_INFINITY;
					inter.m_max     = Double.NEGATIVE_INFINITY;
				}
				else
				{
					inter.m_min     = elem.getAttribute( "min" ).getDoubleValue();
					inter.m_max     = elem.getAttribute( "max" ).getDoubleValue();

					if ( inter.m_max <= inter.m_min )
					{
						throw ( new JDOMException( "<inter id=" + elem.getAttributeValue( "id" ) + "> max must be greater than min" ));
					}
				}

				String  typeStr = elem.getAttributeValue( "type" );
				inter.m_type    = typeStr.equals( "linear" ) ?
								LINEAR_ITR :
								( typeStr.equals( "spline" ) ?
									SPLINE_ITR :
									STEP_ITR );
				inter.m_keysPos = new float[n];

				for ( i = 0; i < n; i ++ )
				{
					keyElm  = (Element)keyLst.get( i );
					pos     = keyElm.getAttribute( "pos" ).getFloatValue();

					if ( pos > lastPos && pos >= 0.f && pos <= 1.f )
					{
						inter.m_keysPos[i]  = pos;
					}
					else    // Keys overlap or out of range!
					{
						throw ( new JDOMException( "<inter id=" + elem.getAttributeValue( "id" ) + "> keys overlaps or out of range [0,1]" ));
					}
					// remplacer NOINTER_CLS par cls!

					value   = XMLBase.getAttRefValue( "value", cls, keyElm, root, refs, XMLBase.CDATA | XMLBase.IDREF );
					inter.add( value instanceof RefValue ? ((RefValue)value ).getRawValue( refs ) : value );
//					inter.add( getAttRefValue( "value", cls, keyElm, root, refs, CDATA | IDREF ));
				}
			}
			else
			{
				throw ( new JDOMException( "<inter id=" + elem.getAttributeValue( "id" ) + "> unknown class type" ));
			}
		}
		catch ( DataConversionException e )
		{
			throw ( new JDOMException( "<inter id=" + elem.getAttributeValue( "id" ) + "> data conversion error" ));
		}

		return new InterValue( inter );
	}

	public Object toClient( Hashtable refs )
	{
		System.out.println( "XInter should not be directly converted to Client, it doesn't implements toClient( Hashtable refs )" );
		return null;
	}

	private float normalize( double val )
	{
		if ( val <= m_min ) return 0.f;
		if ( val >= m_max ) return 1.f;

		return (float)(( val - m_min )/( m_max - m_min ));
	}

	protected float getNum( Object rawProp )
	{
		float   val;

		if ( rawProp instanceof Number )
		{
			val  = normalize(((Number)rawProp ).floatValue());
		}
		else if ( rawProp instanceof Boolean )
		{
			val  = ((Boolean)rawProp ).booleanValue() ? 1.f : 0.f ;
		}
		else if ( rawProp instanceof String )
		{
			try
			{
				val  = normalize( Float.parseFloat((String)rawProp ));
			}
			catch( NumberFormatException e )
			{
				val = 0.f;
			}
		}
		else    // default value
		{
			val = 0.f;
		}

		return val;
	}

	protected float interpolate( float AC, float B, float C, float BD, float pos )
	{
		switch ( m_type )
		{
			case STEP_ITR:      return stepInter( B, C, pos );
			case LINEAR_ITR:    return linearInter( B, C, pos );
			case SPLINE_ITR:    return splineInter( AC, B, C, BD, pos );
			default:            return 0.f;
		}
	}

	private float stepInter( float B, float C, float pos )
	{
		return pos == 1.f ? C : B;
	}

	private float linearInter( float B, float C, float pos )
	{
		return B + pos *( C - B );
	}

	private float splineInter( float AC, float B, float C, float BD, float pos )
	{
		float   bc  = C - B,
				d   = B,
				c   = AC,
				a   = BD + c - bc - bc,
				b   = bc - c - a;

		return d + pos *( c + pos *( b + pos * a ));
	}

//	public static void main( String[] args )
//	{
//		XInter  inter = new XInter();
//
//		inter.m_min     = -4.f;
//		inter.m_max     = 16.f;
//		inter.m_binding = "var";
//		inter.m_class   = Color.class;
//		inter.m_type    = SPLINE_ITR;
//		inter.m_keysPos = new float[] { .2f, .4f, .7f, .9f };
//		inter.add( new ColorX( 255, 255, 255 ));
//		inter.add( new ColorX( 239, 127, 63 ));
//		inter.add( new ColorX( 255, 0, 0 ));
//		inter.add( new ColorX( 159, 31, 255 ));
//
//		InterValue  iVal    = new InterValue( inter );
//		Color       c;
//
//		for ( int i = (int)inter.m_min; i < (int)inter.m_max; i ++ )
//		{
//			c = new Color( iVal.getColor( new Integer( i )).m_color );
//			System.out.println( " [" + i + "] = ( " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + " )" );
//		}
//	}
}
