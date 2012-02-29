/**
 * JMI application global context
 */
var JMI = JMI || {};

/**
 * namespace creation function
 * create a JMI object to manipulate a java like namespace
 * dot is used as a object separator
 *
 * @param  ns_string the namespace identifier (ie : com.socialcomputing.AClass)
 */
JMI.namespace = function(ns_string) {

	// if no argument or an empty string is given, return the JMI root namespace
	if( typeof ns_string === "undefined" || ns_string === "") {
		return JMI;
	}

	var parts = ns_string.split('.');
	var parent = JMI;
	var i;

	// Iterating throw all namespace components and creating
	// objects when necessary
	for( i = 0; i < parts.length; i += 1) {
		if( typeof parent[parts[i]] === "undefined") {
			parent[parts[i]] = {};
		}
		parent = parent[parts[i]];
	}

	// returns the deepest lvl component (object) of the given namespace
	return parent;
};

JMI.canvas = function() {
	return window.HTMLCanvasElement;
};

JMI.Map = function(params) {
	var divParent, backgroundColor, server, touchMenuDelay;
	if(!params.parent) {
		throw 'JMI client: parent id not set';
	}
	if( typeof params.parent === "string") {
		divParent = document.getElementById(params.parent);
		if(divParent === null) {
			throw 'JMI client: unknown parent element ' + params.parent;
		}
	} else if( typeof params.parent === "object") {
		divParent = params.parent;
	} else {
		throw 'JMI client: invalid parent ' + params.parent;
	}

	backgroundColor = params.backgroundColor || divParent.style.backgroundColor;
	server = params.server || 'http://server.just-map-it.com/';
	touchMenuDelay = params.touchMenuDelay || 1000;

	// Opera doesn't fully support canvas
	if((!params.client || params.client === JMI.Map.CANVAS) && JMI.canvas() && !window.opera) {
		return new JMI.components.CanvasMap(divParent, server, touchMenuDelay, backgroundColor, params.parameters);
	}
	if(!params.client || params.client === JMI.Map.SWF) {
		if(!params.swf) {
			throw 'JMI client: swf path is not set';
		}
		return new JMI.components.SwfMap(divParent, server, params.swf, backgroundColor, params.parameters);
	}
	throw 'No JMI client supported';
};

JMI.Map.CANVAS = "canvas";
JMI.Map.SWF = "swf";

JMI.namespace("Map.event");

JMI.Map.event.EMPTY = "empty";
JMI.Map.event.READY = "ready";
JMI.Map.event.STATUS = "status";
JMI.Map.event.ERROR = "error";
JMI.Map.event.ATTRIBUTE_CLICK = "attribute_click";
JMI.Map.event.ATTRIBUTE_DBLECLICK = "attribute_dblclick";
JMI.Map.event.ATTRIBUTE_HOVER = "attribute_hover";
JMI.Map.event.ACTION = "action";
JMI.Map.event.NAVIGATE = "navigate";
// Not yest implemented
//JMI.Map.event.LINK_CLICK = "link_click";
//JMI.Map.event.LINK_DBLECLICK = "link_dblclick";
//JMI.Map.event.LINK_HOVER = "link_hover";