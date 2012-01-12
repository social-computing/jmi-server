package com.socialcomputing.jmi.components
{
	import com.socialcomputing.jmi.script.ActiveZone;
	import com.socialcomputing.jmi.script.Env;

	dynamic public class Attribute
	{
		private var env:Env;
		private var _zone:ActiveZone;
		
		public function Attribute( env:Env, ref:ActiveZone) {
			this.env = env;
			this._zone = ref;
			for( var o : * in this._zone.m_props) {
				this[o] = this._zone.m_props[o];
			}
		}
		
		public function get zone():ActiveZone
		{
			return _zone;
		}

		public function select( selection:String):void {
			var selId:int   = env.selections[selection];
			if ( selId != -1 )
			{
				selId = 1 << selId;
				_zone.m_selection   |= selId;
			}
		}
	}
}