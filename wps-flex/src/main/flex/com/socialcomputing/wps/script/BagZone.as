package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.PlanComponent;
    
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.Rectangle;
    
    import spark.primitives.Rect;

/**
 * <p>Title: BagZone</p>
 * <p>Description: A graphical Place holding properties.<br>
 * This is a parent zone including subzones. It's also a "place".</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class BagZone extends ActiveZone implements Activable
{
	/**
	 * Clusterized subZones table.
	 */
	public   var m_subZones:Array= null;

	/**
	 * Initial angular direction of Satellites.
	 * It change depending on the distance to window borders.
	 * The main zone will start at this angle. Evaluated during init.
	 */
    [transient]
	private var _m_dir:Number;

	/**
	 * Angular step between two subZones.
	 * Basicaly 2PI/subs, but in case of half circle (borders) it's PI/subs!
	 * Evaluated during init.
	 */
    [transient]
	private var _m_stp:Number;

	/**
	 * Creates a BagZone with its subZones.
	 * Its size and position should be initialized by setting the "_SCALE" (float) and "_VERTICES" (Point[1]) properties;
	 * @param subs	A subZone table whose parent is this.
	 */
	public function BagZone(subs:Array)
	{
		m_subZones  = subs;
	}

	/**
	 * Perform precalc and basic initialisation.
	 * Initialize the BBox of this zone, the max BBox of all zones and the BBox of the plan.
	 * Eval the border distance and set the initial direction and side bits.
	 * It also perform basic initialisation through inheritance.
	 * @param applet    WPSApplet owning this zone.
	 * @param g         A Graphics on which this must be painted.
	 * @param isFirst	True if this init is the first one. False if this is a "refresh" init.
	 */
    
	public override function init(applet:PlanComponent, s:Sprite, isFirst:Boolean):void {
		var i:int, n:int = m_subZones != null ? m_subZones.length : 0;
		super.init(applet, s, isFirst);
		
		if (isFirst)      // One time init
		{
			m_parent = null;

			if ( n > 0)    m_stp = Base.Pi2 /( n + 1);

			m_dir       = 10.;

			for ( i = 0; i < n; i ++ )
			{
				m_subZones[i].m_parent = this;
			}

			// m_bounds = m_restSwh.getBounds( applet, s.graphics, this, false );
			// DEBUG
			//g.lineStyle(1, 0xFF0000);
			//g.drawRect(m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height);
			// END DEBUG
			
			// var tempRectangle:Rectangle = m_curSwh.getBounds(applet, s.graphics, this, true);
			this.m_bounds = m_curSwh.getBounds(applet, s.graphics, this, true);
			
			// DEBUG
			//g.lineStyle(1, 0x0000FF);
			// g.drawRect(tempRectangle.x, tempRectangle.y, tempRectangle.width, tempRectangle.height);
			// END DEBUG
			
			//this.m_bounds = this.m_bounds.union(tempRectangle);
			
			
			var isLeft:Boolean= m_bounds.x < 0;

			if ( n > 0)
			{
				//float   dir     = 0.f,
				var stp:Number= .25* Base.Pi2;
				//   isLeft || isRight
				if ( isLeft ||( m_bounds.x + m_bounds.width > applet.width ))
				{
					m_stp = Base.Pi2 /( n << 1);

					if ( isLeft )
					{
						m_dir = -stp;
						m_subZones[n-1].m_flags |= SIDE_BIT | LEFT_BIT;
					}
					else
					{
						m_dir = stp;
						m_subZones[n-1].m_flags |= SIDE_BIT;
					}
				}
			}
			//   isLeft || isRight
			if ( isLeft ||( m_bounds.x + m_bounds.width > applet.width ))
			{
				m_flags |= isLeft ? SIDE_BIT | LEFT_BIT : SIDE_BIT;
			}
		}

		//m_bounds    = m_restSwh.getBounds( applet, s.graphics, this, false );

		var win:Rectangle= applet.plan.m_prevBox.union( m_bounds );

		// m_bounds    = m_bounds.union( m_curSwh.getBounds( applet, s.graphics, this, true ));

		
		if ( win.y > m_bounds.y )
		{
			win.height	+= win.y - m_bounds.y;
			win.y		= m_bounds.y;
		}
		else if ( win.y + win.height < m_bounds.y + m_bounds.height )
		{
			win.height	= m_bounds.y  + m_bounds.height - win.y;
		}

		applet.plan.m_prevBox = win;

		m_bounds.inflate(2, 2);

		var w:int= m_bounds.width,
			h:int=m_bounds.height;
		var maxBox:Dimension= applet.plan.m_maxBox;

		if ( w > maxBox.width )     maxBox.width    = w;
		if ( h > maxBox.height )    maxBox.height   = h;

		m_bounds = m_bounds.intersection(applet.size.toRectangle());
	}

	/**
	 * Paint this Place when the cursor hover it.
	 * This is achieved by blitting the basic background and drawing the cur swatch over it.
	 * Those operation are made in another buffer that is finaly blitted on the Applet's Graphics.
	 * This reduce CPU overhead and avoid screen flickering.
	 * @param applet    WPSApplet owning this zone.
	 * @param g         A Graphics on which this must be painted.
	 */
    [synchronized]
	public function paintCur(applet:PlanComponent, s:Sprite):void {
		// var bufGfx:Graphics= applet.plan.m_blitBuf.graphics;
		
		// TODO
		trace("BagZone paintCur begin");
/*		bufGfx.drawImage( applet.backDrawingSurface, 0, 0, m_bounds.width, m_bounds.height, m_bounds.x, m_bounds.y, m_bounds.x + m_bounds.width, m_bounds.y + m_bounds.height, null );
		bufGfx.translate( -m_bounds.x, -m_bounds.y );
		m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.ALL_TYP, true );
		bufGfx.translate( m_bounds.x, m_bounds.y );
*/
		//g.setClip( m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height );
		//g.drawImage( applet.m_plan.m_blitBuf, m_bounds.x, m_bounds.y, null );
		
		// applet.renderShape( applet.plan.m_blitBuf, m_bounds.width, m_bounds.height);
		
		// Should not be called here, right ?
		// applet.renderShape(applet.backDrawingSurface, m_bounds.width, m_bounds.height);
		trace("BagZone paintCur end");
	}

    public function get m_dir():Number
    {
        return _m_dir;
    }

    public function set m_dir(value:Number):void
    {
        _m_dir = value;
    }

    public function get m_stp():Number
    {
        return _m_stp;
    }

    public function set m_stp(value:Number):void
    {
        _m_stp = value;
    }


}
}