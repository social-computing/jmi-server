<%@page import="org.hibernate.Session"%>
<%@page import="org.hibernate.Transaction"%>
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
//Collection<Swatch> sws = sManager.findAll(); 
Collection<Dictionary> dics = dManager.findAll();

//Context context = new InitialContext();
//DataSource ds = (DataSource) context.lookup("java:comp/env/jdbc/WPSPooledDS");
//Connection  connection = ds.getConnection();
Connection connection = HibernateUtil.getSessionFactory().getCurrentSession().connection();
DatabaseMetaData meta = connection.getMetaData();
%>


<table border=0 width="100%">
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
	</table>
  </td>
  <td>&nbsp;</td>
 </tr>
</table>

<br/><br/>

<!-- UPLOAD DICTIONARY -->
<% 
if( request.getParameter( "openresults") != null && session.getAttribute( "UploadDefinitionFileResults") != null)
{%>	
	<script language="javascript">
		var win = window.open( 'upload_results.jsp', 'mpstadminresults', 'width=600,height=600,scrollbars=yes,resizable=yes,dependent=yes');
		win.focus();
	</script>
<%}%>	
<form name="test" enctype="multipart/form-data" method="POST" action="upload">
	<input type="hidden" name="action" value="uploadDictionaryFile" />
	<input type="hidden" name="redirect" value="./welcome.jsp?openresults=1" />
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >
	<tr>
	<td><span class="subTitleBlue">Load a dictionary file (*.xml, *.zip) : </span></td> 
	<td><input type="file" name="definitionFile" size="50" ></td>
	</tr>
	<tr>
	<td />
	<td><input type="submit" value="Load" /></td>
	</tr>
	</table>
</form>

