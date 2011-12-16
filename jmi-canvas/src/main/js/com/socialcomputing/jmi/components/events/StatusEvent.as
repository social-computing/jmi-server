package com.socialcomputing.wps.components.events
{
	import flash.events.Event;
	
	public class StatusEvent extends Event
	{
		public static const STATUS:String = "status";
		public static const ERROR:String = "error";
		
		private var _status:String;
		
		public function StatusEvent(type:String, status:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			this._status = status;
		}

		public function get status():String
		{
			return _status;
		}
	}
}