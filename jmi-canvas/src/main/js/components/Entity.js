/*global define, JMI */
JMI.namespace("components.Entity");

JMI.components.Entity = ( function() {

	var Entity = function(index) {
		this._index = index;
	};

	Entity.prototype = {
		constructor : JMI.components.Entity

	};

	return Entity;
}());
