package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.script.ActiveZone;
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.LinkZone;
	
	import mx.skins.halo.ActivatorSkin;

	dynamic public class Entity
	{
		private var env:Env;
		private var links:Vector.<LinkZone>;
		private var _attributes:Vector.<Attribute>;
		
		public function Entity( env:Env)
		{
			links = new Vector.<LinkZone>();
			_attributes = new Vector.<Attribute>();
			this.env = env;
		}
		
		public function get attributes():Vector.<Attribute>
		{
			return _attributes;
		}

		public function addLink(link:LinkZone):void {
			links.push( link);
		}
		
		public function addAttribute(attribute:Attribute):void {
			_attributes.push( attribute);
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