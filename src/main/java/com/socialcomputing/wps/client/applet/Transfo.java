package com.socialcomputing.wps.client.applet;

import java.awt.Point;
import java.io.Serializable;

/**
 * <p>Title: Transfo</p>
 * <p>Description: A geometric transformation<br>
 * This is used to transform a shape into another by translation and/or scaling.
 * It is also be used to store coordinates.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class Transfo implements Serializable
{
	/**
	 * True if this translation use cartesian coordinates or false if they are polar.
	 * Cartesian are used for bitmaps (font, images) and polar for subzone positioning around the Place.
	 */
	public      static final int    CART_BIT        = 0x01;

	/**
	 * True if the translation is in absolute units(pixels), false if it is relative to the shape center.
	 * ABS_BIT is used to locate font at the exact pixel and thus make useful masks.
	 */
	public      static final int    ABS_BIT         = 0x02;

	/**
	 * True if the position is bilineary interpolated.
	 * In this case the position depends on the shape who use this Transfo.
	 */
	public      static final int    INTER_BIT       = 0x04;

	/**
	 * Angular direction in POLAR coordinates.
	 * X pos or WIDTH in cartesian coordinates.
	 */
	public   	float               m_dir;

	/**
	 * Radius in Polar coordinates.
	 * Y pos or HEIGHT in cartesian coordinates.
	 */
	public   	float               m_pos;

	/**
	 * Scale of the shape to create using this transfo.
	 */
	public		float               m_scl;

	/**
	 * Flags holding the CART_BIT, ABS_BIT and INTER_BIT.
	 */
	public 		int                 m_flags;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected   static final long   serialVersionUID    = 6643486339085246307L;

	/**
	 * Creates a new Transfo using parameters whose meaning depends on the flags argument.
	 * @param dir		Angular direction in POLAR coordinates. X pos or WIDTH in cartesian coordinates.
	 * @param pos		Radius in Polar coordinates. Y pos or HEIGHT in cartesian coordinates.
	 * @param scl		Scale of the shape to create using this transfo.
	 * @param flags		An int holding one or more bits in : {CART_BIT,ABS_BIT,INTER_BIT}.
	 */
	public Transfo( float dir, float pos, float scl, int flags )
	{
		m_dir   = dir;
		m_pos   = pos;
		m_scl   = scl;
		m_flags = flags;
	}

	/**
	 * Transform an already defined Transfo using this one.
	 * @param transfo		A Transfo that define a position and scale to transform. It should be of the same type as this.
	 * 						This means its flags should be the same.
	 * @param isForward		Useless now.
	 * @return				A new Transfo resulting of the compositiong with this.
	 * 						Or this if t is null or not compatible.
	 */
	protected Transfo transform( Transfo transfo, boolean isForward )
	{
		if ( transfo == null || m_flags != transfo.m_flags)
		{
			return new Transfo( m_dir, m_pos, m_scl, m_flags );
		}
		else
		{
			return new Transfo( m_dir + transfo.m_dir, m_pos * transfo.m_pos, m_scl * transfo.m_scl, m_flags );
		}
	}

	/**
	 * Return the translation corresponding to this transformation.
	 * This should have the CART_BIT.
	 * @return		A new Point holding the coordinates of the translation.
	 */
	public Point getCart()
	{
		return new Point((int)m_dir, (int)m_pos );
	}
}