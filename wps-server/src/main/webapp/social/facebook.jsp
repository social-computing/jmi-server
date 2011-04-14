<?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
<%
String code = request.getParameter("code");
if( code == null) { %>
<meta http-equiv="refresh" content="0; url=https://www.facebook.com/dialog/oauth?client_id=108710779211353&redirect_uri=http://denis.social-computing.org:8080/wps/social/facebook.jsp" />
<title>Redirection</title>
<meta name="robots" content="noindex,follow" />
</head>
<body>
<p><a href="https://www.facebook.com/dialog/oauth?client_id=108710779211353&redirect_uri=http://denis.social-computing.org:8080/wps/social/facebook.jsp">Redirection</a></p>
</body>
</html>
<%} else {%>
<script type="text/javascript" src="../applet/jquery.js" ></script>
<script type="text/javascript" src="../applet/jquery.wpsmap.js" ></script>
<script type="text/javascript" >
	function setMap(params) {
		params['planName'] = 'Facebook_sample';
		params['fb-auth-code'] = '<%=code%>';

		$("#map").wpsmap({
			wps: params, 
			display: {color:'336699'},
			plugin: {codebase: '../applet/', wpsurl: './../services/', noscript:'../../noscript.jsp'}
		});
	}
	// Fired from applet
	function NewWin( id, name) {
		setMap( {entityId:id});
		$('#titre').html( name);
	}
	// Fired from applet
	function Discover( id, name) {
		setMap( {analysisProfile:'DiscoveryProfile',attributeId:id});
		$('#titre').html( name);
	}
</SCRIPT>
</HEAD>
<BODY bgcolor=ffffff topmargin=0 leftmargin=0 marginheight=0 marginwidth=0>
	<script type="text/javascript"> 
		$(document).ready(function(){
			$("#map").bind('ready', function(e) {
				onMapReady();
				});
			$("#map").bind('void', function(e) {
				$("#map").html("<h1>Void map</h1>");
				});
			$("#map").bind('error', function(e, context) {
				error = "<h1>Error !</h1><br/>";
				jQuery.each( context, function(name, value) {
						error = error + name + ": " + value + "<br/>"
					});
				$("#map").html( error);
				});
			setMap( {analysisProfile:'GlobalProfile'});
			$('#titre').html( "All");
		});
	</script>
	<div id="map" style="width:100%;height:100%" ></div>
</BODY>
<%} %>
</HTML>
