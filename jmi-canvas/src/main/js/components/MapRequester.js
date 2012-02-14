JMI.namespace("components.MapRequester");

JMI.components.MapRequester = (function() {
	
	var jmiServerUrl, map;
	
	var MapRequester = function( map, jmiServerUrl) {
		if( !map || !(map instanceof JMI.components.CanvasMap)) {
			throw('map component is not set');
		}
		this.map = map;
		this.jmiServerUrl = jmiServerUrl || 'http://server.just-map-it.com/';
	};
	
	MapRequester.prototype = {
		constructor: JMI.components.MapRequester,
		
		getMap: function(name, parameters) {
			document.body.style.cursor = 'wait';
			var client = new XMLHttpRequest(); 
			var requester = this;
			client.onreadystatechange = function() {
				if( this.readyState == 4) {
					document.body.style.cursor = 'default';
					if( this.status == 200) {
						requester.map.setData( client.responseText);
					}
					else { 
						requester.map.dispatchEvent({map: this.map, type: JMI.Map.event.ERROR, message: 'Error ' + client.status + ': ' + client.statusText + '\n' + requester.jmiServerUrl + '...'});
					}
				}
			}; 
			var url = this.jmiServerUrl;
			if( url.charAt(url.length - 1) != '/') {
				url += '/';
			}
			url += 'services/engine/0/' + name + '.json?';
			url = this.addParameter( url, 'width', this.map.size.width);
			url = this.addParameter( url, 'height', this.map.size.height);
			for( var p in parameters) {
				url = this.addParameter( url, p, parameters[p]);
			}
			client.open( "GET", "/jmi-canvas/src/main/resources/feeds2.json", true); 
			//client.open( "GET", url, true); 
			client.send();
		},
	
		addParameter: function( url, param, value, first) {
			if( url.charAt(url.length - 1) != '?') {
				url = url + '&';
			}
			url = url + param + '=' + encodeURIComponent( value);
			return url;
		}
	};
	
	return MapRequester;
}());
