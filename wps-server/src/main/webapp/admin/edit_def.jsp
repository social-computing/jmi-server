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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<link rel="stylesheet" href="../css/wps.css" />
<link rel="stylesheet" href="../css/main.css" />
<style type="text/css">
textarea {
	width: 92%;
	height: 92%;
}
</style>
</head>
<body>
<form name="upload" enctype="multipart/form-data" method="post" action="upload">
<%
String dictionaryName = request.getParameter( "dictionary");
String swatchName = request.getParameter("swatch");

if (dictionaryName != null) {
    if (swatchName == null) {%>
<input type="hidden" name="dictionary" value="<%=dictionaryName %>" />
<input type="hidden" name="action" value="updateDictionary" />
<input type="hidden" name="redirect" value="./dictionary-detail.jsp?dictionary=<%=dictionaryName %>&openresults=1" />
<textarea name="definition">
<%
		DictionaryManager managerD = new DictionaryManagerImpl();
		Dictionary dic = managerD.findByName(dictionaryName);
		out.print(dic.getDefinition());
    } else {
%>
<input type="hidden" name="dictionary" value="<%=dictionaryName %>" />
<input type="hidden" name="action" value="updateSwatch" />
<input type="hidden" name="redirect" value="./dictionary-detail.jsp?dictionary=<%=dictionaryName %>&openresults=1" />
<textarea name="definition">
<%
		SwatchManager managerS = new SwatchManagerImpl();
		Swatch swatch = managerS.findByName(swatchName, dictionaryName);
		out.print(swatch.getDefinition());
    }
}
%>
</textarea>
<input type="submit" value="Save"/>
</form>
</body>
</html>