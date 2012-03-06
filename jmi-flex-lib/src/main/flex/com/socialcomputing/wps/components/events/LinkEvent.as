package com.socialcomputing.wps.components.events
{
	import com.socialcomputing.wps.script.LinkZone;
	
	import flash.events.Event;
	
	public class LinkEvent extends Event
	{
		public static const CLICK:String = "link_click";
		public static const DOUBLE_CLICK:String = "link_dblclick";
		public static const HOVER:String = "link_hover";
		private var _link:LinkZone;
		private var _localX:int, _localY:int;
		
		public function LinkEvent( type:String, link:LinkZone, x:int, y:int, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super( type, bubbles, cancelable);
			this._link = link;
			this._localX = x;
			this._localY = y;
		}

		public function get link():LinkZone
		{
			return _link;
		}
		
		public function get localX():int
		{
			return _localX;
		}
		
		public function get localY():int
		{
			return _localY;
		}
	}
}