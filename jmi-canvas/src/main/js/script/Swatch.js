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
	};
	
	Swatch.prototype = {
		constructor: JMI.script.Swatch,
        
        /*
         * Draws the satellites of this that have the required flags enabled.
         * @param applet        Applet holding this.
         * @param s             Sprite to paint in.
         * @param zone          Zone to paint.
         * @param isCur         True if zone is hovered.
         * @param isFront       True to paint only satellites over the transparent filter. False to only paint those below.
         * @param showTyp       Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
         * @param showLinks     True if links between satelites should be drawn. False for the opposite.
         */
        paint: function(applet, s, zone, isCur, isFront, showTyp, showLinks) {
            var sat = this.satellites[0];
            var shape = sat._shape;
            var flags = getFlags(zone._props);
            var transfo = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone._props);
            
            // Draws Satellites links first (if they exists)
            // so they can be partly covered by other sats
            if (this.isEnabled(flags, JMI.script.LINK_BIT) && showLinks) {
                this.drawSats(applet, s, zone, shape, transfo, true, isCur, isFront, showTyp);
            }
          
            // Draws Satellites without links
            this.drawSats(applet, s, zone, shape, transfo, false, isCur, isFront, showTyp);
        },
        
        /*
         * Draws satellites that have the required flags enabled.
         * Those without transfo use a default transformation.
         * 
         * @param applet        Applet holding this.
         * @param s             Sprite to paint in.
         * @param zone          Zone to draw the sats.
         * @param shape         Default shape coming from the first satellite([0]).
         * @param transfo       Default transformation coming from the first satellite([0]).
         * @param isLinkOnly    True to draw only links between satelites.
         * @param isCur         True if zone is hovered.
         * @param isFront       True to paint only satellites over the transparent filter. False to only paint those below.
         * @param showTyp       Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
         */
        drawSats: function(applet, s, zone, shape, transfo, isLinkOnly, isCur, isFront, showTyp) {
            // TODO : portage, voir instanceof avec héritage
            var isBag = zone instanceof JMI.script.BagZone;
            var supZone = isBag ? zone : null;
            var zones = isBag ? supZone._subZones : null;
            var curZone= applet.plan._curZone,
                subZone;
            var sat = this.satellites[0];
            var satData = isCur ? zone._curData[0] : zone._restData[0];
            var satRelTrf, satTrf;
            var i, n = this.satellites.length,
                flags;
            var hasRestBit, hasCurBit, isCurSub;
            var satCtr,
                supCtr = shape.getCenter(zone);
            
            if (!this.isLinkOnly) {
                // Draws the place itself using the first Satellite
                sat.paint(applet, s, zone, null, null, false, satData, showTyp);
            }
            
            for (i = 1 ; i < n ; i++) {
                sat     = this.satellites[i];
                satData = isCur ? zone._curData[i] : zone._restData[i];
                flags   = satData._flags;
                
                if (((this.isLinkOnly && this.isEnabled(flags, JMI.script.Satellite.LINK_BIT)) || !this.isLinkOnly)
                    && this.isEnabled(flags, JMI.script.Satellite.VISIBLE_BIT)
                    // This Sat is visible
                    && (isFront != this.isEnabled(flags, JMI.script.Satellite.BACK_BIT))) {
                    
                    // Bags
                    if (isBag) {
                        hasRestBit  = this.isEnabled(flags, JMI.script.Satellite.REST_BIT);
                        hasCurBit   = this.isEnabled(flags, JMI.script.Satellite.CUR_BIT);
                        satRelTrf   = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone._props);
                        satTrf      = transfo != null ? transfo.transform(satRelTrf, true) : null;
                        
                        if(supZone._dir != 10.) {
                            if (!this.isEnabled(flags, JMI.script.Satellite.NOSIDED_BIT)) satTrf._dir = supZone._dir;
                            else {
                                if(this.isEnabled(supZone._flags, JMI.script.ActiveZone.LEFT_BIT)) satTrf._dir += (JMI.script.Base.Pi2 / 2);
                            }
                        }
                        
                        var dir = satTrf._dir;
                        
                        // draws SubZones
                        if (zones != null && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUB_BIT)) {
                            for (subZone in zones) {
                                satTrf._dir   += supZone._stp;
                                isCurSub       = subZone == curZone;
                                satData        = isCur ? subZone._curData[i] : subZone._restData[i];
                                
                                if ((!isCur || ((hasRestBit && !isCurSub) || (hasCurBit && isCurSub)))) {
                                    satCtr = shape.transformOut(zone, satTrf);
                                    sat.paint(applet, s, subZone, satCtr, supCtr, isLinkOnly, satData, showTyp);
                                }
                            }
                        }
                        
                        // draws SuperZone
                        if (this.isEnabled(flags, JMI.script.Satellite.SUPER_BIT)) {
                            isCurSub = supZone == curZone;
                            satData  = isCur ? zone._curData[i] : zone._restData[i];
                            
                            if (zones != null) satTrf._dir = dir;
                            
                            if ((!isCur || ((hasRestBit && !isCurSub) || (hasCurBit && isCurSub)))) {
                                satCtr = shape.transformOut(zone, satTrf);
                                sat.paint(applet, s, supZone, satCtr, supCtr, isLinkOnly, satData, showTyp);
                            }
                        }
                    }
                    
                    // links
                    else {
                        sat.paint(applet, s, zone, null, null, false, satData, showTyp);
                    }
                }
            }
        },        

        /*
         * Gets this bounds by merging the satellites bounds.
         * 
         * @param applet        The Applet that owns this.
         * @param g             A graphics to get the FontMetrics used by this.
         * @param zone          The zone that holds the properties used by this swatch.
         * @param isCurZone     True if zone is hovered.
         * 
         * @return              This swatch bounding box for zone.
         */
        getBounds: function(applet, g, zone, isCurZone) {
            var bounds = new JMI.script.Rectangle(0, 0, 0, 0);
            var sat = this.satellites[0];
            var shape = sat._shape;
            // TODO : portage, instanceof et heritage
            var isBag = zone instanceof JMI.script.BagZone;
            var supZone = isBag ? zone : null;
            var zones = isBag ? supZone._subZones : null;
            var subZone;
            var satRelTrf, satTrf,
                transfo = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone._props);
            var i, n = this.satellites.length,
                flags;
            //boolean         hasRestBit, hasCurBit, hasLinkBit, isCur;
            var satData;
            var satCtr,
                supCtr = shape.getCenter(zone);
            
            // Gets the bounds of the place itself using the first Satellite
            sat.setBounds(applet, g, zone, null, null, bounds);
            
            // Iterate through the swatch satellite list
            for (i = 1 ; i < n ; i ++) {
                sat         = this.satellites[i];
                satData     = isCurZone ? zone._curData[i] : zone._restData[i];
                flags       = satData._flags;
                
                // This Sat is visible
                if (JMI.script.Base.isEnabled(flags, JMI.script.Satellite.VISIBLE_BIT)) {
                    
                    // If this is a BagZone                    
                    if (isBag) {
                        //hasRestBit  = Base.isEnabled( flags, Satellite.REST_BIT );
                        //hasCurBit   = Base.isEnabled( flags, Satellite.CUR_BIT );
                        satRelTrf   = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone._props);
                        satTrf      = transfo.transform(satRelTrf, true);
                        
                        if (supZone._dir != 10.) satTrf._dir = supZone._dir;
                        
                        // Gets SuperZone bounds
                        if ((!this.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData._isVisible)
                            && Base.isEnabled(flags, JMI.script.Satellite.SUPER_BIT)) {
                            satCtr  = shape.transformOut(zone, satTrf);
                            sat.setBounds(applet, g, zone, satCtr, supCtr, bounds);
                        }
                        
                        // gets SubZones bounds
                        if (zones != null && JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SUB_BIT)) {
                            
                            // TODO : portage, see if this for loop works as expected
                            for (subZone in zones) {
                                satTrf._dir   += supZone._stp;
                                satData        = isCurZone ? subZone._curData[i] : subZone._restData[i];
                                flags          = satData._flags;
                                
                                if (!this.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData._isVisible) {
                                    satCtr  = shape.transformOut(zone, satTrf);
                                    sat.setBounds(applet, g, subZone, satCtr, supCtr, bounds);
                                }
                            }
                        }
                    }
        
                    // This is a LinkZone 
                    else {
                        sat.setBounds(applet, g, zone, null, null, bounds);
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
                        
            if(parentzone._bounds.contains(pos._x, pos._y)) {
                var sat     = this.satellites[0];
                var shape   = sat._shape;
                var isBag   = zone instanceof JMI.script.BagZone;
                var supZone = isBag ? zone : null;
                var zones= isBag ? supZone._subZones : null;
                var curZone= planComponent.plan._curZone;
                var subZone;
                var satRelTrf, satTrf;
                var transfo= sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone._props);
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
                    satData = isCurZone ? zone._curData[i] : zone._restData[i];
                    flags   = satData._flags;
                    
                    // This Sat is visible and it's not a tip (avoid anoying place popup!)
                    if (isEnabled(flags, JMI.script.Satellite.VISIBLE_BIT) && 
                       (isCurZone || !this.isEnabled(flags, JMI.script.Satellite.TIP_BIT))) {
                        
                        isVisible   = !this.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData._isVisible;
                        
                        // If it's a BagZone
                        if(isBag) {
                            hasCurBit = this.isEnabled(flags, JMI.script.Satellite.CUR_BIT);
                            hasSubBit = this.isEnabled(flags, JMI.script.Satellite.SUB_BIT);
                            satRelTrf = sat.getTransfo(JMI.script.Satellite.TRANSFO_VAL, zone._props);
                            
                            if (zones != null && hasSubBit && hasCurBit && satRelTrf != null && satRelTrf._pos == 0.) {
                                if (isVisible && sat.contains(planComponent, g, zone, null, null, transfo, pos, true, true)) {
                                    return sat;
                                }
                                else {
                                    continue;
                                }
                            }
                            
                            
                            hasRestBit  = this.isEnabled(flags, JMI.script.Satellite.REST_BIT);
                            satTrf      = transfo.transform(satRelTrf, true);
                            
                            if (isBag && supZone._dir != 10.) satTrf._dir = supZone._dir;
                            
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
                                satTrf._dir += (zones.length + 1) * supZone._stp;
                                
                                for(j = m - 1 ; j >= 0 ; j --) {
                                    subZone       = zones[j];
                                    satTrf._dir  -= supZone._stp;
                                    isCur         = subZone == curZone;
                                    satData       = isCurZone ? subZone._curData[i] : subZone._restData[i];
                                    flags         = satData._flags;
                                    isVisible     = !this.isEnabled(flags, JMI.script.Satellite.SEL_BIT) || satData._isVisible;
                                    
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
            var flags;
            var isTip, isSel;
            
            // TODO : portage boulce for, iteration sur les éléments d'un tableau
            for (var sat in this.satellites) {
                satData = new JMI.script.SatData();
                flags   = sat.getFlags(zone._props);
                satData._flags = flags;
                
                isTip = this.isEnabled(flags, JMI.script.Satellite.TIP_BIT);
                isSel = this.isEnabled(flags, JMI.script.Satellite.SEL_BIT);
                
                if (isTip || isSel) {
                    var sels  = sat.parseString(JMI.script.Satellite.SELECTION_VAL, zone._props);
                    var sel = -1;
                    
                    if (sels != null) {
                        if(applet.env._selections[sels[0]] != null)
                            sel = applet.env._selections[sels[0]];
                    }
                    
                    satData._isVisible = sat.isVisible(zone, isTip, applet.plan._curSel, sel);
                }
                else {
                    satData._isVisible = true;
                }
                
                // TODO : portage, equivalent de push sur array
                satDatas.push(satData);
            }
            return satDatas;
        }		
	};
	
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