<%@ page import="com.socialcomputing.wps.server.plandictionary.loader.*" %>

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
		if( resetStart)
			ResetStart();
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
if( request.getParameter( "dictionary") == null) return;
DictionnaryLoaderDao dld = new DictionnaryLoaderDao();
BeanDictionaryLoader dic = dld.findByName(request.getParameter( "dictionary")); //dicHome.findByPrimaryKey( request.getParameter( "dictionary"));
%>
<table width="100%">
<tr>
<td><h1>dictionary <%=dic.getName()%></h1></td>
</tr>
<tr>
<td><a href="view_def.jsp?type=plan&dictionary=<%=java.net.URLEncoder.encode(dic.getName(),"UTF-8")%>" target="_blank"><span class="texblanc">View definition</span></a></td>
<td><a href="dictionary-history.jsp?dictionary=<%=java.net.URLEncoder.encode(dic.getName(),"UTF-8")%>" target="_blank"><span class="texblanc">View history</span></a></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr><td>
	<form name="test" method="GET" action="view-applet.jsp" target="_blank">
	<input type="hidden" name="dictionary" value="<%=dic.getName()%>" >
	<input type="hidden" name="internal" value="n" >
	<table width="60%" border=0>
	 <tr><td colspan=3><span class="subTitleBlue" >Test it</span></td></tr>
	 <tr>
	 	<td nowrap><span class="texblanc">Input parameters</span></td>
	 	<td colspan=2><input type="text" size="60" name="appletparams" value="entityId=" ></td>
	 </tr>
	 <tr>
	 	<td />
		<td align="center" ><a href="view-applet.jsp" onclick="document.test.internal.value='n';document.test.submit();return false;" >view</a></td>
		<td align="center" ><a href="view-applet.jsp" onclick="document.test.internal.value='y';document.test.submit();return false;" >details</a></td>
	 </tr>
	</table>
	</form>
</td></tr>
</table>
</body>
</html>
