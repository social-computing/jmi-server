package com.socialcomputing.wps.util.controls
{
	import com.socialcomputing.wps.script.Dimension;
	
	import flash.display.BitmapData;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.geom.Rectangle;

	/**
	 * Image utility class.
	 * It is done this way instead of a specific Image class because of ActionScript3
	 * contructor overload limitations : on class can only have one constructor signature. 
	 */
	public class ImageUtil {
		
		public static function copy(src:Sprite, dst:Sprite):void {
			dst.graphics.copyFrom( src.graphics);
		}
		
		/**
		 * Apply a half transparent color over an image.
		 * This is achieved by drawing 45° lines every 2 pixels.
		 * @param image		The image to cover.
		 * @param dim		size of the image.
		 */
		public static function filterImage( sprite:Sprite, dim:Dimension, color:uint):void {
			var g:Graphics = sprite.graphics;
			g.lineStyle(1, color, 1);
			
			var w:int= dim.width - 1,
				h:int = dim.height - 1,
				min:int = Math.min( w, h ),
				i:int, j:int, n:int = min + 2;
			
			for ( i = 1, j =( w + h + 1)% 2; i < n; i += 2, j += 2)
			{
				//g.drawLine( 0, i, i, 0);
				g.moveTo(0, i);
				g.lineTo(i, 0);
				//g.drawLine( w - j, h, w, h - j );
				g.moveTo(w - j, h);
				g.lineTo(w, h-j);
			}
			if ( w > h )
			{
				n = w - min;
				
				for ( i = 1+( h % 2); i < n; i += 2)
				{
					//g.drawLine( i, h, min + i, 0);
					g.moveTo(i, h);
					g.lineTo(min+i, 0);
				}
			}
			else
			{
				n = h - min;
				
				for ( i = 1+( w % 2); i < n; i += 2)
				{
					//g.drawLine( w, i, 0, min + i );
					g.moveTo(w, i);
					g.lineTo(0, min + i);
				}
			}
		}
	}
}