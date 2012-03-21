/*global define, JMI */
JMI.namespace("components.Selection");

JMI.components.Selection = ( function() {

	var Selection = function(map,name,id) {
		this.map = map;
		this.name = name;
		this.id = 1 << id;
	};

	Selection.prototype = {
		constructor : JMI.components.Selection,

		show: function() {
			this.map.showSelection(this.name);
		},
		hide: function() {
			this.map.showSelection('');
		},
		clear: function() {
			this.map.clearSelection(this.name);
		},
		set: function(s) {
		},
		add: function(s) {
			var refs = this._refs(s), i, ref;
			this.map.addSelection(this.name,refs.attributes,refs.links);
		},
		remove: function(s) {
		},
		_refs: function(s) {
			var i, ret, attributes = [], links = [];
			if( s) {
				if(s instanceof JMI.components.Attribute) {
					attributes.push( s._index);
				}
				else if( s instanceof JMI.components.Link) {
					links.push( s._index);
				}
				else if(s instanceof Array) {
					for( i = 0 ; i < s.length; ++i) {
						ret = this._refs( s[i]);
						if( ret.attributes.length > 0) {
							attributes.push( ret.attributes);
						}
						if( ret.links.length > 0) {
							links.push( ret.links);
						}
					}
				}
			}
			return { 'attributes': attributes, 'links': links};
		}
	};
	

	return Selection;
}());
