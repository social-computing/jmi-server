JMI.namespace("script.Satellite");

/*
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
JMI.script.Satellite = (function() {
    /*
     * Creates a Satellite with its shape and slices.
     * 
     * @param shape		A shape that is filled by the slices.
     * @param slices	A table of slices used to render this.
     */
	var Satellite = function() {
	    /*
         * Shape used to draw this.
         * This can be a simple dot, a disk, a rectangle or a polygon.
         * :JMI.script.ShapeX
         */
         this.shapex;
         
         /*
          * The elementary slices that are stacked to draw this satellite.
          * They describe how to fill the shape.
          * //:Vector.<Slice>;
          */
	     this.slices;
	     
		 JMI.script.Base.call( this);
	};
	
	Satellite.prototype = {
		constructor: JMI.script.Satellite,
		
        /*
         * Returns wether this satellite is visible.
         * 
         * @param zone      Zone from which the satellite belongs.
         * @param isTip     True if this is a Tip.
         * @param curSel    Identifier of the current active selection on the Plan.
         * @param sel       Identifier of this satellite selection or -1 if there is none.
         * 
         * @return          True if this satellite is visible, false otherwise.
         */
        // TODO : portage, décalage de bits
        isVisible: function(zone, isTip, curSel, sel) {
            var hasSel = curSel >= 0,
                isSel  = JMI.script.Base.isEnabled(zone.selection, 1 << curSel);
            
            return isTip ? !hasSel || !isSel : hasSel && sel == curSel && isSel;
        },
        
        /*
         * Draws this satellite on a Graphics.
         * It's position and size is evaluated by its parent and transfo.
         * A type filtering can be applied to select a special kind of satellite.
         * 
         * @param applet        The Applet that owns this.
         * @param s             A sprite to draw this in.
         * @param zone          The zone that holds the properties used by this satellite.
         * @param satCtr        This satellite center.
         * @param supCtr        This parent satellite center.
         * @param isLinkOnly    True to paint only the link between this and its parent if it exists.
         * @param satData       This satellite data buffer.
         * @param showTyp       The type of satellite to display.[ALL_TYP,BASE_TYP,TIP_TYP,SEL_TYP]
         */
        paint: function(applet, s, zone, satCtr, supCtr, isLinkOnly, satData, showTyp) {
            var flags = satData.flags;
            var isTip       = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.TIP_BIT),
                isSel       = JMI.script.Base.isEnabled(flags, JMI.script.Satellite.SEL_BIT),
                isVisible   = isTip || isSel ? satData.isVisible : true;
            
            var supZone = zone.getParent();

            if (isVisible) {
                // we must draw this Satellite Link if it exists
                if (isLinkOnly) {
                    // This has a Link
                    if (JMI.script.Base.isEnabled(flags, JMI.script.Satellite.LINK_BIT)) {
                        var x1 = supCtr.x,
                            y1 = supCtr.y,
                            x2 = satCtr.x,
                            y2 = satCtr.y;
                        this.setColor(s, JMI.script.Satellite.LINK_DRK_COL_VAL, zone.props);
                        s.graphics.moveTo(x1, y1 + 1);
                        s.graphics.lineTo(x2, y2 + 1);
                        if (this.setColor(s, JMI.script.Satellite.LINK_LIT_COL_VAL, zone.props)) {
                            s.graphics.moveTo(x1 - 1, y1);
                            s.graphics.lineTo(x2 - 1, y2);
                            s.graphics.moveTo(x1, y1);
                            s.graphics.lineTo(x2, y2);
                            s.graphics.moveTo(x1 + 1, y1 );
                            s.graphics.lineTo(x2 + 1, y2);
                            
                            this.setColor(s, JMI.script.Satellite.LINK_NRM_COL_VAL, zone.props);
                            s.graphics.moveTo(x1, y1 - 1);
                            s.graphics.lineTo(x2, y2 - 1);
                        }
                    }
                }
                
                else {
                    var isShowable = isSel;
                    
                    switch (showTyp) {
                        case JMI.script.Satellite.ALL_TYP  : isShowable = true; break;
                        case JMI.script.Satellite.BASE_TYP : isShowable = !(isTip || isSel); break;
                        case JMI.script.Satellite.TIP_TYP  : isShowable = isTip; break;
                    }
                    if (isShowable) {
                        for (var i = 0; i < this.slices.length; i++) {
                            this.slices[i].paint(applet, s, supZone, zone, this.shapex, satCtr, supCtr);
                        }
                    }
                }
            }
        },
        
        /*
         * Return wether a point is inside this Satellite
         * 
         * @param planComponent     The PlanComponent that owns this.
         * @param g                 A graphics to get the FontMetrics used by this.
         * @param zone              The zone that holds the properties used by this satellite.
         * @param satCtr            This satellite center.
         * @param supCtr            This parent satellite center.
         * @param transfo           The transformation that give the position and scale of this using its parent.
         * @param pos               A point position to test.
         * @param isPie             True if this is in the 'pie' part of this satellite.
         * @param isFake            True if this is the first (main) Satellite.
         * 
         * @return                  True if the cursor's position is inside this satellite, false otherwise.
         */
        contains: function(planComponent, g, zone, satCtr, supCtr, transfo, pos, isPie, isFake) {
            var i, n = this.slices.length;
            // If the parent satellite center is not set, take this satellite's shape center as center
            if(supCtr == null) supCtr = this.shapex.getCenter(zone);
            
            // Iterate throw all this satellite's slices and check if one of them contains the cursor's position
            // Stop if it's the case 
            for(i = 0 ; (i < n && !this.slices[i].contains(planComponent, g, zone.getParent(), zone, this.shapex, satCtr, supCtr, pos)) ; i++){};
            
            // if the cursor's position is in one of the slices
            if(i < n) {
                planComponent.planContainer.map.plan.newZone = zone;
                if (isPie) {
                    var supZone         = zone;
                    var zones           = supZone.subZones;
                    var nbZones         = zones.length + 1;
                    
                    var center = isFake ? this.shapex.getCenter(supZone) : supCtr;
                    var dir = (supZone.dir != 10.) ? supZone.dir : transfo.direction,
                        step = supZone.stp,
                        m = .5 * (JMI.script.Base.Pi2 / step - nbZones),
                        a = Math.atan2(pos.y - center.y, pos.x - center.x);
                    
                    if (dir < 0)  dir += JMI.script.Base.Pi2;
                    if (a < 0)    a   += JMI.script.Base.Pi2;
                    if (a < dir)  a   += JMI.script.Base.Pi2;
                    
                    a = .5 + (a - dir) / step;
                    i = Math.round(a);
                    
                    if (i > 0) {
                        if (i < nbZones) {
                            planComponent.planContainer.map.plan.newZone = zones[i-1];
                        }
                        else if (a - nbZones < m) {
                            planComponent.planContainer.map.plan.newZone = zones[nbZones-2];
                        }
                    }
                }
                return true;
            }
            return false;
        },
        
        /*
         * Sets this bounds by updating an already created Rectangle.
         * 
         * @param applet           The Applet that owns this.
         * @param gDrawingContext  A 2d graphic context to draw the shape in.
         * @param zone             The zone that holds the properties used by this satellite.
         * @param satCtr           This satellite center.
         * @param supCtr           This parent satellite center.
         * @param bounds           A Rectangle to merge with this bounds.
         */
        setBounds: function(applet, gDrawingContext, zone, satCtr, supCtr, bounds) {
            var i;
            var nbSlices = this.slices.length;
            for(i = 0 ; i < nbSlices ; i++) {
                this.slices[i].setBounds(applet, gDrawingContext, zone.getParent(), zone, this.shapex, satCtr, supCtr, bounds);
            }
        },

        /*
         * Execute one or more actions matching an event.
         * The actions are stored in this satellite, one list for each of the 3 possible events.
         * They are executed using there declaration order in the list. They can be one of this:
         * <ul>
         * <li>show             Shows a message in the StatusBar.</li>
         * <li>open             Opens an URL in a new window or a frame.</li>
         * <li>popup            Popup a menu at the cursor position.</li>
         * <li>pop              Pops a tooltip near the cursor position.</li>
         * <li>play             Plays a sound file.</li>
         * <li>dump             Dumps a text on the Java Console.</li>
         * </ul>
         * 
         * @param applet        The Applet that owns this.
         * @param zone          The zone that holds the properties used by this satellite.
         * @param pos           The current cursor position. Used to popup a menu.
         * @param actionId      Type of event that triggers the action.[HOVER_VAL,CLICK_VAL,DBLCLICK_VAL].
         */
        execute: function(applet, zone, pos, actionId) {
            var firstSat = zone.curSwatch.satellites[0];
            var isExe = this.isDefined(actionId);
        
            // Events
            // TODO : portage gestion des événements
            if (zone != null) {
                // TODO : portage instanceof et héritage
                if (zone instanceof JMI.script.LinkZone) {
                    //dispatchEvent( new LinkClickEvent( plan.curZone as LinkZone));
                }
                else {
                	// TODO 
                    /*var event = null;
                    if(actionId == JMI.script.Satellite.CLICK_VAL) event = AttributeEvent.CLICK;
                    else if(actionId == JMI.script.Satellite.DBLCLICK_VAL) event = AttributeEvent.DOUBLE_CLICK;
                    else if(actionId == JMI.script.Satellite.HOVER_VAL) event = AttributeEvent.HOVER;
                    if(event != null) {
                        applet.dispatchEvent( new AttributeEvent( event, applet.findAttribute( zone), pos.x, pos.y));
                    }
                    */
                }
            }
        
            if (isExe) {
                var actionStr = this.getString(actionId, zone.props);
                
                if (actionStr != null) {
                    var actions = this.getString(actionId, zone.props ).split("\n");
                    var action, func, args;
                    var i, j, n = actions.length;
                    
                    for (i = 0; i < n; i ++) {
                        action  = actions[i];
                        j       = action.indexOf(' ');
                        func    = action.substring(0, j);
                        args    = this.parseString3(action.substring(j + 1, action.length), zone.props )[0];
                        
                        // Shows a message in the StatusBar
                        if (func == ("show")) {
                            applet.showStatus(args);
                        }
                        
                        // Go to a page, opening a new browser window
                        else if (func == ("open")) {
                            j = args.indexOf(JMI.script.Satellite.SUBSEP);
                            
                            // tracking
                            if (j != -1) {
                                args = args.substring(j, args.length);
                            }
                            
                            applet.performAction( args);
                        }
                        
                        // Popup a menu
                        // TODO : portage getion du menu
                        else if (func == ("popup")) {
                            var menux = zone.curSwh.refs[args];
                            
                            if (menux != null) {
                                var menuData = [];
                                menux.parseMenu(menuData, zone);
                                var menu = Menu.createMenu(applet, menuData, false);
                                menu.variableRowHeight = true;
                                menu.labelField = "label";
                                //menu.setStyle("fontWeight", "bold");
                                menu.addEventListener(JMI.script.MenuEvent.ITEM_CLICK, applet.menuHandler);
                                var point = applet.localToGlobal(pos);
                                menu.show(point.x, point.y);
                                menu.visible = false;
                                if (point.x + menu.width > applet.width) 
                                    point.x = Math.max(point.x - menu.width, 0);
                                if (point.y + menu.height > applet.height) 
                                    point.y = Math.max(point.y - menu.height, 0);
                                menu.move(point.x, point.y);
                                menu.visible = true;
                            }
                        }
                        
                        // Pop a tooltip
                        else if (func == ("pop")) {
                            var slice = zone.curSwh.refs[args];
                            
                            if (slice != null) {
                                var delay  = slice.getInt(JMI.script.Slice.DELAY_VAL, zone.props);
                                var length = slice.getInt(JMI.script.Slice.LENGTH_VAL, zone.props);
                                //var text:HTMLText = slice.getText(Slice.TEXT_VAL, zone.m_props);
                                //applet.toolTip    = text.parseString(HTMLText.TEXT_VAL, zone.m_props).join("\n");
                                applet.plan.popSlice(zone, slice, delay, length, args);
                            }
                        }
                        
                        // TODO :  Plays a sound 
                        else if (func == ("play")) {
                            /*var clip:AudioClip= AudioClip(applet.m_env.medias[ args ]);
                            
                            if ( clip == null )
                            {
                                clip = applet.getAudioClip( applet.getCodeBase(), args );
                                clip.play();
                                applet.m_env.medias.put( args, clip );
                            }*/
                        }
                        
                        // Print a string in the console
                        else if (func == ("dump")) {
                            // TODO : portage, instruction de debug equivalente
                            trace(args);
                        }
                    }
                }
            }
            
            // TODO : portage comparation d'objets ?
            else if (this != firstSat) {
                firstSat.execute(applet, zone, pos, actionId);
            }
        }
	};
	
	// Héritage
	for (var element in JMI.script.Base.prototype ) {
		if( !Satellite.prototype[element])
			Satellite.prototype[element] = JMI.script.Base.prototype[element];
	}
	
	return Satellite;
}());

// Constants
/*
 * Index of the Transfo prop in VContainer table.
 * If there is no Transfo it is copied from the default one (first Satellite of the swatch).
 */
JMI.script.Satellite.TRANSFO_VAL = 1;

/*
 * Index of the hovered event prop in VContainer table
 */
JMI.script.Satellite.HOVER_VAL = 2;

/*
 * Index of the click event prop in VContainer table
 */
JMI.script.Satellite.CLICK_VAL = 3;

/*
 * Index of the double click event prop in VContainer table.
 */
JMI.script.Satellite.DBLCLICK_VAL = 4;

/*
 * Index of the selection prop in VContainer table if this is a selection Satellite.
 */
JMI.script.Satellite.SELECTION_VAL = 5;

/*
 * Index of the dark link color prop in VContainer table.
 */
JMI.script.Satellite.LINK_DRK_COL_VAL = 6;

/*
 * Index of the link color prop in VContainer table.
 */
JMI.script.Satellite.LINK_NRM_COL_VAL = 7;

/*
 * Index of the bright link color prop in VContainer table.
 */
JMI.script.Satellite.LINK_LIT_COL_VAL = 8;

/*
 * True if this Satellite is visible.
 * It can be interesting to create fake invisible satellite (even if it has never been tested).
 */
JMI.script.Satellite.VISIBLE_BIT = 0x001;

/*
 * This has a link with its parent.
 * Useful for creating s�lection tips.
 */
JMI.script.Satellite.LINK_BIT = 0x002;

/*
 * This can only be visible on a SuperZone (a zone that clusterize the others).
 */
JMI.script.Satellite.SUPER_BIT = 0x004;

/*
 * This can only be visible on a SubZone (a clusterized zone).
 */
JMI.script.Satellite.SUB_BIT = 0x008;

/*
 * This can only be visible on a current zone (a hovered zone).
 */
JMI.script.Satellite.CUR_BIT = 0x010;

/*
 * This can only be visible on a rest zone (a not hovered zone).
 */
JMI.script.Satellite.REST_BIT = 0x020;

/*
 * This is visible under the filter of the current zone.
 */
JMI.script.Satellite.BACK_BIT = 0x040;

/*
 * This is a Selection sat.
 * To know that this sat should always stay on top of the others.
 */
JMI.script.Satellite.SEL_BIT = 0x080;

/*
 * This is a Tip sat.
 * To know that this sat should not be tested when bounds are evaluated.
 */
JMI.script.Satellite.TIP_BIT = 0x100;

/*
 * This is sat can't be right or left sided.
 */
JMI.script.Satellite.NOSIDED_BIT = 0x200;

/*
 * Draws only Selection sats.
 */
JMI.script.Satellite.SEL_TYP = 0;

/*
 * Draws only Tip sats.
 */
JMI.script.Satellite.TIP_TYP = 1;

/*
 * Draws all sats but Selection and tips ones.
 */
JMI.script.Satellite.BASE_TYP = 2;

/*
 * Draws all sats.
 */
JMI.script.Satellite.ALL_TYP = 3;