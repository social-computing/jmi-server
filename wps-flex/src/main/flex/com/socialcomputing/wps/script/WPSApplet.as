package com.socialcomputing.wps.script
{
    import flash.geom.Rectangle;
    
    import mx.controls.Image;
    import mx.core.UIComponent;

	// La future classe de composant à renommer et refactorer 
	public class WPSApplet extends UIComponent
	{
		public var m_env:Env;
		public var m_plan:Plan;
		
        public var m_backImg:Image;
		public var m_restImg:Image;
        
		public var m_backImgUrl:String;
        
        public static var s_hasGfxInc:Boolean;
        public var m_error:String;
		
		public function WPSApplet()
		{
		}
        public function getSize():Dimension {
            return new Dimension( width, height);
        }
		public function getSizeR():Rectangle {
			return new Rectangle( 0, 0, width, height);
		}

		public function showStatus(message:String):void {
			trace( message);
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
							String[]    params      = paramStr.split( "," );
							
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