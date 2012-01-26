JMI.namespace("script.PlanContainer");

JMI.script.PlanContainer = (function() {
    
    var PlanContainer = function() {
	};
	
	PlanContainer.prototype = {
		constructor: JMI.script.PlanContainer
	};
	
	return PlanContainer;
}());


JMI.script.PlanContainer.fromJSON = function ( jsonString) {
	var planContainer = new JMI.script.PlanContainer();
	if(jsonString == null) {
		planContainer.error = "the json data can't be null";
	}
	else {
		var jmiData = JMI.lib.jsonParse( jsonString, function (key, value) {
	        if (value && typeof value === 'object') {
	        	if( key === 'containers') {
	        		var i = 0;
	        		i++;
	        	}
	        	if( 'env' === key && value.hasOwnProperty('selections')) {
	        		var env = new JMI.script.Env();
	        		for (var attr in value) {
	        			env[attr] = value[attr];
    				}
    				return env;
	        	}
	        	else if( 'plan' === key && value.hasOwnProperty('nodes')) {
	        		var plan = new JMI.script.Plan();
	        		for (var attr in value) {
	        			plan[attr] = value[attr];
    				}
    				// Résolution des références
					var n = plan.nodes.length; 
					for( var i = 0; i < n; ++i) {
						// BagZone : append ActiveZone
						for (var j = 0; j < plan.nodes[i].subZones.length; ++j) { 
							plan.nodes.push( plan.nodes[i].subZones[j]);
						}			
					}
					// Résolution des références
					// Convert from et to index to Bagzone reference
					for (var i = 0; i < plan.links.length; ++i) {
						var link = plan.links[i];
						if( link.from != -1) {
							link.from = plan.nodes[ link.from];
							link.props["_VERTICES"][0] = link.from.props["_VERTICES"][0];
						}
						if( link.to != -1) {
							link.to = plan.nodes[ link.to];
							link.props["_VERTICES"][1] = link.to.props["_VERTICES"][0];
						}
					} 
    				return plan;
	        	}
	        	else if( 'Transfo' === value.cls) {
	        		return new JMI.script.Transfo( value.dir, value.pos, value.scl, value.flags);
	        	}
	        	else if( 'Slice' === value.cls) {
	        		var slice = new JMI.script.Slice();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				slice[attr] = value[attr];
    				}
    				return slice;
	        	}
	        	else if( 'Satellite' === value.cls) {
	        		var slice = new JMI.script.Satellite();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				slice[attr] = value[attr];
    				}
    				return slice;
	        	}
	        	else if( 'Point' === value.cls) {
	        		return new JMI.script.Point( value.x, value.y);
	        	}
	        	else if( 'ColorX' === value.cls) {
	        		return new JMI.script.ColorX( value.color);
	        	}
	        	else if( 'VContainer' === value.cls) {
	        		return new JMI.script.VContainer( value.value, value.bound);
	        	}
	        	else if( 'ActiveZone' === value.cls) {
	        		var shape = new JMI.script.ActiveZone();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				shape[attr] = value[attr];
    				}
    				return shape;
    			}
	        	else if( 'BagZone' === value.cls) {
	        		var shape = new JMI.script.BagZone();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				shape[attr] = value[attr];
    				}
    				return shape;
    			}
	        	else if( 'LinkZone' === value.cls) {
	        		var shape = new JMI.script.LinkZone();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				shape[attr] = value[attr];
    				}
    				return shape;
    			}
	        	else if( 'ShapeX' === value.cls) {
	        		var shape = new JMI.script.ShapeX();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				shape[attr] = value[attr];
    				}
    				return shape;
    			}
	        	else if( 'MenuX' === value.cls) {
	        		var shape = new JMI.script.MenuX();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				shape[attr] = value[attr];
    				}
    				return shape;
    			}
	        	else if( 'FontX' === value.cls) {
	        		var shape = new JMI.script.FontX();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				shape[attr] = value[attr];
    				}
    				return shape;
    			}
	        	else if( 'Swatch' === value.cls) {
	        		var swatch = new JMI.script.Swatch();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				swatch[attr] = value[attr];
    				}
    				return swatch;
    			}
	        	else if( 'HtmlText' === value.cls) {
	        		var swatch = new JMI.script.HTMLText();
	        		for (var attr in value) {
	        			if( attr !== 'cls')
	        				swatch[attr] = value[attr];
    				}
    				return swatch;
		       	} else {
		       		if( value.hasOwnProperty('cls'))
		       			aptana.log( key + ' unconverted = ' + value.cls);
		         	return value;
		       	}
	       	} else {
	         	return value;
	       }
		}
		);
		for (var attr in jmiData) {
			planContainer[attr] = jmiData[attr];
		}
	}
	return planContainer;
};
