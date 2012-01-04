package com.socialcomputing.jmi.plan
{
	import com.socialcomputing.jmi.script.JSONToWPSDecoder;

	dynamic public class PlanContainer
	{
		public function PlanContainer() {
		}

		public static function fromJSON(jsonObject:Object): PlanContainer {
			var planContainer:PlanContainer = new PlanContainer();
			if(jsonObject == null) {
				planContainer[ "error"]   = "the json object can't be null";
			}
			else {
				for( var p:String in jsonObject) {
					if( p == "map") {
						if( jsonObject.map.hasOwnProperty("env"))
							planContainer.env = JSONToWPSDecoder.toEnv(jsonObject.map.env);
						if( jsonObject.map.hasOwnProperty("plan"))
							planContainer.plan = JSONToWPSDecoder.toPlan(jsonObject.map.plan);
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