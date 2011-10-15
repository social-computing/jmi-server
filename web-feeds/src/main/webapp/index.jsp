<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
    <head>
        <title>Map your feeds!</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta http-equiv="content-language" content="en">
		<link rel="shortcut icon" href="./favicon.ico" />
        <style type="text/css" media="screen"> 
			html, body	{ height:100%; }
			body { margin:0; padding:0; overflow:auto; height:100%; text-align:left; background-color: #FFFFFF; }   

			#header { width:100%; height:78px; }
			#map	{ width:100%; height:88%;}
			
			#bandeau {width: 100%; }
			#bandeau td {text-align:left; }
			#bandeau #logo {width:148px; }
			#bandeau .label {padding-top:20px;text-align:left;font-family:arial;color:#37b0e3;font-size:14px;}
			#bandeau .social {width:120px; align:left}
			
			#flashContent { display:none; }
			object:focus { outline:none; }
        </style>
<%String feed = request.getParameter("feed");
if (feed != null && feed.length() > 0) {%>
		<script type="text/javascript">
		  function getMap() {
			  if (navigator.appName.indexOf ("Microsoft") !=-1) {
				  return window["wps-feeds"];
			  } else {
				  return document["wps-feeds"];
			  }
		  }
      
         function empty() {
        	 alert( "Sorry, map is empty");
         }
         function ready() {
          // do something here
         }
         function status( status) {
          // do something here
         }
         function error( error) {
          	alert( error);
         }
         function Navigate( url) {
        	 window.open( url, "_blank");
         }
         // Actions that are swatch defined
		  function NewWin( args)
		  {
			var parameters = {};
			parameters["entityId"] = args[0];
			parameters["feed"] = args[2];
			getMap().compute( parameters);
		  }
		  function Discover( args)
		  {
			var parameters = {};
			parameters["attributeId"] = args[0];
			parameters["analysisProfile"] = "DiscoveryProfile";
			parameters["feed"] = args[2];
		  	getMap().compute( parameters);
		  }
        </script>
		<!-- Enable Browser History by replacing useBrowserHistory tokens with two hyphens -->
        <!-- BEGIN Browser History required section -->
        <link rel="stylesheet" type="text/css" href="./client/history/history.css" />
        <script type="text/javascript" src="./client/history/history.js"></script>
        <!-- END Browser History required section -->  
		
        <script type="text/javascript" src="./client/swfobject.js"></script>
        <script type="text/javascript">
            <!-- For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection. --> 
            var swfVersionStr = "10.0.0";
            <!-- To use express install, set to playerProductInstall.swf, otherwise the empty string. -->
            var xiSwfUrlStr = "./client/playerProductInstall.swf";
            var flashvars = {};
            flashvars.allowDomain = "*";
            //flashvars.wpsserverurl = "http://localhost:8080/wps-server";
            flashvars.wpsserverurl = "http://map.social-computing.com/";
            flashvars.wpsplanname = "Feeds";
            flashvars.analysisProfile = "GlobalProfile";
            flashvars.feed = "<%=java.net.URLEncoder.encode(feed)%>";
            var params = {};
            params.quality = "high";
            params.bgcolor = "#FFFFFF";
            params.allowscriptaccess = "always";
            params.allowfullscreen = "true";
            var attributes = {};
            attributes.id = "wps-feeds";
            attributes.name = "wps-feeds";
            attributes.align = "middle";
            swfobject.embedSWF(
                "./client/wps-flex-1.0-SNAPSHOT.swf", "flashContent", 
                "100%", "100%", 
                swfVersionStr, xiSwfUrlStr, 
                flashvars, params, attributes);
			<!-- JavaScript enabled so display the flashContent div in case it is not replaced with a swf object. -->
			swfobject.createCSS("#flashContent", "display:block;text-align:left;");
        </script>
<jsp:include page="./js/ga.js" /> 
</head>
 <%}%>   
    <body>
<div id="header">
		<table id="bandeau" >
		<tr>
			<td id="logo" rowspan="2">		
				<a href="http://www.social-computing.com/" target="_blank"><img border="0" width="144" height="70" title="Social Computing" src="./images/logo-sc-white.jpg" /></a>
			</td>
			<td class="label"><b>Enter an url:</b>
			</td>
			<td class="social"><g:plusone size="medium" href="http://www.mapyourfeeds.com/"></g:plusone></td>
			<td class="social"><a title="Post to Google Buzz" class="google-buzz-button" href="http://www.google.com/buzz/post" data-button-style="small-count" data-url="http://www.mapyourfeeds.com"></a><script type="text/javascript" src="http://www.google.com/buzz/api/button.js"></script></td>
			<td class="social"><iframe src="//www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.mapyourfeeds.com%2F&amp;send=false&amp;layout=button_count&amp;width=70&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font=arial&amp;height=21&amp;appId=205005596217672" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:150px; height:21px;" allowTransparency="true"></iframe></td>
		</tr>
		<tr>
			<td>
				<form method="get">
					<input type="text" name="feed" size="80" value="<%=feed != null ? feed : "" %>" />
					<input type="submit" value="View map" />
				</form>
			</td>
			<td>
				<a title="Add Map Your Feeds! as a Google Gadget" target="_blank" href="http://www.google.com/ig/directory?url=www.mapyourfeeds.com/google/igoogle-social-computing-feeds.xml"><img src="http://buttons.googlesyndication.com/fusion/add.gif" style="width:104px; height:17px;border:0px;" alt="Add this as an Google gadget" /></a>
			</td>
			<td class="social"><a href="https://twitter.com/share" class="twitter-share-button" data-url="http://www.mapyourfeeds.com" data-count="horizontal">Tweet</a><script type="text/javascript" src="//platform.twitter.com/widgets.js"></script></td>
			<td class="social"><script src="http://platform.linkedin.com/in.js" type="text/javascript"></script><script type="IN/Share" data-url="http://www.mapyourfeeds.com/" data-counter="right"></script></td>
		</tr>
		</table>
</div>

<div id="map" >
<%if (feed != null && feed.length() > 0) {%>
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
            <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100%" id="wps-feeds">
                <param name="movie" value="./client/wps-flex-1.0-SNAPSHOT.swf" />
                <param name="quality" value="high" />
                <param name="bgcolor" value="#FFFFFF" />
                <param name="allowScriptAccess" value="always" />
                <param name="allowFullScreen" value="true" />
                <!--[if !IE]>-->
                <object type="application/x-shockwave-flash" data="./client/wps-flex-1.0-SNAPSHOT.swf" width="100%" height="100%">
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
<%} %>
</div>

	<script type="text/javascript" src="https://apis.google.com/js/plusone.js"></script>
   </body>
</html>
