package com.socialcomputing.wps.components
{
	import flash.events.Event;
	
	public class NodeEvent extends Event
	{
		public static var NODE_CLICK:String = "clicknode";
		public static var NODE_MOUSEDOWN:String = "mousedownnode";
		public static var NODE_MOUSEUP:String = "mouseupnode";
		public static var NODE_MOUSEOVER:String = "mouseovernode";
		public static var NODE_DOUBLECLICK:String = "doubleclicknode";

		private var _node:Node;
		public function NodeEvent(node:Node, type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this._node = node;
		}

		public function get node():Node
		{
			return _node;
		}
	}
}