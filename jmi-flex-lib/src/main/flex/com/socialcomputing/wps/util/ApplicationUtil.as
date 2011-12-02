package com.socialcomputing.wps.util
{
	import mx.core.FlexGlobals;

	public class ApplicationUtil {
		
		//--------------------------------------------------------------------------
		//
		//  Constructor
		//
		//--------------------------------------------------------------------------
		/**
		 *  @private
		 */
		public function ApplicationUtil() {
			super();
		}

		
		/**
		 * Retuns the URL where the swf application is hosted   
		 */
		public static function getSwfRoot():String {
			var root:String = FlexGlobals.topLevelApplication.url;
			if(root) {
				root = root.slice(0, root.lastIndexOf("/"));
			}
			return root;
		}
	}
}