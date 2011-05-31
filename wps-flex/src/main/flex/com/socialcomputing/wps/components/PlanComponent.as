package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.plan.PlanContainer;
	import com.socialcomputing.wps.script.BagZone;
	import com.socialcomputing.wps.script.ColorX;
	import com.socialcomputing.wps.script.Dimension;
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.Plan;
	import com.socialcomputing.wps.script.SatData;
	import com.socialcomputing.wps.script.Satellite;
	import com.socialcomputing.wps.script.ShapeX;
	import com.socialcomputing.wps.script.Slice;
	import com.socialcomputing.wps.script.VContainer;
	import com.socialcomputing.wps.util.controls.ImageUtil;
	import com.socialcomputing.wps.util.shapes.RectangleUtil;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.InteractiveObject;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.controls.Image;
	import mx.controls.Menu;
	import mx.events.MenuEvent;
	import mx.managers.CursorManager;
	
	import spark.components.Group;
	import spark.core.SpriteVisualElement;
	
	[DefaultBindingProperty(destination="dataProvider")]
	
	[IconFile("Plan.png")]
	
	[Event(name="ready", type="flash.events.Event")]
	[Event(name="error", type="flash.events.Event")]
	[Event(name="action", type="com.socialcomputing.wps.components.ActionEvent")]
	[Event(name="status", type="com.socialcomputing.wps.components.StatusEvent")]
	
	public class PlanComponent extends Group
	{
		include "../script/Version.as"
		
		private var _dataProvider:PlanContainer = null;
		private var _backgroundColor:int = 0xFFFFFF;
		private var _nodes:Array = null;
		private var _curPos:Point= new Point();
		private var _ready:Boolean = false;
		private var _clear:Boolean = false;

		private var _backImgUrl:String;
		//private var _backImg:Image;
		//private var _restImg:Image;

		/*
		 *  Specific display elements
		 */
		private var _onScreen:BitmapData;
		private var _offScreen:BitmapData;
		private var _drawingSurface:SpriteVisualElement;
		
		/**
		 * Image used to quickly restore the aspect of a zone that is no longer current.
		 * It includes the background + links + Satellites of each place at rest.
		 */
		private var _restDrawingSurface:Sprite;  

		/**
		 * Image used as a background on which the current zone is drawn.
		 * It includes the background, and the zones rendered with their 'ghosted' satellites form the rest swatch.
		 * The resulting image is then filtered with a transparency color.
		 */
		private var _backDrawingSurface:Sprite; 

		private var _curDrawingSurface:Sprite;
		
		public function PlanComponent()
		{
			super();
			
			// Drawing surface of the component
			_drawingSurface = new SpriteVisualElement();
			this.addElement(_drawingSurface);

			this._curDrawingSurface = new Sprite();
			this._restDrawingSurface = new Sprite();
			this._backDrawingSurface = new Sprite();
		
			this.doubleClickEnabled = true;
			addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
			addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
			addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
			addEventListener(MouseEvent.CLICK, mouseClickHandler);
			addEventListener(MouseEvent.DOUBLE_CLICK, mouseDoubleClickHandler);
		}
		
		public function get backDrawingSurface():Sprite
		{
			return _backDrawingSurface;
		}

		public function get backgroundColor():int
		{
			return _backgroundColor;
		}

		public function set backgroundColor(value:int):void
		{
			_backgroundColor = value;
		}

		public function get ready():Boolean {
			return plan != null && _ready;
		}			
		
		public function get plan():Plan
		{
			if(this._dataProvider == null) {
				return null
			}
			return _dataProvider.plan;
		}
		
		public function get env():Env
		{
			if(this._dataProvider == null) {
				return null
			}
			return _dataProvider.env;
		}
		
		public function get size():Dimension {
			return new Dimension(this.width, this.height);
		}
		
		public function get curDrawingSurface():Sprite
		{
			return _curDrawingSurface;
		}
		public function get restDrawingSurface():Sprite
		{
			return _restDrawingSurface;
		}
		
		public function get backImgUrl():String
		{
			return _backImgUrl;
		}
		
		public function get curPos():Point {
			return _curPos;
		}
		
		public function set curPos(pos:Point):void {
			_curPos = pos;
		}
		
		public function set dataProvider(value:Object):void
		{
			this._ready = false;
			this._onScreen = new BitmapData(this.width, this.height);
			this._offScreen = new BitmapData(this.width, this.height);
			this._drawingSurface.addChild(new Bitmap(this._onScreen));
			
            /*this.curDrawingSurface.x = 700;
            this._drawingSurface.addChild(this.curDrawingSurface);
            this.restDrawingSurface.x = 700;
            this.restDrawingSurface.y = 200;
            this._drawingSurface.addChild(this.restDrawingSurface);*/
            this.restDrawingSurface.name = "REST drawing surface";
            this.curDrawingSurface.name = "CUR drawing surface";
            
			// If the given value is null return for now
			// TODO : If the local plancontainer is set, reset objects 
			if(value == null) {
				clear();
				return;
			}
			
			showStatus( "" );
			
			CursorManager.setBusyCursor();
			if(value is PlanContainer) {
				this._dataProvider = value as PlanContainer;
			}
			else {
				this._dataProvider = PlanContainer.fromJSON(value);
			}

			var needPrint:Boolean = false; // Later
			_dataProvider.env.init( this, needPrint);

			try {
				plan.m_applet = this;
				plan.m_curSel = -1;
				plan.initZones(this.restDrawingSurface, plan.m_links, true);
                plan.initZones(this.restDrawingSurface, plan.m_nodes, true);
				plan.resize(size);
				plan.init();
                plan.m_applet.env.loader.start();
				plan.resize(size);
				plan.init();
                
				this._ready = true;
		
			}
			catch(error:Error) {
				trace( error.getStackTrace());	
			}
				
			CursorManager.removeBusyCursor();
			
			/*
			 * Don't redraw immediately, because maybe the code that's calling us is
			 * going to change several settings, and we don't want to redraw for each 
			 * setting change. Instead, tell the flex framework that
			 * we need to be redrawn; the framework will ensure that updateDisplayList
			 * is invoked after all scripts have finished executing.
			 */
			this.invalidateProperties();
			this.invalidateDisplayList();
			if(ready)
				dispatchEvent(new Event("ready"));
			else
				dispatchEvent(new Event( "error"));
		}

		public function clear():void {
			//showStatus( "" );
			ImageUtil.clear( this._restDrawingSurface);
			this._restDrawingSurface.graphics.beginFill( this._ready ? this.env.m_inCol.m_color : this._backgroundColor);
			this._restDrawingSurface.graphics.drawRect(0, 0, this.width, this.height);
			this._restDrawingSurface.graphics.endFill();
			this._dataProvider = null;
			this._ready = false;
			this._clear = true;
			this.invalidateProperties();
			this.invalidateDisplayList();		
		}
		
		public function showStatus(message:String):void {
			dispatchEvent(new StatusEvent( message));
		}
		
		public function mouseOverHandler(event:MouseEvent):void {
			trace("mouseOverHandler");
		}
		
		public function mouseMoveHandler(event:MouseEvent):void {
			this.curPos.x = event.localX;
			this.curPos.y = event.localY;
			if(ready) {
				_dataProvider.plan.updateZoneAt(this.curPos);
			}
		}
		
		public function mouseOutHandler(event:MouseEvent):void {
			trace("mouseOutHandler");
		}
		
		public function mouseClickHandler(event:MouseEvent):void {
			if ( ready && plan.m_curSat != null )
			{
				var point:Point = new Point();
				point.x = event.localX;
				point.y = event.localY;
				plan.updateZoneAt( point);
				plan.m_curSat.execute( this, plan.m_curZone, point, Satellite.CLICK_VAL);
			}
		}
		
		public function mouseDoubleClickHandler(event:MouseEvent):void {
			if ( ready && plan.m_curSat != null )
			{
				var point:Point = new Point();
				point.x = event.localX;
				point.y = event.localY;
				plan.updateZoneAt( point);
				plan.m_curSat.execute( this, plan.m_curZone, point, Satellite.DBLCLICK_VAL);
			}
		}
		
		
		/**
		 * @inheritDoc
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			trace("Update graphic display");
			if(this._ready || this._clear) {
				this.renderShape(this._restDrawingSurface, this.width, this.height);
				this._clear = false;
			}
		}
		
		/**
		 * Wrapper for a menu item
		 **/
		public function menuHandler( evt:MenuEvent):void {
			performAction( evt.item.action);
		}
		
		/**
		 * Wrapper for a menu item that call performAction with the ActionCommand String as argument.
		 **/
		public function actionPerformed( actionStr:String ):void {
			performAction( actionStr);
		}
		
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
		public function performAction( actionStr:String):void {
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
						var params:Array    = paramStr.split( ",");
						dispatchEvent( new ActionEvent( func, params));
					}
					return;
				}
				else if( target.charAt( 0 )== '_' )   // open a frame window
				{
					target      = actionStr.substring( 1, sep );
					actionStr   = actionStr.substring( sep + 1 );
				}
				else
				{
					target  = "_blank";
				}
			}
			// TODO FireEvent
/*			getAppletContext().showDocument( convertURL( actionStr ), target );
			*/
			dispatchEvent(new ActionEvent( actionStr));
		}
		
		
		public function renderShape(sprite:Sprite, width:uint, height:uint, position:Point = null):void {
			trace("renderShape method called");
			
			// If no position is specified, take (0,0)
			if(position == null) {
				position = new Point(0, 0);
			}
			
			// Transforming the offscreen back display to a BitmapData
			this._offScreen.draw(sprite, new Matrix());
			
			// Copying the content of the back buffer on screen
			var sourceZone:Rectangle = new Rectangle(position.x, position.y, width, height);
			_onScreen.copyPixels(this._offScreen, sourceZone, position);

			trace("renderShape method end");
		}
	}
}
