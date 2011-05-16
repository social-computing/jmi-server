package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.PlanComponent;
    
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.Point;
    import flash.geom.Rectangle;

    /**
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
    public class Swatch extends Base
    {
        /**
         * Index of the title prop that can be reteived using JavaScript.
         * This should be deprecated as Javascript directly access to raw NAME propertie instead.
         */
        public const TITLE_VAL:int = 1;
        
        /**
         * True if this holds one or more satellites linked to their parent (optimisation).
         */
        public const LINK_BIT:int = 0x02;
        
        /**
         * Objects references (MenuX, Slice...) created by the server side.
         * This is used by the events to find menu or slices to pop.
         */
        public var m_refs:Array;
        
        /**
         * Layers of satellites that describes this.
         * Satellites are where the real information of this is.
         */
        public var m_satellites:Vector.<Satellite>;
        
        /**
         * Creates a new Swatch by filling its satellite table.
         * The first one (index 0) hold default values (tranformation and events).
         * @param satellites	Satellites describing this Swatch.
         */
        public function Swatch( satellites:Vector.<Satellite>)
        {
            m_satellites    = satellites;
        }
        
        public function getSatellites():Vector.<Satellite> {
            return m_satellites;
        }
        
        /**
         * Draws the satellites of this that have the required flags enabled.
         * @param applet		Applet holding this.
         * @param g				Graphics to paint in.
         * @param zone			Zone to paint.
         * @param isCur			True if zone is hovered.
         * @param isFront		True to paint only satellites over the transparent filter. False to only paint those below.
         * @param showTyp		Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
         * @param showLinks		True if links between satelites should be drawn. False for the opposite.
         * @throws UnsupportedEncodingException 
         */
        public function paint(applet:PlanComponent, s:Sprite, zone:ActiveZone, isCur:Boolean, isFront:Boolean, showTyp:int, showLinks:Boolean):void {
            trace("[Swatch paint method called]");
			var sat:Satellite = m_satellites[0];
            var shape:ShapeX = sat.m_shape;
            var flags:int = getFlags(zone.m_props);
            var transfo:Transfo = sat.getTransfo(Satellite.TRANSFO_VAL, zone.m_props.m_propss);
            
            // Draws Satellites links first (if they exists)
            // so they can be partly covered by other sats
            if ( isEnabled( flags, LINK_BIT )&& showLinks )
            {
                trace("Drawing satellites links");
                drawSats( applet, s, zone, shape, transfo, true, isCur, isFront, showTyp );
            }
          
            // Draws Satellites without links
            drawSats( applet, s, zone, shape, transfo, false, isCur, isFront, showTyp );
			trace("[Swatch paint end]");
        }
        
        /**
         * Draws satellites that have the required flags enabled.
         * Those without transfo use a default transformation.
		 * 
         * @param applet		Applet holding this.
         * @param g				Graphics to paint in.
         * @param zone			Zone to draw the sats.
         * @param shape			Default shape coming from the first satellite([0]).
         * @param transfo		Default transformation coming from the first satellite([0]).
         * @param isLinkOnly	True to draw only links between satelites.
         * @param isCur			True if zone is hovered.
         * @param isFront		True to paint only satellites over the transparent filter. False to only paint those below.
         * @param showTyp		Flags indicating what type of satellite to draw.(Satellite.XXX_TYP)
         */
        protected function drawSats(applet:PlanComponent, s:Sprite, zone:ActiveZone, shape:ShapeX, 
									transfo:Transfo, isLinkOnly:Boolean, isCur:Boolean, isFront:Boolean, showTyp:int):void {
            var isBag:Boolean= zone is BagZone;
            var supZone:BagZone= isBag ? BagZone(zone ): null;
            var zones:Array= isBag ? supZone.m_subZones : null;
            var curZone:ActiveZone= applet.plan.m_curZone,
				subZone:ActiveZone;
            var sat:Satellite= m_satellites[0];
            var satData:SatData= isCur ? zone.m_curData[0] : zone.m_restData[0];
            var satRelTrf:Transfo, satTrf:Transfo;
            var i:int, n:int        = m_satellites.length,
                flags:int;
            var hasRestBit:Boolean, hasCurBit:Boolean, isCurSub:Boolean;
            var satCtr:Point,
            supCtr:Point      = shape.getCenter( zone );
            
            if ( !isLinkOnly )
            {
                // Draws the place itself using the first Satellite
                sat.paint(applet, s, zone, null, null, false, satData, showTyp);
            }
            
            for ( i = 1; i < n; i ++ )
            {
                sat     = m_satellites[i];
                satData = isCur ? zone.m_curData[i] : zone.m_restData[i];
                flags   = satData.m_flags;
                
                if ((( isLinkOnly && isEnabled( flags, Satellite.LINK_BIT ))|| !isLinkOnly )&&
                    isEnabled( flags, Satellite.VISIBLE_BIT )&&
                    ( isFront != isEnabled( flags, Satellite.BACK_BIT ))) // This Sat is visible
                {
                    if ( isBag )
                    {
                        hasRestBit  = isEnabled( flags, Satellite.REST_BIT );
                        hasCurBit   = isEnabled( flags, Satellite.CUR_BIT );
                        satRelTrf   = sat.getTransfo( Satellite.TRANSFO_VAL, zone.m_props );
                        satTrf      = transfo != null ? transfo.transform( satRelTrf, true ) : null;
                        
                        if( supZone.m_dir != 10.)
                        {
                            if( !isEnabled( flags, Satellite.NOSIDED_BIT))
                                satTrf.m_dir = supZone.m_dir;
                            else 
                            {
                                if( isEnabled( supZone.m_flags, ActiveZone.LEFT_BIT))
                                    satTrf.m_dir += (Base.Pi2 / 2);
                            }
                            
                        }
                        
                        var dir:Number= satTrf.m_dir;
                        
                        if ( zones != null && Base.isEnabled( flags, Satellite.SUB_BIT ))   // draws SubZones
                        {
							for each( subZone in zones)
                            {
                                satTrf.m_dir   += supZone.m_stp;
                                isCurSub        = subZone == curZone;
                                satData         = isCur ? subZone.m_curData[i] : subZone.m_restData[i];
                                
                                if (( !isCur ||(( hasRestBit && !isCurSub )||( hasCurBit && isCurSub ))))
                                {
                                    satCtr  = shape.transformOut( zone, satTrf );
                                    sat.paint( applet, s, subZone, satCtr, supCtr, isLinkOnly, satData, showTyp  );
                                }
                            }
                        }
                        
                        if ( isEnabled( flags, Satellite.SUPER_BIT )) // draws SuperZone
                        {
                            isCurSub    = supZone == curZone;
                            satData = isCur ? zone.m_curData[i] : zone.m_restData[i];
                            
                            if ( zones != null )    satTrf.m_dir = dir;
                            
                            if (( !isCur ||(( hasRestBit && !isCurSub )||( hasCurBit && isCurSub ))))
                            {
                                satCtr  = shape.transformOut( zone, satTrf );
                                sat.paint( applet, s, supZone, satCtr, supCtr, isLinkOnly, satData, showTyp  );
                            }
                        }
                    }
                    else	// links
                    {
                        sat.paint( applet, s, zone, null, null, false, satData, showTyp );
                    }
                }
            }
        }
        
        /**
         * Gets this bounds by merging the satellites bounds.
		 * 
         * @param applet		The Applet that owns this.
         * @param g				A graphics to get the FontMetrics used by this.
         * @param zone			The zone that holds the properties used by this swatch.
         * @param isCurZone		True if zone is hovered.
		 * 
         * @return				This swatch bounding box for zone.
         */
         public function getBounds(applet:PlanComponent, g:Graphics, zone:ActiveZone, isCurZone:Boolean):Rectangle {
            var bounds:Rectangle = new Rectangle();
            var sat:Satellite    = m_satellites[0];
            var shape:ShapeX     = sat.m_shape;
            var isBag:Boolean    = zone is BagZone;
            var supZone:BagZone  = isBag ? BagZone(zone) : null;
            var zones:Array      = isBag ? supZone.m_subZones : null;
            var subZone:ActiveZone;
            var satRelTrf:Transfo, satTrf:Transfo,
            	transfo:Transfo  = sat.getTransfo(Satellite.TRANSFO_VAL, zone.m_props);
            var i:int, n:int     = m_satellites.length,
                flags:int;
            //boolean         hasRestBit, hasCurBit, hasLinkBit, isCur;
            var satData:SatData;
            var satCtr:Point,
            	supCtr:Point     = shape.getCenter(zone);
            
            // Gets the bounds of the place itself using the first Satellite
            sat.setBounds(applet, g, zone, null, null, bounds);
			
			//			}
			//          for (i = 1 ; i < n ; i ++) {
			//            sat         = m_satellites[i];
			
			// Iterate through the swatch satellite list
			for each(sat in m_satellites) {
                satData     = isCurZone ? zone.m_curData[i] : zone.m_restData[i];
                flags       = satData.m_flags;
                
				// This Sat is visible
                if (Base.isEnabled(flags, Satellite.VISIBLE_BIT)) {
					// If this is a BagZone                    
					if (isBag) {
                        //hasRestBit  = Base.isEnabled( flags, Satellite.REST_BIT );
                        //hasCurBit   = Base.isEnabled( flags, Satellite.CUR_BIT );
                        satRelTrf   = sat.getTransfo( Satellite.TRANSFO_VAL, zone.m_props );
                        satTrf      = transfo.transform( satRelTrf, true );
                        
                        if ( supZone.m_dir != 10.)  satTrf.m_dir = supZone.m_dir;
                        
						// Gets SuperZone bounds
                        if ((!isEnabled(flags, Satellite.SEL_BIT) || satData.m_isVisible)
                            && Base.isEnabled(flags, Satellite.SUPER_BIT)) {
                            satCtr  = shape.transformOut(zone, satTrf);
                            sat.setBounds(applet, g, zone, satCtr, supCtr, bounds);
                        }
                        
						// gets SubZones bounds
                        if ( zones != null && Base.isEnabled( flags, Satellite.SUB_BIT ))   
                        {
							for each( subZone in zones)
                            {
                                satTrf.m_dir   += supZone.m_stp;
                                satData         = isCurZone ? subZone.m_curData[i] : subZone.m_restData[i];
                                flags           = satData.m_flags;
                                
                                if ( !isEnabled( flags, Satellite.SEL_BIT )|| satData.m_isVisible )
                                {
                                    satCtr  = shape.transformOut( zone, satTrf );
                                    sat.setBounds( applet, g, subZone, satCtr, supCtr, bounds );
                                }
                            }
                        }
                    }

					// This is a LinkZone 
                    else {
                        sat.setBounds( applet, g, zone, null, null, bounds );
                    }
                }
            }
			// DEBUG
			/*
			if (!isBag) {
				g.lineStyle(1, 0xFF0000);
				g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
			*/
			// END DEBUG
            return bounds;
        }
        
        /**
         * Gets the satellite under the cursor if it is in this zone swatch or null if it isn't.
		 * 
         * @param planComponent		The Applet that owns this.
         * @param g				A graphics to get the FontMetrics used by this.
         * @param zone			Zone to check the satellites.
         * @param pos			Location of the cursor.
         * @param isCurZone		True if zone is the current one.
         * @return				The sat of this swatch that is hovered or null if there isn't.
         * @throws UnsupportedEncodingException 
         */
        public function getSatAt(planComponent:PlanComponent, g:Graphics, zone:ActiveZone, pos:Point, isCurZone:Boolean):Satellite {
			//trace("[Swatch getSatAt method called]");
			
			// The cursor position is in the Bounding Box
			var parentzone:ActiveZone = zone.getParent();		
			
			// DEBUG
			// Drawing sensitive zone
			/*
			g.lineStyle(1, 0x000000);
			g.drawRect(parentzone.m_bounds.x, parentzone.m_bounds.y,
				       parentzone.m_bounds.x + parentzone.m_bounds.width, parentzone.m_bounds.y + parentzone.m_bounds.height);
			*/
			// END DEBUG
			
			if(parentzone.m_bounds.contains(pos.x, pos.y))      
            {
                var sat:Satellite = m_satellites[0];
                var shape:ShapeX = sat.m_shape;
                var isBag:Boolean = zone is BagZone;
                var supZone:BagZone = isBag ? zone as BagZone : null;
                var zones:Array = isBag ? supZone.m_subZones : null;
                var curZone:ActiveZone = planComponent.plan.m_curZone,
                    subZone:ActiveZone;
                var satRelTrf:Transfo, satTrf:Transfo,
                	transfo:Transfo = sat.getTransfo( Satellite.TRANSFO_VAL, zone.m_props );
                var i:int, 
				    n:int = m_satellites.length,
                    flags:int;
                var hasRestBit:Boolean,
				    hasCurBit:Boolean,
					hasSubBit:Boolean,
					isCur:Boolean,
					isVisible:Boolean;
                var satData:SatData;
                var supCtr:Point = shape.getCenter( zone );
                
				// Iterate throw all this swatch's satellites to test if the cursor is positionned in one of them
                for(i = n - 1 ; i > 0; i --) {
                    sat     = m_satellites[i];
                    satData = isCurZone ? zone.m_curData[i] : zone.m_restData[i];
                    flags   = satData.m_flags;
                    
					// This Sat is visible and it's not a tip (avoid anoying place popup!)
                    if (isEnabled(flags, Satellite.VISIBLE_BIT) && 
					   (isCurZone || !isEnabled(flags, Satellite.TIP_BIT))) {
						
                        isVisible   = !isEnabled( flags, Satellite.SEL_BIT ) || satData.m_isVisible;
                        
						// If it's a BagZone
                        if(isBag)
                        {
                            hasCurBit   = isEnabled( flags, Satellite.CUR_BIT );
                            hasSubBit   = isEnabled( flags, Satellite.SUB_BIT );
                            satRelTrf   = sat.getTransfo( Satellite.TRANSFO_VAL, zone.m_props );
                            
                            if ( zones != null && hasSubBit && hasCurBit && satRelTrf != null && satRelTrf.m_pos == 0.)
                            {
                                if (isVisible && sat.contains(planComponent, g, zone, null, null, transfo, pos, true, true)) {
                                    return sat;
                                }
                                else {
                                    continue;
                                }
                            }
                            
                            
                            hasRestBit  = isEnabled( flags, Satellite.REST_BIT );
                            satTrf      = transfo.transform( satRelTrf, true );
                            
                            if ( isBag && supZone.m_dir != 10.)  satTrf.m_dir = supZone.m_dir;
                            
							// Test if the cursor is the super zone
                            if ( Base.isEnabled( flags, Satellite.SUPER_BIT ))  
                            {
                                isCur = supZone == curZone;
                                if (isVisible &&
									((hasRestBit && !isCur) || (hasCurBit && isCur)) &&
									sat.contains(planComponent, g, zone, shape.transformOut( zone, satTrf ), supCtr, satTrf, pos, false, false )) {
                                    return sat;
                                }
                            }
                            
							// Or if the cursor is in one of the sub zones 
                            if (zones != null && hasSubBit) {
                                var j:int, m:int = zones.length;
                                satTrf.m_dir += (zones.length + 1) * supZone.m_stp;
                                
                                for(j = m - 1 ; j >= 0 ; j --) {
                                    subZone        = zones[j];
                                    satTrf.m_dir  -= supZone.m_stp;
                                    isCur          = subZone == curZone;
                                    
                                    satData     = isCurZone ? subZone.m_curData[i] : subZone.m_restData[i];
                                    flags       = satData.m_flags;
                                    isVisible   = !isEnabled(flags, Satellite.SEL_BIT)|| satData.m_isVisible;
                                    
                                    if(isVisible &&
									   ((hasRestBit && !isCur) || (hasCurBit && isCur)) &&
									   sat.contains(planComponent, g, subZone, shape.transformOut(zone, satTrf), supCtr, satTrf, pos, false, false)) {
                                       return sat;
                                    }
                                }
                            }
                        }
						
						// Else, if it's a link zone
                        else {
                            if(isVisible &&
							   sat.contains(planComponent, g, zone, null, null, transfo, pos, false, true)) {
                            	return sat;
                            }
                        }
                    }
                }
				
                // Tests if the cursor is located in the place itself
                sat = m_satellites[0];
                if(sat.contains(planComponent, g, zone, null, null, transfo, pos, zones != null, true ) ||
                   (isCurZone && !(zone is LinkZone))) {
                    return sat;
                }
            }
            return null;
        }
        
        /**
         * Evaluate this swatch satellites data buffers for a zone.
         * @param applet		The Applet that owns this.
         * @param zone			Zone holding satellites.
         * @param isSuper		True if zone is a BagZone.
         * @return				An array of satellite data.
         * @throws UnsupportedEncodingException 
         */
        public function evalSatData(applet:PlanComponent, zone:ActiveZone, isSuper:Boolean):Vector.<SatData>
        {
            var satDatas:Vector.<SatData>= new Vector.<SatData>();
            var satData:SatData;
            var flags:int;
            var isTip:Boolean, isSel:Boolean;
            
            for each( var sat:Satellite in m_satellites)
            {
                satData = new SatData();
                flags   = sat.getFlags( zone.m_props );
                satData.m_flags     = flags;
                
                isTip   = isEnabled( flags, Satellite.TIP_BIT );
                isSel   = isEnabled( flags, Satellite.SEL_BIT );
                
                if ( isTip || isSel )
                {
                    var sels:Vector.<String> = sat.parseString( Satellite.SELECTION_VAL, zone.m_props );
                    var sel:int= -1;
                    
                    if ( sels != null )
                    {
                        var selId:int= int(applet.env.m_selections[sels[0]]);
                        
                        sel = isNaN(selId) ? selId : -1;
                    }
                    
                    satData.m_isVisible = sat.isVisible( zone,  isTip, applet.plan.m_curSel, sel );
                }
                else
                {
                    satData.m_isVisible = true;
                }
                
                satDatas.push(satData);
            }
            
            return satDatas;
        }
    }
}