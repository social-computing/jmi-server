/*global define, JMI */
JMI.namespace("script.PlanContainer");

JMI.script.PlanContainer = ( function() {

	var PlanContainer = function() {
	};

	PlanContainer.prototype = {
		constructor : JMI.script.PlanContainer
	};

	return PlanContainer;
}());

JMI.script.PlanContainer.fromJSON = function(jsonString) {
	var planContainer = new JMI.script.PlanContainer(),
		attr, jmiData;
	if(jsonString === null) {
		planContainer.error = "the json data can't be null";
	} else {
		jmiData = JMI.lib.jsonParse(jsonString, function(key, value) {
			var attr, result, i, env, plan, n, j, link;
			if(value && typeof value === 'object') {
				if('env' === key && value.hasOwnProperty('selections')) {
					env = new JMI.script.Env();
					for(attr in value) {
						env[attr] = value[attr];
					}
					return env;
				} else if('plan' === key && value.hasOwnProperty('nodes')) {
					plan = new JMI.script.Plan();
					for(attr in value) {
						plan[attr] = value[attr];
					}
					// Résolution des références
					n = plan.nodes.length;
					for( i = 0; i < n; ++i) {
						// BagZone : append ActiveZone
						for(j = 0; j < plan.nodes[i].subZones.length; ++j) {
							plan.nodes.push(plan.nodes[i].subZones[j]);
						}
					}
					// Résolution des références
					// Convert from et to index to Bagzone reference
					for( i = 0; i < plan.links.length; ++i) {
						link = plan.links[i];
						if(link.from !== -1) {
							link.from = plan.nodes[link.from];
							link.props._VERTICES[0] = link.from.props._VERTICES[0];
						}
						if(link.to !== -1) {
							link.to = plan.nodes[link.to];
							link.props._VERTICES[1] = link.to.props._VERTICES[0];
						}
					}
					return plan;
				} else if('Transfo' === value.cls) {
					return new JMI.script.Transfo(value.dir, value.pos, value.scl, value.flags);
				} else if('Slice' === value.cls) {
					result = new JMI.script.Slice();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('Satellite' === value.cls) {
					result = new JMI.script.Satellite();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('Point' === value.cls) {
					return new JMI.script.Point(value.x, value.y);
				} else if('ColorX' === value.cls) {
					return new JMI.script.ColorX(value.color);
				} else if('VContainer' === value.cls) {
					return new JMI.script.VContainer(value.value, value.bound);
				} else if('ActiveZone' === value.cls) {
					result = new JMI.script.ActiveZone();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('BagZone' === value.cls) {
					result = new JMI.script.BagZone();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('LinkZone' === value.cls) {
					result = new JMI.script.LinkZone();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('ShapeX' === value.cls) {
					result = new JMI.script.ShapeX();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('MenuX' === value.cls) {
					result = new JMI.script.MenuX();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('FontX' === value.cls) {
					result = new JMI.script.FontX();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('Swatch' === value.cls) {
					result = new JMI.script.Swatch();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else if('HtmlText' === value.cls) {
					result = new JMI.script.HTMLText();
					for(attr in value) {
						if(attr !== 'cls') {
							result[attr] = value[attr];
						}
					}
					return result;
				} else {
					/*if(value.hasOwnProperty('cls')) {
						//aptana.log( key + ' unconverted = ' + value.cls);
					}*/
					return value;
				}
			} else {
				return value;
			}
		});
		for(attr in jmiData) {
			planContainer[attr] = jmiData[attr];
		}
	}
	return planContainer;
};
