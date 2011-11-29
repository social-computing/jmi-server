<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="com.socialcomputing.facebook.FacebookRestProvider"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<head>
<title>Just Map It! Facebook</title>
<%String code = request.getParameter("code");
String oauth_token = FacebookRestProvider.GetProperty( request.getParameter("signed_request"), "oauth_token");
String user_id = FacebookRestProvider.GetProperty( request.getParameter("signed_request"), "user_id");
if( code == null && oauth_token == null) { %>
<meta name="robots" content="noindex,follow" />
<!--meta http-equiv="refresh" content="0; url=https://www.facebook.com/dialog/oauth?client_id=108710779211353&redirect_uri=http://wps.wps.cloudbees.net/facebook/index.jsp&scope=friends_likes,friends_groups,friends_activities,friends_events,publish_stream,user_photos" /-->
<meta http-equiv="refresh" content="0; url=https://www.facebook.com/dialog/oauth?client_id=<%=FacebookRestProvider.CLIENT_ID%>&redirect_uri=http://apps.facebook.com/just-map-it/&scope=friends_likes,friends_groups,friends_activities,friends_events,publish_stream,user_photos" />
</head>
<body>
<!--script> top.location.href='https://www.facebook.com/dialog/oauth?client_id=108710779211353&redirect_uri=http://wps.wps.cloudbees.net/facebook/index.jsp&scope=friends_likes,friends_groups,friends_activities,friends_events,publish_stream,user_photos'</script-->
<!-- script> top.location.href='https://www.facebook.com/dialog/oauth?client_id=108710779211353&redirect_uri=http://apps.facebook.com/social-computing/&scope=friends_likes,friends_groups,friends_activities,friends_events,publish_stream,user_photos'</script-->
</body>
</html>
<%} else {%>
      <meta name="google" value="notranslate">         
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!-- Include CSS to eliminate any default margins/padding and set the height of the html element and 
     the body element to 100%, because Firefox, or any Gecko based browser, interprets percentage as 
	 the percentage of the height of its parent container, which has to be set explicitly.  Fix for
	 Firefox 3.6 focus border issues.  Initially, don't display flashContent div so it won't show 
	 if JavaScript disabled.
-->
      <style type="text/css" media="screen"> 
	html, body	{ height:100%; }
	body { margin:0; padding:0; overflow:auto; text-align:center; 
	       background-color: #FFFFFF; }   
	object:focus { outline:none; }
	#flashContent { display:none; }
      </style>

<!-- Enable Browser History by replacing useBrowserHistory tokens with two hyphens -->
      <!-- BEGIN Browser History required section -->
      <link rel="stylesheet" type="text/css" href="./client/history/history.css" />
      <script type="text/javascript" src="./client/history/history.js"></script>
      <!-- END Browser History required section -->  

      <script type="text/javascript">
       	function navigate( url, target) {
      		window.open( url, target);
      	}
       </script>
    
      <script type="text/javascript" src="./client/swfobject.js"></script>
      <script type="text/javascript">
          var swfVersionStr = "10.0.0";
          var xiSwfUrlStr = "./client/playerProductInstall.swf";
          var flashvars = {};
          //flashvars.wpsserverurl = "http://localhost:8080/wps-server";
          flashvars.wpsserverurl = "http://server.just-map-it.com";
          flashvars.wpsplanname = "Facebook";
          //flashvars.fbserverurl = "http://localhost:8080/web-facebook";
          flashvars.fbserverurl = "http://facebook.just-map-it.com";
          flashvars.jsessionid = '<%=request.getRequestedSessionId()%>';
          flashvars.access_token = '<%=FacebookRestProvider.GetAccessToken(code)%>';
          flashvars.fbuserid = '<%=user_id%>';
          flashvars.analysisProfile = "GlobalProfile";
          flashvars.kind = "likes";
          var params = {};
          params.quality = "high";
          params.bgcolor = "#FFFFFF";
          params.allowscriptaccess = "sameDomain";
          params.allowfullscreen = "true";
          params.wmode = "opaque";
          var attributes = {};
          attributes.id = "wps-facebook";
          attributes.name = "wps-facebook";
          attributes.align = "middle";
          swfobject.embedSWF(
              "./client/wps-facebook-1.0-SNAPSHOT.swf", "flashContent", 
              "100%", "100%", 
              swfVersionStr, xiSwfUrlStr, 
              flashvars, params, attributes);
		  swfobject.createCSS("#flashContent", "display:block;text-align:left;");
      </script>
  </head>
  <body>
<div id="content">
      <div id="flashContent">
      	<p>
       	To view this page ensure that Adobe Flash Player version 
		10.0.0 or greater is installed. 
	</p>
	<script type="text/javascript"> 
		var pageHost = ((document.location.protocol == "https:") ? "https://" :	"http://"); 
		document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
						+ pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
	</script> 
      </div>
  	
     	<noscript>
          <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100%" id="wps-facebook">
              <param name="movie" value="./client/wps-facebook-1.0-SNAPSHOT.swf" /> 
              <param name="quality" value="high" />
              <param name="bgcolor" value="#FFFFFF" />
              <param name="allowScriptAccess" value="sameDomain" />
              <param name="allowFullScreen" value="true" />
              <!--[if !IE]>-->
              <object type="application/x-shockwave-flash" data="./client/wps-facebook-1.0-SNAPSHOT.swf" width="100%" height="100%">
                  <param name="quality" value="high" />
                  <param name="bgcolor" value="#FFFFFF" />
                  <param name="allowScriptAccess" value="sameDomain" />
                  <param name="allowFullScreen" value="true" />
              <!--<![endif]-->
              <!--[if gte IE 6]>-->
              	<p> 
              		Either scripts and active content are not permitted to run or Adobe Flash Player version
              		10.0.0 or greater is not installed.
              	</p>
              <!--<![endif]-->
                  <a href="http://www.adobe.com/go/getflashplayer">
                      <img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash Player" />
                  </a>
              <!--[if !IE]>-->
              </object>
              <!--<![endif]-->
          </object>
   </noscript>		
   </div>
 </body>
</html>
<%} %>
