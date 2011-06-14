package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.script.BagZone;

	public class Node
	{
		private var ref:BagZone;
		
		public function Node(ref:BagZone)
		{
			this.ref = ref;
		}
		
		public function get(property:String):Object
		{
			return this.ref.m_props[property];
		}
	}
}