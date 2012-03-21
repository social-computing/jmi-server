/*global define, JMI */
JMI.namespace("components.Attribute");

JMI.components.Attribute = ( function() {

	var Attribute = function(index) {
		this._index = index;
	};

	Attribute.prototype = {
		constructor : JMI.script.Attribute
		
	};

	return Attribute;
}());
