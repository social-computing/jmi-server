<%@page import="com.socialcomputing.feeds.*"%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#" lang="en" xml:lang="en">	
<%String feed = request.getParameter("feed");
if( feed == null) feed = "";
int numpage = 0;
String spage = request.getParameter("page");
if( spage != null) numpage = Integer.parseInt( spage);%><head>
<title>Just Map It! Feeds</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="en" />
<meta name="description" content="Just Map It! Feeds lets you view and navigate your feeds thru an interactive map! by Social Computing" />
<meta name="keywords" content="rss, netvibes, google, blogger, feeds, feed, map, cartography, visualization, social, blog, gadget, widget, social computing, category, representation, information" />
<meta name="author" content="Social Computing" /> 
<meta name="robots" content="all" /> 
<meta property="og:title" content="Just Map It! Feeds" />
<meta property="og:description" content="View and navigate your feeds thru an interactive map! by Social Computing" />
<meta property="og:image" content="http://feeds.just-map-it.com/images/thumbnail.png" />
<link rel="shortcut icon" href="http://feeds.just-map-it.com/favicon.ico" />
<link rel="stylesheet" href="./mapyourfeeds.css" type="text/css" media="screen" />
<script type="text/javascript" src="./js/jquery-1.6.4.min.js"></script>
<script type="text/javascript" src="./fancybox/jquery.fancybox-1.3.4.pack.js"></script>
<link rel="stylesheet" type="text/css" href="./fancybox/jquery.fancybox-1.3.4.css" media="screen" />
<script type="text/javascript">
$(document).ready(function() {
	$("#howtouse").fancybox({
		'width'				: '75%',
		'height'			: '75%',
		'autoScale'			: false,
		'transitionIn'		: 'none',
		'transitionOut'		: 'none',
		'type'				: 'iframe'
		});
	});
<%if( feed.length() > 0) {%>
  function ready() {
	  var map = document.getElementById("wps-feeds");
	  var titles = map.getArrayProperty( "$FEEDS_TITLES");
	  if( titles) {
	  	document.title = 'Just Map It! Feeds - ' + titles.join( ', ');
		if( map.getProperty( "$analysisProfile") == "GlobalProfile") {
			document.getElementById("message").innerHTML = titles.join( ', ');
		}
	  }
	  var urls = map.getArrayProperty( "$FEEDS_URLS");
	  if( urls && urls.length == 1) {
		var data = {};
		data["url"] = urls[0];
		$.getJSON( "./rest/feeds/feed.json", data, function( feed){
			var date = new Date();
			if( !feed.thumbnail_date)
				feed.thumbnail_date = 0;
			var delta = date.getTime() - feed.thumbnail_date;
			if( delta > 7 * 24 * 3600 * 1000) {
				map.uploadAsImage( 'http://feeds.just-map-it.com/rest/feeds/feed/thumbnail.png', 'preview', 'image/png', 150, 100, true, data);
			}
		});
	  }
}
  function empty() {
	document.getElementById("message").innerHTML = "Sorry, the map is empty. Does the feed contains categories?";
  }
  function error( error) {
	document.getElementById("message").innerHTML = "Sorry, an error occured. Is this URL correct? <span class='hidden-message'>" + error + "</span>";
  }
  function JMIF_Navigate( url) {
 	 window.open( url, "_blank");
  }
  function JMIF_Focus( args)
  {
	var parameters = {};
	parameters["entityId"] = args[0];
	parameters["feed"] = args[2];
	parameters["track"] = "";
	document.getElementById("wps-feeds").compute( parameters);
	document.getElementById("message").innerHTML = "<i>Focus on category:</i> " + args[1];
  }
  function JMIF_Center( args)
  {
	var parameters = {};
	parameters["attributeId"] = args[0];
	parameters["analysisProfile"] = "DiscoveryProfile";
	parameters["feed"] = args[2];
	parameters["track"] = "";
	document.getElementById("wps-feeds").compute( parameters);
	document.getElementById("message").innerHTML = "<i>Centered on item:</i> " + args[1];
  }
</script>
<!-- Enable Browser History by replacing useBrowserHistory tokens with two hyphens -->
<!-- BEGIN Browser History required section -->
<link rel="stylesheet" type="text/css" href="./client/history/history.css" />
<script type="text/javascript" src="./client/history/history.js"></script>
<!-- END Browser History required section -->  

<script type="text/javascript" src="./client/swfobject.js"></script>
<script type="text/javascript">
    var swfVersionStr = "10.0.0";
    var xiSwfUrlStr = "./client/playerProductInstall.swf";
    var flashvars = {};
    flashvars.allowDomain = "*";
    //flashvars.wpsserverurl = "http://localhost:8080/wps-server";
    //flashvars.track = "http://localhost:8080/web-feeds/rest/feeds/record.json";
    flashvars.wpsserverurl = "http://server.just-map-it.com/";
    flashvars.track = "http://feeds.just-map-it.com/rest/feeds/record.json";
    flashvars.wpsplanname = "Feeds";
    flashvars.analysisProfile = "GlobalProfile";
    flashvars.feed = "<%=java.net.URLEncoder.encode(feed, "UTF-8")%>";
    var params = {};
    params.quality = "high";
    params.bgcolor = "#FFFFFF";
    params.allowscriptaccess = "always";
    params.allowfullscreen = "true";
    params.wmode = "transparent";
    var attributes = {};
    attributes.id = "wps-feeds";
    attributes.name = "wps-feeds";
    attributes.align = "middle";
    swfobject.embedSWF(
        "./client/wps-flex-1.0-SNAPSHOT.swf", "flashContent", 
        "100%", "100%", 
        swfVersionStr, xiSwfUrlStr, 
        flashvars, params, attributes);
swfobject.createCSS("#flashContent", "display:block;text-align:left;");
<%}%>
</script>
<jsp:include page="./js/ga.js" /> 
</head>
<body>
<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) {return;}
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/fr_FR/all.js#xfbml=1&appId=205005596217672";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<div id="header">
<table id="bandeau" border="0">
<tr>
<td id="logo" rowspan="3">
	<a href="./" title="Just Map It! Feeds"><img border="0" width="255" height="50" alt="Just Map It! Feeds" src="./images/justmapit-feeds.png" /></a>
</td>
<td class="label" ><b>Enter one or more URLs (comma separated):</b>
</td>
<td class="share" colspan="2">Share your map</td>
</tr>
<tr>
<td nowrap >
	<form method="get">
		<input type="text" name="feed" title="URLs" size="80" value="<%=feed != null ? feed : "" %>" />
		<input type="submit" value="Just Map It!" />
	</form>
</td>
<td rowspan="2">
<table border="0"><tr>
	<td class="social"><g:plusone size="medium" href="http://feeds.just-map-it.com/"></g:plusone></td>
	<!--td class="social"><a title="Post to Google Buzz" class="google-buzz-button" href="http://www.google.com/buzz/post" data-button-style="small-count" data-url="http://feeds.just-map-it.com"></a><script type="text/javascript" src="http://www.google.com/buzz/api/button.js"></script></td-->
	<td class="social"><fb:like href="<%=request.getRequestURL().toString()+(request.getQueryString() != null? "?"+request.getQueryString() : "")%>" send="true" layout="button_count" width="450" show_faces="false" font="arial"></fb:like></td>
</tr><tr>
	<td class="social"><a href="https://twitter.com/share" class="twitter-share-button" data-count="horizontal">Tweet</a><script type="text/javascript" src="//platform.twitter.com/widgets.js"></script></td>
	<td class="social"><script src="http://platform.linkedin.com/in.js" type="text/javascript"></script><script type="IN/Share" data-counter="right"></script></td>
</tr></table>
</td>
</tr>
<tr>
	<td nowrap colspan="1">
		<p id="message">Just Map It! Feeds lets you view and navigate your feeds thru an interactive map!</p>
	</td>
</tr>
</table>
</div>
<div id="content" >
<%if (feed.length() == 0) {%>
<div id="last-feeds"><h1>Last mapped feeds:</h1></div>
<div class="grid">
<%FeedManager feedManager = new FeedManager();
java.util.List<Feed> feeds = feedManager.last( numpage*10, 10, "true");
for (Feed f : feeds) {%><div class="vignette">
<div class="thumbnail">
<a title="Just Map It! Feed: <%=f.getUrl()%>" href='./?feed=<%=java.net.URLEncoder.encode(f.getUrl(),"UTF-8")%>'><img border="0" width="150" height="100" alt="<%=f.getTitle().replaceAll("\"","&quot;")%>" src="./rest/feeds/feed/thumbnail.png?url=<%=java.net.URLEncoder.encode(f.getUrl(),"UTF-8")%>" /></a>
<!--span class="play"/-->
</div><div class="thumbnail-title">
<h2><a href='./?feed=<%=java.net.URLEncoder.encode(f.getUrl(),"UTF-8")%>'><%=f.getTitle()%></a></h2>
</div></div><%}%></div>
<div class="pagination"><ul>
<%long max = (feedManager.count( "true") / 10) + 1;
for( long i = 0; i < max; ++i) { %>
<li <%=(numpage==i? "class='active'": "")%>><a href=".<%=(i==0? "" : "/?page=" + i)%>"><%=i+1%></a></li>
<%} for( long i = max+1; i < 20; ++i) { %>
<li class='disabled'><a ><%=i+1%></a></li>
<%}%></ul></div>
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
       <param name="wmode" value="transparent" />
       <!--[if !IE]>-->
       <object type="application/x-shockwave-flash" data="./client/wps-flex-1.0-SNAPSHOT.swf" width="100%" height="100%">
           <param name="quality" value="high" />
           <param name="bgcolor" value="#FFFFFF" />
           <param name="allowScriptAccess" value="sameDomain" />
           <param name="allowFullScreen" value="true" />
       	 <param name="wmode" value="transparent" />
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
<%}%>
</div>
<jsp:include page="./footer.jsp" >
	<jsp:param name="feed" value="<%=feed%>" /> 
</jsp:include>
<script type="text/javascript" src="https://apis.google.com/js/plusone.js"></script>
</body>
</html>
