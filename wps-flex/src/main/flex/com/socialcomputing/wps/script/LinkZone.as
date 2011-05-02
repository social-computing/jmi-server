package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.PlanComponent;
    import com.socialcomputing.wps.plan.PlanContainer;
    
    import flash.display.Graphics;
    
/**
     * <p>Title: LinkZone</p>
     * <p>Description: A graphical Link holding properties.<br>
     * The link is tied to 2 Nodes (BagZones) or just one if it's a fake one.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class LinkZone extends ActiveZone implements Activable
    {
        /**
         * Bit indicating that this is a Link whose 'From' node is a fake one (out of the window).
         */
        public static const FAKEFROM_BIT:int= 0x01;
        
        /**
         * Bit indicating that this is a Link whose 'To' node is a fake one (out of the window).
         */
        public static const FAKETO_BIT:int= 0x02;
        
        /**
         * The Place from which the link start.
         */
        public  var m_from:BagZone;
        
        /**
         * The Place to which the link end.
         */
        public  var m_to:BagZone;
        
        /**
         * Creates a Link between two Places.
         * @param from	The Place to start from.
         * @param to	The Place to end to.
         */
        public function LinkZone( from:BagZone, to:BagZone)
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
        public override function init(applet:PlanComponent, g:Graphics, isFirst:Boolean):void {
            super.init( applet, g, isFirst );
            
            m_parent	= null;
            
            if ( !Base.isEnabled( m_flags, FAKEFROM_BIT | FAKETO_BIT ))
            {
                m_bounds    = m_restSwh.getBounds( applet, g, this, false );
                if ( m_curSwh != null )
                    m_bounds    = m_bounds.union( m_curSwh.getBounds( applet, g, this, true ));
                
                m_bounds.inflate( 2, 2);
                
                // var w:int= m_bounds.width,
                // h:int= m_bounds.height;
                // var maxBox:Dimension= applet.plan.m_maxBox;
                // if ( w > maxBox.width )     maxBox.width    = w;
                // if ( h > maxBox.height )    maxBox.height   = h;
				applet.plan.m_maxBox = 
					applet.plan.m_maxBox.resize(Dimension.fromRectangle(m_bounds));
				
				m_bounds = m_bounds.intersection(applet.size.toRectangle());
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
        public function paintCur(applet:PlanComponent, g:Graphics):void {
            if( (m_flags & INVISIBLE_BIT) != 0) return;
            
            var bufGfx:Graphics = applet.plan.m_blitBuf.graphics;
            
            //bufGfx.drawImage( applet.m_backImg2, 0, 0, m_bounds.width, m_bounds.height, m_bounds.x, m_bounds.y, m_bounds.x + m_bounds.width, m_bounds.y + m_bounds.height, null );
			// TODO
			trace("LinkZone paintCur à implémneter");
/*            bufGfx.drawImage( applet.m_restImg, 0, 0, m_bounds.width, m_bounds.height, m_bounds.x, m_bounds.y, m_bounds.x + m_bounds.width, m_bounds.y + m_bounds.height, null );
            bufGfx.translate( -m_bounds.x, -m_bounds.y );
            m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.BASE_TYP, true );
            m_from.paint( applet, bufGfx, false, true, Satellite.ALL_TYP, true );
            m_to.paint( applet, bufGfx, false, true, Satellite.ALL_TYP, true );
            m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.TIP_TYP, true );
            m_curSwh.paint( applet, bufGfx, this, true, true, Satellite.SEL_TYP, true );
            bufGfx.translate( m_bounds.x, m_bounds.y );
            g.setClip( m_bounds.x, m_bounds.y, m_bounds.width, m_bounds.height );
            g.drawImage( applet.m_plan.m_blitBuf, m_bounds.x, m_bounds.y, null );*
			*/
        }
    }
}