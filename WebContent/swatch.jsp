<%@ page import="com.socialcomputing.wps.server.swatchs.loader.*" %>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>

<html>
<head>
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
<base target="main">
<SCRIPT LANGUAGE="JavaScript1.2" > 
	function SubmitForm(resetStart)
	{
		if( resetStart) ResetStart();
		document.test.submit();
		return true;
	}
	function Delete()
	{
		if( confirm("Are you sure you want to delete selected dictionnaires ?"))
		{
			document.test.confirmdelete.value = 'y';
			document.test.submit();
		}
		return false;
	}
	function OnExport( content, contentType)
	{
		document.test.content.value = content;
		document.test.contentType.value = contentType;
		document.test.submit();
		return false;
	}
</SCRIPT>
</head>
<body bgcolor=7f9fdf>
<!--iframe height="0" width="0" src="../exportrequest.jsp"></iframe-->
<%
if(request.getParameter("swatch") == null) return;
SwatchManager manager = new SwatchManager();
Swatch sw = manager.findByName(request.getParameter("swatch"));

//sw = swHome.findByPrimaryKey( request.getParameter( "swatch"));
%>
<table width="100%">
<tr>
<td><h1>swatch : <%=sw.getName()%></h1></td>
</tr>
<tr>
<td><a href="view_def.jsp?swatch=<%=java.net.URLEncoder.encode(sw.getName(), "UTF-8")%>" target="_blank"><span class="texblanc">View definition</span></a></td>
</tr>
<tr><td>&nbsp;</td></tr>
</table>
</body>
</html>
