<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="java.util.List"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>
<%
String dictionaryName = request.getParameter( "dictionary");
if (dictionaryName == null) return;
DictionaryManager dManager = new DictionaryManagerImpl();
Dictionary dic = dManager.findByName(dictionaryName); 
List<Swatch> ls = dic.getSwatchs();
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
		if( confirm("Are you sure you want to delete selected swatches ?"))
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
SwatchManager manager = new SwatchManagerImpl();
if (request.getParameter("confirmdelete") != null && request.getParameter("confirmdelete").equalsIgnoreCase("y"))
{
	int maxDelete = Integer.parseInt( request.getParameter("maxdelete"));
	for (int i = 0; i < maxDelete; i++)
	{
		if( request.getParameter("delete" + i) != null)
		{	// Delete
			manager.remove(request.getParameter("delete" + i) );
		}
	}
}
%>

Dictionary : <%=dictionaryName %>
<br/><br/>
<form name="test" method="GET" action="dictionary-detail.jsp">
<input type="hidden" name="dictionary" value="<%=dictionaryName%>" />
<input type="hidden" name="confirmdelete" value="n" />

<!-- For export -->
<input type="hidden" name="content" value="" />
<input type="hidden" name="contentType" value="" />
<input type="hidden" name="maxdelete" value="<%=ls.size()%>" />

<br>
<table width="100%" >
<tr><td colspan="4" >
<table border=1 width="100%">

 <tr>
  <th width="8%" ><span class="subTitleBlue">#</span></th>
  <th width="8%" ><a href="" title="Delete selected dictionaries" onclick="javascript:return Delete()">delete</a></th>
  <th width="60%" ><span class="subTitleBlue">name</span></th>
 </tr>
<%	
	int i = 0;
	for(Swatch sw : ls)
	{
		%><tr>
		<td align="center" nowrap><span class="texblanc"><%=i+1%></span></td>
		<td align="center" valign="top"><input type="checkbox" name="delete<%=i%>" value="<%=sw.getName()%>" /></td>
		<td nowrap><a href="swatch.jsp?swatch=<%=java.net.URLEncoder.encode(sw.getName(),"UTF-8")%>" ><span class="texblanc"><%=sw.getName()%></span></a></td>
		</tr><%
		i++;
	}
%>	
</table>
</td></tr>
</table>
</form>

<br/><br/>

<!-- UPLOAD SWATCHES -->
<% if( request.getParameter( "openresults") != null && session.getValue( "UploadDefinitionFileResults") != null)
{%>	
	<script language="javascript">
		var win = window.open( 'upload_results.jsp', 'mpstadminresults', 'width=600,height=600,scrollbars=yes,resizable=yes,dependent=yes');
		win.focus();
	</script>
<%}%>	
<form name="upload" enctype="multipart/form-data" method="POST" action="upload">
	<input type="hidden" name="action" value="uploadSwatchFile" />
	<input type="hidden" name="dictionary" value="<%=dictionaryName %>" />
	<input type="hidden" name="redirect" value="./dictionary-detail.jsp?dictionary=<%=dictionaryName %>&openresults=1" />
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >
		<tr>
			<td><span class="subTitleBlue">Load a swatch file (*.xml, *.zip) : </span></td> 
			<td><input type="file" name="definitionFile" size="50" ></td>
		</tr>
		<tr>
			<td><input type="submit" value="Load" /></td>
		</tr>
	</table>
</form>

</body>
</html>
<%
if( request.getParameter( "content") != null && request.getParameter( "content").length() > 0)
{
	StringBuffer sb = new StringBuffer( "action=export");
	sb.append( "&");
	sb.append( request.getQueryString());
	//out.print( sb.toString());
	out.print( "<iframe height=0 width=0 src=\"../sadmin?" + sb.toString() + "\"></iframe>");
}
%>