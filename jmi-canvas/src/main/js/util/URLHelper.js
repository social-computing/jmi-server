package com.socialcomputing.jmi.util
{
	import mx.utils.URLUtil;

	public class URLHelper
	{
		//--------------------------------------------------------------------------
		//
		//  Constructor
		//
		//--------------------------------------------------------------------------
		/**
		 *  @private
		 */
		public function URLHelper() {
			super();
		}
		
		
		/**
		 *  Converts a potentially relative URL to a fully-qualified URL.
		 *  If the URL is always considered relative
		 *  This code is taken form mx.utils.URLUtil, and file URL support is added
		 *
		 *  @param rootURL URL used to resolve the URL specified by the <code>url</code> parameter, if <code>url</code> is relative.
		 *  @param url URL to convert.
		 *
		 *  @return Fully-qualified URL.
		 */
		public static function getFullURL(rootURL:String, url:String):String {
			if (url != null && !URLUtil.isHttpURL(url) && !URLHelper.isFileURL(url)) {
				if (url.indexOf("./") == 0) {
					url = url.substring(2);
				}
				
				if (URLUtil.isHttpURL(rootURL) || URLHelper.isFileURL(rootURL)) {
					// relative path, starting with a "/", "/dev/foo.bar"
					if (url.charAt(0) == '/') {
						url = url.substring(1);	
					}
					
					// relative path, "dev/foo.bar".
					var slashPos:Number = rootURL.lastIndexOf("/") + 1;
					
					// Special case
					if (slashPos <= 8) {
						rootURL += "/";
						slashPos = rootURL.length;
					}
					
					url = rootURL.substring(0, slashPos) + url;
				}
			}
			return url;
		}
		
		
		/**
		 *  Determines if the URL begins with the file:// scheme
		 *
		 *  @param url The URL to analyze.
		 * 
		 *  @return <code>true</code> if the URL starts with "file://", false otherwise.
		 */
		public static function isFileURL(url:String):Boolean
		{
			return url != null &&
				(url.indexOf("file://") == 0);
		}
	}
}