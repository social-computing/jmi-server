package com.socialcomputing.wps.client.applet;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * <p>Title: ActiveZone</p>
 * <p>Description: A graphical zone holding properties.<br>
 * This base class can be a clusterized zone (subZone) and
 * through BagZone it can also be a parent zone (superZone).
 * This kind of zone doesn't contains any graphical informations.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class ActiveZone extends Hashtable implements Serializable
{
	/**
	 * Bit indicating that subnodes of this are located on one side.
	 */
	public  static final int            SIDE_BIT      	= 0x04;

	/**
	 * Bit indicating that subnodes are located on the left side.
	 */
	public  static final int            LEFT_BIT      	= 0x08;

	/**
	 * Bit indicating invisibility.
	 */
	public  static final int            INVISIBLE_BIT   = 0x10;

	/**
	 * Flags holding the previously defined bits (XXX_BIT).
	 */
	public  int                         m_flags;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected   static final long       serialVersionUID = -2650650578818625653L;

	/**
	 * Swatch used to render this zone at rest.
	 */
	protected   Swatch                  m_restSwh;

	/**
	 * Swatch used to render this zone when it is current (hovered).
	 */
	protected   Swatch                  m_curSwh;

	/**
	 * Bounding-Box of this zone including its subZones.
	 * The BBox is the union of the rest and current swatch BBoxs and a small margin.
	 */
	protected   transient Rectangle     m_bounds;

	/**
	 * Flag indicating which of the 32 possible selections are active for this zone.
	 */
	protected   transient   int         m_selection;

	/**
	 * Parent of this zone if it is clusterized or null if this is already a BagZone.
	 */
	protected   transient   ActiveZone  m_parent;

	/**
	 * Fast graphical data lookup for the rest Swatch Satellites.
	 * Not used enough, could improve the performance if more was stored here...
	 */
	protected   transient   SatData[]   m_restData;

	/**
	 * Fast graphical data lookup for the current Swatch Satellites.
	 * Not used enough, could improve the performance if more was stored here...
	 */
	protected   transient   SatData[]   m_curData;

	/**
	 * HTMLText Hashtable to avoid unnecessary calcs.
	 */
	protected   transient   Hashtable   m_datas;

	/**
	 * Sets the two swatchs of this zone.
	 * This is used in PlanGenerator to setup this zone's swatchs.
	 * @param restSwh   Swatch used to render this when it is at rest.
	 * @param curSwh    Swatch used to render this when it is hovered.
	 */
	public void setSwatchs( Swatch restSwh, Swatch curSwh )
	{
		m_restSwh   = restSwh;
		m_curSwh    = curSwh;
	}
	
	public Swatch getRestSwatch() {
	    return m_restSwh;
	}

    public Swatch getCurSwatch() {
        return m_curSwh;
    }
	/**
	 * Perform basic buffer initialization to enhance real time performance.
	 * This include transforming selection prop to an int flag,
	 * copying Env props reference in this prop table and
	 * initializing satellite data for both swatchs.
	 * @param applet    WPSApplet owning this.
	 * @param g         A graphics compatible with the one that will be used for painting.
	 * @param isFirst   True if init called for the first time.
	 */
	protected void init( WPSApplet applet, Graphics g, boolean isFirst )
	{
		if ( isFirst )  // One time init
		{
			Object  sel     = get( "SELECTION" );

			if ( sel != null )
			{
				m_selection = ((Integer)sel ).intValue();
			}

			// Quick access to Env props
			put( "ENV", applet.m_env.m_props );

			// Optimize prop access
			rehash();

			m_datas = new Hashtable();
		}

		boolean isSuper = this instanceof BagZone;

		m_restData  = m_restSwh.evalSatData( applet, this, isSuper );

		if ( m_curSwh != null )
		{
			m_curData   = m_curSwh.evalSatData( applet, this, isSuper );
		}
	}

	/**
	 * Draw this zone on a specified Graphics using the rest or cur swatch.
	 * @param applet    WPSApplet owning this zone.
	 * @param g         A Graphics on which this must be painted.
	 * @param isCur     True if this is the current zone (hovered) of the plan.
	 * @param isFront   True if this zone appears on top of ghosted zones.
	 * @param showTyp   The type of Satellite to show (SEL, TIP, BASE, ALL). See Satellite.XXXX_TYP.
	 * @param showLinks True if we only wants to paint links.
	 */
	protected void paint( WPSApplet applet, Graphics g, boolean isCur, boolean isFront, int showTyp, boolean showLinks )
	{
		if( (m_flags & INVISIBLE_BIT) != 0 ) return;
		Swatch  swatch  = isCur ? m_curSwh : m_restSwh;

		swatch.paint( applet, g, this, isCur, isFront, showTyp, showLinks );
	}

	/**
	 * Get this parent zone if it exists.
	 * @return	The BagZone holding this or null if this is a BagZone.
	 */
	public ActiveZone getParent( )
	{
		return m_parent == null ? this : m_parent;
	}
}
