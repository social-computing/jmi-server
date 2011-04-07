<?xml version="1.0" encoding="UTF-8"?>
<%@page import="java.util.Hashtable"%>
<%@page import="com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper.Type"%>
<%@page import="com.socialcomputing.wps.server.plandictionary.connectors.utils.UrlHelper"%>
<%@page import="com.socialcomputing.wps.server.plandictionary.connectors.utils.OAuthHelper"%>
<html>
	<head>
<%
String oauth_token = request.getParameter("oauth_token");
String oauth_token_secret = request.getParameter("oauth_token_secret");
String oauth_verifier = request.getParameter("oauth_verifier");
String consumer = "7v0Vnjoe1yWD7H40yXp2NA";
String secret = "sWg9k8F8AFLcKPJ70O76aw7hGj8zmpLVcDz4LD0m4";
String callback = "http://denis.social-computing.org:8080/wps/social/twitter.jsp";

if( oauth_token == null) {
    OAuthHelper oAuth = new OAuthHelper();
    oAuth.addSignatureParam( "oauth_callback", callback);
    oAuth.addSignatureParam( "oauth_consumer_key", consumer);
    oAuth.addSignatureParam( "oauth_nonce", oAuth.getNonce());
    oAuth.addSignatureParam( "oauth_signature_method", "HMAC-SHA1");
    oAuth.addSignatureParam( "oauth_timestamp", String.valueOf( System.currentTimeMillis()/1000));
    oAuth.addSignatureParam( "oauth_version", "1.0");
    String signature = oAuth.getSignature( "https://api.twitter.com/oauth/request_token", "POST");
    String oAuthSignature = oAuth.getOAuthSignature( signature, secret);
    
    UrlHelper uh = new UrlHelper();
    uh.setUrl( "https://api.twitter.com/oauth/request_token");
    uh.setType( Type.POST);
    String header = oAuth.getAuthHeader( oAuthSignature);
    uh.addHeader( "Authorization", header);
    uh.openConnections( 0, new Hashtable<String, Object>());
    for( String p : uh.getResult().split("&")) {
        if (p.startsWith( "oauth_token=")) {
            oauth_token = p.substring( p.indexOf( '=') + 1);
        } else if (p.startsWith( "oauth_token_secret=")) {
            oauth_token_secret = p.substring( p.indexOf( '=') + 1);
        }
    }
    %>
	    <meta http-equiv="refresh" content="0; url=http://api.twitter.com/oauth/authorize?oauth_token=<%=oauth_token %>" />
		<title>Redirection</title>
		<meta name="robots" content="noindex,follow" />
	</head>
	<body>
	<p><a href="http://api.twitter.com/oauth/authorize?oauth_token=<%=oauth_token %>">Redirection</a></p>
<%
} else {
%>
    <script type="text/javascript" src="../applet/jquery.js" ></script>
    <script type="text/javascript" src="../applet/jquery.wpsmap.js" ></script>
    <script type="text/javascript" >
    	function setMap(params) {
    		params['planName'] = 'Twitter_sample';
    		params['oauth_consumer_key'] = '<%=consumer%>';
    		params['oauth_token'] = '<%=oauth_token%>';
    		params['oauth_verifier'] = '<%=oauth_verifier%>';
    		params['oauth_token_secret'] = '<%=oauth_token%>';
    		params['oauth_consumer_secret'] = '<%=secret%>';

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
