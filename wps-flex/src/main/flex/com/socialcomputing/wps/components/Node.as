package com.socialcomputing.wps.components
{
	public class Node
	{
		private var _x:uint;
		private var _y:uint;
		private var _width:uint;
		
		public function Node(x:uint, y:uint, width:uint = 10)
		{
			this._x = x;
			this._y = y;
			this._width = width;
		}

		public function get x():uint
		{
			return _x;
		}

		public function get y():uint
		{
			return _y;
		}

		public function get width():uint
		{
			return _width;
		}
	}
}