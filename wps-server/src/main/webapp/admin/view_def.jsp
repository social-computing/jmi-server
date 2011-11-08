<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>
<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}%>

<html>
<head>
<link rel="stylesheet" href="../css/wps.css" />
<link rel="stylesheet" href="../css/main.css" />
<style type="text/css">
textarea {
	width: 99%;
	height: 99%;
}
</style>
</head>
<body>
<textarea name="definition" readonly="true">
<%
String dictionaryName = request.getParameter( "dictionary");
String swatchName = request.getParameter("swatch");
if (dictionaryName != null) {
    if (swatchName == null) {
		DictionaryManager managerD = new DictionaryManagerImpl();
		Dictionary dic = managerD.findByName(dictionaryName);
		out.print( dic.getDefinition());
    } else {
		SwatchManager managerS = new SwatchManagerImpl();
		Swatch swatch = managerS.findByName(swatchName, dictionaryName);
		out.print( swatch.getDefinition());
    }
}%>
</textarea>
</body>
</html>
