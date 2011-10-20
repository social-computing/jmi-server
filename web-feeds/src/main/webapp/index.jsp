<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<%String feed = request.getParameter("feed");
if( feed == null) feed = "";%>
<head>
    <title>Map your feeds!</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="content-language" content="en" />
	<meta name="description" content="View and navigate your feeds thru an interactive map! by Social Computing" />
	<meta name="keywords" content="rss, feeds, feed, map, cartography, visualization, social, blog, gadget, widget, social computing, category, representation, information" />
	<meta name="author" content="Social Computing" /> 
	<meta name="robots" content="all" /> 
	<meta property="og:title" content="Map your feeds!" />
	<meta property="og:description" content="View and navigate your feeds thru an interactive map! by Social Computing" />
	<meta property="og:image" content="http://www.mapyourfeeds.com/images/thumbnail.png" />
	<link rel="shortcut icon" href="./favicon.ico" />
    <style type="text/css" media="screen"> 
		html, body	{ height:100%; }
		body { margin:0; padding:0; overflow:auto; height:100%; text-align:left; background-color: #FFFFFF; }   

		#header { width:100%; height:78px; }
		#map	{ width:100%; height:88%;}
		
		#bandeau {width: 100%; }
		#bandeau td {text-align:left; }
		#bandeau #logo {width:155px; }
		#bandeau .label {margin:0px;text-align:left;font-family:arial;color:#37b0e3;font-size:14px;}
		#bandeau .social {width:120px; align:left}
		#bandeau #message {width:180px;padding-left:4px;margin:0;text-align:left;font-family:arial;color:#c3372f;font-size:14px;font-weight:bold;}
		#bandeau #doc a {width:180px;padding-left:4px;margin:0;text-align:left;font-family:arial;color:#ffffff;font-size:12px;font-weight:bold;}
		#bandeau .hidden-message {text-align:left;font-family:arial;color:#ffffff;font-size:12px;}

		.slogan {padding-top:20px; padding-left:154px;text-align:left;font-family:arial;color:#37b0e3;font-size:18px;}
		
		#flashContent { display:none; }
		object:focus { outline:none; }
    </style>
<script type="text/javascript" src="./js/jquery-1.6.4.min.js"></script>
<%if( feed.length() > 0) {%>
<script type="text/javascript">
  function ready() {
	  var map = $("#wps-feeds")[0];
	  var urls = map.getArrayProperty( "$FEEDS_URLS").split( String.fromCharCode(0xFFFC));
	  var titles = map.getArrayProperty( "$FEEDS_TITLES").split( String.fromCharCode(0xFFFC));
	  var counts = map.getArrayProperty( "$FEEDS_COUNTS").split( String.fromCharCode(0xFFFC));
	  document.title = document.title + ' - ' + titles.join( ', ');
	  if( map.getProperty( "$analysisProfile") == "GlobalProfile") {
		  for( var i=0; i < titles.length; ++i) {
			  var params = { url:urls[i], title:titles[i], count:counts[i] };
			  $.ajax({
				  url: "./services/feeds/record.json",
				  data: $.param( params)
				});
	  	  }
	  }
  }
  function empty() {
	$("#message")[0].innerHTML = "Sorry, the map is empty. Does the feed contains categories ?";
  }
  function error( error) {
	$("#message")[0].innerHTML = "Sorry, an error occured. Is this URL correct? <span class='hidden-message'>" + error + "</span>";
  }
  function Navigate( url) {
 	 window.open( url, "_blank");
  }
  function NewWin( args)
  {
	var parameters = {};
	parameters["entityId"] = args[0];
	parameters["feed"] = args[2];
	$("#wps-feeds")[0].compute( parameters);
	$("#message")[0].innerHTML = "<i>Focus on category:</i> " + args[1];
  }
  function Discover( args)
  {
	var parameters = {};
	parameters["attributeId"] = args[0];
	parameters["analysisProfile"] = "DiscoveryProfile";
	parameters["feed"] = args[2];
	$("#wps-feeds")[0].compute( parameters);
	$("#message")[0].innerHTML = "<i>Centered on item:</i> " + args[1];
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
<%}%>
<jsp:include page="./js/ga.js" /> 
</head>

<body>
<div id="header">
<table id="bandeau" border="0">
<tr>
	<td id="logo" rowspan="3">
		<a href="./" title="Map your feeds!"><img border="0" width="144" height="70" title="Map your feeds!" src="./images/logo-sc-white.jpg" /></a>
	</td>
	<td class="label" ><b>Enter one or more URLs (comma separated):</b>
	</td>
	<td class="social"><g:plusone size="medium" href="http://www.mapyourfeeds.com/"></g:plusone></td>
	<!--td class="social"><a title="Post to Google Buzz" class="google-buzz-button" href="http://www.google.com/buzz/post" data-button-style="small-count" data-url="http://www.mapyourfeeds.com"></a><script type="text/javascript" src="http://www.google.com/buzz/api/button.js"></script></td-->
	<td class="social"><iframe src="//www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.mapyourfeeds.com%2F&amp;send=false&amp;layout=button_count&amp;width=450&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font=arial&amp;height=21&amp;appId=205005596217672" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:450px; height:21px;" allowTransparency="true"></iframe></td>
</tr>
<tr>
	<td nowrap >
		<form method="get">
			<input type="text" name="feed" title="URLs" size="80" value="<%=feed != null ? feed : "" %>" />
			<input type="submit" value="Just Map It!" />
			<span id="doc"><a href="./documentation.html">How to use the service</a></span>
		</form>
	</td>
	<td class="social"><a href="https://twitter.com/share" class="twitter-share-button" data-count="horizontal">Tweet</a><script type="text/javascript" src="//platform.twitter.com/widgets.js"></script></td>
	<td class="social"><script src="http://platform.linkedin.com/in.js" type="text/javascript"></script><script type="IN/Share" data-counter="right"></script></td>
</tr>
<tr>
	<td nowrap colspan="2">
		<p id="message">&nbsp;</p>
	</td>
	<td>
		<a title="Add Map Your Feeds! as a Google Gadget" target="_blank" href="http://www.google.com/ig/directory?url=www.mapyourfeeds.com/google/igoogle-social-computing-feeds.xml"><img src="http://buttons.googlesyndication.com/fusion/add.gif" style="width:104px; height:17px;border:0px;" alt="Add this as an Google gadget" /></a>
	</td>
</tr>
</table>
</div>

<div id="map" >
<%if (feed.length() == 0) {%>
<p class="slogan">View and navigate your feeds thru an interactive map!<p>
<%} else {%>
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
