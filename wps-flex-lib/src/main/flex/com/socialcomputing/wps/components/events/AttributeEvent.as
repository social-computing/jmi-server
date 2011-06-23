package com.socialcomputing.wps.components.events
{
	import com.socialcomputing.wps.components.Attribute;
	
	import flash.events.Event;
	
	public class AttributeEvent extends Event
	{
		public static const CLICK:String = "attribute_click";
		public static const HOVER:String = "attribute_hover";
		private var _attribute:Attribute;
		
		public function AttributeEvent( type:String, attribute:Attribute, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super( type, bubbles, cancelable);
			this._attribute = attribute;
		}

		public function get attribute():Attribute
		{
			return _attribute;
		}

	}
}