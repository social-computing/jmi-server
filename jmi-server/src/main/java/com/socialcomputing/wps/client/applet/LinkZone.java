package com.socialcomputing.wps.client.applet;

import java.awt.Dimension;
import java.awt.Graphics;
//import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;

/**
 * <p>Title: LinkZone</p>
 * <p>Description: A graphical Link holding properties.<br>
 * The link is tied to 2 Nodes (BagZones) or just one if it's a fake one.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public final class LinkZone extends ActiveZone implements Serializable, Activable
{
	/**
	 * Bit indicating that this is a Link whose 'From' node is a fake one (out of the window).
	 */
	public  static final int            FAKEFROM_BIT    = 0x01;

	/**
	 * Bit indicating that this is a Link whose 'To' node is a fake one (out of the window).
	 */
	public  static final int            FAKETO_BIT      = 0x02;

	/**
	 * JDK 1.1 serialVersionUID
	 */
	protected   static final long serialVersionUID = -4546233546469878451L;

	/**
	 * The Place from which the link start.
	 */
	public  BagZone   m_from;

	/**
	 * The Place to which the link end.
	 */
	public  BagZone   m_to;

	/**
	 * Creates a Link between two Places.
	 * @param from	The Place to start from.
	 * @param to	The Place to end to.
	 */
	public LinkZone( BagZone from, BagZone to )
	{
		m_from  = from;
		m_to    = to;
	}

	/**
	 * Perform precalc and basic initialisation.
	 * Initialize the BBox of this zone, the max BBox of all zones and the BBox of the plan.
	 * It also perform basic initialisation through inheritance.
	 * @param applet    WPSApplet owning this.
	 * @param g         A graphics compatible with the one that will be used for painting.
	 * @param isFirst   True if init called for the first time.
	 */
	protected void init( WPSApplet applet, Graphics g, boolean isFirst )
	{
		super.init( applet, g, isFirst );

		m_parent	= null;

		if ( !Base.isEnabled( m_flags, FAKEFROM_BIT | FAKETO_BIT ))
		{
			m_bounds    = m_restSwh.getBounds( applet, g, this, false );
			if ( m_curSwh != null )
				m_bounds    = m_bounds.union( m_curSwh.getBounds( applet, g, this, true ));

			m_bounds.grow( 2, 2 );

			int w       = m_bounds.width,
				h       = m_bounds.height;
			Dimension   maxBox  = applet.m_plan.m_maxBox;

			if ( w > maxBox.width )     maxBox.width    = w;
			if ( h > maxBox.height )    maxBox.height   = h;

			m_bounds = m_bounds.intersection( new Rectangle( applet.getSize()));
		}
	}

	/**
	 * Paint this Link when the cursor hover it.
	 * This is achieved by blitting the basic background, drawing the cur swatch base satellites and the two places over it.
	 * Then the Tip and Sel satellites are drawn over the places.
	 * These operations are made in another buffer that is finaly blitted on the Applet's Graphics.
	 * This reduce CPU overhead and avoid screen flickering.
	 * @param applet    WPSApplet owning this zone.
	 * @param g         A Graphics on which this must be painted.
	 */
	public synchronized void paintCur( WPSApplet applet, Graphics g )
	{
		if( (m_flags & INVISIBLE_BIT) != 0 ) return;
		
		Graphics    bufGfx  = applet.m_plan.m_blitBuf.getGraphics();
	
		//bufGfx.drawImage( applet.m_backImg2, 0, 0, m_bounds.width, m_bounds.height, m_bounds.x, m_bounds.y, m_bounds.x + m_bounds.width, m_bounds.y + m_bounds.height, null );
		bufGfx.drawImage( applet.m_restImg, 0, 0, m_bounds.width, m_bounds.height, m_bounds.x, m_bounds.y, m_bounds.x + m_bounds.width, m_bounds.y + m_bounds.height, null );
		bufGfx.translate( -m_bounds.x, -m_bounds.y );
		m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.BASE_TYP, true );
		m_from.paint( applet, bufGfx, false, true, Satellite.ALL_TYP, true );
		m_to.paint( applet, bufGfx, false, true, Satellite.ALL_TYP, true );
		m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.TIP_TYP, true );
		m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.SEL_TYP, true );
		bufGfx.translate( m_bounds.x, m_bounds.y );
		g.setClip( m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height );
		g.drawImage( applet.m_plan.m_blitBuf, m_bounds.x, m_bounds.y, null );
	}
}
