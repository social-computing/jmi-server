function onWpsReady( id) {
	$( '#' + id).trigger( jQuery.Event("ready"));
}
function onWpsVoid( id) {
	$( '#' + id).trigger( jQuery.Event("void"));
}
function onWpsError( id) {
	var e = jQuery.Event("error");
	var context = new Object();
	for( var i = 1; i < arguments.length; i++ ) {
		var pos = arguments[i].indexOf(":");
		if( pos == -1)
			context[i] = arguments[i];
		else
			context[ arguments[i].substring( 0, pos)] = arguments[i].substring( pos+1);
	}
	$( '#' + id).trigger( e, context);
}

(function($){
  $.fn.extend({ 
	wpsmap: function(options) {
		var container = this;
		var displayOptions = $.extend({
			compute: 'Map computing...',
			load: 'Map loading...',
			color: 'FFFFFF',
			alternate: '<p align="center">Votre navigateur ne permet pas l\'affichage d\'applets java.<br />'
				+  '<a href="http://www.java.com target="_blank">Télécharger Java Software</a>.</p>'
		}, options['display']);
		var pluginOptions = $.extend({
			version: '1.0-SNAPSHOT',
			name: 'WPSApplet',
			codebase: '../client/applet/',
			wpsurl: '../services/',
			wpsclient: '0'
		}, options['plugin']);
		
		// this.css("background-color")
        return this.each(function() {
			wpsparams = jQuery.param( options['wps']);
			html = '<APPLET name="' + pluginOptions.name + '" archive="WPSApplet' + pluginOptions.version + '.jar" code="com.socialcomputing.wps.client.applet.WPSApplet.class" codebase="'+ pluginOptions.codebase + '" MAYSCRIPT="mayscript" align="middle" hspace="0" vspace="0" width="100%" height="100%">'
				+  '<PARAM NAME="WPSParameters"		VALUE="' + wpsparams + '" />'
				+  '<PARAM NAME="ServletUrl"		VALUE="' + pluginOptions.wpsurl + 'engine/' + pluginOptions.wpsclient + '/' + options['wps'].planName + '.java" />'
				+  '<PARAM NAME="ComputeMsg"      	VALUE="' + displayOptions.compute + '" />'
				+  '<PARAM NAME="DownloadMsg"    	VALUE="' + displayOptions.load + '" />'
				+  '<PARAM NAME="InitColor"			VALUE="' + displayOptions.color + '" />';
			if(pluginOptions.noscript)
				html = html + '<PARAM NAME="NoScriptUrl"     	VALUE="' + pluginOptions.noscript + '/>';
			html = html + '<PARAM NAME="OnVoidMapFunc"    	VALUE="javascript:onWpsVoid(' + this.id + ')" />';
			html = html + '<PARAM NAME="OnErrorMapFunc"    	VALUE="javascript:onWpsError(' + this.id + ', {s$err-context})" />';
			html = html + '<PARAM NAME="OnMapReadyFunc"  	VALUE="javascript:onWpsReady(' + this.id + ')" />';
			if(pluginOptions.wakeupurl)
				html = html + '<PARAM NAME="WakeUpURL"  VALUE="' + pluginOptions.wakeupurl + '" />';
			if(pluginOptions.wakeupdelay)
				html = html + '<PARAM NAME="WakeUpDelay"  VALUE="' + pluginOptions.wakeupdelay + '" />';
			if(displayOptions.print)
				html = html + '<PARAM NAME="NeedPrint"  VALUE="' + displayOptions.print + '" />';
			// TODO les headers HTTP
			//+  '<PARAM NAME="HTTPHeaderName0" 	VALUE="COOKIE" />'
				//+  '<PARAM NAME="HTTPHeaderSetValue0" VALUE="JSESSIONID=<%=session.getId()%>" />'
			html = html + pluginOptions.alternate + '</APPLET>';
			container.html( html);
        });
	} 
  });
 
})( jQuery );



