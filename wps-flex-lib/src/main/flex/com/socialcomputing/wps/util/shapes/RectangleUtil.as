package com.socialcomputing.wps.util.shapes
{
	import flash.geom.Rectangle;

	public class RectangleUtil
	{
		public function RectangleUtil()
		{
			// TODO Throw error here
		}
		
		/**
		 * Merge 2 Rectangles.
		 * If the dest Rectangle has one null dimension then copy the source on it.
		 * 
		 * @param dst	Destination Rectangle that will hold its union with src.
		 * @param src	Source Rectangle.
		 */
		public static function merge(dst:Rectangle, src:Rectangle):void {
			if(dst.width * dst.height != 0) {
				copy(dst, dst.union( src));
			}
			else {
				copy(dst, src);
			}
		}
		
		
		/**
		 * Copy the source <code>Rectangle</code> properties to the destination <code>Rectangle</code>
		 * 
		 * @param dst Destination Rectangle to copy the values to 
		 * @param src Source Rectangle to copy the values from
		 * 
		 */
		public static function copy(dst:Rectangle, src:Rectangle):void {
			dst.x = src.x;
			dst.y = src.y
			dst.width = src.width;
			dst.height = src.height;
		}
	}
}