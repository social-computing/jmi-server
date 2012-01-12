package com.socialcomputing.jmi.util
{
	import flash.display.Loader;
	
	public class LoaderEx extends Loader
	{
		private var _stop:Boolean;
		
		public function LoaderEx()
		{
			super();
			this.stop = false;
		}
		
		public override function close():void {
			this.stop = true;
			super.close();
		}
		
		public function get stop():Boolean {
			return this.stop;
		}
	}
}