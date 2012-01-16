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
	_curPos = new JMI.script.Point(),
	_ready = false,

/*
 *  Specific display elements
 */
	_backgroundColor = 0xFFFFFF,
	_onScreen,//:BitmapData,
	_offScreen,//:BitmapData,
	_drawingSurface,//:SpriteVisualElement => Official Canvas 2D Context

/*
 * Image used to quickly restore the aspect of a zone that is no longer current.
 * It includes the background + links + Satellites of each place at rest.
 */
	restDrawingSurface, //:Sprite => Canvas 2D Context  
/*
 * Image used as a background on which the current zone is drawn.
 * It includes the background, and the zones rendered with their 'ghosted' satellites form the rest swatch.
 * The resulting image is then filtered with a transparency color.
 */
	backDrawingSurface,//:Sprite => Canvas 2D Context
	curDrawingSurface,//:Sprite => Canvas 2D Context

/*
 * API
 */
	attributes, //:ArrayCollection;
	entities; //:ArrayCollection;
	
	var Map = function(id) {
		attributes = new Array();
		entities = new Array();
		
		var mapDiv = document.getElementById( id);
		this.size = new JMI.script.Dimension( mapDiv.clientWidth, mapDiv.clientHeight);
		
		// Drawing surface of the component
		var drawingCanvas = document.createElement( "canvas");
		drawingCanvas.width = mapDiv.clientWidth;
		drawingCanvas.height = mapDiv.clientHeight;
		mapDiv.appendChild( drawingCanvas);
		this.drawingSurface = drawingCanvas.getContext( "2d");
	
		// Graphic zones
		var curDrawingCanvas = document.createElement( "canvas");
		curDrawingCanvas.width = mapDiv.clientWidth;
		curDrawingCanvas.height = mapDiv.clientHeight;
		curDrawingCanvas.style.visibility='hidden';
		this.curDrawingSurface = curDrawingCanvas.getContext( "2d");

		var restDrawingCanvas = document.createElement( "canvas");
		restDrawingCanvas.width = mapDiv.clientWidth;
		restDrawingCanvas.height = mapDiv.clientHeight;
		restDrawingCanvas.style.visibility='hidden';
		this.restDrawingSurface = restDrawingCanvas.getContext( "2d");

		var backDrawingCanvas = document.createElement( "canvas");
		backDrawingCanvas.width = mapDiv.clientWidth;
		backDrawingCanvas.height = mapDiv.clientHeight;
		backDrawingCanvas.style.visibility='hidden';
		this.backDrawingSurface = backDrawingCanvas.getContext( "2d");
		
		// Event listeners
		this.doubleClickEnabled = true;
/*		this.addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
		this.addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
		this.addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
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
			// TODO portage this.clear();
			
			this.attributes = [];
			this.entities = [];
			
			// If the given value is null 
			// Reset all objects of this component
			if(value == null) {
				this.planContainer = null;
				//this.plan = null;
				//this.
				// TODO : If the local plancontainer is set, reset objects
				this.invalidateProperties();
				this.invalidateDisplayList();
				return;
			}
			
			// TODO this.showStatus("");
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
				this.planContainer.map.plan.initZones(this.restDrawingSurface, this.planContainer.map.plan.links, true);
	            this.planContainer.map.plan.initZones(this.restDrawingSurface, this.planContainer.map.plan.nodes, true);
				this.planContainer.map.plan.resize(this.size);
				this.planContainer.map.plan.init();
				this.planContainer.map.plan.resize(this.size);
				this.planContainer.map.plan.init();
/*			    for ( var zone in this.planContainer.map.plan.nodes) {
					this.attributes.addItem( new Attribute( this.planContainer.map.env, zone));
				}*/
				this.ready = true;

				document.body.style.cursor = 'default';
				
				this.renderShape( this.restDrawingSurface, this.width, this.height);
				/*TODO if(this.ready)
					dispatchEvent(new Event(Map.READY));*/
			}
		},
		
		renderShape: function( context, width, height, position) {
			// If no position is specified, take (0,0)
			if(position == null) {
				position = new JMI.script.Point(0, 0);
			}
			
			if( width > 0 && height > 0) { 
				// Copying the content of the context on to visible canvas context
				drawingSurface.drawImage( context, position.x, position.y, position.x, position.y, width, height);
			}
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


/*public function get ready():Boolean {
	return plan != null && _ready;
}			

public function get plan():Plan
{
	if(this.planContainer == null) {
		return null;
	}
	return planContainer.map.plan;
}

public function get env():Env
{
	if(this.planContainer == null) {
		return null
	}
	return planContainer.map.env;
}

public function get size():Dimension {
	return new Dimension(this.width, this.height);
}

public function get bitmapData():BitmapData
{
	return _offScreen;
}
public function get curDrawingSurface():Sprite
{
	return _curDrawingSurface;
}
public function get restDrawingSurface():Sprite
{
	return _restDrawingSurface;
}

public function get curPos():Point {
	return _curPos;
}

public function set curPos(pos:Point):void {
	_curPos = pos;
}

public function get planContainer():Object
{
	return this.planContainer;	
}*/

/*
com.socialcomputing.jmi.components.Map.prototype.clear = function() {
	ImageUtil.clear(this.backDrawingSurface);
	ImageUtil.clear(this.restDrawingSurface);
	ImageUtil.clear(this.curDrawingSurface);
	ImageUtil.clear(this.drawingSurface);
	
	if(this.width != 0 && this.height !=  0) {
		this.onScreen = context.createImageData(this.width, this.height);
		this.offScreen = context.createImageData(this.width, this.height);
		// TODO portage : devrait etre inutile
		//this.drawingSurface.addChild(new Bitmap(this.onScreen));
	}
}

public function showStatus(message:String):void {
	dispatchEvent(new StatusEvent( StatusEvent.STATUS, message));
}

public function mouseOverHandler(event:MouseEvent):void {
	mouseMoveHandler( event);
}

public function mouseOutHandler(event:MouseEvent):void {
	mouseMoveHandler( event);
}

public function mouseMoveHandler(event:MouseEvent):void {
	this.curPos.x = event.localX;
	this.curPos.y = event.localY;
	if(ready) {
		planContainer.map.plan.updateZoneAt(this.curPos);
	}
}

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
	
	this.restDrawingSurface.graphics.beginFill(this.ready ? this.env.inCol.m_color : this.backgroundColor);
	this.restDrawingSurface.graphics.drawRect(0, 0, this.width, this.height);
	this.restDrawingSurface.graphics.endFill();
		
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

/**
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
/*public function performAction( actionStr:String):void {
	var jsStr:String  = "javascript";
	var target:String = "_blank";
	var sep:int       = actionStr.indexOf( ':' ),
		pos:int;

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
				var func:String     = actionStr.substring( 0, pos ),
					paramStr:String = actionStr.substring( pos + 1, actionStr.length- 1 );
				var params:Array    = paramStr.split( String.fromCharCode( 0xFFFC));
				dispatchEvent( new ActionEvent( func, params));
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
	dispatchEvent(new NavigateEvent( actionStr, target)); 
}

public function getProperty( name:String):Object {
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
