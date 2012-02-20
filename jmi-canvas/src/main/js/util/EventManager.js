/*global define, JMI */
JMI.namespace("util.EventManager");

JMI.util.EventManager = ( function() {

	var EventManager = function() {
		this.listeners = {};
	};

	EventManager.prototype = {
		constructor : JMI.util.EventManager,

		addListener : function(type, listener) {
			if( typeof this.listeners[type] === "undefined") {
				this.listeners[type] = [];
			}
			this.listeners[type].push(listener);
		},
		fire : function(event) {
			var listeners, i, len;
			if( typeof event === "string") {
				event = {
					type : event
				};
			}
			if(!event.target) {
				event.target = this;
			}

			if(!event.type) {//falsy
				throw new Error("Event object missing 'type' property.");
			}

			if(this.listeners[event.type] instanceof Array) {
				listeners = this.listeners[event.type];
				for(i = 0, len = listeners.length; i < len; i++) {
					listeners[i].call(this, event);
				}
			}
		},
		removeListener : function(type, listener) {
			var listeners, i, len;
			if(this.listeners[type] instanceof Array) {
				listeners = this.listeners[type];
				for(i = 0, len = listeners.length; i < len; i++) {
					if(listeners[i] === listener) {
						listeners.splice(i, 1);
						break;
					}
				}
			}
		}
	};

	return EventManager;
}());
