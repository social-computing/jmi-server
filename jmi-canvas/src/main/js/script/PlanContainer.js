JMI.namespace("script.PlanContainer");

JMI.script.PlanContainer = (function() {
    
    var PlanContainer = function() {
	};
	
	PlanContainer.prototype = {
		constructor: JMI.script.PlanContainer,
	};
	
	return PlanContainer;
}());


script.PlanContainer.fromJSON = function ( jsonString) {
	var planContainer = new JMI.script.PlanContainer();
	if(jsonString == null) {
		planContainer.error = "the json data can't be null";
	}
	else {
		// TODO portage coming soon
		/*
		for( var p in jsonString) {
			if( p == "map") {
				if( jsonObject.map.hasOwnProperty("env"))
					planContainer.env = JSONToWPSDecoder.toEnv(jsonObject.map.env);
				if( jsonObject.map.hasOwnProperty("plan"))
					planContainer.plan = JSONToWPSDecoder.toPlan(jsonObject.map.plan);
			}
			else {
				planContainer[ p] = jsonObject[p];
			}
		}*/
	}
	return planContainer;
}
