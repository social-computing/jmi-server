JMI.namespace("script.ActiveZone");

/*
 * <p>Title: ActiveZone</p>
 * <p>Description: A graphical zone holding properties.<br>
 * This base class can be a clusterized zone (subZone) and
 * through BagZone it can also be a parent zone (superZone).
 * This kind of zone doesn't contains any graphical informations.</p>
 */
JMI.script.ActiveZone = (function() {
	
	var ActiveZone = function() {
	    
	    // Swatch used to render this zone at rest.
	    // :jmi.script.Swatch
        this.restSwatch = null;
        
        // Swatch used to render this zone when it is current (hovered).
        // :jmi.script.Swatch
        this.curSwatch = null;
        
        // Flags holding the previously defined bits (XXX_BIT).
        this.flags = null;
        this.props = null; //Array
        
        /*
         * Bounding-Box of this zone including its subZones.
         * The BBox is the union of the rest and current swatch BBoxs and a small margin.
         * :jmi.script.Rectangle
         */
        this.bounds = null; 
        
        //Flag indicating which of the 32 possible selections are active for this zone.
        this.selection = null;
        
        //Parent of this zone if it is clusterized or null if this is already a BagZone.
        //:ActiveZone;
        this.parent = null; 
        
        /*
         * Fast graphical data lookup for the rest Swatch Satellites.
         * Not used enough, could improve the performance if more was stored here...
         * :Vector.<SatData>;
         */
        this.restData = null; 
        
        /*
         * Fast graphical data lookup for the current Swatch Satellites.
         * Not used enough, could improve the performance if more was stored here...
         * :Vector.<SatData>;
         */
        this.curData = null; 
        
        // HTMLText dictionary to avoid unnecessary calcs.
        //:Dictionary;
        this.datas = null; //:Dictionary;
	};
	
	ActiveZone.prototype = {
		constructor: JMI.script.ActiveZone,
		
		/*
         * Perform basic buffer initialization to enhance real time performance.
         * This include transforming selection prop to an int flag,
         * copying Env props reference in this prop table and
         * initializing satellite data for both swatchs.
         * 
         * @param applet    The applet context object
         * @param isFirst   True if init called for the first time.
         */
        init: function (applet, gDrawingContext, isFirst) {
            // One time init
            if (isFirst) {
                var sel = this.props.SELECTION;
                if (sel !== null) {
                    this.selection = sel;
                }
                
                // Quick access to Env props
                this.props.ENV = applet.planContainer.map.env.props;
                this.datas = {};
            }
            
            var isSuper = this instanceof JMI.script.BagZone;
            this.restData = this.restSwatch.evalSatData(applet, this, isSuper);
            
            if (this.curSwatch !== null) {
                this.curData = this.curSwatch.evalSatData(applet, this, isSuper);
            }
        },
        
        /*
         * Draw this zone on a specified Graphics using the rest or cur swatch.
         * 
         * @param applet           WPSApplet owning this zone.
         * @param gDrawingContext  A 2d graphic context to draw the shape in.
         * @param isCur            True if this is the current zone (hovered) of the plan.
         * @param isFront          True if this zone appears on top of ghosted zones.
         * @param showTyp          The type of Satellite to show (SEL, TIP, BASE, ALL). See Satellite.XXXX_TYP.
         * @param showLinks        True if we only wants to paint links.
         */
        paint: function(applet, gDrawingContext, isCur, isFront, showTyp, showLinks) {
            if ((this.flags & JMI.script.ActiveZone.INVISIBLE_BIT) !== 0)  {
            	return;
            }
            var swatch = isCur ? this.curSwatch : this.restSwatch;
            swatch.paint(applet, gDrawingContext, this, isCur, isFront, showTyp, showLinks);
        },
        
        /*
         * Get this parent zone if it exists.
         * 
         * @return  The BagZone holding this or null if this is a BagZone.
         */
        getParent: function () {
            return this.parent === null ? this : this.parent;
        },
        
        // API
		select: function( selection) {
			var selId = this.props.ENV.selections[selection];
			if ( selId != -1 )
			{
				selId = 1 << selId;
				this.selection |= selId;
			}
		}
        
	};
	
	return ActiveZone;
}());

// Constantes
// Bit indicating that subnodes of this are located on one side.
JMI.script.ActiveZone.SIDE_BIT = 0x04;

// Bit indicating that subnodes are located on the left side.
JMI.script.ActiveZone.LEFT_BIT = 0x08;

// Bit indicating invisibility.
JMI.script.ActiveZone.INVISIBLE_BIT = 0x10;