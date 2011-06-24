package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.Map;
    import com.socialcomputing.wps.util.controls.ImageUtil;
    
    import flash.display.Graphics;
    import flash.display.Shape;
    import flash.display.Sprite;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    import flash.ui.MouseCursor;
    import flash.utils.Dictionary;
    
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
        private var _m_curSel:int;
        
        /**
         * Current Satellite (the one that is active).
         * If there is no current Satellite, it should be null.
         */
        [transient]
        protected var _m_curSat:Satellite;
        
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
        // [transient]
        //public var m_blitBuf:Shape;
        
        /**
         * Current super BagZone (the one that is active).
         * If there is no current ActiveZone, it should be null.
         */
        [transient]
        private var _m_curZone:ActiveZone;
        
        /**
         * Current ActiveZone (the one that is active). This can be a subZone, different from m_curZone.
         * If there is no current ActiveZone, it should be null.
         */
        [transient]
        public var m_newZone:ActiveZone;
        
        /**
         * The Applet holding this Plan.
         */
        [transient]
        public var m_applet:Map;
        
        /**
         * Table of waiters to manage tooltips.
         */
        [transient]
        public var m_tipTimers:Object;
        
        /**
         * Initialize an array of zones (Nodes or Links).
         * This call the init method of the zones.
         * It also evaluate the bounding box of each zone and then allocate the m_blitBuf image buffer.
         * @param g			A graphics to get the font metrics.
         * @param zones		An array of zones (m_nodes or m_links).
         * @param isFirst	True if this is the first call of the session (optimisation).
         */
        //protected synchronized function initZones( g:Graphics, zones:Array, isFirst:Boolean):void {
        public function initZones(s:Sprite, zones:Array, isFirst:Boolean):void {
            var i:int;
			var n:int = zones.length;
            var dim:Dimension = m_applet.size;
            
            // Reset the BBOX of the biggest zone
            m_prevBox = new Rectangle(dim.width >> 1, dim.height >> 1, 1, 1);
            
            if (zones == m_links) m_maxBox = new Dimension(0, 0);
            
            // Reversed order so subZones are initialized before supZones!
			for (i = n - 1 ; i >= 0 ; i --) {
                zones[i].init( m_applet, s, isFirst );
            }
			
            // Allocate a temporary bitmap to dblBuffer curZone rendering using the biggest Zone BBox
            // if ( zones == m_nodes )
            // {
			//    m_blitBuf  = new Shape(); //size  m_maxBox);
            // }
        }
        
        /**
         * Draws an array of zones at rest with specified satellites.
		 * 
         * @param g			Graphics to paint on.
         * @param zones		An array of zones to paint.
         * @param n			Zone count. In normal order, zones are drawn from index 0 to n-1.
         * @param isFront	True if this paint only the satellites over the transparent filter.
         * @param showTyp	The type of satellite to display.[ALL_TYP,BASE_TYP,TIP_TYP,SEL_TYP]
         * @param showLinks	True if this paint only satellite links (selection).
         * @param isRev		True if the array is drawn from in reversed order. That means from n-1 to 0.
         */
        private function paintZones( s:Sprite, zones:Array, n:int, isFront:Boolean, showTyp:int, showLinks:Boolean, isRev:Boolean):void {
			//zones[0].paint(m_applet, s, false, isFront, showTyp, showLinks);
            if (isRev) {
                for (var i:int = n - 1 ; i >= 0 ; i --) {
                    zones[i].paint(m_applet, s, false, isFront, showTyp, showLinks);
                }
            }
            else {
                for (i = 0 ; i < n ; i++) {
                    zones[i].paint(m_applet, s, false, isFront, showTyp, showLinks);
                }
            }
        }
        
		
        /**
         * Prepare the Plan by initializing zones, allocating and filling the image buffers and repainting the Applet.
         */
        //protected synchronized function init( ):void {
        public function init():void {
			this.m_tipTimers = new Object()
            var dim:Dimension= m_applet.size;
            var restDrawingSurface:Sprite = m_applet.restDrawingSurface;
			var curDrawingSurface:Sprite  = m_applet.curDrawingSurface;
			var backDrawingSurface:Sprite = m_applet.backDrawingSurface;

            // If there is any background image, load it
            //if (m_applet.backImgUrl != null)
                //renderBitmap( restGfx, m_applet.m_backImgUrl, 0, 0, null );
			
			ImageUtil.clear(restDrawingSurface);
			restDrawingSurface.graphics.beginFill( this.m_applet.env.m_inCol.m_color);
			restDrawingSurface.graphics.drawRect(0, 0, this.m_applet.width, this.m_applet.height);
			restDrawingSurface.graphics.endFill();

            // Init Links, Nodes and subNodes.
			// Commented by jonathan dray, 09/05
			initZones(restDrawingSurface, m_links, false);
			initZones(restDrawingSurface, m_nodes, false);	
				
            // Init backImg and restImg with background, links and places parts that are "ghostable"
			// Commented by jonathan dray, 09/05
			paintZones(restDrawingSurface, m_links, m_links.length, false, Satellite.ALL_TYP, true, false );
			paintZones(restDrawingSurface, m_nodes, m_nodesCnt, false, Satellite.ALL_TYP, true, true );
            
            // Filters backImg so it looks ghosted
			if( this.m_applet.env.m_filterCol != null) {
				ImageUtil.copy( restDrawingSurface, backDrawingSurface);
				ImageUtil.filterImage( backDrawingSurface, dim, this.m_applet.env.m_filterCol.getColor().color);
				// m_applet.renderShape( m_applet.restDrawingSurface, 0, 0); // ??? size
				//m_applet.env.filterImage(m_applet.backDrawingSurface, dim);
			}
            
            // Finish drawing restImg with places parts that are allways visible (tip, sel...)
			paintZones(restDrawingSurface, m_links, m_links.length, true, Satellite.BASE_TYP, true, false );
			paintZones(restDrawingSurface, m_links, m_links.length, true, Satellite.TIP_TYP, false, false );
			paintZones(restDrawingSurface, m_links, m_links.length, true, Satellite.SEL_TYP, false, false );
			
			paintZones(restDrawingSurface, m_nodes, m_nodesCnt, true, Satellite.BASE_TYP, true, true );
			paintZones(restDrawingSurface, m_nodes, m_nodesCnt, true, Satellite.TIP_TYP, false, true );
			paintZones(restDrawingSurface, m_nodes, m_nodesCnt, true, Satellite.SEL_TYP, false, true );
			
        }

		
        /**
		 * When the mouse move, the mouse cursor can hover some specific zones on the plan that need to be refreshed
		 * IE : A node / link is hovered
		 * Check if the satellite at a location has changed and sets the current zone accordingly.
		 * 
         * @param  p	Position of the cursor.
         * @return True if the current satellite has changed. 
         */
        public function updateZoneAt(p:Point):Boolean {
			var curSat:Satellite;
            var zone:ActiveZone,
            	parent:ActiveZone = m_curZone != null ? m_curZone.getParent() : null;
            

            var i:int;
            
			// Check if there is a current Active Zone (Satellite ?)
            if(m_curSat != null) {
                curSat = m_curZone.m_curSwh.getSatAt(m_applet, m_applet.curDrawingSurface.graphics, parent, p, true);
                
				// The cursor is in the current Zone
                if (curSat != null) {
					//Alert.show("a current zone is hovered");
                    return updateCurrentZone( curSat, p);
                }
            }
            
            // The cursor is in not in the current Zone, it can be in another one...
            for(i = 0 ; i < m_nodesCnt ; i++) {
                zone = m_nodes[i];
                
				// We know p is not in curZone so don't test it!
                if (zone != parent) {
                    curSat = zone.m_restSwh.getSatAt(m_applet, m_applet.curDrawingSurface.graphics, zone, p, false);
                    
					// The cursor is on this node
                    if(curSat != null) {
						//Alert.show("an inactive zone is hovered")
                        return updateCurrentZone( curSat, p);
                    }
                }
            }
			
            // The cursor is not in a Node, it can be in a Link...
			//i = 0;
            for(i = m_linksCnt - 1 ; i >= 0 ; i --) {
                zone = m_links[i] as LinkZone;
				
				// We know p is not in curZone so don't test it!
                if (zone != parent && zone.m_curSwh != null) {                                                
					
					// If this zone has no current Swatch, it can't be current.
                    curSat = zone.m_restSwh.getSatAt(m_applet, m_applet.curDrawingSurface.graphics, zone, p, false );
                    
					// The cursor is on this link
                    if (curSat != null) {
						//Alert.show("a link is hovered")
                        return updateCurrentZone( curSat, p);
                    }
                }
            }
            
            // Last case, the cursor is not in a Zone
            this.m_newZone = null;
            return updateCurrentZone( null, p);
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
        private function updateCurrentZone( curSat:Satellite, p:Point):Boolean {

			if ( m_curZone != m_newZone )           // The current Satellite has changed
            {
                for each ( var waiter:TipTimer in m_tipTimers)
                {
                 	waiter.interrupt();
                }
            }
            
			// The current Satellite has changed
            if (m_curZone != m_newZone || m_curSat != curSat) {
				// If flying over background reset to default arrow
                var cursTyp:String = MouseCursor.AUTO;    
				
                if (m_curZone != null &&
					(m_newZone == null || m_curZone.getParent() != m_newZone.getParent())) {
					// Restore its rest image
                    //ON rollover non active zone => redraw
					var curZoneBounds:Rectangle = m_curZone.getParent().m_bounds;
					ImageUtil.clear( this.m_applet.curDrawingSurface);
					this.m_applet.renderShape(this.m_applet.restDrawingSurface, curZoneBounds.width, curZoneBounds.height, new Point(curZoneBounds.x, curZoneBounds.y));
                    this.m_applet.toolTip = null;
                }
                
                m_curSat = curSat;
                
				// A new Zone is hovered, let's paint it!
                if (m_curSat != null && (m_curZone != m_newZone)) {
                    m_curZone = m_newZone;
				    ImageUtil.clear( this.m_applet.curDrawingSurface);
                    paintCurZone();              
                    m_curSat.execute( m_applet, m_curZone, p, Satellite.HOVER_VAL);
                    cursTyp = MouseCursor.HAND;   // Sets the cursor to a hand if the mouse entered a Zone
                }
                else {
                    m_curZone = m_newZone;
                    if (m_curSat == null) m_applet.showStatus("");
                }
                // TODO ???
                //m_applet.setCursor( Cursor.getPredefinedCursor( cursTyp ));
                //g.dispose();
                
                return true;
            }
            
            return false;
        }
        
        /**
         * Draws the current zone (Link or Node).
		 * 
         * @param s	the sprite to paint the current zone in
         */
        public function paintCurZone():void {
			// A new Zone is hovered, let's paint it!
			if (m_curZone != null) {
                (Activable(m_curZone.getParent())).paintCur( m_applet);
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
		public function popSlice( zone:ActiveZone, slice:Slice, delay:int, length:int, key:String):void
		{
			var tipTimer:TipTimer  = m_tipTimers[key];
			
			if ( tipTimer != null )
			{
				if ( tipTimer.zone != zone) {
					tipTimer.interrupt()
				}
				else {
					return;
				}
			}
			
			tipTimer = new TipTimer( this, zone, slice, key, delay, length );
			m_tipTimers[key] = tipTimer;
		}
		
        /**
         * Evaluates the new position and size of the Places and Streets, and allocate the buffers accordingly to the new size.
         * @param dim	New size of the Applet.
         */
        public function resize(dim:Dimension):void {
            if (m_prevBox != null
                    && ((m_prevBox.width != dim.width) || ( m_prevBox.height != dim.height ))
					&& dim.width > 100 && dim.height > 100 )
                    {
				var i:int;
                var margin:int= 10;
                var scale:Number;
				var sx:Number;
                var sy:Number;
                var dx:Number;
                var dy:Number;
                var s:Number;
                var zone:ActiveZone;
                var isFakeFrom:Boolean;
                var isFakeTo:Boolean;
                
				// trace("Plan resize à terminer");
				// TODO 
                // m_applet.backImg = ImageUtil.fromRectangle(dim);
                // m_applet.restImg = ImageUtil.fromRectangle(dim);
                
				// too few places, lets reduce their size
                if (this.m_nodesCnt < 8) {
                    scale	= 1.0 + (2.0 / this.m_nodesCnt);
                    m_prevBox.x += int(0.5 * (m_prevBox.width * (1.0 - scale)));
                    m_prevBox.y += int(0.5 * (m_prevBox.height * (1.0 - scale)));
                    m_prevBox.width = int(m_prevBox.width * scale);
                    m_prevBox.height = int(m_prevBox.height * scale);
                }
                
                sx  = (dim.width  - margin) / m_prevBox.width;
                sy  = (dim.height - margin) / m_prevBox.height;
                dx  = m_prevBox.x - (margin >> 1);
                dy  = m_prevBox.y - (margin >> 1);
				s	= sx > sy ? sy : sx;

				// Iterate through all "real" nodes 
				for (i = 0 ; i < this.m_nodesCnt ; i++) {
					zone = this.m_nodes[i];
                    resizePoint(zone, 0, dx, dy, sx, sy);
                    scale = zone.m_props["_SCALE"] as Number;
                    zone.m_props["_SCALE"] = s * scale;
                    zone.m_datas.length=0;
                }
                
				// Iterate through remaining nodes (fake ones ??)
                while (i < this.m_nodes.length) {
                    m_nodes[i++].m_datas = new Dictionary();
                }

				// Iterate through all links (real and fakes)
				for each (zone in m_links) {
					LinkZone.FAKEFROM_BIT;
                    isFakeFrom  = Base.isEnabled(zone.m_flags, LinkZone.FAKEFROM_BIT);
                    isFakeTo    = Base.isEnabled(zone.m_flags, LinkZone.FAKETO_BIT);
                    
                    if (isFakeFrom)    resizePoint(zone, 0, dx, dy, sx, sy);
                    else if (isFakeTo) resizePoint(zone, 1, dx, dy, sx, sy);
                    
                    scale = zone.m_props["_SCALE"] as Number;
                    zone.m_props["_SCALE"] = s * scale;
                    zone.m_datas.length=0;
                }
                m_prevBox = new Rectangle();
				m_prevBox.height = dim.height;
				m_prevBox.width = dim.width; 
            }
        }
        
        /**
         * Change the location of a Zone Point after a resize.
         * The resize transform a location by translating it then scaling it.
		 * 
         * @param zone	Zone holding the vertices (Points) to transform.
         * @param i		Index of the Point to transform in the vertices array.
         * @param dx	Horizontal translation before scaling.
         * @param dy	Vertical translation before scaling.
         * @param sx	Horizontal scaling after translation.
         * @param sy	Vertical scaling after translation.
         */
		private function resizePoint(zone:ActiveZone, i:int, dx:Number, dy:Number, sx:Number, sy:Number):void {
            var p:Point = zone.m_props["_VERTICES"][i];
            p.x = int(sx * (p.x - dx));
            p.y = int(sy * (p.y - dy));
        }
        
        /**
         * Copy an image rectangle to a Graphics (blitting).
         * @param g			Graphics to copy the image in.
         * @param image		An Image from which a rectangle is copyed.
         * @param bounds	Bounds of the image part to copy into g.
         */
        //private synchronized function blitImage( g:Graphics, image:Image, bounds:Rectangle):void {
        public function blitImage(g:Graphics, shape:Shape, bounds:Rectangle):void {
            var dim:Dimension= m_applet.size;
            var x1:int= bounds.x < 0? 0: bounds.x,
                y1:int = bounds.y < 0? 0: bounds.y,
                x2:int = bounds.x + bounds.width + 1,
                y2:int = bounds.y + bounds.height + 1;
            
            if ( x2 > dim.width )   x2 = dim.width;
            if ( y2 > dim.height )  y2 = dim.height;
            
			/// TODO
			trace( "Plan blitImage à terminer");
            //g.renderBitmap( g, shape, x1, y1, x2, y2, x1, y1, x2, y2);
        }

		public function get m_curSel():int
        {
            return _m_curSel;
        }

        public function set m_curSel(value:int):void
        {
			_m_curSel = value;
        }

		public function get m_curSat():Satellite
		{
			return _m_curSat;
		}
		
		public function set m_curSat(value:Satellite):void
		{
			_m_curSat = value;
		}
		
        public function get m_curZone():ActiveZone
        {
            return _m_curZone;
        }

        public function set m_curZone(value:ActiveZone):void
        {
            _m_curZone = value;
        }


    }
}