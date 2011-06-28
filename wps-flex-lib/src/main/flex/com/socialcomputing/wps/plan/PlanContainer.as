package com.socialcomputing.wps.plan
{
	import com.socialcomputing.wps.script.Env;
	import com.socialcomputing.wps.script.JSONToWPSDecoder;
	import com.socialcomputing.wps.script.Plan;

	dynamic public class PlanContainer
	{
		private var _env:Env;
		private var _plan:Plan;
		
		public function PlanContainer() {
		}

		public function get plan():Plan
		{
			return _plan;
		}
		
		public function get env():Env
		{
			return _env;
		}
		
		public static function fromJSON(jsonObject:Object): PlanContainer {
			var planContainer:PlanContainer = new PlanContainer();
			if(jsonObject == null) {
				planContainer[ "error"]   = "the json object can't be null";
			}
			else {
				for( var p:String in jsonObject) {
					if( p == "map") {
						planContainer._env = JSONToWPSDecoder.toEnv(jsonObject.map.env);
						planContainer._plan = JSONToWPSDecoder.toPlan(jsonObject.map.plan);
					}
					else {
						planContainer[ p] = jsonObject[p];
					}
				}
			}
			return planContainer;
		}

	}
}