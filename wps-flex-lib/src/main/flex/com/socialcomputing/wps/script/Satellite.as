package com.socialcomputing.wps.script  {
    import com.socialcomputing.wps.components.Map;
    
    import flash.display.Graphics;
    import flash.display.Sprite;
    import flash.geom.Point;
    import flash.geom.Rectangle;
    
    import mx.collections.ArrayCollection;
    import mx.controls.Menu;
    import mx.core.ClassFactory;
    import mx.events.MenuEvent;
    
    /**
     * <p>Title: Satellite</p>
     * <p>Description: An elementary part of a swatch that can be stacked.<br>
     * Each Satellite contains many flags to describe how to display it.
     * It also contains many container to manage the positioning, events, selection and link to parent.
     * A Shape and a table of slices are also necessary to know how to draw this.</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p>Company: MapStan (Voyez Vous)</p>
     * @author flugue@mapstan.com
     * @version 1.0
     */
    public class Satellite extends Base
    {
        /**
         * Index of the Transfo prop in VContainer table.
         * If there is no Transfo it is copied from the default one (first Satellite of the swatch).
         */
        public static const TRANSFO_VAL:int= 1;
        
        /**
         * Index of the hovered event prop in VContainer table
         */
        public static const HOVER_VAL:int= 2;
        
        /**
         * Index of the click event prop in VContainer table
         */
        public static const CLICK_VAL:int= 3;
        
        /**
         * Index of the double click event prop in VContainer table.
         */
        public static const DBLCLICK_VAL:int= 4;
        
        /**
         * Index of the selection prop in VContainer table if this is a selection Satellite.
         */
        public static const SELECTION_VAL:int= 5;
        
        /**
         * Index of the dark link color prop in VContainer table.
         */
        public static const LINK_DRK_COL_VAL:int= 6;
        
        /**
         * Index of the link color prop in VContainer table.
         */
        public static const LINK_NRM_COL_VAL:int= 7;
        
        /**
         * Index of the bright link color prop in VContainer table.
         */
        public static const LINK_LIT_COL_VAL:int= 8;
        
        /**
         * True if this Satellite is visible.
         * It can be interesting to create fake invisible satellite (even if it has never been tested).
         */
        public static const VISIBLE_BIT:int= 0x001;
        
        /**
         * This has a link with its parent.
         * Useful for creating s�lection tips.
         */
        public static const LINK_BIT:int= 0x002;
        
        /**
         * This can only be visible on a SuperZone (a zone that clusterize the others).
         */
        public static const SUPER_BIT:int= 0x004;
        
        /**
         * This can only be visible on a SubZone (a clusterized zone).
         */
        public static const SUB_BIT:int= 0x008;
        
        /**
         * This can only be visible on a current zone (a hovered zone).
         */
        public static const CUR_BIT:int= 0x010;
        
        /**
         * This can only be visible on a rest zone (a not hovered zone).
         */
        public static const REST_BIT:int= 0x020;
        
        /**
         * This is visible under the filter of the current zone.
         */
        public static const BACK_BIT:int= 0x040;
        
        /**
         * This is a Selection sat.
         * To know that this sat should always stay on top of the others.
         */
        public static const SEL_BIT:int= 0x080;
        
        /**
         * This is a Tip sat.
         * To know that this sat should not be tested when bounds are evaluated.
         */
        public static const TIP_BIT:int= 0x100;
        
        /**
         * This is sat can't be right or left sided.
         */
        public static const NOSIDED_BIT:int= 0x200;
        
        /**
         * Draws only Selection sats.
         */
        public static const SEL_TYP:int= 0;
        
        /**
         * Draws only Tip sats.
         */
        public static const TIP_TYP:int= 1;
        
        /**
         * Draws all sats but Selection and tips ones.
         */
        public static const BASE_TYP:int= 2;
        
        /**
         * Draws all sats.
         */
        public static const ALL_TYP:int= 3;
        
        /**
         * Shape used to draw this.
         * This can be a simple dot, a disk, a rectangle or a polygon.
         */
        public var m_shape:ShapeX;
        
        /**
         * The elementary slices that are stacked to draw this satellite.
         * They describe how to fill the shape.
         */
		public   var m_slices:Vector.<Slice>;
        
        /**
         * Creates a Satellite with its shape and slices.
         * @param shape		A shape that is filled by the slices.
         * @param slices	A table of slices used to render this.
         */
        public function Satellite( shape:ShapeX, slices:Vector.<Slice>)
        {
            m_shape     = shape;
            m_slices    = slices;
        }
        
        /**
         * Returns wether this satellite is visible.
         * @param zone		Zone from which the satellite belongs.
         * @param isTip		True if this is a Tip.
         * @param curSel	Identifier of the current active selection on the Plan.
         * @param sel		Identifier of this satellite selection or -1 if there is none.
         * @return			True if this satellite is visible, false otherwise.
         */
        public function isVisible( zone:ActiveZone, isTip:Boolean, curSel:int, sel:int):Boolean {
            var hasSel:Boolean= curSel >= 0,
                isSel:Boolean= isEnabled( zone.m_selection, 1<< curSel );
            
            return isTip ? !hasSel || !isSel : hasSel && sel == curSel && isSel;
        }
        
        /**
         * Draws this satellite on a Graphics.
         * It's position and size is evaluated by its parent and transfo.
         * A type filtering can be applied to select a special kind of satellite.
         * @param applet		The Applet that owns this.
         * @param g				A graphics to draw this in.
         * @param zone			The zone that holds the properties used by this satellite.
         * @param satCtr		This satellite center.
         * @param supCtr		This parent satellite center.
         * @param isLinkOnly	True to paint only the link between this and its parent if it exists.
         * @param satData		This satellite data buffer.
         * @param showTyp		The type of satellite to display.[ALL_TYP,BASE_TYP,TIP_TYP,SEL_TYP]
         * @throws UnsupportedEncodingException 
         */
        public function paint(applet:Map, s:Sprite, zone:ActiveZone, satCtr:Point, supCtr:Point, isLinkOnly:Boolean, satData:SatData, showTyp:int):void {
			var flags:int= satData.m_flags;
            var isTip:Boolean		= isEnabled( flags, Satellite.TIP_BIT ),
                isSel:Boolean       = isEnabled( flags, Satellite.SEL_BIT ),
                isVisible:Boolean   = isTip || isSel ? satData.m_isVisible : true;
            
            var supZone:ActiveZone = zone.getParent();
            
            if ( isVisible )
            {
                if ( isLinkOnly )               // we must draw this Satellite Link if it exists
                {
                    if ( isEnabled( flags, LINK_BIT ))    // This has a Link
                    {
                        var x1:int = supCtr.x,
                            y1:int = supCtr.y,
                            x2:int = satCtr.x,
                            y2:int = satCtr.y;
                        setColor( s, LINK_DRK_COL_VAL, zone.m_props );
                        //g.drawLine( x1, y1 + 1, x2, y2 + 1);
                        s.graphics.moveTo(x1, y1 + 1);
                        s.graphics.lineTo(x2, y2 + 1);
                        if ( setColor( s, LINK_LIT_COL_VAL, zone.m_props ))
                        {
                            //g.drawLine( x1 - 1, y1, x2 - 1, y2 );
                            s.graphics.moveTo(x1 - 1, y1);
                            s.graphics.lineTo(x2 - 1, y2);
                            //g.drawLine( x1, y1, x2, y2 );
                            s.graphics.moveTo(x1, y1);
                            s.graphics.lineTo(x2, y2);
                            //g.drawLine( x1 + 1, y1, x2 + 1, y2 );
                            s.graphics.moveTo(x1 + 1, y1 );
                            s.graphics.lineTo(x2 + 1, y2);
                            
                            setColor( s, LINK_NRM_COL_VAL, zone.m_props );
                            //g.drawLine( x1, y1 - 1, x2, y2 - 1);
                            s.graphics.moveTo(x1, y1 - 1);
                            s.graphics.lineTo(x2, y2 - 1);
                        }
                    }
                }
                else
                {
					
                    var isShowable:Boolean= isSel;
                    
                    switch ( showTyp )
                    {
                        case ALL_TYP    : isShowable = true; break;
                        case BASE_TYP   : isShowable = !( isTip || isSel ); break;
                        case TIP_TYP    : isShowable = isTip; break;
                        //			case SEL_TYP :  return isSel;
                    }
                    if ( isShowable )
                    {
						for each( var slice:Slice in m_slices)
                        {
                            slice.paint( applet, s, supZone, zone, m_shape, satCtr, supCtr );
                        }
                    }
                }
            }
        }
        
        /**
         * Return wether a point is inside this Satellite
		 * 
         * @param planComponent		The PlanComponent that owns this.
         * @param g					A graphics to get the FontMetrics used by this.
         * @param zone				The zone that holds the properties used by this satellite.
         * @param satCtr			This satellite center.
         * @param supCtr			This parent satellite center.
         * @param transfo			The transformation that give the position and scale of this using its parent.
         * @param pos				A point position to test.
         * @param isPie				True if this is in the 'pie' part of this satellite.
         * @param isFake			True if this is the first (main) Satellite.
		 * 
         * @return					True if the cursor's position is inside this satellite, false otherwise.
         */
        public function contains(planComponent:Map, g:Graphics, zone:ActiveZone, satCtr:Point, 
								 supCtr:Point, transfo:Transfo, pos:Point, isPie:Boolean, isFake:Boolean):Boolean {
            //trace("[Satellite contains method called]");
			var i:int, n:int = m_slices.length;
            // If the parent satellite center is not set, take this satellite's shape center as center
            if(supCtr == null) supCtr = m_shape.getCenter(zone);
            
			// Iterate throw all this satellite's slices and check if one of them contains the cursor's position
			// Stop if it's the case 
            for(i = 0 ; (i < n && !m_slices[i].contains(planComponent, g, zone.getParent(), zone, m_shape, satCtr, supCtr, pos)) ; i++);
            
			// if the cursor's position is in one of the slices
            if(i < n) {
				planComponent.plan.m_newZone = zone;
                if (isPie) {
                    var supZone:BagZone = zone as BagZone;
                    var zones:Array     = supZone.m_subZones;
                    var nbZones:int     = zones.length + 1;
                    
                    var center:Point = isFake ? m_shape.getCenter(supZone) : supCtr;
                    var dir:Number   = (supZone.m_dir != 10.) ? supZone.m_dir : transfo.m_dir,
                        step:Number  = supZone.m_stp,
                        m:Number     = .5 * (Pi2 / step - nbZones),
                        a:Number     = Math.atan2(pos.y - center.y, pos.x - center.x);
                    
                    if (dir < 0)  dir += Pi2;
                    if (a < 0)    a   += Pi2;
                    if (a < dir)  a   += Pi2;
                    
                    a = .5+(a - dir) / step;
                    i = int( a);
                    
                    if (i > 0) {
                        if (i < nbZones) {
							planComponent.plan.m_newZone = zones[i-1];
                        }
                        else if (a - nbZones < m) {
							planComponent.plan.m_newZone = zones[nbZones-2];
                        }
                    }
                }
				//trace("[Satellite contains method end, return value = true]");
                return true;
            }
			//trace("[Satellite contains method end, return value = false]");
            return false;
        }
        

		/**
         * Sets this bounds by updating an already created Rectangle.
         * @param applet		The Applet that owns this.
         * @param g				A graphics to get the FontMetrics used by this.
         * @param zone			The zone that holds the properties used by this satellite.
         * @param satCtr		This satellite center.
         * @param supCtr		This parent satellite center.
         * @param bounds		A Rectangle to merge with this bounds.
         * @throws UnsupportedEncodingException 
         */
        public function setBounds(applet:Map, g:Graphics, zone:ActiveZone,
								  satCtr:Point, supCtr:Point, bounds:Rectangle):void {
			for each(var slice:Slice in m_slices) {
                slice.setBounds(applet, g, zone.getParent(), zone, m_shape, satCtr, supCtr, bounds);
				
            }
			// DEBUG
			// var fSlice:Slice = m_slices[0];
			// fSlice.setBounds(applet, g, zone.getParent(), zone, m_shape, satCtr, supCtr, bounds);
			/*
			if(zone is LinkZone) {
				g.lineStyle(1, 0x00FF00);
				g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
			}
			*/
			// END DEBUG
        }
        
		
        /**
         * Execute one or more actions matching an event.
         * The actions are stored in this satellite, one list for each of the 3 possible events.
         * They are executed using there declaration order in the list. They can be one of this:
         * <ul>
         * <li>show				Shows a message in the StatusBar.</li>
         * <li>open				Opens an URL in a new window or a frame.</li>
         * <li>popup			Popup a menu at the cursor position.</li>
         * <li>pop				Pops a tooltip near the cursor position.</li>
         * <li>play				Plays a sound file.</li>
         * <li>dump				Dumps a text on the Java Console.</li>
         * </ul>
         * @param applet		The Applet that owns this.
         * @param zone			The zone that holds the properties used by this satellite.
         * @param pos			The current cursor position. Used to popup a menu.
         * @param actionId		Type of event that triggers the action.[HOVER_VAL,CLICK_VAL,DBLCLICK_VAL].
         */
        public function execute(applet:Map, zone:ActiveZone, pos:Point, actionId:int):void {
            var firstSat:Satellite= zone.m_curSwh.m_satellites[0];
            var isExe:Boolean= isDefined( actionId );
            
            if ( isExe )
            {
                var actionStr:String= getString( actionId, zone.m_props );
                
                if ( actionStr != null )
                {
                    var actions:Array= getString( actionId, zone.m_props ).split("\n");
                    var action:String, func:String, args:String;
                    var i:int, j:int, n:int = actions.length;
                    
                    for ( i = 0; i < n; i ++ )
                    {
                        action  = actions[i];
                        j       = action.indexOf( ' ' );
                        func    = action.substring( 0, j );
                        args    = parseString3( action.substring( j + 1, action.length), zone.m_props )[0];
                        
                        if ( func == ( "show" ))         // Shows a message in the StatusBar
                        {
                            applet.showStatus( args );
                        }
                        else if ( func == ( "open" ))      // Go to a page, opening a new browser window
                        {
                            j   = args.indexOf( SUBSEP );
                            
                            if ( j != -1)  // tracking
                            {
                                args    = args.substring( j, args.length);
                            }
                            
                            applet.actionPerformed( args );
                        }
                        else if ( func == ( "popup" ))    // Popup a menu
                        {
                            var menux:MenuX= zone.m_curSwh.m_refs[args] as MenuX;
                            
                            if ( menux != null )
                            {
								var menuData:ArrayCollection = new ArrayCollection();
								menux.parseMenu( menuData, zone );
								var menu:Menu = Menu.createMenu( applet, menuData, false);
                                menu.variableRowHeight = true;
                                menu.labelField = "label";
                                //menu.setStyle("fontWeight", "bold");
								menu.addEventListener(MenuEvent.ITEM_CLICK, applet.menuHandler);								
								var point:Point = applet.localToGlobal(pos);
								menu.show( point.x, point.y );
							}
                        }
						// Pop a tooltip
                        else if (func == ("pop")) {
                            var slice:Slice = zone.m_curSwh.m_refs[args] as Slice;
                            
							if (slice != null) {
                                var delay:int     = slice.getInt(Slice.DELAY_VAL, zone.m_props);
                                var length:int    = slice.getInt(Slice.LENGTH_VAL, zone.m_props);
								//var text:HTMLText = slice.getText(Slice.TEXT_VAL, zone.m_props);
								//applet.toolTip    = text.parseString(HTMLText.TEXT_VAL, zone.m_props).join("\n");
								applet.plan.popSlice( zone, slice, delay, length, args);
                            }
                        }
                        else if ( func == ( "play" ))    // Plays a sound in .au Sun audio format
                        {
							// TODO
/*                            var clip:AudioClip= AudioClip(applet.m_env.m_medias[ args ]);
                            
                            if ( clip == null )
                            {
                                clip = applet.getAudioClip( applet.getCodeBase(), args );
                                clip.play();
                                applet.m_env.m_medias.put( args, clip );
                            }*/
                        }
                        else if ( func == ( "dump" ))   // Print a string in the console
                        {
                            trace( args );
                        }
                    }
                }
            }
            else if ( this != firstSat )
            {
                firstSat.execute( applet, zone, pos, actionId );
            }
        }
        
        public function getShape():ShapeX {
            return m_shape;
        }
        
        public function getSlices():Vector.<Slice> {
            return m_slices;
        }
    }
}