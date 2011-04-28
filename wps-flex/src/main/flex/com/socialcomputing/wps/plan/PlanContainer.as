package com.socialcomputing.wps.plan
{
	public class PlanContainer
	{
		private var _duration:int;
		private var _map:Object;
		private var _name:String;
		private var _type:String;
		private var _mime:String;
		
		public function PlanContainer(duration:int,
									  map:Object,
									  name:String,
		                              type:String,
									  mime:String){
			this._duration = duration;
			this._map = map;
			this._name = name;
			this._type = type;
			this._mime = mime;
		}

		public function get duration():int
		{
			return _duration;
		}

		public function get map():Object
		{
			return _map;
		}

		public function get name():String
		{
			return _name;
		}

		public function get type():String
		{
			return _type;
		}

		public function get mime():String
		{
			return _mime;
		}

		public static function fromJSON(jsonObject:Object): PlanContainer {
			if(jsonObject == null) {
				throw new ArgumentError("the json object can't be null");
			}
			
			// TODO : Add checks here
			// The given object should have all the necessary properties			
			
			return new PlanContainer(
				jsonObject.duration,
				jsonObject.map,
				jsonObject.name,
				jsonObject.type,
				jsonObject.mime);
		}
	}
}