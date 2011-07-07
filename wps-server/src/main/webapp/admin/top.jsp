<%@page import="org.hibernate.Session"%>

<%@page import="com.socialcomputing.utils.database.HibernateUtil"%>
<%@ page import="java.util.*, java.sql.*, javax.sql.*, javax.naming.*" %>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>
<%
Connection connection = HibernateUtil.getSessionFactory().getCurrentSession().connection();
DatabaseMetaData meta = connection.getMetaData();
%>

<table width="100%" cellpadding="0" cellspacing="0" width="100%" >
<tr>
<td align="left" valign="middle">
	<a href="welcome.jsp" ><img title="Social Computing" src="../images/logo-sc-white.jpg" /></a>
</td>
<td align="left" valign="middle" >
	<h1><b>Web Positioning System Administration <span class="texblanc"> - <%=meta.getDatabaseProductName()%> - v. <%=meta.getDatabaseProductVersion()%></span></b></h1>
</td>
<td align="right" valign="middle" >
	<!-- <span class="texblanc"><%=meta.getDatabaseProductName()%> - v. <%=meta.getDatabaseProductVersion()%></span>-->
	<!-- <span class="texblanc"><%=meta.getURL()%></span-->
	<!-- <span class="texblanc"><%=meta.getDriverName()%> - v. <%=meta.getDriverVersion()%></span> -->
</td>
</tr>
</table>
