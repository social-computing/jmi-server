package com.socialcomputing.wps.components
{
	import mx.core.UIComponent;

	public class Node
	{
		private var _id:uint;
		private var _x:uint;
		private var _y:uint;
		private var _width:uint;
		
		public function Node(id:uint, x:uint, y:uint, width:uint = 10)
		{
			this._id = id;
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

		public function get id():uint
		{
			return _id;
		}

	}
}