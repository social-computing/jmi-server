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
 * The first linksCnt links are the real ones.
 */
	var links, //:Array;
/**
 * Number of real Links (the ones that are linked to nodes at both sides).
 */
	linksCnt,//:int;
/**
 * The table of nodes (places). This include the clusterized ones (those who only apears when a zone is hovered).
 * The first nodesCnt are the cluster(BagZone) ones.
 */
	nodes, //:Array;

/**
 * Number of cluster Nodes (the ones that are always visible).
 */
	nodesCnt,//:int;

/**
 * Id of the current active selection (only one at a time).
 * This id is between [0, 31]
 * If there is no current s�lection, this index is -1
 */
	curSel, //:int;

/**
 * Current Satellite (the one that is active).
 * If there is no current Satellite, it should be null.
 */
	curSat = JMI.script.Satellite,

/**
 * Bounding box of the Plan before resizing (pixels).
 */
	prevBox = JMI.script.Rectangle,

/**
 * Maximum bounding box of all zones. This is also the blitBuf size.
 */
	maxBox = JMI.script.Dimension,

/**
 * Temporary buffer for zone blitting operations.Its size is maxBox.
 */
// [transient]
//public var blitBuf:Shape;

/**
 * Current super BagZone (the one that is active).
 * If there is no current ActiveZone, it should be null.
 */
	curZone = JMI.script.ActiveZone,

/**
 * Current ActiveZone (the one that is active). This can be a subZone, different from curZone.
 * If there is no current ActiveZone, it should be null.
 */
	newZone = JMI.script.ActiveZone,

/**
 * The this.applet holding this Plan.
 */
	applet,

/**
 * Table of waiters to manage tooltips.
 */
	tipTimers; //:Object;

	var Plan = function() {
	};
	
    Plan.prototype = {
        constructor: JMI.script.Plan,
        
	/*
	 * Initialize an array of zones (Nodes or Links).
	 * This call the init method of the zones.
	 * It also evaluate the bounding box of each zone and then allocate the blitBuf image buffer.
	 * @param g			A graphics to get the font metrics.
	 * @param zones		An array of zones (nodes or links).
	 * @param isFirst	True if this is the first call of the session (optimisation).
	 */
	initZones: function(s, zones, isFirst) {
	    var i,
			n = zones.length,
	    	dim = this.applet.size;
	    
	    // Reset the BBOX of the biggest zone
	    this.prevBox = new JMI.script.Rectangle(dim.width >> 1, dim.height >> 1, 1, 1);
	    
	    if (zones == this.links) 
	    	this.maxBox = new JMI.script.Dimension(0, 0);
	    
	    // Reversed order so subZones are initialized before supZones!
		for (i = n - 1 ; i >= 0 ; i --) {
	        zones[i].init( this.applet, s, isFirst );
	    }
		
	    // Allocate a temporary bitmap to dblBuffer curZone rendering using the biggest Zone BBox
	    // if ( zones == nodes )
	    // {
		//    blitBuf  = new Shape(); //size  maxBox);
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
		//zones[0].paint(this.applet, s, false, isFront, showTyp, showLinks);
	    if (isRev) {
	        for (var i = n - 1 ; i >= 0 ; i --) {
	            zones[i].paint(this.applet, s, false, isFront, showTyp, showLinks);
	        }
	    }
	    else {
	        for (i = 0 ; i < n ; i++) {
	            zones[i].paint(this.applet, s, false, isFront, showTyp, showLinks);
	        }
	    }
	},
	
	/*
	 * Prepare the Plan by initializing zones, allocating and filling the image buffers and repainting the this.applet.
	 */
	init: function() {
		this.tipTimers = new Object()
	    var dim = this.applet.size;
	    var restDrawingSurface = this.applet.restDrawingSurface;
		var curDrawingSurface  = this.applet.curDrawingSurface;
		var backDrawingSurface = this.applet.backDrawingSurface;
	
	    // If there is any background image, load it
	    //if (this.applet.backImgUrl != null)
	        //renderBitmap( restGfx, this.applet.backImgUrl, 0, 0, null );
		
		/*ImageUtil.clear(restDrawingSurface);
		restDrawingSurface.graphics.beginFill( this.applet.env.inCol.color);
		restDrawingSurface.graphics.drawRect(0, 0, this.applet.width, this.applet.height);
		restDrawingSurface.graphics.endFill();*/
	
	    // Init Links, Nodes and subNodes.
		this.initZones(restDrawingSurface, this.links, false);
		this.initZones(restDrawingSurface, this.nodes, false);	
			
	    // Init backImg and restImg with background, links and places parts that are "ghostable"
		this.paintZones(restDrawingSurface, this.links, this.links.length, false, JMI.script.Satellite.ALL_TYP, true, false );
		this.paintZones(restDrawingSurface, this.nodes, this.nodesCnt, false, JMI.script.Satellite.ALL_TYP, true, true );
	    
	    // Filters backImg so it looks ghosted
		if( this.applet.env.filterCol != null) {
			ImageUtil.copy( restDrawingSurface, backDrawingSurface);
			ImageUtil.filterImage( backDrawingSurface, dim, this.applet.env.filterCol.getColor().color);
			// this.applet.renderShape( this.applet.restDrawingSurface, 0, 0); // ??? size
			//this.applet.env.filterImage(this.applet.backDrawingSurface, dim);
		}
	    
	    // Finish drawing restImg with places parts that are allways visible (tip, sel...)
		this.paintZones(restDrawingSurface, this.links, this.links.length, true, JMI.script.Satellite.BASE_TYP, true, false );
		this.paintZones(restDrawingSurface, this.links, this.links.length, true, JMI.script.Satellite.TIP_TYP, false, false );
		this.paintZones(restDrawingSurface, this.links, this.links.length, true, JMI.script.Satellite.SEL_TYP, false, false );
		
		this.paintZones(restDrawingSurface, this.nodes, this.nodesCnt, true, JMI.script.Satellite.BASE_TYP, true, true );
		this.paintZones(restDrawingSurface, this.nodes, this.nodesCnt, true, JMI.script.Satellite.TIP_TYP, false, true );
		this.paintZones(restDrawingSurface, this.nodes, this.nodesCnt, true, JMI.script.Satellite.SEL_TYP, false, true );
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
		var curSat = JMI.script.Satellite,
			zone = JMI.script.ActiveZone,
	    	parent = curZone != null ? curZone.getParent() : null,
	        i;
	    
		// Check if there is a current Active Zone (Satellite ?)
	    if(curSat != null) {
	        curSat = curZone.curSwh.getSatAt(this.applet, this.applet.curDrawingSurface.graphics, parent, p, true);
	        
			// The cursor is in the current Zone
	        if (curSat != null) {
				//Alert.show("a current zone is hovered");
	            return updateCurrentZone( curSat, p);
	        }
	    }
	    
	    // The cursor is in not in the current Zone, it can be in another one...
	    for(i = 0 ; i < nodesCnt ; i++) {
	        zone = nodes[i];
	        
			// We know p is not in curZone so don't test it!
	        if (zone != parent) {
	            curSat = zone.restSwh.getSatAt(this.applet, this.applet.curDrawingSurface.graphics, zone, p, false);
	            
				// The cursor is on this node
	            if(curSat != null) {
					//Alert.show("an inactive zone is hovered")
	                return updateCurrentZone( curSat, p);
	            }
	        }
	    }
		
	    // The cursor is not in a Node, it can be in a Link...
		//i = 0;
	    for(i = linksCnt - 1 ; i >= 0 ; i --) {
	        zone = links[i];
			
			// We know p is not in curZone so don't test it!
	        if (zone != parent && zone.curSwh != null) {                                                
				
				// If this zone has no current Swatch, it can't be current.
	            curSat = zone.restSwh.getSatAt(this.applet, this.applet.curDrawingSurface.graphics, zone, p, false );
	            
				// The cursor is on this link
	            if (curSat != null) {
					//Alert.show("a link is hovered")
	                return updateCurrentZone( curSat, p);
	            }
	        }
	    }
	    
	    // Last case, the cursor is not in a Zone
	    this.newZone = null;
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
	
		if ( curZone != newZone )//|| curSat != curSat)           // The current Satellite has changed
	    {
	        for each ( var waiter in tipTimers)
	        {
	         	waiter.interrupt();
	        }
	    }
	    
		// The current Satellite has changed
	    if (curZone != newZone || curSat != curSat) {
			// If flying over background reset to default arrow
	        var cursTyp = MouseCursor.AUTO;    
			
	        if (curZone != null &&
				(newZone == null || curZone.getParent() != newZone.getParent())) {
				// Restore its rest image
	            //ON rollover non active zone => redraw
				var curZoneBounds = curZone.getParent().bounds;
				ImageUtil.clear( this.applet.curDrawingSurface);
				this.applet.renderShape(this.applet.restDrawingSurface, curZoneBounds.width, curZoneBounds.height, new Point(curZoneBounds.x, curZoneBounds.y));
	            this.applet.toolTip = null;
	        }
	        
	        curSat = curSat;
	        
			// A new Zone is hovered, let's paint it!
	        if (curSat != null && (curZone != newZone)) {
	            curZone = newZone;
			    ImageUtil.clear( this.applet.curDrawingSurface);
	            paintCurZone();              
	            curSat.execute( this.applet, curZone, p, Satellite.HOVER_VAL);
	            cursTyp = MouseCursor.HAND;   // Sets the cursor to a hand if the mouse entered a Zone
	        }
	        else {
	            curZone = newZone;
	            if (curSat == null) this.applet.showStatus("");
	        }
	        // TODO ???
	        //this.applet.setCursor( Cursor.getPredefinedCursor( cursTyp ));
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
		if (curZone != null) {
	        curZone.getParent().paintCur( this.applet);
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
		var tipTimer = tipTimers[key];
		
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
		tipTimers[key] = tipTimer;
	},
	
	/**
	 * Evaluates the new position and size of the Places and Streets, and allocate the buffers accordingly to the new size.
	 * @param dim	New size of the this.applet.
	 */
	resize: function(dim) {
	    if (this.prevBox != null
	            && ((this.prevBox.width != dim.width) || ( this.prevBox.height != dim.height ))
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
	        if (this.nodesCnt < 8) {
	            scale	= 1.0 + (2.0 / this.nodesCnt);
	            this.prevBox.x += Math.round(0.5 * (this.prevBox.width * (1.0 - scale)));
	            this.prevBox.y += Math.round(0.5 * (this.prevBox.height * (1.0 - scale)));
	            this.prevBox.width = Math.round(this.prevBox.width * scale);
	            this.prevBox.height = Math.round(this.prevBox.height * scale);
	        }
	        
	        sx  = (dim.width  - margin) / this.prevBox.width;
	        sy  = (dim.height - margin) / this.prevBox.height;
	        dx  = this.prevBox.x - (margin >> 1);
	        dy  = this.prevBox.y - (margin >> 1);
			//s	= sx > sy ? sy : sx;
	        s = (sx + sy)/2;
	        
			// Iterate through all "real" nodes 
			for (i = 0 ; i < this.nodesCnt ; i++) {
				zone = this.nodes[i];
	            this.resizePoint(zone, 0, dx, dy, sx, sy);
	            scale = zone.props["_SCALE"];
	            zone.props["_SCALE"] = s * scale;
	            zone.datas.length=0;
	        }
	        
			// Iterate through remaining nodes (fake ones ??)
	        while (i < this.nodes.length) {
	            nodes[i++].datas = {};
	        }
	
			// Iterate through all links (real and fakes)
			for each (var zone in this.links) {
				//LinkZone.FAKEFROM_BIT;
	            isFakeFrom  = JMI.script.Base.isEnabled(zone.flags, JMI.script.LinkZone.FAKEFROM_BIT);
	            isFakeTo    = JMI.script.Base.isEnabled(zone.flags, JMI.script.LinkZone.FAKETO_BIT);
	            
	            if (isFakeFrom)    this.resizePoint(zone, 0, dx, dy, sx, sy);
	            else if (isFakeTo) this.resizePoint(zone, 1, dx, dy, sx, sy);
	            
	            scale = zone.props["_SCALE"];
	            zone.props["_SCALE"] = s * scale;
	            zone.datas.length=0;
	        }
	        this.prevBox = new JMI.script.Rectangle(0, 0, dim.height, dim.width);
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
	    var p = zone.props["_VERTICES"][i];
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
	    var dim = this.applet.size;
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


