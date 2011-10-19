<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<head>
<title>Integrate your feeds map on your website - Map your feeds!</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="en">
<meta name="description" content="Integrate your feeds map in your website! by Social Computing" />
<meta name="author" content="Social Computing" /> 
<meta name="robots" content="all" /> 
<jsp:include page="./js/ga.js" /> 
</head>
<body>
<%String feed = request.getParameter("feed");
if( feed==null) feed="";
String m = request.getParameter("m");
if( m==null) m = "message";
String w = request.getParameter("w");
if( w==null || w.length() == 0) w = "500";
String h = request.getParameter("h");
if( h==null || h.length() == 0) h = "500";
%>
<div id="formulaire">
	<form method="GET">
		url*: <input type="text" name="feed" size="80" value="<%=feed%>"><br/>
		width*: <input type="text" name="w" size="10" value="<%=w%>"><br/>
		height*: <input type="text" name="h" size="10" value="<%=h%>"><br/>
		div id for message: <input type="text" name="m" size="10" value="<%=m%>"><br/>
		<input type="submit" value="Get code">
	*: required
	</form>
</div>
<div id="code">
	Place this code in your page:<br/>
	<textarea name="code" readonly="true" style="width:100%"><script type="text/javascript" src="http://www.mapyourfeeds.com/int.jsp?url=<%=java.net.URLEncoder.encode(feed)%>&m=<%=java.net.URLEncoder.encode(m)%>&w=<%=w%>&h=<%=h%>"></script></textarea> 
</div>
<div id="preview">
	Preview:<br/>
	<div id="message"><i>Here is a div with id='message'</i></div>
	<script type="text/javascript" src="./int.jsp?url=<%=java.net.URLEncoder.encode(feed)%>&m=<%=java.net.URLEncoder.encode(m)%>&w=<%=w%>&h=<%=h%>"></script>
</div>
</body>
</html>
