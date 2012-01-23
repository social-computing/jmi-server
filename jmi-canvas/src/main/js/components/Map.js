JMI.namespace("components.Map");
/*	[Event(name="ready",    type="flash.events.Event")]
	[Event(name="empty",    type="flash.events.Event")]
	[Event(name="error",    type="com.socialcomputing.jmi.components.events.StatusEvent")]
	[Event(name="action",   type="com.socialcomputing.jmi.components.events.ActionEvent")]
	[Event(name="navigate", type="com.socialcomputing.jmi.components.events.NavigateEvent")]
	[Event(name="status",   type="com.socialcomputing.jmi.components.events.StatusEvent")]
	[Event(name="attribute_click", type="com.socialcomputing.jmi.components.events.AttributeEvent")]
	[Event(name="attribute_double_click", type="com.socialcomputing.jmi.components.events.AttributeEvent")]
	[Event(name="attribute_hover", type="com.socialcomputing.jmi.components.events.AttributeEvent")]
	//[Event(name="link_click",      type="com.socialcomputing.jmi.components.events.LinkClickEvent")]
*/	
JMI.components.Map = (function() {

var planContainer = JMI.script.PlanContainer,
	ready = false,

/*
 *  Specific display elements
 */
	backgroundColor = 0xFFFFFF,
	drawingContext,//:SpriteVisualElement => Official Canvas 2D Context

/*
 * Image used to quickly restore the aspect of a zone that is no longer current.
 * It includes the background + links + Satellites of each place at rest.
 */
	restDrawingContext, //:Sprite => Canvas 2D Context  
/*
 * Image used as a background on which the current zone is drawn.
 * It includes the background, and the zones rendered with their 'ghosted' satellites form the rest swatch.
 * The resulting image is then filtered with a transparency color.
 */
	backDrawingContext,//:Sprite => Canvas 2D Context
	curDrawingContext,//:Sprite => Canvas 2D Context

/*
 * API
 */
	attributes, //:ArrayCollection;
	entities; //:ArrayCollection;
	
	var Map = function(id) {
		this.curPos = new JMI.script.Point(),
		this.ready = false;
		this.attributes = [];
		this.entities = [];
		
		var mapDiv = document.getElementById( id);
		this.size = new JMI.script.Dimension( mapDiv.clientWidth, mapDiv.clientHeight);
		
		// Drawing surface of the component
		this.drawingCanvas = document.createElement( "canvas");
		this.drawingCanvas.width = mapDiv.clientWidth;
		this.drawingCanvas.height = mapDiv.clientHeight;
		mapDiv.appendChild( this.drawingCanvas);
		this.drawingContext = this.drawingCanvas.getContext( "2d");
		this.drawingCanvas.JMI = this;
	
		// Graphic zones
		this.curDrawingCanvas = document.createElement( "canvas");
		this.curDrawingCanvas.width = mapDiv.clientWidth;
		this.curDrawingCanvas.height = mapDiv.clientHeight;
		this.curDrawingCanvas.style.visibility='hidden';
		this.curDrawingContext = this.curDrawingCanvas.getContext( "2d");

		this.restDrawingCanvas = document.createElement( "canvas");
		this.restDrawingCanvas.width = mapDiv.clientWidth;
		this.restDrawingCanvas.height = mapDiv.clientHeight;
		//this.restDrawingCanvas.style.visibility='hidden';
		this.restDrawingContext = this.restDrawingCanvas.getContext( "2d");

		this.backDrawingCanvas = document.createElement( "canvas");
		this.backDrawingCanvas.width = mapDiv.clientWidth;
		this.backDrawingCanvas.height = mapDiv.clientHeight;
		this.backDrawingCanvas.style.visibility='hidden';
		this.backDrawingContext = this.backDrawingCanvas.getContext( "2d");
		
		// Event listeners
		this.drawingCanvas.addEventListener( 'mousemove', this.mouseMoveHandler, false);
		this.drawingCanvas.addEventListener( 'mouseover', this.mouseOverHandler, false);
		this.drawingCanvas.addEventListener( 'mouseout', this.mouseOutHandler, false);
/*
		this.addEventListener(MouseEvent.CLICK, mouseClickHandler);
		this.addEventListener(MouseEvent.DOUBLE_CLICK, mouseDoubleClickHandler);
		this.addEventListener(ResizeEvent.RESIZE, resizeHandler);
		this.addEventListener(NavigateEvent.NAVIGATE, navigateHandler);
*/		
/*		var wpsMenu:ContextMenu = new ContextMenu();
		wpsMenu.hideBuiltInItems();
		var menuItem:ContextMenuItem = new ContextMenuItem("powered by Just Map It! - Social Computing");
		menuItem.addEventListener( ContextMenuEvent.MENU_ITEM_SELECT, openSoCom);
		wpsMenu.customItems.push( menuItem);
		this.contextMenu = wpsMenu;*/
	};
	
    Map.prototype = {
        constructor: JMI.components.Map,
		
		setData: function(value) {
			// Set component status to "not ready"
			this.ready = false;
		
			// Stop loaders
			if( this.planContainer && this.planContainer.map.env) {
				this.planContainer.map.env.close();
			}
			
			// Clear current
			if( this.planContainer && this.planContainer.map.plan) {
				this.planContainer.map.plan.curSat = null;
				this.planContainer.map.plan.curZone = null;
			}
			
			// Clear all drawing surfaces
			this.clear();
			
			this.attributes = [];
			this.entities = [];
			
			// If the given value is null 
			// Reset all objects of this component
			if(value == null) {
				if( this.planContainer)
					delete( this.planContainer);
				return;
			}
			
			this.showStatus("");
			document.body.style.cursor = 'wait';
			if(value instanceof JMI.script.PlanContainer) {
				this.planContainer = value;
			}
			else {
				this.planContainer = JMI.script.PlanContainer.fromJSON( value);
			}
			if( this.planContainer.hasOwnProperty( "error")) {
				// Server error
				CursorManager.removeBusyCursor();
				dispatchEvent(new StatusEvent(StatusEvent.ERROR, this.planContainer.error));
			}
			else if( !this.planContainer.hasOwnProperty( "map")) {
				// Empty map
				document.body.style.cursor = 'default';
				// TODO
				//dispatchEvent(new Event( Map.EMPTY));*/
			}
			else {
				var needPrint = false; // Later
		
				this.planContainer.map.env.init(this, needPrint);
				this.planContainer.map.plan.applet = this;
				this.planContainer.map.plan.curSel = -1;
				this.planContainer.map.plan.initZones(this.restDrawingContext, this.planContainer.map.plan.links, true);
	            this.planContainer.map.plan.initZones(this.restDrawingContext, this.planContainer.map.plan.nodes, true);
				this.planContainer.map.plan.resize(this.size);
				this.planContainer.map.plan.init();
				this.planContainer.map.plan.resize(this.size);
				this.planContainer.map.plan.init();
/*			    for ( var zone in this.planContainer.map.plan.nodes) {
					this.attributes.addItem( new Attribute( this.planContainer.map.env, zone));
				}*/
				this.ready = true;
				document.body.style.cursor = 'default';

				this.renderShape( this.restDrawingCanvas, this.size.width, this.size.height);
				/*TODO portage
				if(this.ready)
					dispatchEvent(new Event(Map.READY));*/
			}
		},
		
		renderShape: function( canvas, width, height, position) {
			// If no position is specified, take (0,0)
			if(position == null) {
				position = new JMI.script.Point();
			}
			
			if( width > 0 && height > 0) { 
				// Copying the content of the context on to visib canvas context
				this.drawingContext.drawImage( canvas, position.x, position.y, width, height, position.x, position.y, width, height);
			}
		},
		
		clear: function() {
			//JMI.util.ImageUtil.clear(this.backDrawingCanvas, this.backDrawingContext);
			JMI.util.ImageUtil.clear(this.restDrawingCanvas, this.restDrawingContext);
			JMI.util.ImageUtil.clear(this.curDrawingCanvas, this.curDrawingContext);
			JMI.util.ImageUtil.clear(this.drawingCanvas, this.drawingContext);
		},
		
		mouseMoveHandler: function(event) {
			if( this instanceof HTMLCanvasElement) {
				this.JMI.curPos.x = event.clientX;
				this.JMI.curPos.y = event.clientY;
				if(this.JMI.ready) {
					this.JMI.planContainer.map.plan.updateZoneAt( this.JMI.curPos);
				}
			}
/*			else
				aptana.log( this);*/
		},

		mouseOverHandler: function(event) {
			this.JMI.mouseMoveHandler( event);
		},

		mouseOutHandler: function(event) {
			this.JMI.mouseMoveHandler( event);
		},

		showStatus: function(message) {
			// TODO portage
			//dispatchEvent(new StatusEvent( StatusEvent.STATUS, message));
		},

		/*
		 * Perform an URL action.
		 * The action depends on the string passed:
		 * <ul>
		 * <li>URL : Opens the URL in a new window.</li>
		 * <li>_target:URL : Opens the URL in the frame called target if it exists or else in a new window whose name is set to target.</li>
		 * <li>javascript:function(args) : If LiveConnect is enabled, call the Javascript function with args (arg1,..,argn).
		 * Else, if an alternate page is defined (NoScriptUrl Applet parameter), this page is opened with the function(args) passed using the CGI syntax.</li>
		 * <li>javascript:_target:function(args) : See javascript:function(args) and _target cases.</li>
		 * </ul>
		 * @param actionStr		An URL like string describing what action to do.
		 * @throws UnsupportedEncodingException 
		 */
		performAction: function( actionStr) {
			var jsStr = "javascript";
			var target = "_blank";
			var sep       = actionStr.indexOf( ':' ),
				pos;
		
			if ( sep != -1 )
			{
				target  = actionStr.substring( 0, sep );
				if( target.toLowerCase() == jsStr )   // Call javascript function
				{
					actionStr   = actionStr.substring( jsStr.length+ 1 );
					if( actionStr.charAt( 0 )== '_' )
					{	// javascript:_target:function()
						pos = actionStr.indexOf( ':' );
						if( pos <= 0) return;
						target      = actionStr.substring( 1, pos );
						actionStr   = actionStr.substring( pos + 1 );
					}
			
					pos     = actionStr.indexOf( '(' );
					if( pos > 0)
					{
						var func     = actionStr.substring( 0, pos ),
							paramStr = actionStr.substring( pos + 1, actionStr.length- 1 );
						var params   = paramStr.split( String.fromCharCode( 0xFFFC));
						// TODO dispatchEvent( new ActionEvent( func, params));
					}
					return;
				}
				else if( target.charAt( 0 )== '_' )   // open a frame window
				{
					target      = actionStr.substring( 0, sep );
					actionStr   = actionStr.substring( sep + 1 );
				}
				else
				{
					target  = "_blank";
				}
			}
			// TODO dispatchEvent(new NavigateEvent( actionStr, target)); 
		},
		
		openSoCom: function ( e) {
			//TODO portage
			navigateToURL( new URLRequest( "http://www.social-computing.com"), "_blank");
		}
	};
	
	return Map;
}());

JMI.components.Map.version = "1.0-SNAPSHOT";
JMI.components.Map.EMPTY = "empty";
JMI.components.Map.READY = "ready";


/*
public function get bitmapData():BitmapData
{
	return _offScreen;
}
*/

/*

public function mouseClickHandler(event:MouseEvent):void {
	if ( ready && plan.curSat != null )
	{
		var point:Point = new Point();
		point.x = event.localX;
		point.y = event.localY;
		plan.updateZoneAt( point);
		plan.curSat.execute( this, plan.curZone, point, Satellite.CLICK_VAL);
	}
}

public function findAttribute( zone:ActiveZone):Attribute {
	for each( var attribute:Attribute in attributes) {
		if( attribute.zone == zone)
			return attribute;
	}
	return null;
}

private function findLink( zone:ActiveZone):Link {
	return new Link( zone);
}

public function mouseDoubleClickHandler(event:MouseEvent):void {
	if ( ready && plan.curSat != null )
	{
		var point:Point = new Point();
		point.x = event.localX;
		point.y = event.localY;
		plan.updateZoneAt( point);
		plan.curSat.execute( this, plan.curZone, point, Satellite.DBLCLICK_VAL);
	}
}

public function resizeHandler(event:ResizeEvent):void {
	//trace("resize, new size = (" + this.width + ", " + this.height + ")");
	this.clear();
	
	this.restDrawingContext.graphics.beginFill(this.ready ? this.env.inCol.m_color : this.backgroundColor);
	this.restDrawingContext.graphics.drawRect(0, 0, this.width, this.height);
	this.restDrawingContext.graphics.endFill();
		
	if(this.ready) {
		this.plan.resize( this.size);
		this.plan.init();
		this.invalidateSize();  
	}
}
		
public function navigateHandler(event:NavigateEvent):void {
	navigateToURL( new URLRequest( event.url), event.btarget);
}

public function menuHandler( evt:MenuEvent):void {
	performAction( evt.item.action);
}


public function actionPerformed( actionStr:String ):void {
	performAction( actionStr);
}*/


/*public function getProperty( name:String):Object {
	if( env != null && env.props.hasOwnProperty( name))
		return env.props[name];
	return null;
}

public function defineEntities( nodeFields:Array, nodeId:String="POSS_ID", linkId:String="REC_ID"):void {
	
	// Extraction des entités
	var ents:Object = new Object();
	for each( var zone:ActiveZone in plan.nodes) {
		var ids:Array = zone.m_props[nodeId] as Array;
		for( var i:int = 0; i < ids.length; ++i) {
			if( !ents.hasOwnProperty( ids[i])) {
				var entity:Entity = new Entity( env);
				entity[nodeId] = ids[i];
				for each( var name:Object in nodeFields) {
					entity[name] = zone.m_props[name][i];
				}
				ents[ids[i]] = entity;
				this.entities.addItem( entity);
			}
		}
	}
	for each( var link:LinkZone in plan.links) {
		ids = link.m_props[linkId] as Array;
		for each( var id:String in ids) {
			ents[id].addLink( link);
		}
	}
	for each( var attribute:Attribute in this.attributes) {
		ids = attribute.zone.m_props[nodeId] as Array;
		for( i = 0; i < ids.length; ++i) {
			ents[ids[i]].addAttribute( attribute);
		}
	}
}*/

/**
 * Sets the currently displayed selection.
 * Called by JavaScript.
 * @param selNam	A selection name as defined in the Dictionary.
 */
/*public function setSelection( selection:String):void
{
	var selId:int   = getSelId( selection );
	plan.curSel = selId;
	plan.init();
	this.invalidateDisplayList();
}

public function clearSelection( selection:String):void {
	clearZoneSelection( selection, plan.nodes, plan.nodes.length );
	clearZoneSelection( selection, plan.links, plan.linksCnt );
}*/

/**
 * Remove zones from a selection.
 * The display must be refresh to reflect the new selection.
 * @param selNam	A selection name as defined in the Dictionary.
 * @param zones		An array of Zones (Nodes or Links).
 * @param n			Number of zone to remove from selection in the array, starting from index 0.
 */
/*private function clearZoneSelection( selection:String, zones:Array, n:int):void
{
	var selId:int   = getSelId( selection );
	if ( selId != -1 )
	{
		var unselBit:int = ~( 1 << selId );
		for( var i:int = 0; i < n; i ++ )
		{
			zones[i].m_selection &= unselBit;
		}
	}
}*/

/**
 * Gets the id of a selection, knowing its name.
 * @param selNam	A selection name as defined in the Dictionary.
 * @return			An ID in [0,31] or -1 if the selection name is unknown.
 */
/*private function getSelId( selection:String):int
{
	if( env.selections[selection] == null)
		return -1;
	return  env.selections[selection];
}

*/
