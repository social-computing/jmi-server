<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%><%
DictionaryManager manager = new DictionaryManagerImpl();

if (request.getParameter("confirmdelete") != null && request.getParameter("confirmdelete").equalsIgnoreCase("y")) {
	int maxDelete = Integer.parseInt(request.getParameter("maxdelete"));
	for (int i = 0; i < maxDelete; i++)	{
		if (request.getParameter("delete" + i) != null){ 	
			manager.remove(request.getParameter("delete" + i));
		}
	}
}
// UPLOAD DICTIONARY
if( request.getParameter( "openresults") != null && session.getAttribute( "UploadDefinitionFileResults") != null)
{%>	
	<script type="text/javascript">
		var win = window.open( 'upload_results.jsp', 'mpstadminresults', 'width=600,height=600,scrollbars=yes,resizable=yes,dependent=yes');
		win.focus();
	</script>
<%}	
Collection<Dictionary> dics = manager.findAll();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<title>Just Map It! Administration</title>
	<meta http-equiv="content-language" content="en" />
	<link rel="stylesheet" href="../css/main.css"/>
	<link rel="stylesheet" href="../css/wps.css" />
	<script type="text/javascript" > 
		function SubmitForm(resetStart) {
			if (resetStart)
				ResetStart();
			document.test.submit();
			return true;
		}
		
		function Delete() {
			if( confirm("Are you sure you want to delete selected dictionnaires ?")) {
				document.test.confirmdelete.value = 'y';
				document.test.submit();
			}
			return false;
		}
		
		function OnExport(content, contentType){
			document.test.content.value = content;
			document.test.contentType.value = contentType;
			document.test.submit();
			return false;
		}
	</script>
</head>
<body>
<div id="top"><jsp:include page="top.jsp" /></div>
<div id="menu"><jsp:include page="menu.jsp" /></div>
<div id="content">
<form name="test" method="get" action="dictionaries.jsp">
<input type="hidden" name="confirmdelete" value="n" />
<!-- For export -->
<input type="hidden" name="content" value="" />
<input type="hidden" name="contentType" value="" />
<input type="hidden" name="maxdelete" value="<%=dics.size()%>" />
 <h1>Dictionaries</h1>
<table class="tableau" width="100%">
 <tr>
  <th width="8%" ><a href="" title="Delete selected dictionaries" onclick="javascript:return Delete()">delete</a></th>
  <th width="60%" align="left"><span class="subTitleBlue">Name</span></th>
 </tr>
<%	
	Iterator<Dictionary> it = dics.iterator();
	for (int i = 0 ; it.hasNext() ; ++i) {
		Dictionary dic = (Dictionary) it.next();
		%><tr>
		<td align="center" valign="top"><input type="checkbox" name="delete<%=i%>" value="<%=dic.getName()%>" /></td>
		<td nowrap><a title="View dictionary details" href="dictionary-detail.jsp?dictionary=<%=java.net.URLEncoder.encode(dic.getName(),"UTF-8")%>" ><%=dic.getName()%></a></td>
		</tr><%
	}
%>	
</table>
</form>
<br/><br/>

<form name="uploadForm" enctype="multipart/form-data" method="post" action="upload">
	<input type="hidden" name="action" value="uploadDictionaryFile" />
	<input type="hidden" name="redirect" value="./dictionaries.jsp?openresults=1" />
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >
	<tr>
	<td><span class="subTitleBlue">Load a dictionary file (*.xml, *.zip) : </span></td> 
	<td><input type="file" name="definitionFile" size="50" /></td>
	</tr>
	<tr>
	<td />
	<td><input type="submit" value="Load" /></td>
	</tr>
	</table>
</form>

</div>
</body>
</html>
<%
if( request.getParameter("content") != null && request.getParameter("content").length() > 0) {
	StringBuffer sb = new StringBuffer("action=export");
	sb.append("&");
	sb.append(request.getQueryString());
	//out.print( sb.toString());
	out.print("<iframe height=0 width=0 src=\"../sadmin?" + sb.toString() + "\"></iframe>");
}
%>