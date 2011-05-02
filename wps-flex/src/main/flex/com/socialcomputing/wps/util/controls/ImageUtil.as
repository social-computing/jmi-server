package com.socialcomputing.wps.util.controls
{
	import com.socialcomputing.wps.script.Dimension;
	
	import flash.geom.Rectangle;
	
	import mx.controls.Image;

	/**
	 * Image utility class.
	 * It is done this way instead of a specific Image class because of ActionScript3
	 * contructor overload limitations : on class can only have one constructor signature. 
	 */
	public class ImageUtil {
		
		public function ImageUtil() {
			// TODO : Check that this isn't called directly 
		}
		
		/**
		 * Image constructor with size properties  
		 * @param width the image width
		 * @param height the image height
		 */		
		public static function fromSize(width:uint, height:uint):Image {
			var i:Image = new Image();
			i.width = width;
			i.height = height;
			return i;
		}
		
		/**
		 * Image construction from a given <code>Dimension</code>  
		 * @param dimension a <code>Dimension</code> Object
		 */
		public static function fromDimension(dimension:Dimension):Image {
			if(dimension == null) {
				throw new ArgumentError("The given dimension object can't be null");
			}
			var i:Image = new Image();
			i.width = dimension.width;
			i.height = dimension.height;
			return i;
		}
		
		/**
		 * Image construction from a given <code>Rectange</code>
		 * Create a new image with the width and height of the given Rectangle  
		 * @param rectangle a <code>Rectangle</code> Object
		 */
		public static function fromRectangle(rect:Rectangle):Image {
			if(rect == null) {
				throw new ArgumentError("The given rectangle object can't be null");
			}
			var i:Image = new Image();
			i.width = rect.width;
			i.height = rect.height;
			return i;
		}
	}
}