package com.socialcomputing.jmi.components
{
	import com.socialcomputing.jmi.script.ActiveZone;
	
	public class Link
	{
		private var ref:ActiveZone;
		
		public function Link(ref:ActiveZone)
		{
			this.ref = ref;
		}
		
		public function get(property:String):Object
		{
			return this.ref.m_props[property];
		}
	}
}