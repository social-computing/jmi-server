<%@ page import="java.util.*, java.sql.*, javax.sql.*, javax.naming.*, com.socialcomputing.wps.server.plandictionary.loader.*,com.socialcomputing.wps.server.swatchs.loader.*" %>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>


<%
DictionnaryLoaderDao dld = new DictionnaryLoaderDao();
SwatchLoaderDao sld = new SwatchLoaderDao();
Collection sws = sld.findAll(); //swHome.findAll();
Collection dics = dld.findAll(); //dicHome.findAll();

Context context = new InitialContext();
DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
Connection  connection = ds.getConnection();
DatabaseMetaData meta = connection.getMetaData();
%>
<html>
<head>
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
<base target="main">
</head>
<body>
<body bgcolor=7f9fdf>

<table border=0 width=100%">
 <tr> 
  <td>
	<table width="100%" border=1>
	 <tr>
	  <td nowrap><span class="subTitleBlue">WPS database</span></td>
	  <td align="center" ><span class="texblanc"><%=meta.getDatabaseProductName()%> - v. <%=meta.getDatabaseProductVersion()%></span></td>
	 </tr>
	 <tr>
	  <td nowrap><span class="subTitleBlue">WPS database driver</span></td>
	  <td align="center" ><span class="texblanc"><%=meta.getDriverName()%> - v. <%=meta.getDriverVersion()%></span></td>
	 </tr>
	 <tr>
	  <td nowrap><span class="subTitleBlue">WPS database url</span></td>
	  <td align="center" ><span class="texblanc"><%=meta.getURL()%></span></td>
	 </tr>
	</table>
  </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
  <td>
	<table width="30%" border=1>
	 <tr>
	  <td nowrap><span class="subTitleBlue"># dictionaries</span></td>
	  <td align="center" nowrap><span class="texblanc"><b><%=dics.size()%></b></span></td>
	 </tr>
	 <tr>
	  <td nowrap><span class="subTitleBlue"># swatches</span></td>
	  <td align="center" width="50%" nowrap><span class="texblanc"><b><%=sws.size()%></b></span></td>
	 </tr>
	</table>
  </td>
  <td>&nbsp;</td>
 </tr>
</table>
</body>
</html>
<%
connection.close();
%>