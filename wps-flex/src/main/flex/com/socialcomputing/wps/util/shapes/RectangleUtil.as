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
			var rect:Rectangle;
			
			if(dst.width * dst.height != 0) {
				rect = dst.union(src);
			}
			else {
				rect = src;
			}
			
			setBounds( dst, rect);
			
			/*
			if(dst.width * dst.height != 0) {
				
				var xMax:Number = dst.x + dst.width,
					yMax:Number = dst.y + dst.height;
				
				dst.x = Math.min( dst.x, src.x );
				dst.y = Math.min( dst.y, src.y );
				dst.width   = Math.max( xMax, src.x + src.width )- dst.x;
				dst.height  = Math.max( yMax, src.y + src.height )- dst.y;
				

			}
				//else    dst.set.setBounds( src );
			else {
				dst.x = src.x;
				dst.y = src.y;
				dst.width = src.width;
				dst.height = src.height;
			}
			*/
		}
		
		public static function setBounds(dst:Rectangle, src:Rectangle):void {
			dst.x = src.x;
			dst.y = src.y
			dst.width = src.width;
			dst.height = src.height;
		}
	}
}