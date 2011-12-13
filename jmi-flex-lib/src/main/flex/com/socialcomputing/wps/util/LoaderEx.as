package com.socialcomputing.wps.util
{
	import flash.display.Loader;
	
	public class LoaderEx extends Loader
	{
		private var _stop:Boolean;
		
		public function LoaderEx()
		{
			super();
			this._stop = false;
		}
		
		public override function close():void {
			this._stop = true;
			super.close();
		}
		
		public function get stop():Boolean {
			return this._stop;
		}
	}
}