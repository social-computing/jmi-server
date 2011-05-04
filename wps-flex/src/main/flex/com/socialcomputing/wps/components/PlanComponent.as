package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.plan.PlanContainer;
	import com.socialcomputing.wps.script.Dimension;
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.Plan;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.InteractiveObject;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.geom.Matrix;
	import flash.geom.Point;
	
	import mx.controls.Alert;
	import mx.controls.Image;
	import mx.managers.CursorManager;
	
	import spark.components.Group;
	import spark.core.SpriteVisualElement;
	
	public class PlanComponent extends Group
	{
		public static var s_hasGfxInc:Boolean;
		
		private var _dataProvider:PlanContainer = null;
		private var _nodes:Array = null;
		private var _drawingSurface: SpriteVisualElement;
		private var _curPos:Point= new Point();
		private var _ready:Boolean = false;

		private var _backImgUrl:String;
		private var _backImg:Image;
		private var _restImg:Image;
		private var onScreen:BitmapData;
		
		public function PlanComponent()
		{
			super();
			// Drawing surface of the component
			_drawingSurface = new SpriteVisualElement();
			this.addElement(_drawingSurface);
			
			addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
			addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
			addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
			/*			
				addEventListener(MouseEvent.MOUSE_CLICK, mouseClickHandler);
				addEventListener(MouseEvent.MOUSE_DOUBLE_CLICK, mouseDoubleClickHandler);
			*/	
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
		
		public function get backImg():Image
		{
			return _backImg;
		}
		
		public function get restImg():Image
		{
			return _restImg;
		}
		
		public function get backImgUrl():String
		{
			return _backImgUrl;
		}
		
		public function set backImg(value:Image):void
		{
			_backImg = value;
		}
		
		public function set restImg(value:Image):void
		{
			_restImg = value;
		}
		
		public function set dataProvider(value:Object):void
		{
			// If the given value is null return for now
			// TODO : If the local plancontainer is set, reset objects 
			if(value == null) {
				return;
			}
			
			// CursorManager.setBusyCursor();
			if(value is PlanContainer) {
				this._dataProvider = value as PlanContainer;
			}
			else {
				this._dataProvider = PlanContainer.fromJSON(value);
			}

			// var needPrint:Boolean = false; // Later
			//_dataProvider.env.init( this, needPrint);
/*			m_backImg	= createImage( m_size.width, m_size.height );
			m_restImg	= createImage( m_size.width, m_size.height );
*/
			try {
				// TODO : Handle this properly
			    // Should manage the onScreen object each time the service is called
				this.onScreen = new BitmapData(this.width, this.height);					
				this._drawingSurface.addChild(new Bitmap(this.onScreen));
				
				// Drawing in the offscreen back buffer
			    var backBuffer:BitmapData = new BitmapData(this.width, this.height);
				var drawCanvas:Shape = new Shape();
				drawCanvas.graphics.lineStyle(1,0xFF00FF);
				drawCanvas.graphics.lineTo(20,0);
				drawCanvas.graphics.lineTo(20,20);
				drawCanvas.graphics.lineTo(0,20);
				drawCanvas.graphics.lineTo(0,0);
				backBuffer.draw(drawCanvas, new Matrix());
				
				// Copying the content of the back buffer on screen
				onScreen.copyPixels(backBuffer, backBuffer.rect, new Point(0,0));
				
				plan.m_applet     = this;
				plan.m_curSel     = -1;
				plan.initZones( this.graphics, plan.m_links, true );
				plan.initZones( this.graphics, plan.m_nodes, true );
				plan.resize( size);
				plan.init();
				plan.resize( size);
				_ready = true;
				
			}
			catch(error:Error) {
				trace( error.getStackTrace());	
			}
				
			// showStatus( "" );
			//CursorManager.removeBusyCursor();
			
			/*
			 * Don't redraw immediately, because maybe the code that's calling us is
			 * going to change several settings, and we don't want to redraw for each 
			 * setting change. Instead, tell the flex framework that
			 * we need to be redrawn; the framework will ensure that updateDisplayList
			 * is invoked after all scripts have finished executing.
			 */
			this.invalidateDisplayList();		
		}
		
		
		public function showStatus(message:String):void {
			trace( message);
		}
		
		public function get curPos():Point {
			return _curPos;
		}
		
		public function set curPos(pos:Point):void {
			_curPos = pos;
		}
		
		public function mouseOverHandler(event:MouseEvent):void {
			trace("mouseOverHandler");
		}
		
		public function mouseMoveHandler(event:MouseEvent):void {
			/*
			curPos.x    = event.stageX;
			curPos.y    = event.stageY;
			if( ready) {
				_dataProvider.plan.updateZoneAt( curPos ); // The Zone, SubZone or Satellite can have changed
			}
			*/
		}
		
		public function mouseOutHandler(event:MouseEvent):void {
			trace("mouseOutHandler");
		}
		
		public function mouseClickHandler(event:MouseEvent):void {
			trace("mouseClickHandler");
		}
		
		public function mouseDoucleClickHandler(event:MouseEvent):void {
			trace("mouseDoucleClickHandler");
		}
		
		
		/**
		 * @inheritDoc
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			trace("Update graphic display");
			//if ( ready)
			//{
				//graphics.drawImage( _restImg, 0, 0, null );
			//	plan.paintCurZone( graphics );  // A new Zone is hovered, let's paint it!
			//}
		}
		
		/**
		 * Wrapper for a menu item that call performAction with the ActionCommand String as argument.
		 **/
		public function actionPerformed( aactionStr:String ):void {
			performAction( aactionStr);
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
			// TODO ACYTYIONS			
			/*
			String  target      = "_blank";
			int     sep         = actionStr.indexOf( ':' );
			
			if ( sep != -1 )
			{
			target  = actionStr.substring( 0, sep );
			
			if( target.equalsIgnoreCase( jsStr ))   // Call javascript function
			{
			actionStr   = actionStr.substring( jsStr.length()+ 1 );
			if( actionStr.charAt( 0 )== '_' )
			{	// javascript:_target:function()
			int pos = actionStr.indexOf( ':' );
			if( pos <= 0) return;
			target      = actionStr.substring( 1, pos );
			actionStr   = actionStr.substring( pos + 1 );
			}
			
			// LiveConnect!
			if( m_planWindow != null)
			{
			int pos     = actionStr.indexOf( '(' );
			
			if( pos > 0)
			{
			String  	func        = actionStr.substring( 0, pos ),
			paramStr    = actionStr.substring( pos + 1, actionStr.length()- 1 );
			String[]    params      = Base.getTextParts( paramStr, "," );
			
			m_planWindow.call( func, params );
			}
			return;
			}
			else    // Javascript not supported try to emulate it, if possible
			{
			String	noScriptUrl	= getParameter( "NoScriptUrl" );
			
			if( target.equalsIgnoreCase( "null" )|| noScriptUrl == null )	return;
			
			actionStr = addCGIParam( noScriptUrl, "func=" + URLEncoder.encode( actionStr , "UTF-8" ), true );
			}
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
			//System.out.println( actionStr + " in " + target);
			getAppletContext().showDocument( convertURL( actionStr ), target );
			*/
		}
	}
}