/*global define, JMI */
JMI.namespace("components.MapRequester");

JMI.components.MapRequester = (function() {
	
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
			
			var client = new XMLHttpRequest(),
				requester = this,
				p, url;
			document.body.style.cursor = 'wait';
			client.onreadystatechange = function() {
				if( this.readyState === 4) {
					document.body.style.cursor = 'default';
					if( this.status === 200) {
						requester.map.setData( client.responseText);
					}
					else { 
						setTimeout( function() {
							requester.map.dispatchEvent({map: requester.map, type: JMI.Map.event.ERROR, message: 'Error ' + client.status + ': ' + client.statusText + '\n' + requester.jmiServerUrl + '...'});
						},100);
					}
				}
			}; 
			url = this.jmiServerUrl;
			if( url.charAt(url.length - 1) !== '/') {
				url += '/';
			}
			url += 'services/engine/0/' + name + '.json?';
			url = this.addParameter( url, 'width', this.map.size.width);
			url = this.addParameter( url, 'height', this.map.size.height);
			for( p in parameters) {
				url = this.addParameter( url, p, parameters[p]);
			}
			try {
				//client.open( "GET", "/jmi-canvas/src/main/resources/feeds2.json", true); 
				client.open( "GET", url, true); 
				client.send();
			}
			catch(err) {
				document.body.style.cursor = 'default';
				setTimeout( function() {
					requester.map.dispatchEvent({map: this.map, type: JMI.Map.event.ERROR, message: err + 'Check browser security parameters: allow access data sources across domains.'});
				},100);
			}
  		},
	
		addParameter: function( url, param, value, first) {
			if( url.charAt(url.length - 1) !== '?') {
				url = url + '&';
			}
			url = url + param + '=' + encodeURIComponent( value);
			return url;
		}
	};
	
	return MapRequester;
}());
