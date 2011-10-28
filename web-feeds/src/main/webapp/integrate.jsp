<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<head>
<title>Add Just Map It! Feeds to your webpage - Just Map It! Feeds</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="en">
<meta name="description" content="Add Just Map It! Feeds to your webpage. by Social Computing" />
<meta name="author" content="Social Computing" /> 
<meta name="robots" content="all" /> 
<link rel=StyleSheet href="./mapyourfeeds.css" type="text/css" media="screen" />
<jsp:include page="./js/ga.js" /> 
</head>
<body>
<div id="header">
<table id="bandeau" border="0">
<tr>
	<td id="logo" rowspan="3">
		<a href="./" title="Just Map It! Feeds"><img border="0" width="255" height="50" alt="Just Map It! Feeds" src="./images/justmapit-feeds.png" /></a>
	</td>
	<td style="vertical-align:bottom;"><h1>Add Just Map It! Feeds to your webpage</h1>
	</td>
</tr>
</table>
</div>
<div id="integrate">
<%String feed = request.getParameter("feed");
if( feed==null) feed="";
String m = request.getParameter("m");
if( m==null) m = "message";
String w = request.getParameter("w");
if( w==null || w.length() == 0) w = "500";
String h = request.getParameter("h");
if( h==null || h.length() == 0) h = "500";
%>
<table border="0">
<tr><td style="vertical-align:top;">
<div id="formulaire">
	<h2>1 - Complete the form</h2>
	<form method="GET">
	<table>
	<tr><td>URL(s) (comma separated) *</td><td><input type="text" name="feed" size="80" value="<%=feed%>"></td></tr>
	<tr><td>Width *</td><td><input type="text" name="w" size="10" value="<%=w%>"></td></tr>
	<tr><td>Height *</td><td><input type="text" name="h" size="10" value="<%=w%>"></td></tr>
	<tr><td>Div id for message</td><td><input type="text" name="m" size="10" value="<%=m%>"></td></tr>
	<tr><td></td><td>* required</td></tr>
	<tr><td></td><td><input type="submit" value="Get code"></td></tr>
	</table>	
	</form>
</td><td  style="vertical-align:top;">
<div id="preview">
	<h2>2 - Preview - 500 * 300px</h2>
	<div id="message"><i>Here is a div with id='message'</i></div>
	<script type="text/javascript" src="./int.jsp?url=<%=java.net.URLEncoder.encode(feed)%>&m=message&w=500&h=300"></script>
</div>
</td></tr>
<tr><td colspan="2">
<div id="code">
	<h2>3 - Copy this code in your page</h2>
	<textarea name="code" readonly="true" style="width:100%"><script type="text/javascript" src="http://feeds.just-map-it.com/int.jsp?url=<%=java.net.URLEncoder.encode(feed)%>&m=<%=java.net.URLEncoder.encode(m)%>&w=<%=w%>&h=<%=h%>"></script></textarea> 
</div>
</td></tr>
</table>
</div>
<jsp:include page="./footer.jsp" >
	<jsp:param name="feed" value="<%=feed%>" /> 
</jsp:include>
</body>
</html>
