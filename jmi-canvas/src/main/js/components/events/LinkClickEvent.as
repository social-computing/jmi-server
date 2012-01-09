package com.socialcomputing.wps.components.events
{
	import com.socialcomputing.wps.components.Link;
	
	import flash.events.Event;
	
	public class LinkClickEvent extends Event
	{
		private var _link:Link;
		
		public function LinkClickEvent(link:Link, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super("click", bubbles, cancelable);
			this._link = link;
		}

		public function get link():Link
		{
			return _link;
		}

	}
}