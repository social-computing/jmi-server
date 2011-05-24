package com.socialcomputing.wps.components
{
	import flash.events.Event;
	
	public class StatusEvent extends Event
	{
		private var _status:String;
		
		public function StatusEvent(status:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super("status", bubbles, cancelable);
			this._status = status;
		}

		public function get status():String
		{
			return _status;
		}

		public function set status(value:String):void
		{
			_status = value;
		}

	}
}