package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.plan.PlanContainer;
	import com.socialcomputing.wps.script.Dimension;
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.Plan;
	
	import mx.controls.Alert;
	import mx.controls.Image;
	
	import spark.components.Group;
	import spark.core.SpriteVisualElement;
	
	public class PlanComponent extends Group
	{
		public static var s_hasGfxInc:Boolean;
		
		private var _dataProvider:PlanContainer = null;
		private var _nodes:Array = null;
		private var _drawingSurface: SpriteVisualElement;
		
		// Background Image
		private var _backImg:Image;
		private var _backImgUrl:String;
		
		// ?
		private var _restImg:Image;
		
		public function PlanComponent()
		{
			super();
			this.addEventListener(NodeEvent.NODE_MOUSEDOWN,  onNodeMouseDown, true);
			// TODO : See how to draw links ...
			// Might be on a separate drawing surface ?
			/* 
			_drawingSurface = new SpriteVisualElement();
			this.addElement(_drawingSurface);
			*/
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
		
		public function set dataProvider(value:Object):void
		{
			// If the given value is null return for now
			// TODO : If the local plancontainer is set, reset objects 
			if(value == null) {
				return;
			}
			
			if(value is PlanContainer) {
				this._dataProvider = value as PlanContainer;
			}
			else {
				this._dataProvider = PlanContainer.fromJSON(value);
			}
			
			// Fake elements here
			this._nodes = new Array(new Node(1, 30, 30), new Node(2, 70, 80));
			for each (var n:Node in this._nodes) {
				var nc:NodeComponent = new NodeComponent();
				
				nc.bcolor = 0xFFFFFF;
				nc.color = 0x555555;
				nc.node = n;
				this.addElement(nc);
			}
			
			/*
			 * Don't redraw immediately, because maybe the code that's calling us is
			 * going to change several settings, and we don't want to redraw for each 
			 * setting change. Instead, tell the flex framework that
			 * we need to be redrawn; the framework will ensure that updateDisplayList
			 * is invoked after all scripts have finished executing.
			 */
			this.invalidateDisplayList();
		}
		
		
		protected function onNodeMouseDown(event:NodeEvent):void
		{
			Alert.show("Selected node : " + event.node.id);
		}
		
		public function showStatus(message:String):void {
			trace( message);
		}
		
		
		/**
		 * @inheritDoc
		 */
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
			super.updateDisplayList(unscaledWidth, unscaledHeight);
			trace("Update graphic display");
			// graphics.clear();
			// Draw a rectange in the graphic area
			// For test purposes only
			/* 
			
			graphics.beginFill( 0xffffff, 0.0 );
			graphics.drawRect( 0, 0, unscaledWidth, unscaledHeight );
			graphics.endFill();
			*/
			/*
			if(_dataProvider != null){
				drawComponents();
			}
			*/
		}
		
		// The drawing of all plan components here
		/*
		private function drawComponents():void {
			trace("draw components");
			
			graphics.lineStyle(1, 0x000000, 1.0);
			for each (var i:Node in this.node) {
				graphics.drawEllipse(i.x, i.y, i.width, i.width);
			}
		}
		*/
		
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
		
		/**
		 * Get this component size as an <code>Dimension</code> object 
		 */
		public function getSize():Dimension {
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



	}
}