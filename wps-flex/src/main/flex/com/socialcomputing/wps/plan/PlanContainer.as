package com.socialcomputing.wps.plan
{
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.JSONToWPSDecoder;
	import com.socialcomputing.wps.script.Plan;

	public class PlanContainer
	{
		private var _duration:int;
		private var _env:Env;
		private var _plan:Plan;
		private var _name:String;
		private var _type:String;
		private var _mime:String;
		
		public function PlanContainer(duration:int,
									  env:Env,
									  plan:Plan,
									  name:String,
		                              type:String,
									  mime:String){
			this._duration = duration;
			this._env = env;
			this._plan = plan;
			this._name = name;
			this._type = type;
			this._mime = mime;
		}

		public function get duration():int
		{
			return _duration;
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
				JSONToWPSDecoder.toEnv(jsonObject.map.env),
				JSONToWPSDecoder.toPlan(jsonObject.map.plan),
				jsonObject.name,
				jsonObject.type,
				jsonObject.mime);
		}
	}
}