package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.Map;
    import com.socialcomputing.wps.util.controls.ImageUtil;
    
    import flash.display.BitmapData;
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.Matrix;
    import flash.geom.Point;
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
	public var m_subZones:Array = null;

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
    
	public override function init(applet:Map, s:Sprite, isFirst:Boolean):void {
		var i:int;
		var nbSubZones:int = m_subZones != null ? m_subZones.length : 0;
		super.init(applet, s, isFirst);
		var restSwhBounds:Rectangle;			
		var curSwhBounds:Rectangle; 
		
		// First time init
		if (isFirst) {
			this.m_parent = null;

			if (nbSubZones > 0) this.m_stp = Base.Pi2 / (nbSubZones + 1);
			this.m_dir = 10.0;

			for (i = 0 ; i < nbSubZones ; i ++) {
				m_subZones[i].m_parent = this;
			}

			restSwhBounds = this.m_restSwh.getBounds(applet, s.graphics, this, false);			
			curSwhBounds  = this.m_curSwh.getBounds(applet, s.graphics, this, true);
			this.m_bounds = restSwhBounds.union(curSwhBounds);
			
			var isLeft:Boolean = this.m_bounds.x < 0;

			if (nbSubZones > 0) {
				// float dir = 0.f,
				var stp:Number = 0.25 * Base.Pi2;
				// isLeft || isRight
				if (isLeft || (this.m_bounds.x + this.m_bounds.width > applet.width)) {
					this.m_stp = Base.Pi2 / (nbSubZones << 1);

					if (isLeft) {
						m_dir = -stp;
						m_subZones[nbSubZones - 1].m_flags |= SIDE_BIT | LEFT_BIT;
					}
					else {
						m_dir = stp;
						m_subZones[nbSubZones - 1].m_flags |= SIDE_BIT;
					}
				}
			}
			
			// isLeft || isRight
			if (isLeft || (this.m_bounds.x + this.m_bounds.width > applet.width)) {
				m_flags |= isLeft ? SIDE_BIT | LEFT_BIT : SIDE_BIT;
			}
		}

		restSwhBounds     = this.m_restSwh.getBounds(applet, s.graphics, this, false);
		var win:Rectangle = applet.plan.m_prevBox.union(restSwhBounds);
		curSwhBounds      = this.m_curSwh.getBounds(applet, s.graphics, this, true);
		this.m_bounds     = restSwhBounds.union(curSwhBounds);
		
		/*
		this.m_bounds = this.m_restSwh.getBounds(applet, s.graphics, this, false);
		var win:Rectangle = applet.plan.m_prevBox.union(m_bounds);
		this.m_bounds = this.m_bounds.union(m_curSwh.getBounds(applet, s.graphics, this, true));
		*/
		
		if (win.y > this.m_bounds.y) {
			win.height += win.y - this.m_bounds.y;
			win.y      = this.m_bounds.y;
		}
		else if (win.y + win.height < this.m_bounds.y + this.m_bounds.height) {
			win.height	= this.m_bounds.y + this.m_bounds.height - win.y;
		}

		applet.plan.m_prevBox = win;
		this.m_bounds.inflate(2, 2);

		var w:int = this.m_bounds.width;
		var h:int = this.m_bounds.height;
		var maxBox:Dimension= applet.plan.m_maxBox;

		if (w > maxBox.width)  maxBox.width  = w;
		if (h > maxBox.height) maxBox.height = h;

		this.m_bounds = this.m_bounds.intersection(applet.size.toRectangle());
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
	public function paintCur(applet:Map):void {
		/*
		bufGfx.drawImage( applet.m_backImg, 0, 0, m_bounds.width, m_bounds.height, m_bounds.x, m_bounds.y, m_bounds.x + m_bounds.width, m_bounds.y + m_bounds.height, null );
		bufGfx.translate( -m_bounds.x, -m_bounds.y );*/
        
        // Copy backDrawingSurface hovered zone to curDrawingSurface
        // Use this method instead of ImageUtil.copy to improve performance  
        var backBitmap:BitmapData = new BitmapData(m_bounds.width+m_bounds.x, m_bounds.height+m_bounds.y);
        backBitmap.draw(applet.backDrawingSurface, null, null, null, new Rectangle(m_bounds.x, m_bounds.y, m_bounds.width+m_bounds.x, m_bounds.height+m_bounds.y));
        applet.curDrawingSurface.graphics.beginBitmapFill(backBitmap);
        applet.curDrawingSurface.graphics.drawRect(m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height);
        applet.curDrawingSurface.graphics.endFill();
        
		m_curSwh.paint( applet, applet.curDrawingSurface, this, true, true, Satellite.ALL_TYP, true);
		/*
		bufGfx.translate( m_bounds.x, m_bounds.y );
		g.setClip( m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height );
		g.drawImage( applet.m_plan.m_blitBuf, m_bounds.x, m_bounds.y, null );
*/
		applet.renderShape( applet.curDrawingSurface, m_bounds.width, m_bounds.height, new Point(m_bounds.x, m_bounds.y));
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