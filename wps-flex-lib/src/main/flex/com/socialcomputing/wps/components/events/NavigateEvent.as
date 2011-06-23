package com.socialcomputing.wps.components.events
{
	import flash.events.Event;
	
	public class NavigateEvent extends Event
	{
		public static const NAVIGATE:String = "navigate";
		private var _url:String;
		private var _target:String;
		
		public function NavigateEvent(url:String, btarget:String="_blank", bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super( NavigateEvent.NAVIGATE, bubbles, cancelable);
		}

		public function get btarget():String
		{
			return _target;
		}

		public function get url():String
		{
			return _url;
		}

	}
}