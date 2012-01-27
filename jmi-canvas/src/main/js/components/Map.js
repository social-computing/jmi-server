JMI.namespace("components.Map");

JMI.components.Map = (function() {

	var Map = function(parent) {
		this.backgroundColor = 0xFFFFFF,
		this.curPos = new JMI.script.Point(),
		this.ready = false;
		this.planContainer = null;
		this.eventManager = new JMI.util.EventManager();
		this.size = new JMI.script.Dimension();

	    if (!parent) {
	    	throw 'JMI map: parent id must be set';
		}
	    if (typeof parent == "string") {
			this.parent = document.getElementById(parent);
			if( this.parent == null) {
				throw 'JMI map: unknown parent element ' + parent;
			}
		}
	    else if (typeof parent == "object") {
	    	this.parent  = parent;
	    }
	    else {
			throw 'JMI map: invalid parent ' + parent;
	    }	
		this.parent.JMI = this;
		this.size.width = this.parent.clientWidth;
		this.size.height = this.parent.clientHeight;
				
		// Drawing surface of the component
		this.drawingCanvas = document.createElement("canvas");
		this.drawingCanvas.width = this.size.width;
		this.drawingCanvas.height = this.size.height;
		this.parent.appendChild(this.drawingCanvas);
		this.drawingContext = this.drawingCanvas.getContext("2d");
		this.drawingCanvas.JMI = this;
	
		// Graphic zones
		this.curDrawingCanvas = document.createElement("canvas");
		this.curDrawingCanvas.width = this.size.width;
		this.curDrawingCanvas.height = this.size.height;
		this.curDrawingCanvas.style.visibility = 'hidden';
		this.curDrawingContext = this.curDrawingCanvas.getContext("2d");

		this.restDrawingCanvas = document.createElement("canvas");
		this.restDrawingCanvas.width = this.size.width;
		this.restDrawingCanvas.height = this.size.height;
		this.restDrawingCanvas.style.visibility='hidden';
		this.restDrawingContext = this.restDrawingCanvas.getContext("2d");

		this.backDrawingCanvas = document.createElement("canvas");
		this.backDrawingCanvas.width = this.size.width;
		this.backDrawingCanvas.height = this.size.height;
		this.backDrawingCanvas.style.visibility = 'hidden';
		this.backDrawingContext = this.backDrawingCanvas.getContext("2d");
		
		// Event listeners
		this.drawingCanvas.addEventListener('mousemove', this.mouseMoveHandler, false);
		this.drawingCanvas.addEventListener('mouseover', this.mouseOverHandler, false);
		this.drawingCanvas.addEventListener('mouseout', this.mouseOutHandler, false);
		this.drawingCanvas.addEventListener('click', this.mouseClickHandler, false);
		this.drawingCanvas.addEventListener('dblclick', this.mouseDoubleClickHandler, false);

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

			// If the given value is null 
			// Reset all objects of this component
			if(value == null) {
				if( this.planContainer)
					delete( this.planContainer);
				return;
			}
			
			this.showStatus('');
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
				this.dispatchEvent({map: this, type: JMI.components.Map.ERROR, message: this.planContainer.error});
			}
			else if( !this.planContainer.hasOwnProperty( "map")) {
				// Empty map
				document.body.style.cursor = 'default';
				this.dispatchEvent({ map: this, type: JMI.components.Map.EMPTY});
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
				this.ready = true;
				document.body.style.cursor = 'default';

				this.invalidate();
				if(this.ready)
					this.dispatchEvent({map: this, type:JMI.components.Map.READY});
			}
		},
		getProperty: function( name) {
			if( this.planContainer && env.props[name])
				return this.planContainer.map.env.props[name];
			return null;
		},
		
		invalidate: function() {
			this.renderShape(this.restDrawingCanvas, this.size.width, this.size.height);
		},		
        renderShape: function(canvas, width, height, position) {
            // If no position is specified, take (0,0)
            position = position || new JMI.script.Point();
            if(width > 0 && height > 0) {
                // Copying the content of the context on to visible canvas context
                this.drawingContext.drawImage(canvas, position.x, position.y, width, height, position.x, position.y, width, height);
            }
        },
		clear: function() {
			JMI.util.ImageUtil.clear(this.backDrawingCanvas, this.backDrawingContext);
			JMI.util.ImageUtil.clear(this.restDrawingCanvas, this.restDrawingContext);
			JMI.util.ImageUtil.clear(this.curDrawingCanvas, this.curDrawingContext);
			JMI.util.ImageUtil.clear(this.drawingCanvas, this.drawingContext);
		},
		mouseMoveHandler: function(event) {
			if (this instanceof HTMLCanvasElement) {
			    var mousePosition = JMI.components.Map.getPosition(this, event);
				this.JMI.curPos.x = mousePosition.x;
				this.JMI.curPos.y = mousePosition.y;
				var debugDiv = document.getElementById("mouse");
				//debugDiv.innerHTML = this.x
				debugDiv.innerHTML = "<br> mouse position : (" + mousePosition.x + ", " + mousePosition.y + ")";
				if (this.JMI.ready) {
					this.JMI.planContainer.map.plan.updateZoneAt(this.JMI.curPos);
				}
			}
		},
		mouseOverHandler: function(event) {
			this.JMI.mouseMoveHandler( event);
		},
		mouseOutHandler: function(event) {
			this.JMI.mouseMoveHandler( event);
		},
		mouseClickHandler: function(event) {
			if (this instanceof HTMLCanvasElement) {
			    var mousePosition = JMI.components.Map.getPosition(this, event);
				if ( this.JMI.ready && this.JMI.planContainer.map.plan.curSat != null )
				{
					this.JMI.planContainer.map.plan.updateZoneAt( mousePosition);
					this.JMI.planContainer.map.plan.curSat.execute( this.JMI, this.JMI.planContainer.map.plan.curZone, mousePosition, JMI.script.Satellite.CLICK_VAL);
				}
			}
		},
		mouseDoubleClickHandler: function(event) {
			if (this instanceof HTMLCanvasElement) {
			    var mousePosition = JMI.components.Map.getPosition(this, event);
				if ( this.JMI.ready && this.JMI.planContainer.map.plan.curSat != null )
				{
					this.JMI.planContainer.map.plan.updateZoneAt( mousePosition);
					this.JMI.planContainer.map.plan.curSat.execute( this.JMI, this.JMI.planContainer.map.plan.curZone, mousePosition, JMI.script.Satellite.DBLCLICK_VAL);
				}
			}
		},
		resize: function(width, height){
			this.clear();

			this.size.width = width; 
			this.size.height = height;
			 
			this.drawingCanvas.width = width;
			this.drawingCanvas.height = height;
			this.curDrawingCanvas.width = width;
			this.curDrawingCanvas.height = height;
			this.restDrawingCanvas.width = width;
			this.restDrawingCanvas.height = height;
			this.backDrawingCanvas.width = width;
			this.backDrawingCanvas.height = height;
				
			if(this.ready) {
				this.planContainer.map.plan.resize( this.size);
				this.planContainer.map.plan.init();
			}
			this.invalidate();
		},
		showStatus: function(message) {
			this.dispatchEvent( {map: this, type: JMI.components.Map.STATUS, message: message});
		},
		log: function(message) {
			if( aptana && aptana.log)
				aptana.log( message);
			if( console && console.log)
				console.log( message);
		},
		addEventListener: function(event, listener) {
			this.eventManager.addListener(event, listener);
		},
		dispatchEvent: function(event) {
			this.eventManager.fire(event);
		},
		removeEventListener: function(event, listener) {
			this.eventManager.removeListener(event, listener);
		},
		/**
		 * Sets the currently displayed selection.
		 * Called by JavaScript.
		 * @param selNam	A selection name as defined in the Dictionary.
		 */
		setSelection: function(selection) {
			var selId = this.getSelId(selection);
			this.JMI.planContainer.map.plan.curSel = selId;
			this.JMI.planContainer.map.plan.init();
			this.invalidate();
		},
		clearSelection: function( selection) {
			this.clearZoneSelection( selection, this.JMI.planContainer.map.plan.nodes, this.JMI.planContainer.map.plan.nodes.length );
			this.clearZoneSelection( selection, this.JMI.planContainer.map.plan.links, this.JMI.planContainer.map.plan.linksCnt );
			this.invalidate();
		},
		/**
		 * Remove zones from a selection.
		 * The display must be refresh to reflect the new selection.
		 * @param selNam	A selection name as defined in the Dictionary.
		 * @param zones		An array of Zones (Nodes or Links).
		 * @param n			Number of zone to remove from selection in the array, starting from index 0.
		 */
		clearZoneSelection: function( selection, zones, n) {
			var selId = getSelId(selection);
			if ( selId != -1 )
			{
				var unselBit = ~( 1 << selId );
				for( var i = 0; i < n; i ++ )
				{
					zones[i].selection &= unselBit;
				}
			}
		},
		/**
		 * Gets the id of a selection, knowing its name.
		 * @param selNam	A selection name as defined in the Dictionary.
		 * @return			An ID in [0,31] or -1 if the selection name is unknown.
		 */
		getSelId: function( selection) {
			if( this.JMI.planContainer.map.env.selections[selection] == null)
				return -1;
			return  env.selections[selection];
		},
		
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
						this.dispatchEvent( {map: this, type: JMI.components.Map.ACTION, fn: func, args: params});
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
			this.dispatchEvent( {map: this, type: JMI.components.Map.NAVIGATE, url: actionStr, target: target});
			window.open( actionStr, target);
		},
		openSoCom: function ( e) {
			window.open( "http://www.social-computing.com", "_blank");
		}
	};
	
	return Map;
}());

JMI.components.Map.version = "1.0-SNAPSHOT";
JMI.components.Map.EMPTY = "empty";
JMI.components.Map.READY = "ready";
JMI.components.Map.STATUS = "status";
JMI.components.Map.ERROR = "error";
JMI.components.Map.ATTRIBUTE_CLICK = "attribute_click";
JMI.components.Map.ATTRIBUTE_DBLECLICK = "attribute_dblclick";
JMI.components.Map.ATTRIBUTE_HOVER = "attribute_hover";
JMI.components.Map.ACTION = "action";
JMI.components.Map.NAVIGATE = "navigate";
// Not yest implemented
//JMI.components.Map.LINK_CLICK = "link_click";
//JMI.components.Map.LINK_DBLECLICK = "link_dblclick";
//JMI.components.Map.LINK_HOVER = "link_hover";

// Adapted from: http://www.quirksmode.org/js/findpos.html and 
// http://stackoverflow.com/questions/5085689/tracking-mouse-position-in-canvas
JMI.components.Map.getPosition= function(canvas, e) {
    var left = 0, top = 0;

    if(canvas.offsetParent) {
        while(canvas) {
            left += canvas.offsetLeft;
            top += canvas.offsetTop;
            canvas = canvas.offsetParent;
        }
    }
    return {
        x : e.pageX - left,
        y : e.pageY - top
    };
};

/*
public function get bitmapData():BitmapData
{
	return _offScreen;
}
*/

/*


private function findLink( zone:ActiveZone):Link {
	return new Link( zone);
}


public function menuHandler( evt:MenuEvent):void {
	performAction( evt.item.action);
}

public function actionPerformed( actionStr:String ):void {
	performAction( actionStr);
}*/


/*

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

