<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}%><html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<style type="text/css">
textarea {
	width: 99%;
	height: 99%;
}
</style>
</head>
<body>
<%String dtd =  request.getParameter( "dtd");%>
<textarea name="definition"><%if (dtd.equals( "dico")) { %>
<%@ include file="/dtd/WPS-dictionary.dtd" %>
<% } else if (dtd.equals( "jdbc")) { %>
<%@ include file="/dtd/JDBC-connector.dtd" %>
<% } else if (dtd.equals( "rest")) { %>
<%@ include file="/dtd/REST-connector.dtd" %>
<% } else if (dtd.equals( "swatch")) { %>
<%@ include file="/dtd/swatch.dtd" %>
<% } %>
</textarea>
</body>
</html>