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
	<a title="Just Map It!" href="./index.jsp" ><img alt="Just Map It! Administration" src="../images/justmapit_admin.png" /></a>
</td>
<td align="center" valign="middle" >
	<h1><b>Just Map It! Administration <span class="texblanc"> - <%=meta.getDatabaseProductName()%> - v. <%=meta.getDatabaseProductVersion()%></span></b></h1>
</td>
<td align="right" valign="middle" >
	<a title="Social Computing" href="http://www.social-computing.com" target="_blank"><img alt="Social Computing" src="../images/logo-sc-white.jpg" style="text-align:right" /></a>
</td>
</tr>
</table>
