JMI.namespace("components.MapRequester");

JMI.components.MapRequester = (function() {
	
	var jmiServerUrl;
	
	var MapRequester = function( jmiServerUrl) {
		this.jmiServerUrl = jmiServerUrl || 'http://server.just-map-it.com/';
	};
	
	MapRequester.prototype = {
		constructor: JMI.components.MapRequester,
		
		// TODO gérer les erreurs
		getMap: function(name, width, height, parameters, onReady) {
			document.body.style.cursor = 'wait';
			var client = new XMLHttpRequest(); 
			client.onreadystatechange = function() {
				if( this.readyState == 4) {
					document.body.style.cursor = 'default';
					onReady( client.responseText);
				}
			}; 
			var url = this.jmiServerUrl;
			if( url.charAt(url.length - 1) != '/')
				url += '/';
			url += 'services/engine/0/' + name + '.json?';
			url = this.addParameter( url, 'width', width);
			url = this.addParameter( url, 'height', height);
			for( var p in parameters) {
				url = this.addParameter( url, p, parameters[p]);
			}
			client.open( "GET", "/jmi-canvas/src/main/resources/feeds.json", true); 
			//client.open( "GET", url, true); 
			client.send();
		},
	
		addParameter: function( url, param, value, first) {
			if( url.charAt(url.length - 1) != '?')
				url = url + '&';
			url = url + param + '=' + encodeURIComponent( value);
			return url;
		}
	};
	
	return MapRequester;
}());
