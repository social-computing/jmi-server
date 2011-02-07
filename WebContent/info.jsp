<%@page import="org.hibernate.Session"%>
<%@page import="com.socialcomputing.utils.database.HibernateUtil"%>
<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>
<%@ page import="java.util.*, java.sql.*, javax.sql.*, javax.naming.*" %>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>


<%
DictionaryManager dManager = new DictionaryManagerImpl();
SwatchManager sManager = new SwatchManagerImpl();
Collection<Swatch> sws = sManager.findAll(); 
Collection<Dictionary> dics = dManager.findAll();

//Context context = new InitialContext();
//DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
//Connection  connection = ds.getConnection();
Session s = HibernateUtil.currentSession();
Connection connection = s.connection();

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