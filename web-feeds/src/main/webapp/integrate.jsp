<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<head>
<title>Integrate - Map your feeds!</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="content-language" content="en">
<jsp:include page="./js/ga.js" /> 
</head>
<body>
<%String feed = request.getParameter("feed");
if( feed==null) feed="";
String w = request.getParameter("w");
if( w==null || w.length() == 0) w = "700";
String h = request.getParameter("h");
if( h==null || h.length() == 0) h = "300";
%>
<div id="formulaire">
	<form method="GET">
		url: <input type="text" name="feed" size="80" value="<%=feed%>"><br/>
		width: <input type="text" name="w" size="10" value="<%=w%>">
		height: <input type="text" name="h" size="10" value="<%=h%>">
		<input type="submit" value="Get code">
	</form>
</div>
<div id="code">
	Place this code in your page:<br/>
	<textarea name="code" readonly="true" style="width:100%"><script type="text/javascript" src="http://www.mapyourfeeds.com/int.jsp?url=<%=java.net.URLEncoder.encode(feed)%>&w=<%=w%>&h=<%=h%>"></script></textarea> 
</div>
<div id="preview">
	Preview:<br/>
	<script type="text/javascript" src="./int.jsp?url=<%=java.net.URLEncoder.encode(feed)%>&w=<%=w%>&h=<%=h%>"></script>
</div>
</body>
</html>
