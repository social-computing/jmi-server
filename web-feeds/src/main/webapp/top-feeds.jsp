<%@page import="com.socialcomputing.feeds.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<head>
<title>Most mapped feeds - Just Map It! Feeds</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="en" />
<meta name="description" content="Most mapped feeds mapped by Just Map It! Feeds" />
<meta name="keywords" content="most, top, rss, feeds, feed, map, cartography, visualization, social, social computing, category, representation, information" />
<meta name="author" content="Social Computing" /> 
<meta name="robots" content="all" /> 
<link rel="shortcut icon" href="http://feeds.just-map-it.com/favicon.ico" />
<link rel=StyleSheet href="./mapyourfeeds.css" type="text/css" media="screen" />
<jsp:include page="./js/ga.js" /> 
</head>
<body>
<div id="documentation">
<div id="header">
<table id="bandeau" border="0">
<tr>
	<td id="logo" rowspan="2">
		<a href="./" title="Just Map It! Feeds"><img border="0" width="255" height="50" alt="Just Map It! Feeds" src="./images/justmapit-feeds.png" /></a>
	</td>
	<td style="vertical-align:bottom;"><h1>Most mapped feeds</h1>
	</td>
</tr>
</table>
</div><br/><br/><br/>
<div id="top-feeds-cloud">
<%FeedManager feedManager = new FeedManager();
java.util.List<Feed> feeds = feedManager.top( "100", "true");
float max = feeds.size() > 0 ? feeds.get(0).getCount() : 1;
for( Feed feed : feeds) {
	double dsize = (java.lang.Math.log(( feed.getCount() / max * (java.lang.Math.E-1)) + 1)); // ln scale
	long size = java.lang.Math.round( (dsize * 20) + 10);
%>
<a title="Just Map It! Feed: <%=feed.getUrl()%>" href='./?feed=<%=feed.getUrl()%>' style="font-size: <%=size%>px"><%=feed.getTitle()%></a>	
<%}%>
</div>
</div>
<jsp:include page="./footer.jsp" >
	<jsp:param name="feed" value="" /> 
</jsp:include>
</body>
</html>