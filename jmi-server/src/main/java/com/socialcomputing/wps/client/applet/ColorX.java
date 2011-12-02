package com.socialcomputing.wps.client.applet;

import java.awt.Color;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * <p>Title: ColorX</p>
 * <p>Description: A wrapper for the java.awt.Color class.<br>
 * Because Serializtion is not compatible between client and server for the original class.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class ColorX implements Serializable
{
	/**
	 * JDK 1.1 serialVersionUID
	 */
	static final long serialVersionUID = 549845334561351853L;

	/**
	 * A 32 bit int that hold Color invormation in ARGB format.
	 * Each component is a 8 bits value:
	 * <ul>
	 * <li>Alpha	from bit 31 to bit 24.</li>
	 * <li>Red		from bit 23 to bit 16.</li>
	 * <li>Green	from bit 15 to bit 8.</li>
	 * <li>Blue		from bit 7 to bit 0.</li>
	 * </ul>
	 * Warning! Alpha is no more used.
	 */
	public  int m_color;
	public  String m_scolor = null;

	/**
	 * Creates a new ColorX using an int in ARGB format.
	 * @param color	The raw ARGB color.
	 */
	public ColorX( int color )
	{
		m_color = color;
	}

	/**
	 * Creates a new ColorX using swatch properties.
	 * @param color	The swtach property format.
	 */
	public ColorX( String color )
	{
		m_scolor = color;
	}

	/**
	 * Creates a new ColorX using its color components.
	 * @param red		Red component as a 8 bits value (from 0 to 255).
	 * @param green		Green component as a 8 bits value (from 0 to 255).
	 * @param blue		Blue component as a 8 bits value (from 0 to 255).
	 */
	public ColorX( int red, int green, int blue )
	{
		m_color = ( red << 16 )|( green << 8 )| blue;
	}

	/**
	 * Convert this ColorX to a java.awt.Color.
	 * @return	a new Color equivalent to this.
	 */
	Color getColor()
	{
		return new Color( m_color );
	}

	Color getColor( Hashtable props) throws UnsupportedEncodingException
	{
		if( m_scolor == null) return new Color( m_color );
		String str = Base.parseString( m_scolor, props, false);
		return new Color( ( str == null ? 0 : Integer.parseInt( str)));
	}
}