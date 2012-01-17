JMI.namespace("script.Swatch");

/*
 * <p>Title: Swatch</p>
 * <p>Description: A template that describe how to draw a zone and interact with it.<br>
 * Each kind of zones usally shares the same swatchs, one for the rest and one for the current(hovered) state.
 * As the swatchs use properties that differs in each zones, the rendering and events can be differents
 * for each zone even if they share the same swatchs.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
JMI.script.Swatch = (function() {
	var Swatch = function() {
	    /**
         * Layers of satellites that describes this.
         * Satellites are where the real information of this is.
         * :Vector.<Satellite>;
         */
    	this.satellites,// = satellites;

        /**
         * Objects references (MenuX, Slice...) created by the server side.
         * This is used by the events to find menu or slices to pop.
         * :Array;
         */
    	this.refs; 

		JMI.script.Base.call(this);
	};
	
	Swatch.prototype = {
		constructor: JMI.script.Swatch,
        
        /*
         * Draws the satellites of this that have the required flags enabled.
         * @param applet           Applet holding this.
         * @param gDrawingContext  A 2d graphic context to draw the shape in.
         * @param zone             Zone to paint.
         * @param isCur            True if zone is hovered.
         * @param isFront          True to paint only satellites over the transparent filter. False to only paint those below.
         * @param showTyp          Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
         * @param showLinks        True if links between satelites should be drawn. False for the opposite.
         */
        paint: function(applet, gDrawingContext, zone, isCur, isFront, showTyp, showLinks) {
            var satellite = this.satellites[0];
            var shape = satellite.shapex;
            var flags = this.getFlags(zone.props);
            var transfo = satellite.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone.props);
            
            // Draws Satellites links first (if they exists)
            // so they can be partly covered by other sats
            if (JMI.script.Base.isEnabled(flags, JMI.script.Swatch.LINK_BIT) && showLinks) {
                this.drawSats(applet, gDrawingContext, zone, shape, transfo, true, isCur, isFront, showTyp);
            }
          
            // Draws Satellites without links
            this.drawSats(applet, gDrawingContext, zone, shape, transfo, false, isCur, isFront, showTyp);
        },
        
        /*
         * Draws satellites that have the required flags enabled.
         * Those without transfo use a default transformation.
         * 
         * @param applet           Applet holding this.
         * @param gDrawingContext  A 2d graphic context to draw the shape in.
         * @param zone             Zone to draw the sats.
         * @param shape            Default shape coming from the first satellite([0]).
         * @param transfo          Default transformation coming from the first satellite([0]).
         * @param isLinkOnly       True to draw only links between satelites.
         * @param isCur            True if zone is hovered.
         * @param isFront          True to paint only satellites over the transparent filter. False to only paint those below.
         * @param showTyp          Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
         */
        drawSats: function(applet, gDrawingContext, zone, shape, transfo, isLinkOnly, isCur, isFront, showTyp) {
            var isBag = zone instanceof JMI.script.BagZone;
            var supZone = isBag ? zone : null;
            var zones = isBag ? supZone.subZones : null;
            var curZone= applet.planContainer.map.plan.curZone,
                subZone;
            var sat = this.satellites[0];
            var satData = isCur ? zone.curData[0] : zone.restData[0];
            var satRelTrf, satTrf;
            var i, n = this.satellites.length,
                flags;
            var hasRestBit, hasCurBit, isCurSub;
            var satCtr,
                supCtr = shape.getCenter(zone);
            
            if (!isLinkOnly) {
                // Draws the place itself using the first Satellite
                sat.paint(applet, gDrawingContext, zone, null, null, false, satData, showTyp);
            }
            
            for (i = 1 ; i < n ; i++) {
                sat     = this.satellites[i];
                satData = isCur ? zone.curData[i] : zone.restData[i];
                flags   = satData.flags;
                
                if (((isLinkOnly && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.LINK_BIT)) || !isLinkOnly)
                    && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.VISIBLE_BIT)
                    // This Sat is visible
                    && (isFront != JMI.script.Base.isEnabled(flags, JMI.script.Satellite.BACK_BIT))) {
                    
                    // Bags
                    if (isBag) {
                        hasRestBit = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.REST_BIT);
                        hasCurBit  = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.CUR_BIT);
                        satRelTrf  = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone.props);
                        satTrf     = transfo != null ? transfo.transform(satRelTrf) : null;
                        
                        if(supZone.dir != 10.) {
                            if (!JMI.script.Base.isEnabled(flags, JMI.script.Satellite.NOSIDED_BIT)) satTrf.direction = supZone.dir;
                            else {
                                if(JMI.script.Base.isEnabled(supZone.flags, JMI.script.ActiveZone.LEFT_BIT)) satTrf.direction += (JMI.script.Base.Pi2 / 2);
                            }
                        }
                        
                        var dir = satTrf.direction;
                        
                        // draws SubZones
                        if (zones != null && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUB_BIT)) {
                            for (subZone in zones) {
                                satTrf.direction += supZone.stp;
                                isCurSub          = subZone == curZone;
                                satData           = isCur ? subZone.curData[i] : subZone.restData[i];
                                
                                if ((!isCur || ((hasRestBit && !isCurSub) || (hasCurBit && isCurSub)))) {
                                    satCtr = shape.transformOut(zone, satTrf);
                                    sat.paint(applet, gDrawingContext, subZone, satCtr, supCtr, isLinkOnly, satData, showTyp);
                                }
                            }
                        }
                        
                        // draws SuperZone
                        if (JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUPER_BIT)) {
                            isCurSub = supZone == curZone;
                            satData  = isCur ? zone.curData[i] : zone.restData[i];
                            
                            if (zones != null) satTrf.direction = dir;
                            
                            if ((!isCur || ((hasRestBit && !isCurSub) || (hasCurBit && isCurSub)))) {
                                satCtr = shape.transformOut(zone, satTrf);
                                sat.paint(applet, gDrawingContext, supZone, satCtr, supCtr, isLinkOnly, satData, showTyp);
                            }
                        }
                    }
                    
                    // links
                    else {
                        sat.paint(applet, gDrawingContext, zone, null, null, false, satData, showTyp);
                    }
                }
            }
        },        

        /*
         * Gets this bounds by merging the satellites bounds.
         * 
         * @param applet           The Applet that owns this.
         * @param gDrawingContext  A 2d graphic context to draw the shape in.
         * @param zone             The zone that holds the properties used by this swatch.
         * @param isCurZone        True if zone is hovered.
         * 
         * @return  This swatch bounding box for zone.
         */
        getBounds: function(applet, gDrawingContext, zone, isCurZone) {
            var bounds = new JMI.script.Rectangle();
            var sat = this.satellites[0];
            var shape = sat.shapex;
            var isBag = zone instanceof JMI.script.BagZone;
            var supZone = isBag ? zone : null;
            var zones = isBag ? supZone.subZones : null;
            var subZone;
            var satRelTrf, satTrf,
                transfo = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone.props);
            var i, n = this.satellites.length,
                flags;
            //boolean         hasRestBit, hasCurBit, hasLinkBit, isCur;
            var satData;
            var satCtr,
                supCtr = shape.getCenter(zone);
            
            // Gets the bounds of the place itself using the first Satellite
            sat.setBounds(applet, gDrawingContext, zone, null, null, bounds);
            
            // Iterate through the swatch satellite list
            for (i = 1 ; i < n ; i++) {
                sat         = this.satellites[i];
                satData     = isCurZone ? zone.curData[i] : zone.restData[i];
                flags       = satData.flags;
                
                // This Sat is visible
                if (JMI.script.Base.isEnabled(flags, JMI.script.Satellite.VISIBLE_BIT)) {
                    
                    // If this is a BagZone                    
                    if (isBag) {
                        //hasRestBit  = Base.isEnabled( flags, Satellite.REST_BIT );
                        //hasCurBit   = Base.isEnabled( flags, Satellite.CUR_BIT );
                        satRelTrf = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone.props);
                        satTrf    = transfo.transform(satRelTrf, true);
                        
                        if (supZone.dir != 10.) satTrf.direction = supZone.dir;
                        
                        // Gets SuperZone bounds
                        if ((!JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData.isVisible)
                            && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUPER_BIT)) {
                            satCtr  = shape.transformOut(zone, satTrf);
                            sat.setBounds(applet, gDrawingContext, zone, satCtr, supCtr, bounds);
                        }
                        
                        // gets SubZones bounds
                        if (zones != null && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUB_BIT)) {
                            
                            // TODO : portage, see if this for loop works as expected
                            for (subZone in zones) {
                                satTrf.direction += supZone.stp;
                                satData           = isCurZone ? subZone.curData[i] : subZone.restData[i];
                                flags             = satData.flags;
                                
                                if (!JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData.isVisible) {
                                    satCtr  = shape.transformOut(zone, satTrf);
                                    sat.setBounds(applet, gDrawingContext, subZone, satCtr, supCtr, bounds);
                                }
                            }
                        }
                    }
        
                    // This is a LinkZone 
                    else {
                        sat.setBounds(applet, gDrawingContext, zone, null, null, bounds);
                    }
                }
            }
            
            return bounds;
        },
        
        /*
         * Gets the satellite under the cursor if it is in this zone swatch or null if it isn't.
         * 
         * @param planComponent     The Applet that owns this.
         * @param g             A graphics to get the FontMetrics used by this.
         * @param zone          Zone to check the satellites.
         * @param pos           Location of the cursor.
         * @param isCurZone     True if zone is the current one.
         * @return              The sat of this swatch that is hovered or null if there isn't.
         */
        getSatAt: function(planComponent, g, zone, pos, isCurZone) {
            // The cursor position is in the Bounding Box
            var parentzone = zone.getParent();      
                        
            if(parentzone.bounds.contains(pos.x, pos.y)) {
                var sat     = this.satellites[0];
                var shape   = sat.shapex;
                var isBag   = zone instanceof JMI.script.BagZone;
                var supZone = isBag ? zone : null;
                var zones= isBag ? supZone.subZones : null;
                var curZone= planComponent.planContainer.map.plan.curZone;
                var subZone;
                var satRelTrf, satTrf;
                var transfo= sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone.props);
                var i;
                var n = this.satellites.length;
                var flags;
                var hasRestBit;
                var hasCurBit;
                var hasSubBit;
                var isCur;
                var isVisible;
                var satData;
                var supCtr = shape.getCenter(zone);
                
                // Iterate throw all this swatch's satellites to test if the cursor is positionned in one of them
                for(i = n - 1 ; i > 0 ; i--) {
                    sat     = this.satellites[i];
                    satData = isCurZone ? zone.curData[i] : zone.restData[i];
                    flags   = satData.flags;
                    
                    // This Sat is visible and it's not a tip (avoid anoying place popup!)
                    if (JMI.script.Base.isEnabled(flags, JMI.script.Satellite.VISIBLE_BIT) && 
                       (isCurZone || !JMI.script.Base.isEnabled(flags, JMI.script.Satellite.TIP_BIT))) {
                        
                        isVisible   = !JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData.isVisible;
                        
                        // If it's a BagZone
                        if(isBag) {
                            hasCurBit = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.CUR_BIT);
                            hasSubBit = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUB_BIT);
                            satRelTrf = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone.props);
                            
                            if (zones != null && hasSubBit && hasCurBit && satRelTrf != null && satRelTrf.position == 0.) {
                                if (isVisible && sat.contains(planComponent, g, zone, null, null, transfo, pos, true, true)) {
                                    return sat;
                                }
                                else {
                                    continue;
                                }
                            }
                            
                            
                            hasRestBit  = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.REST_BIT);
                            satTrf      = transfo.transform(satRelTrf, true);
                            
                            if (isBag && supZone.dir != 10.) satTrf.dir = supZone.dir;
                            
                            // Test if the cursor is the super zone
                            if (JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUPER_BIT)) {
                                isCur = supZone == curZone;
                                if (isVisible 
                                    && ((hasRestBit && !isCur) || (hasCurBit && isCur))
                                    && sat.contains(planComponent, g, zone, shape.transformOut(zone, satTrf), supCtr, satTrf, pos, false, false)) {
                                    return sat;
                                }
                            }
                            
                            // Or if the cursor is in one of the sub zones 
                            if (zones != null && hasSubBit) {
                                var j, m = zones.length;
                                satTrf.direction += (zones.length + 1) * supZone.stp;
                                
                                for(j = m - 1 ; j >= 0 ; j --) {
                                    subZone       = zones[j];
                                    satTrf.direction -= supZone.stp;
                                    isCur             = subZone == curZone;
                                    satData           = isCurZone ? subZone.curData[i] : subZone.restData[i];
                                    flags             = satData.flags;
                                    isVisible         = !JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData.isVisible;
                                    
                                    if(isVisible 
                                       && ((hasRestBit && !isCur) || (hasCurBit && isCur))
                                       && sat.contains(planComponent, g, subZone, shape.transformOut(zone, satTrf), supCtr, satTrf, pos, false, false)) {
                                       return sat;
                                    }
                                }
                            }
                        }
                        
                        // Else, if it's a link zone
                        else {
                            if(isVisible 
                                && sat.contains(planComponent, g, zone, null, null, transfo, pos, false, true)) {
                                return sat;
                            }
                        }
                    }
                }
                
                // Tests if the cursor is located in the place itself
                // TODO : portage instanceof et héritage
                sat = this.satellites[0];
                if(sat.contains(planComponent, g, zone, null, null, transfo, pos, zones != null, true) 
                    || (isCurZone && !(zone instanceof JMI.script.LinkZone))) {
                    return sat;
                }
            }
            return null;
        },

        /*
         * Evaluate this swatch satellites data buffers for a zone.
         * 
         * @param applet        The Applet that owns this.
         * @param zone          Zone holding satellites.
         * @param isSuper       True if zone is a BagZone.
         * 
         * @return              An array of satellite data.
         */
        evalSatData: function(applet, zone, isSuper) {
            var satDatas = [];
            var satData;
            // var flags;
            var isTip, isSel;
            
            var i = this.satellites.length - 1;
            do {
                var sat = this.satellites[i];
                var flags = sat.getFlags(zone.props);
                satData = new JMI.script.SatData(flags);
                
                isTip = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.TIP_BIT);
                isSel = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SEL_BIT);
                
                if (isTip || isSel) {
                    var sels  = sat.parseString(JMI.script.Satellite.SELECTION_VAL, zone.props);
                    var sel = -1;
                    
                    if (sels != null) {
                        if(applet.planContainer.map.env.selections[sels[0]] != null)
                            sel = applet.planContainer.map.env.selections[sels[0]];
                    }
                    
                    satData.isVisible = sat.isVisible(zone, isTip, applet.planContainer.map.plan.curSel, sel);
                }
                else {
                    satData.isVisible = true;
                }
                satDatas.push(satData);                
            }
            while(i--);
            /*
            for each (var sat in this.satellites) {

            }
            */
            return satDatas;
        }		
	};
	
	// Héritage
	for (var element in JMI.script.Base.prototype ) {
		if( !Swatch.prototype[element])
			Swatch.prototype[element] = JMI.script.Base.prototype[element];
	}
	
	return Swatch;
}());

// Constants
/**
 * Index of the title prop that can be reteived using JavaScript.
 * This should be deprecated as Javascript directly access to raw NAME propertie instead.
 */
JMI.script.Swatch.TITLE_VAL = 1;

/**
 * True if this holds one or more satellites linked to their parent (optimisation).
 */
JMI.script.Swatch.LINK_BIT = 0x02;