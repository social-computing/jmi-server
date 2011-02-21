jQuery.fn.wpsmap = function( options) {
	var container = this;
	
	var displayOptions = jQuery.extend({
		compute: 'Map computing...',
		load: 'Map loading...',
		color: 'FFFFFF'
	}, options['display']);

	var pluginOptions = jQuery.extend({
		version: '1.0-SNAPSHOT',
		codebase: './applet/',
		wpsurl: '../maker'
	}, options['plugin']);

	var handlerOptions = jQuery.extend({
	}, options['handler']);
	
	// this.css("background-color")
	wpsparams = jQuery.param( options['wps']);
	this.html( 
		'<APPLET name="WPSApplet" archive="WPSApplet' + pluginOptions.version + '.jar" code="com.socialcomputing.wps.client.applet.WPSApplet.class" codebase="'+ pluginOptions.codebase + '" MAYSCRIPT="mayscript" align="middle" hspace="0" vspace="0" width="100%" height="100%">'
		+  '<PARAM NAME="WPSParameters"		VALUE="' + wpsparams + '" />'
		+  '<PARAM NAME="ServletUrl"		VALUE="' + pluginOptions.wpsurl + '" />'
		+  '<PARAM NAME="VoidPlanUrl"    	VALUE="../sample-applet.jsp?error=nodata&' + wpsparams + '" />'
		+  '<PARAM NAME="NoScriptUrl"     	VALUE="../noscript.jsp" />'
		+  '<PARAM NAME="ErrorPlanUrl"    	VALUE="../sample-applet.jsp?error=internal&' + wpsparams + '" />'
		+  '<PARAM NAME="ComputeMsg"      	VALUE="' + displayOptions.compute + '" />'
		+  '<PARAM NAME="DownloadMsg"    	VALUE="' + displayOptions.load + '" />'
		+  '<PARAM NAME="InitColor"			VALUE="' + displayOptions.color + '" />'
		+  '<PARAM NAME="OnAppletReadyFunc" VALUE="javascript:_toolsFrame:onAppletReady()" />'
		//+  '<PARAM NAME="HTTPHeaderName0" 	VALUE="COOKIE" />'
		//+  '<PARAM NAME="HTTPHeaderSetValue0" VALUE="JSESSIONID=<%=session.getId()%>" />'
		+  '<p align="center"><span class="texblanc"><br><br>'
		+  'Votre navigateur ne permet pas l\'affichage d\'applets java.<br><br>'
		+  'Télécharger Java Software ici : <a href="http://www.java.com"  target="_blank">http://www.java.com</a>.<br><br>'
		+  '</span></p>'
		+  '</APPLET>'
	);
   return this;
 };
