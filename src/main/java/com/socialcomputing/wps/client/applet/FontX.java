package com.socialcomputing.wps.client.applet;

import java.awt.Font;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * <p>Title: FontX</p>
 * <p>Description: A wrapper for the java.awt.Font class.<br>
 * Because Serializtion is not compatible between client and server for the original class.</p>
 * All the fields are containers so this Font can be easily changed by Swatchs.
 * This is the way a FontX is initialized after being created by the default constructor.
 * The syle bits are stored in the default flag container (FLAG_VAL).</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class FontX extends Base implements Serializable
{
	/**
	 * Index of the bit flag prop in VContainer table
	 */
//	public      static final int    FLAGS_VAL           = 0;

	/**
	 * Index of the Font name prop in VContainer table
	 */
	public      static final int    NAME_VAL            = 1;

	/**
	 * Index of the Font size prop in VContainer table
	 */
	public      static final int    SIZE_VAL            = 2;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected   static final long   serialVersionUID    = -372468800533888196L;

	/**
	 * A Font Buffer to reduce temporary Font object creation.
	 */
	private     static final Hashtable  s_fontBuf       = new Hashtable();

	/**
	 * Convert this FontX to a java.awt.Font.
	 * @param props		A property table that should hold props referenced by this containers.
	 * @return			a new Font equivalent to this.
	 */
	Font getFont( Hashtable props )
	{
		Font    font;
		int	    flags   = getFlags( props ),
				size    = getInt( SIZE_VAL, props );
		String  name    = getString( NAME_VAL, props ),
				key     = name + flags + size;

		if (( font = (Font)s_fontBuf.get( key ))== null )
		{
			font = new Font( name, flags, size );
			s_fontBuf.put( key, font );
		}

		return font;
	}
}