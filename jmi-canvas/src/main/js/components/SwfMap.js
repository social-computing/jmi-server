/*global define, JMI, swfobject */
JMI.namespace("components.SwfMap");

JMI.components.SwfMap = (function() {

	var SwfMap = function(parent, server, swf, jmiparams) {
		this.server = server;
		this.swf = swf;
		this.parent = parent;
		this.parent.JMI = this;
		this.size = new JMI.script.Dimension();
		this.size.width = this.parent.clientWidth;
		this.size.height = this.parent.clientHeight;
		this.eventManager = new JMI.util.EventManager();

		var params = {
		  quality: 'high',
		  wmode: 'opaque',
		  bgcolor: this.parent.style.backgroundColor,
		  allowscriptaccess: 'always',
		  allowfullscreen: 'true'
		},
		attributes = {
		  id: 'JMI_' + this.parent.id,
		  name: 'JMI_' + this.parent.id
		};
		this.parent.innerHTML = "<div id='" + attributes.id + "'>Flash player is required</div>";
		var comp = this;
		swfobject.embedSWF(this.swf, attributes.id, "100%", "100%", "10.0.0", "expressInstall.swf", 
							this.checkParams(jmiparams), params, attributes,
							function(res) {
								if( !res.success) {
									throw('Error creating JMI flash client');
								}
								comp.swfmap = res.ref; //swfobject.getObjectById(e.id);
								comp.swfmap.JMI = comp;
							});
	};
	
    SwfMap.prototype = {
        constructor: JMI.components.SwfMap,

		checkParams: function(jmiparams) {
			if (!jmiparams.hasOwnProperty('allowDomain')) {
				jmiparams.allowDomain = '*';
			}
			jmiparams.mainCallback = 'JMI.components.SwfMap.mainCallback';
			jmiparams.wpsplanname = jmiparams.map;
			jmiparams.wpsserverurl = this.server;
			return jmiparams;
		},	
		compute: function(jmiparams) {
			if( this.swfmap) {
				this.swfmap.compute(this.checkParams(jmiparams));
			}
		},
		getProperty: function(name) {
			this.swfmap.getProperty(name);
		},
		addEventListener: function(event, listener) {
			this.eventManager.addListener(event, listener);
		},
		dispatchEvent: function(event) {
			this.eventManager.fire(event);
		},
		removeEventListener: function(event, listener) {
			this.eventManager.removeListener(event, listener);
		}
	};
	
	return SwfMap;
}());

JMI.components.SwfMap.mainCallback = function(id, event) {
	var map = swfobject.getObjectById(id);
	if( map) {
		map.map = map;
		map.JMI.dispatchEvent( event);
	}
};
