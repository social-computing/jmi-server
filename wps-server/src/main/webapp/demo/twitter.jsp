<?xml version="1.0" encoding="UTF-8"?>
<%@page import="java.util.Hashtable"%>
<%@page import="com.socialcomputing.wps.server.plandictionary.connectors.datastore.social.portablecontacts.TwitterEntityConnector"%>
<html>
	<head>
<%
String oauth_token = request.getParameter("oauth_token");
String oauth_token_secret = request.getParameter("oauth_token_secret");
String oauth_verifier = request.getParameter("oauth_verifier");
String consumer = "CEtRSZrHKpPdXmaCbVF8jQ";
String secret = "KaGfhstQYiDFYqYDHfIJHxJdg7sHFfrqoAE7zevhk";
String callback = "http://denis.social-computing.org:8080/wps/social/twitter.jsp";

if( oauth_token == null) {
    String result = TwitterEntityConnector.GetTwitterRequestToken( consumer, secret, callback);
    for( String p : result.split("&")) {
        if (p.startsWith( "oauth_token=")) {
            oauth_token = p.substring( p.indexOf( '=') + 1);
        } else if (p.startsWith( "oauth_token_secret=")) {
            oauth_token_secret = p.substring( p.indexOf( '=') + 1);
            session.setAttribute( "oauth_token_secret", oauth_token_secret);
        }
    }
    %>
	    <meta http-equiv="refresh" content="0; url=http://api.twitter.com/oauth/authorize?oauth_token=<%=oauth_token %>&oauth_token_secret=<%=oauth_token_secret%>" />
		<title>Redirection</title>
		<meta name="robots" content="noindex,follow" />
	</head>
	<body>
	<p><a href="http://api.twitter.com/oauth/authorize?oauth_token=<%=oauth_token %>&oauth_token_secret=<%=oauth_token_secret%>">Redirection</a></p>
<%
} else {
%>
    <script type="text/javascript" src="../applet/jquery.js" ></script>
    <script type="text/javascript" src="../applet/jquery.wpsmap.js" ></script>
    <script type="text/javascript" >
    	function setMap(params) {
    		params['planName'] = 'Twitter_sample';
    		params['oauth_token'] = '<%=oauth_token%>';
    		params['oauth_verifier'] = '<%=oauth_verifier%>';
    		params['oauth_token_secret'] = '<%= session.getAttribute( "oauth_token_secret")%>';

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
    </script>
    </head>
    <body bgcolor=ffffff topmargin=0 leftmargin=0 marginheight=0 marginwidth=0>
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

    <%} %>
    </body>
</html>
