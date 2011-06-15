package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.script.ActiveZone;
	import com.socialcomputing.wps.script.Env;

	dynamic public class Attribute
	{
		private var env:Env;
		private var ref:ActiveZone;
		
		public function Attribute( env:Env, ref:ActiveZone) {
			this.env = env;
			this.ref = ref;
			for( var o : * in this.ref.m_props) {
				this[o] = this.ref.m_props[o];
			}
		}
		
		public function select( selection:String):void {
			var selId:int   = env.m_selections[selection];
			if ( selId != -1 )
			{
				selId = 1 << selId;
				ref.m_selection   |= selId;
			}
		}
	}
}