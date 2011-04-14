<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>


<html>
<head>
<link rel="stylesheet" href="css/wps.css" />
<link rel="stylesheet" href="css/main.css" />
<style type="text/css">
textarea {
	width: 75%;
	height: 75%;
}
</style>

</head>

<body>

<form name="upload" enctype="multipart/form-data" method="POST" action="upload">
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
<input type="submit"/>
</form>
</body>
</html>