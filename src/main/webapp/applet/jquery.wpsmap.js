function onWpsReady( id) {
	$( '#' + id).trigger( jQuery.Event("ready"));
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
			codebase: './applet/',
			wpsurl: '../maker'
		}, options['plugin']);
		var handlerOptions = $.extend({
		}, options['handler']);
		
		// this.css("background-color")
        return this.each(function() {
			wpsparams = jQuery.param( options['wps']);
			html = '<APPLET name="' + pluginOptions.name + '" archive="WPSApplet' + pluginOptions.version + '.jar" code="com.socialcomputing.wps.client.applet.WPSApplet.class" codebase="'+ pluginOptions.codebase + '" MAYSCRIPT="mayscript" align="middle" hspace="0" vspace="0" width="100%" height="100%">'
				+  '<PARAM NAME="WPSParameters"		VALUE="' + wpsparams + '" />'
				+  '<PARAM NAME="ServletUrl"		VALUE="' + pluginOptions.wpsurl + '" />'
				+  '<PARAM NAME="ComputeMsg"      	VALUE="' + displayOptions.compute + '" />'
				+  '<PARAM NAME="DownloadMsg"    	VALUE="' + displayOptions.load + '" />'
				+  '<PARAM NAME="InitColor"			VALUE="' + displayOptions.color + '" />';
			if(handlerOptions.empty)
				html = html + '<PARAM NAME="VoidPlanUrl"    	VALUE="' + handlerOptions.empty + wpsparams + '" />';
			if(handlerOptions.noscript)
				html = html + '<PARAM NAME="NoScriptUrl"     	VALUE="' + handlerOptions.noscript + '/>';
			if(handlerOptions.error)
				html = html + '<PARAM NAME="ErrorPlanUrl"    	VALUE="' + handlerOptions.error + wpsparams + '" />';
			html = html + '<PARAM NAME="OnAppletReadyFunc"  VALUE="javascript:onWpsReady(' + this.id + ')" />';
			if(pluginOptions.wakeupurl)
				html = html + '<PARAM NAME="WakeUpURL"  VALUE="' + handler.wakeupurl + '" />';
			if(pluginOptions.wakeupdelay)
				html = html + '<PARAM NAME="WakeUpDelay"  VALUE="' + handler.wakeupdelay + '" />';
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



