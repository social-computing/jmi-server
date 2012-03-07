/*global define, JMI */
JMI.namespace("components.Link");

JMI.components.Link = ( function() {

	var Link = function(index) {
		this._index = index;
	};
	
	Link.prototype = {
		constructor : JMI.script.Link

	};

	return Link;
}());
