package com.socialcomputing.wps.components.events
{
	import flash.events.Event;
	
	public class StatusEvent extends Event
	{
		public static const STATUS:String = "status";
		private var _status:String;
		
		public function StatusEvent(status:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(StatusEvent.STATUS, bubbles, cancelable);
			this._status = status;
		}

		public function get status():String
		{
			return _status;
		}
	}
}