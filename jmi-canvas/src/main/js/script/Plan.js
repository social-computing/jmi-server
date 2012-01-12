JMI.namespace("script.Plan");
/*
 * <p>Title: Plan</p>
 * <p>Description: This describe a Plan comming from the Server and manage the interaction with the zones.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.Plan = (function() {
/*
 * The table of links (streets). This include the fakes one (those who get out of the screen).
 * The first m_linksCnt links are the real ones.
 */
	var m_links, //:Array;
/**
 * Number of real Links (the ones that are linked to nodes at both sides).
 */
	m_linksCnt,//:int;
/**
 * The table of nodes (places). This include the clusterized ones (those who only apears when a zone is hovered).
 * The first m_nodesCnt are the cluster(BagZone) ones.
 */
	m_nodes, //:Array;

/**
 * Number of cluster Nodes (the ones that are always visible).
 */
	m_nodesCnt,//:int;

/**
 * Id of the current active selection (only one at a time).
 * This id is between [0, 31]
 * If there is no current s�lection, this index is -1
 */
	m_curSel, //:int;

/**
 * Current Satellite (the one that is active).
 * If there is no current Satellite, it should be null.
 */
	m_curSat = JMI.script.Satellite,

/**
 * Bounding box of the Plan before resizing (pixels).
 */
	m_prevBox = JMI.script.Rectangle,

/**
 * Maximum bounding box of all zones. This is also the m_blitBuf size.
 */
	m_maxBox = JMI.script.Dimension,

/**
 * Temporary buffer for zone blitting operations.Its size is m_maxBox.
 */
// [transient]
//public var m_blitBuf:Shape;

/**
 * Current super BagZone (the one that is active).
 * If there is no current ActiveZone, it should be null.
 */
	m_curZone = JMI.script.ActiveZone,

/**
 * Current ActiveZone (the one that is active). This can be a subZone, different from m_curZone.
 * If there is no current ActiveZone, it should be null.
 */
	m_newZone = JMI.script.ActiveZone,

/**
 * The Applet holding this Plan.
 */
	m_applet = JMI.component.Map,

/**
 * Table of waiters to manage tooltips.
 */
	m_tipTimers; //:Object;

	var Plan = function() {
	};
	
    Plan.prototype = {
        constructor: JMI.script.Plan,
        
	/*
	 * Initialize an array of zones (Nodes or Links).
	 * This call the init method of the zones.
	 * It also evaluate the bounding box of each zone and then allocate the m_blitBuf image buffer.
	 * @param g			A graphics to get the font metrics.
	 * @param zones		An array of zones (m_nodes or m_links).
	 * @param isFirst	True if this is the first call of the session (optimisation).
	 */
	initZones: function(s, zones, isFirst) {
	    var i,
			n = zones.length,
	    	dim = m_applet.size;
	    
	    // Reset the BBOX of the biggest zone
	    m_prevBox = new com.socialcomputing.jmi.script.Rectangle(dim.width >> 1, dim.height >> 1, 1, 1);
	    
	    if (zones == m_links) 
	    	m_maxBox = new com.socialcomputing.jmi.script.Dimension(0, 0);
	    
	    // Reversed order so subZones are initialized before supZones!
		for (i = n - 1 ; i >= 0 ; i --) {
	        zones[i].init( m_applet, s, isFirst );
	    }
		
	    // Allocate a temporary bitmap to dblBuffer curZone rendering using the biggest Zone BBox
	    // if ( zones == m_nodes )
	    // {
		//    m_blitBuf  = new Shape(); //size  m_maxBox);
	    // }
	},
	
	/*
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
	paintZones: function( s, zones, n, isFront, showTyp, showLinks, isRev) {
		//zones[0].paint(m_applet, s, false, isFront, showTyp, showLinks);
	    if (isRev) {
	        for (var i = n - 1 ; i >= 0 ; i --) {
	            zones[i].paint(m_applet, s, false, isFront, showTyp, showLinks);
	        }
	    }
	    else {
	        for (i = 0 ; i < n ; i++) {
	            zones[i].paint(m_applet, s, false, isFront, showTyp, showLinks);
	        }
	    }
	},
	
	/*
	 * Prepare the Plan by initializing zones, allocating and filling the image buffers and repainting the Applet.
	 */
	init: function() {
		this.m_tipTimers = new Object()
	    var dim = m_applet.size;
	    var restDrawingSurface = m_applet.restDrawingSurface;
		var curDrawingSurface  = m_applet.curDrawingSurface;
		var backDrawingSurface = m_applet.backDrawingSurface;
	
	    // If there is any background image, load it
	    //if (m_applet.backImgUrl != null)
	        //renderBitmap( restGfx, m_applet.m_backImgUrl, 0, 0, null );
		
		ImageUtil.clear(restDrawingSurface);
		restDrawingSurface.graphics.beginFill( this.m_applet.env.m_inCol.m_color);
		restDrawingSurface.graphics.drawRect(0, 0, this.m_applet.width, this.m_applet.height);
		restDrawingSurface.graphics.endFill();
	
	    // Init Links, Nodes and subNodes.
		initZones(restDrawingSurface, m_links, false);
		initZones(restDrawingSurface, m_nodes, false);	
			
	    // Init backImg and restImg with background, links and places parts that are "ghostable"
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
	},
	
	/*
	 * When the mouse move, the mouse cursor can hover some specific zones on the plan that need to be refreshed
	 * IE : A node / link is hovered
	 * Check if the satellite at a location has changed and sets the current zone accordingly.
	 * 
	 * @param  p	Position of the cursor.
	 * @return True if the current satellite has changed. 
	 */
	updateZoneAt: function(p) {
		var curSat = com.socialcomputing.jmi.script.Satellite,
			zone = com.socialcomputing.jmi.script.ActiveZone,
	    	parent = m_curZone != null ? m_curZone.getParent() : null,
	        i;
	    
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
	        zone = m_links[i];
			
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
	},
	
	
	/*
	 * Compare the old and new state of curSat.
	 * Returns wether it has changed and update display if necessary.
	 */
	/*
	 * Update the current zone, knowing the current satellite, execute the 'hover' event and refresh the display accordingly.
	 * @param g			The graphics to paint in.
	 * @param curSat	Current Sat (the one that is hovered by the cursor at this moment) or null (background).
	 * @param p			Location of the cursor.	Used for the 'hover' event.
	 * @return			True if the current satellite has changed.
	 */
	updateCurrentZone: function( curSat, p) {
	
		if ( m_curZone != m_newZone )//|| m_curSat != curSat)           // The current Satellite has changed
	    {
	        for each ( var waiter in m_tipTimers)
	        {
	         	waiter.interrupt();
	        }
	    }
	    
		// The current Satellite has changed
	    if (m_curZone != m_newZone || m_curSat != curSat) {
			// If flying over background reset to default arrow
	        var cursTyp = MouseCursor.AUTO;    
			
	        if (m_curZone != null &&
				(m_newZone == null || m_curZone.getParent() != m_newZone.getParent())) {
				// Restore its rest image
	            //ON rollover non active zone => redraw
				var curZoneBounds = m_curZone.getParent().m_bounds;
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
	},
	
	/**
	 * Draws the current zone (Link or Node).
	 * 
	 * @param s	the sprite to paint the current zone in
	 */
	paintCurZone: function() {
		// A new Zone is hovered, let's paint it!
		if (m_curZone != null) {
	        m_curZone.getParent().paintCur( m_applet);
	    }
	},
	
	/**
	 * Popup a slice after a delay and during a fixed time (tooltips).
	 * A unique identifier for this slice is used to store it and kill its waiter if needed.
	 * @param zone		Zone holding this Slice properties.
	 * @param slice		A slice describing how to draw this tooltip.
	 * @param delay		Delay before poping the slice.
	 * @param length	Time to keep the slice visible before hiding it. -1 means stay visible until another tip with the same key is poped.
	 * @param key		A unique ID for each slices of the same kind (tooltip != infoTip).
	 */
	popSlice: function( zone, slice, delay, length, key) {
		var tipTimer = m_tipTimers[key];
		
		if ( tipTimer != null )
		{
			if ( tipTimer.zone != zone) {
				tipTimer.interrupt()
			}
			else {
				if( tipTimer.started)
					return;
			}
		}
		
		tipTimer = new TipTimer( this, zone, slice, key, delay, length );
		m_tipTimers[key] = tipTimer;
	},
	
	/**
	 * Evaluates the new position and size of the Places and Streets, and allocate the buffers accordingly to the new size.
	 * @param dim	New size of the Applet.
	 */
	resize: function(dim) {
	    if (m_prevBox != null
	            && ((m_prevBox.width != dim.width) || ( m_prevBox.height != dim.height ))
				&& dim.width > 100 && dim.height > 100 )
	    {
			var i;
	        var margin = 10;
	        var scale; //:Number;
			var sx; //:Number;
	        var sy; //:Number;
	        var dx; //:Number;
	        var dy; //:Number;
	        var s; //:Number;
	        var zone = JMI.script.ActiveZone;
	        var isFakeFrom; //:Boolean;
	        var isFakeTo; //:Boolean;
	        
			// too few places, lets reduce their size
	        if (this.m_nodesCnt < 8) {
	            scale	= 1.0 + (2.0 / this.m_nodesCnt);
	            m_prevBox.x += Math.round(0.5 * (m_prevBox.width * (1.0 - scale)));
	            m_prevBox.y += Math.round(0.5 * (m_prevBox.height * (1.0 - scale)));
	            m_prevBox.width = Math.round(m_prevBox.width * scale);
	            m_prevBox.height = Math.round(m_prevBox.height * scale);
	        }
	        
	        sx  = (dim.width  - margin) / m_prevBox.width;
	        sy  = (dim.height - margin) / m_prevBox.height;
	        dx  = m_prevBox.x - (margin >> 1);
	        dy  = m_prevBox.y - (margin >> 1);
			//s	= sx > sy ? sy : sx;
	        s = (sx + sy)/2;
	        
			// Iterate through all "real" nodes 
			for (i = 0 ; i < this.m_nodesCnt ; i++) {
				zone = this.m_nodes[i];
	            resizePoint(zone, 0, dx, dy, sx, sy);
	            scale = zone.m_props["_SCALE"];
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
	            
	            scale = zone.m_props["_SCALE"];
	            zone.m_props["_SCALE"] = s * scale;
	            zone.m_datas.length=0;
	        }
	        m_prevBox = new JMI.script.Rectangle(0, 0, dim.height, dim.width);
	    }
	},
	
	/*
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
	resizePoint: function(zone, i, dx, dy, sx, sy) {
	    var p = zone.m_props["_VERTICES"][i];
	    p.x = Math.round(sx * (p.x - dx));
	    p.y = Math.round(sy * (p.y - dy));
	},
	
	/*
	 * Copy an image rectangle to a Graphics (blitting).
	 * @param g			Graphics to copy the image in.
	 * @param image		An Image from which a rectangle is copyed.
	 * @param bounds	Bounds of the image part to copy into g.
	 */
	blitImage: function (g, shape, bounds) {
	    var dim = m_applet.size;
	    var x1 = bounds.x < 0? 0: bounds.x,
	        y1 = bounds.y < 0? 0: bounds.y,
	        x2 = bounds.x + bounds.width + 1,
	        y2 = bounds.y + bounds.height + 1;
	    
	    if ( x2 > dim.width )   x2 = dim.width;
	    if ( y2 > dim.height )  y2 = dim.height;
	    
		/// TODO
		trace( "Plan blitImage à terminer");
	    //g.renderBitmap( g, shape, x1, y1, x2, y2, x1, y1, x2, y2);
	}
    };
     
    return Plan; 
}());


