/*global define, JMI */
JMI.namespace("components.Selection");

JMI.script.Selection = ( function() {

	var Selection = function(map,name,id) {
		this.map = map;
		this.name = name;
		this.id = id;
	};

	Selection.prototype = {
		constructor : JMI.script.Selection,

		show: function() {
		},
		hide: function() {
		},
		set: function(s) {
		},
		add: function(s) {
			var refs = this._refs(s), i, ref;
			for(i = 0; i < t.length; ++i) {
				//ref = refs[i];
				if( ref instanceof JMI.components.Attribute) {
					map.nodes[ref._index].selection |= this.id;
				}
			}
		},
		remove: function(s) {
		},
		clear: function(s) {
		},
		
		_refs: function(s) {
			if( !s) {
				throw('Nothing to select');
			}
			if(s instanceof JMI.components.Attribute || s instanceof JMI.components.Link) {
				return [s];
			}
			if(s instanceof Array) {
				return s;
			}
		}
	};
	

	return Selection;
}());
