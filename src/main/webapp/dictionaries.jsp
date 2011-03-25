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
%>

<%
DictionaryManager manager = new DictionaryManagerImpl();

if (request.getParameter("confirmdelete") != null && request.getParameter("confirmdelete").equalsIgnoreCase("y")) {
	int maxDelete = Integer.parseInt(request.getParameter("maxdelete"));
	for (int i = 0; i < maxDelete; i++)	{
		if (request.getParameter("delete" + i) != null){ 	
			manager.remove(request.getParameter("delete" + i));
		}
	}
}
Collection<Dictionary> dics = manager.findAll();
%>

<html>
	<head>
		<title>WPS Administration</title>
		<META http-equiv="content-type" content="text/html;charset=ISO-8859-1">
		<META http-equiv="content-language" content="fr-FX">
		<link rel="stylesheet" href="css/main.css"/>
		<link rel="stylesheet" href="css/wps.css">
		<SCRIPT LANGUAGE="JavaScript1.2" > 
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
		</SCRIPT>
	</head>
	<body>
	<div id="top"><jsp:include page="top.jsp" /></div>
	<div id="menu"><jsp:include page="menu.jsp" /></div>
	<div id="content">
	
		<form name="test" method="GET" action="dictionaries.jsp">
		<input type="hidden" name="confirmdelete" value="n" />
		
		<!-- For export -->
		<input type="hidden" name="content" value="" />
		<input type="hidden" name="contentType" value="" />
		
		<input type="hidden" name="maxdelete" value="<%=dics.size()%>" />
		 
		<br>
		<table width="100%" >
		<tr><td colspan="4" >
		<table border=1 width="100%">
		
		 <tr>
		  <th width="8%" ><span class="subTitleBlue">#</span></th>
		  <th width="8%" ><a href="" title="Delete selected dictionaries" onclick="javascript:return Delete()">delete</a></th>
		  <th width="60%" ><span class="subTitleBlue">name</span></th>
		  <th width="24%" ><span class="subTitleBlue">next filtering date</span></th>
		 </tr>
		<%	
			Iterator<Dictionary> it = dics.iterator();
			for (int i = 0 ; it.hasNext() ; ++i) {
				Dictionary dic = (Dictionary) it.next();
				%><tr>
				<td align="center" nowrap><span class="texblanc"><%=i+1%></span></td>
				<td align="center" valign="top"><input type="checkbox" name="delete<%=i%>" value="<%=dic.getName()%>" /></td>
				<td nowrap><span class="texblanc"><a href="dictionary-detail.jsp?dictionary=<%=java.net.URLEncoder.encode(dic.getName(),"UTF-8")%>" ><%=dic.getName()%></a></span></td>
				<td nowrap><span class="texblanc"><%=(dic.getNextFilteringDate()==null ? "&nbsp;" : dic.getNextFilteringDate().toString())%></span></td>
				</tr><%
			}
		%>	
		</table>
		</td></tr>
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