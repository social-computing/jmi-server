package com.socialcomputing.wps.script
{
	import flash.geom.Rectangle;
	
	import mx.olap.aggregators.MaxAggregator;

	public class Dimension
	{
		private var _width:uint;
		private var _height:uint;
		
		/**
		 *  Construct a new Dimension Object from a <code>Rectangle</code>
		 * 	It is done this way instead of a specific constructor because of ActionScript3
	     * contructor overload limitations : on class can only have one constructor signature. 
	     */
		public static function fromRectangle(rect:Rectangle):Dimension {
			if(rect == null) {
				throw new ArgumentError("The given rectangle can't be null");
			}
			return new Dimension(rect.width, rect.height);
		}
		
		public function Dimension(width:uint, height:uint)
		{
			_width = width;
			_height = height;
		}
		
		public function resize(d:Dimension):Dimension {
			return new Dimension(Math.max(this.width, d.width), 
							     Math.max(this.height, d.height));
		}
		
		public function get width():uint
		{
			return _width;
		}

		public function get height():uint
		{
			return _height;
		}
	}
}