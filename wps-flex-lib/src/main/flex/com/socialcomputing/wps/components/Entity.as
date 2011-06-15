package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.LinkZone;

	dynamic public class Entity
	{
		private var env:Env;
		private var links:Vector.<LinkZone>;
		
		public function Entity( env:Env)
		{
			links = new Vector.<LinkZone>();
			this.env = env;
		}
		
		public function addLink(link:LinkZone):void {
			links.push( link);
		}
		
		public function select( selection:String):void {
			var selId:int   = env.m_selections[selection];
			if ( selId != -1 )
			{
				selId = 1 << selId;
				for each ( var link:LinkZone in links) {
					link.m_selection   |= selId;
				}
			}
		}
	}
}