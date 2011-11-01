<%@page import="com.socialcomputing.feeds.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#" lang="en" xml:lang="en">	
<head>
<title>Top feess mapped - Just Map It! Feeds</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="en" />
<meta name="description" content="Top feeds mapped by Just Map It! Feds" />
<meta name="keywords" content="rss, netvibes, google, blogger, feeds, feed, map, cartography, visualization, social, blog, gadget, widget, social computing, category, representation, information" />
<meta name="author" content="Social Computing" /> 
<meta name="robots" content="all" /> 
<link rel="shortcut icon" href="http://feeds.just-map-it.com/favicon.ico" />
<link rel=StyleSheet href="./mapyourfeeds-doc.css" type="text/css" media="screen" />
<script type="text/javascript" src="./js/jquery-1.6.4.min.js"></script>
</head>
<body>
<div id="documentation">
<div id="header">
<table id="bandeau" border="0">
<tr>
	<td id="logo" rowspan="3">
		<a href="./" title="Just Map It! Feeds"><img border="0" width="255" height="50" alt="Just Map It! Feeds" src="./images/justmapit-feeds.png" /></a>
	</td>
	<td style="vertical-align:bottom;"><h1>Feeds most mapped</h1>
	</td>
</tr>
</table>
</div>
<%FeedManager feedManager = new FeedManager();
for( Feed feed : feedManager.top( "100", "true")) {%>
<h2><a href='./?feed=<%=feed.getUrl()%>' title='Just Map It! Feeds'><%=feed.getTitle()%></a></h2>	
<%}%>
</div>
</body>
</html>