<%@page import="com.socialcomputing.wps.server.plandictionary.loader.Dictionary"%>
<%@ page import="java.util.*, java.sql.*, javax.sql.*, javax.naming.*, java.rmi.*, java.io.*" %>
<%@ page import="com.socialcomputing.wps.server.plandictionary.loader.*" %>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}

protected String escape2(String s) {
	StringBuffer sb = new StringBuffer();
	int start = 0;
	int pos = s.indexOf( '\n');
	while( pos != -1)
	{
		sb.append( s.substring( start, pos));
		sb.append( "\\\n");
		start = pos+1;
		pos = s.indexOf( '\n', start);
	}
	sb.append( s.substring( start));
	return sb.toString();
}
protected String escape(String s) {
	StringBuffer sb = new StringBuffer();
	s = s.replace( '\'', '"');
	int start = 0;
	int pos = s.indexOf( '"');
	while( pos != -1)
	{
		sb.append( escape2( s.substring( start, pos)));
		sb.append( "\\\"");
		start = pos+1;
		pos = s.indexOf( '"', start);
	}
	sb.append( escape2( s.substring( start)));
	return sb.toString();
}
%>
<%
int start = 1;
if( request.getParameter( "start") != null)
	start = Integer.parseInt( request.getParameter( "start"));
int viewstep = request.getParameter( "viewstep") == null ? 25 : Integer.parseInt( request.getParameter( "viewstep"));

String sortcol = request.getParameter( "sortcol");
String sortorder = request.getParameter( "sortorder");
String sortquery = request.getParameter( "sortquery");
if( sortcol == null) sortcol = "date";
if( sortorder == null) sortorder = " -";
if( sortquery == null) sortquery = "date desc";
%>
<html>
<head>
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
<SCRIPT LANGUAGE="JavaScript1.2" > 
	function ResetStart()
	{
		if( document.test.start != null)
			document.test.start.value = 1;
		return true;
	}
	function SubmitForm(resetStart)
	{
		if( resetStart)
			ResetStart();
		document.test.submit();
		return true;
	}
	function Sort( col, order, query)
	{
		ResetStart();
		document.test.sortcol.value = col;
		document.test.sortorder.value = order;
		document.test.sortquery.value = query;
		document.test.submit();
		return false;
	}
	function GotoNext()
	{
		document.test.start.value = <%=start+viewstep%>;
		document.test.submit();
		return false;
	}
	function GotoPred()
	{
		<%if( viewstep == -1) {%>
			ResetStart();
		<%} else {%>
			document.test.start.value = <%=start > viewstep ? start-viewstep : 1%>;
		<%}%>
		document.test.submit();
		return false;
	}
</SCRIPT>
</head>
<body bgcolor=7f9fdf>
<!--iframe height="0" width="0" src="../exportrequest.jsp"></iframe-->
<!--   
<ejb:useHome id="dicHome" type="com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoaderHome" location="java:comp/env/ejb/WPSDictionaryLoader" />
<ejb:useBean id="dic" type="com.socialcomputing.wps.server.plandictionary.loader.DictionaryLoader" scope="page"/>
-->
<%
	if( request.getParameter( "dictionary") == null) return;
	DictionaryManager manager = new DictionaryManager();
	Dictionary dic = manager.findByName(request.getParameter( "dictionary"));

	
String tableName = com.socialcomputing.wps.server.plandictionary.WPSDictionary.getHistoryTableName( dic.getName());

Context context = new InitialContext();
DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
Connection  connection = ds.getConnection();

Statement st = connection.createStatement();
ResultSet rs = st.executeQuery( "select count(*) from " + tableName);
int count = 0;
if( rs.next()) count = rs.getInt( 1);
rs.close();
%>
<table width="100%">
<tr>
<td><h1>dictionary <%=dic.getName()%> - <span class="texblanc"><%=count%> plans</span></h1></td>
</tr>
<tr>
</table>


<form name="test" method="GET" action="dictionary-history.jsp">
<input type="hidden" name="dictionary" value="<%=dic.getName()%>" />
<table width="100%" >
<tr><td colspan="4" >
<table border=1 width="100%">
<tr>
<th width="10%" ><span class="subTitleBlue">#</span></th>
<th width="30%" ><span class="subTitleBlue"><a href="" title="Trier" onclick="javascript:return Sort( 'date', <%=(sortcol.equalsIgnoreCase( "date") && sortorder.equalsIgnoreCase( " +") ? "' -', 'date desc'" : "' +', 'date asc'")%>)">date&nbsp;
<% if( sortcol.equalsIgnoreCase( "date"))
 	out.println( (sortorder.endsWith( "+") ? "+" : "-"));
%></a></span></th>
<th width="20%" ><span class="subTitleBlue"><a href="" title="Trier" onclick="javascript:return Sort( 'type', <%=(sortcol.equalsIgnoreCase( "type") && sortorder.equalsIgnoreCase( " +") ? "' -', 'type asc'" : "' +', 'type desc'")%>)">type&nbsp;
<% if( sortcol.equalsIgnoreCase( "type"))
 	out.println( (sortorder.endsWith( "+") ? "+" : "-"));
%></a></span></th>
<th width="10%" ><span class="subTitleBlue"><a href="" title="Trier" onclick="javascript:return Sort( 'duration', <%=(sortcol.equalsIgnoreCase( "duration") && sortorder.equalsIgnoreCase( " +") ? "' -', 'duration desc'" : "' +', 'duration asc'")%>)">duration (ms)&nbsp;
<% if( sortcol.equalsIgnoreCase( "duration"))
 	out.println( (sortorder.endsWith( "+") ? "+" : "-"));
%></a></span></th>
<th width="10%" ><span class="subTitleBlue">parameter</span></th>
<th width="10%" ><span class="subTitleBlue"><a href="" title="Trier" onclick="javascript:return Sort( 'status', <%=(sortcol.equalsIgnoreCase( "status") && sortorder.equalsIgnoreCase( " +") ? "' -', 'status desc'" : "' +', 'status asc'")%>)">error&nbsp;
<% if( sortcol.equalsIgnoreCase( "status"))
 	out.println( (sortorder.endsWith( "+") ? "+" : "-"));
%></a></span></th>
</tr>
<%	
String sql = "";
switch( com.socialcomputing.utils.database.DatabaseHelper.GetDbType( connection))
{
	case com.socialcomputing.utils.database.DatabaseHelper.DB_MYSQL:
		sql = "select DATE_FORMAT(date,'%Y/%m/%d - %H:%i') as dat, type, duration, parameters, status, info from " + tableName + " order by " + sortquery + " limit "+ String.valueOf( start-1) + "," + viewstep;
		break;
	case com.socialcomputing.utils.database.DatabaseHelper.DB_SQLSERVER:
		sql = "select top " + String.valueOf( start+viewstep-1) + " CONVERT(varchar, date, 121) as dat, type, duration, parameters, status, info from " + tableName + " order by " + sortquery;
		break;
	case com.socialcomputing.utils.database.DatabaseHelper.DB_HSQL:
		sql = "select limit " + String.valueOf( start-1) + " " + viewstep + " date as dat, date, type, duration, parameters, status, info from " + tableName + " order by " + sortquery;
		break;
}
//out.print( sql); 
rs = st.executeQuery( sql);
switch( com.socialcomputing.utils.database.DatabaseHelper.GetDbType( connection))
{
	case com.socialcomputing.utils.database.DatabaseHelper.DB_SQLSERVER:
		for( int i = 0; i < start-1 && rs.next(); ++i) ; // Skip first results
		break;
}
for( int i = 0; rs.next(); ++i)
{	%><tr>
	<td align="center" nowrap><span class="texblanc"><%=start+i%></span></td>
	<td align="center" nowrap><span class="texblanc"><%=rs.getString( "dat")%>&nbsp;</span></td>
	<td align="center" nowrap><span class="texblanc"><%=rs.getString( "type")%>&nbsp;</span></td>
	<td align="center" nowrap><span class="texblanc"><%=rs.getString( "duration")%>&nbsp;</span></td>
	<td align="center" nowrap><a href="." onclick='alert( "<%=escape( rs.getString( "parameters"))%>");return false;'>view</a></td>
	<td align="center" nowrap>
		<%if( rs.getInt( "status") != 0)
		{%>
			<a href="." onclick='alert( "<%=escape( rs.getString( "info"))%>");return false;'>view</a>
		<%}
		else out.print( "&nbsp;");%>
	</td>
	</tr><%
}
rs.close();
%>	
</table></td></tr>
<tr>
<td nowrap width="20%" align="left">
	<%if( start > 1)
	{%>
	<a href="" title="Voir les abonn�s pr�c�dents" onclick="javascript:return GotoPred()" >&lt;&lt;&lt; les <%=viewstep%> pr�c�dents</a>
	<%}%>
</td>

<td nowrap width="0%" align="center">
  <!--span class="subTitleBlue">commencer �</span-->
  <input type="hidden" name="start" value="<%=start%>" size="10" />
  <input type="hidden" name="sortcol" value="<%=sortcol%>" />
  <input type="hidden" name="sortorder" value="<%=sortorder%>" />
  <input type="hidden" name="sortquery" value="<%=sortquery%>" />
</td>
<td nowrap width="60%" align="center">
  <span class="subTitleBlue"># abonn�s / page</span>
  <select name="viewstep" onchange="javascript:ResetStart(); return SubmitForm(false)" >
	<option value="25"/>25</option>
	<option value="50" <%=viewstep==50 ? "SELECTED" : ""%> />50</option>
	<option value="100" <%=viewstep==100 ? "SELECTED" : ""%> />100</option>
	<option value="-1" <%=viewstep==-1 ? "SELECTED" : ""%>/>tous</option>
  </select>  
</td>

<td nowrap width="20%" align="right">
	<%if( viewstep != -1 && count > start + viewstep -1)
	{%>
	<a href="" title="Voir les abonn�s suivants" onclick="javascript:return GotoNext()" >&gt;&gt;&gt; les <%=viewstep%> suivants</a>
	<%}%>
</td>
</tr></table>
</form>

</body>
</html>
<%
connection.close();
%>