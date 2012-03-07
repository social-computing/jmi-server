/*global define, JMI */
JMI.namespace("components.Node");

JMI.components.Node = ( function() {

	var Node = function(index) {
		this._index = index;
		this.attributes = [];
	};
	
	Node.prototype = {
		constructor : JMI.script.Node

	};

	return Node;
}());
