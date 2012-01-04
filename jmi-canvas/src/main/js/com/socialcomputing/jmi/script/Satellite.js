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
JMI.namespace("com.socialcomputing.jmi.script.Satellite") = (function() {
/*
 * Shape used to draw this.
 * This can be a simple dot, a disk, a rectangle or a polygon.
 */
	var m_shape = com.socialcomputing.jmi.script.ShapeX,

/*
 * The elementary slices that are stacked to draw this satellite.
 * They describe how to fill the shape.
 */
	m_slices, //:Vector.<Slice>;
	Constr;
	
/*
 * Creates a Satellite with its shape and slices.
 * @param shape		A shape that is filled by the slices.
 * @param slices	A table of slices used to render this.
 */
	Constr = function( shape, slices) {
	    m_shape     = shape;
	    m_slices    = slices;
	}
	Constr.prototype = {
		constructor: com.socialcomputing.jmi.script.Satellite,
		version: "2.0"
	}
	return Constr;
}());

/*
 * Index of the Transfo prop in VContainer table.
 * If there is no Transfo it is copied from the default one (first Satellite of the swatch).
 */
com.socialcomputing.jmi.script.Satellite.TRANSFO_VAL = 1;

/*
 * Index of the hovered event prop in VContainer table
 */
com.socialcomputing.jmi.script.Satellite.HOVER_VAL = 2;

/*
 * Index of the click event prop in VContainer table
 */
com.socialcomputing.jmi.script.Satellite.CLICK_VAL = 3;

/*
 * Index of the double click event prop in VContainer table.
 */
com.socialcomputing.jmi.script.Satellite.DBLCLICK_VAL = 4;

/*
 * Index of the selection prop in VContainer table if this is a selection Satellite.
 */
com.socialcomputing.jmi.script.Satellite.SELECTION_VAL = 5;

/*
 * Index of the dark link color prop in VContainer table.
 */
com.socialcomputing.jmi.script.Satellite.LINK_DRK_COL_VAL = 6;

/*
 * Index of the link color prop in VContainer table.
 */
com.socialcomputing.jmi.script.Satellite.LINK_NRM_COL_VAL = 7;

/*
 * Index of the bright link color prop in VContainer table.
 */
com.socialcomputing.jmi.script.Satellite.LINK_LIT_COL_VAL = 8;

/*
 * True if this Satellite is visible.
 * It can be interesting to create fake invisible satellite (even if it has never been tested).
 */
com.socialcomputing.jmi.script.Satellite.VISIBLE_BIT = 0x001;

/*
 * This has a link with its parent.
 * Useful for creating s�lection tips.
 */
com.socialcomputing.jmi.script.Satellite.LINK_BIT = 0x002;

/*
 * This can only be visible on a SuperZone (a zone that clusterize the others).
 */
com.socialcomputing.jmi.script.Satellite.SUPER_BIT = 0x004;

/*
 * This can only be visible on a SubZone (a clusterized zone).
 */
com.socialcomputing.jmi.script.Satellite.SUB_BIT = 0x008;

/*
 * This can only be visible on a current zone (a hovered zone).
 */
com.socialcomputing.jmi.script.Satellite.CUR_BIT = 0x010;

/*
 * This can only be visible on a rest zone (a not hovered zone).
 */
com.socialcomputing.jmi.script.Satellite.REST_BIT = 0x020;

/*
 * This is visible under the filter of the current zone.
 */
com.socialcomputing.jmi.script.Satellite.BACK_BIT = 0x040;

/*
 * This is a Selection sat.
 * To know that this sat should always stay on top of the others.
 */
com.socialcomputing.jmi.script.Satellite.SEL_BIT = 0x080;

/*
 * This is a Tip sat.
 * To know that this sat should not be tested when bounds are evaluated.
 */
com.socialcomputing.jmi.script.Satellite.TIP_BIT = 0x100;

/*
 * This is sat can't be right or left sided.
 */
com.socialcomputing.jmi.script.Satellite.NOSIDED_BIT = 0x200;

/*
 * Draws only Selection sats.
 */
com.socialcomputing.jmi.script.Satellite.SEL_TYP = 0;

/*
 * Draws only Tip sats.
 */
com.socialcomputing.jmi.script.Satellite.TIP_TYP = 1;

/*
 * Draws all sats but Selection and tips ones.
 */
com.socialcomputing.jmi.script.Satellite.BASE_TYP = 2;

/*
 * Draws all sats.
 */
com.socialcomputing.jmi.script.Satellite.ALL_TYP = 3;

/*
 * Returns wether this satellite is visible.
 * @param zone		Zone from which the satellite belongs.
 * @param isTip		True if this is a Tip.
 * @param curSel	Identifier of the current active selection on the Plan.
 * @param sel		Identifier of this satellite selection or -1 if there is none.
 * @return			True if this satellite is visible, false otherwise.
 */
com.socialcomputing.jmi.script.Satellite.prototype.isVisible = function( zone, isTip, curSel, sel) {
    var hasSel= curSel >= 0,
        isSel = isEnabled( zone.m_selection, 1<< curSel );
    
    return isTip ? !hasSel || !isSel : hasSel && sel == curSel && isSel;
}

/*
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
com.socialcomputing.jmi.script.Satellite.prototype.paint = function(applet, s, zone, satCtr, supCtr, isLinkOnly, satData, showTyp) {
	var flags = satData.m_flags;
    var isTip 		= isEnabled( flags, Satellite.TIP_BIT ),
        isSel       = isEnabled( flags, Satellite.SEL_BIT ),
        isVisible   = isTip || isSel ? satData.m_isVisible : true;
    
    var supZone = zone.getParent();
    
    if ( isVisible )
    {
        if ( isLinkOnly )               // we must draw this Satellite Link if it exists
        {
            if ( isEnabled( flags, LINK_BIT ))    // This has a Link
            {
                var x1 = supCtr.x,
                    y1 = supCtr.y,
                    x2 = satCtr.x,
                    y2 = satCtr.y;
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
            var isShowable = isSel;
            
            switch ( showTyp )
            {
                case ALL_TYP    : isShowable = true; break;
                case BASE_TYP   : isShowable = !( isTip || isSel ); break;
                case TIP_TYP    : isShowable = isTip; break;
                //			case SEL_TYP :  return isSel;
            }
            if ( isShowable )
            {
				for each( var slice in m_slices)
                {
                    slice.paint( applet, s, supZone, zone, m_shape, satCtr, supCtr );
                }
            }
        }
    }
}

/*
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
com.socialcomputing.jmi.script.Satellite.prototype.contains = function(planComponent, g, zone, satCtr, 
						 supCtr, transfo, pos, isPie, isFake) {
    //trace("[Satellite contains method called]");
	var i, n= m_slices.length;
    // If the parent satellite center is not set, take this satellite's shape center as center
    if(supCtr == null) supCtr = m_shape.getCenter(zone);
    
	// Iterate throw all this satellite's slices and check if one of them contains the cursor's position
	// Stop if it's the case 
    for(i = 0 ; (i < n && !m_slices[i].contains(planComponent, g, zone.getParent(), zone, m_shape, satCtr, supCtr, pos)) ; i++){};
    
	// if the cursor's position is in one of the slices
    if(i < n) {
		planComponent.plan.m_newZone = zone;
        if (isPie) {
            var supZone 		= zone;
            var zones			= supZone.m_subZones;
            var nbZones	        = zones.length + 1;
            
            var center = isFake ? m_shape.getCenter(supZone) : supCtr;
            var dir = (supZone.m_dir != 10.) ? supZone.m_dir : transfo.m_dir,
                step = supZone.m_stp,
                m = .5 * (Pi2 / step - nbZones),
                a = Math.atan2(pos.y - center.y, pos.x - center.x);
            
            if (dir < 0)  dir += Pi2;
            if (a < 0)    a   += Pi2;
            if (a < dir)  a   += Pi2;
            
            a = .5+(a - dir) / step;
            i = Math.round( a);
            
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


/*
 * Sets this bounds by updating an already created Rectangle.
 * @param applet		The Applet that owns this.
 * @param g				A graphics to get the FontMetrics used by this.
 * @param zone			The zone that holds the properties used by this satellite.
 * @param satCtr		This satellite center.
 * @param supCtr		This parent satellite center.
 * @param bounds		A Rectangle to merge with this bounds.
 * @throws UnsupportedEncodingException 
 */
com.socialcomputing.jmi.script.Satellite.prototype.setBounds = function(applet, g, zone,
						  satCtr, supCtr, bounds) {
	for each(var slice in m_slices) {
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


/*
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
com.socialcomputing.jmi.script.Satellite.prototype.execute = function(applet, zone, pos, actionId) {
    var firstSat = zone.m_curSwh.m_satellites[0];
    var isExe = isDefined( actionId );

	// Events
	if( zone != null) {
		if( zone instanceof LinkZone) {
			//dispatchEvent( new LinkClickEvent( plan.m_curZone as LinkZone));
		}
		else { // TODO portage Event
			var event = null;
			if( actionId == com.socialcomputing.jmi.script.Satellite.CLICK_VAL) 
				event = AttributeEvent.CLICK;
			else if( actionId == com.socialcomputing.jmi.script.Satellite.DBLCLICK_VAL) 
				event = AttributeEvent.DOUBLE_CLICK;
			else if( actionId == com.socialcomputing.jmi.script.Satellite.HOVER_VAL) 
				event = AttributeEvent.HOVER;
			if( event != null) {
				applet.dispatchEvent( new AttributeEvent( event, applet.findAttribute( zone), pos.x, pos.y));
			}
		}
	}

    if ( isExe )
    {
        var actionStr = getString( actionId, zone.m_props );
        
        if ( actionStr != null )
        {
            var actions = getString( actionId, zone.m_props ).split("\n");
            var action, func, args;
            var i, j, n = actions.length;
            
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
                    var menux = zone.m_curSwh.m_refs[args];
                    
                    if ( menux != null )
                    {
						var menuData = new Array();
						menux.parseMenu( menuData, zone );
						var menu = Menu.createMenu( applet, menuData, false);
                        menu.variableRowHeight = true;
                        menu.labelField = "label";
                        //menu.setStyle("fontWeight", "bold");
						menu.addEventListener(MenuEvent.ITEM_CLICK, applet.menuHandler);
						var point = applet.localToGlobal(pos);
						menu.show( point.x, point.y );
						menu.visible = false;
						if( point.x + menu.width > applet.width) 
							point.x = Math.max( point.x - menu.width, 0);
						if( point.y + menu.height > applet.height) 
							point.y = Math.max( point.y - menu.height, 0);
						menu.move( point.x, point.y);
						menu.visible = true;
					}
                }
				// Pop a tooltip
                else if (func == ("pop")) {
                    var slice = zone.m_curSwh.m_refs[args];
                    
					if (slice != null) {
                        var delay  = slice.getInt(Slice.DELAY_VAL, zone.m_props);
                        var length = slice.getInt(Slice.LENGTH_VAL, zone.m_props);
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

com.socialcomputing.jmi.script.Satellite.prototype.getShape = function() {
    return m_shape;
}

com.socialcomputing.jmi.script.Satellite.prototype.getSlices = function() {
    return m_slices;
}
