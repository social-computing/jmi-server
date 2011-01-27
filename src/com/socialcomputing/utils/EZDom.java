package com.socialcomputing.utils;

import org.jdom.Element;

import com.socialcomputing.utils.math.Bounds;

/**
 * Title:        EZDebug
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VOYEZ VOUS
 * @author
 * @version 1.0
 */

public final class EZDom
{
	public static float readFloat( Element elem, String name )
	throws NumberFormatException
	{
		return readFloat( elem, name, 0.f );
	}

	public static float readFloat( Element elem, String name, float defVal )
	throws NumberFormatException
	{
		String	value	= elem.getAttributeValue( name );

		if ( value != null )	defVal	= Float.parseFloat( value );

		return defVal;
	}

	public static int readInt( Element elem, String name )
	throws NumberFormatException
	{
		return readInt( elem, name, 0 );
	}

	public static int readInt( Element elem, String name, int defVal )
	throws NumberFormatException
	{
		String	value	= elem.getAttributeValue( name );

		if ( value != null )	defVal	= Integer.parseInt( value );

		return defVal;
	}

	public static Bounds readDomBounds( Element elem, String attStr )
	throws NumberFormatException
	{
		String  valueStr    = elem.getAttributeValue( attStr ),
				minStr, maxStr;
		int     pos;
		float   min, max;

		if ( valueStr != null &&(( pos = valueStr.lastIndexOf( ' ' ))!= -1 ))
		{
			minStr          = valueStr.substring( 0, pos );
			maxStr          = valueStr.substring( pos + 1 );
			min             = Float.parseFloat( minStr );
			max             = Float.parseFloat( maxStr );

			return new Bounds( min, max );
		}

		return null;
	}

	public static void writeDomBounds( Element elem, String attStr, Bounds bounds )
	{
		elem.setAttribute( attStr,  String.valueOf( bounds.m_min )+ " " + bounds.m_max );
	}
}