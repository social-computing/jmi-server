package com.socialcomputing.wps.components.events
{
	import com.socialcomputing.wps.components.Attribute;
	
	import flash.events.Event;
	
	public class AttributeEvent extends Event
	{
		public static const CLICK:String = "attribute_click";
		public static const DOUBLE_CLICK:String = "attribute_double_click";
		public static const HOVER:String = "attribute_hover";
		private var _attribute:Attribute;
		private var _localX:int, _localY:int;
		
		public function AttributeEvent( type:String, attribute:Attribute, x:int, y:int, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super( type, bubbles, cancelable);
			this._attribute = attribute;
			this._localX = x;
			this._localY = y;
		}

		public function get attribute():Attribute
		{
			return _attribute;
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