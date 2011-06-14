package com.socialcomputing.wps.components
{
	import com.socialcomputing.wps.script.ActiveZone;

	public class Node
	{
		private var ref:ActiveZone;
		public var name:String;
		
		public function Node(ref:ActiveZone)
		{
			this.ref = ref;
			this.name = "name";
		}
		
		public function get(property:String):Object
		{
			return this.ref.m_props[property];
		}
	}
}