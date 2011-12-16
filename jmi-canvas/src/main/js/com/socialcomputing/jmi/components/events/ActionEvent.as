package com.socialcomputing.wps.components.events
{
	import flash.events.Event;
	
	public class ActionEvent extends Event
	{
		private var _action:String;
		private var _args:Array;
		
		public function ActionEvent(action:String, args:Array=null, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super("action", bubbles, cancelable);
			this._action = action;
			this._args = args;
		}

		public function get args():Array
		{
			return _args;
		}

		public function get action():String
		{
			return _action;
		}

	}
}