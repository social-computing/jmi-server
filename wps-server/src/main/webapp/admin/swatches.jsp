<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>

<html>
<head>
<link rel="stylesheet" href="../css/wps.css">
<link rel="stylesheet" href="../css/result.css">
<base target="main">
<script type="text/javascript" > 
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
</script>
</head>
<body bgcolor=7f9fdf>
<!--iframe height="0" width="0" src="../exportrequest.jsp"></iframe-->
<%
SwatchManager manager = new SwatchManagerImpl();

if( request.getParameter( "confirmdelete") != null && request.getParameter( "confirmdelete").equalsIgnoreCase( "y"))
{
	int maxDelete = Integer.parseInt( request.getParameter( "maxdelete"));
	for( int i = 0; i < maxDelete; i++)
	{
		if( request.getParameter( "delete" + i) != null)
		{	// Delete
			;//manager.remove( request.getParameter( "delete" + i) );
		}
	}
}
Collection<Swatch> sws = manager.findAll();
%>
<form name="test" method="GET" action="swatches.jsp">
<input type="hidden" name="confirmdelete" value="n" />

<!-- For export -->
<input type="hidden" name="content" value="" />
<input type="hidden" name="contentType" value="" />
<input type="hidden" name="maxdelete" value="<%=sws.size()%>" />

<br>
<table width="100%" >
<tr><td colspan="4" >
<table border=1 width="100%">

 <tr>
  <th width="8%" ><span class="subTitleBlue">#</span></th>
  <th width="8%" ><a href="" title="Delete selected swatches" onclick="javascript:return Delete()">delete</a></th>
  <th width="60%" ><span class="subTitleBlue">name</span></th>
 </tr>
<%	
	Iterator it = sws.iterator();
	for( int i = 0; it.hasNext(); ++i)
	{
		Swatch sw = (Swatch) it.next();
		%><tr>
		<td align="center" nowrap><span class="texblanc"><%=i+1%></span></td>
		<td align="center" valign="top"><input type="checkbox" name="delete<%=i%>" value="<%=sw.getName()%>" /></td>
		<td nowrap><a href="swatch.jsp?swatch=<%=java.net.URLEncoder.encode(sw.getName(),"UTF-8")%>" ><span class="texblanc"><%=sw.getName()%></span></a></td>
		</tr><%
	}
%>	
</table>
</td></tr>
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