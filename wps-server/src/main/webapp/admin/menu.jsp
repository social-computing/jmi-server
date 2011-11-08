<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="java.util.Collection"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>

<%
DictionaryManager dManager = new DictionaryManagerImpl();
Collection<Dictionary> dics = dManager.findAll();
%>
<table>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><a title="<%=dics.size()%> dictionaries" href="dictionaries.jsp" >Dictionaries</a></td></tr>
<tr>
	<td>
		<ul>
			<% for(Dictionary d :dics) { %>
			<li><a title="View dictionary details" href="dictionary-detail.jsp?dictionary=<%=d.getName() %>"><%=d.getName() %></a></li>
			<%} %>
		</ul>
	</td>
</tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td height=20 ><hr noshade align="center" width="50%" size="1"></td></tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><span class="blueText"><b>Documentation</b></span></td></tr>
<!--   <tr><td align="left" nowrap><a href="doc/WPS.PDF" target="_blank"><font size="-2" >&nbsp;&nbsp;&nbsp;&nbsp;Technical guide</font></a></td></tr> --> 
<tr><td align="left" nowrap><a href="./docDTD.jsp"><font size="-2" >&nbsp;&nbsp;&nbsp;&nbsp;DTD definition</font></a></td></tr>
<tr><td align="left" nowrap><a href="./docApplet.jsp" ><font size="-2" >&nbsp;&nbsp;&nbsp;&nbsp;Applet</font></a></td></tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td height=20 ><hr noshade align="center" width="50%" size="1"></td></tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><a href="../demo/sample-applet.jsp" target="_blank">Sample Applet</a></td></tr>
<tr><td align="left" nowrap><a href="../demo/sample-flex.jsp" target="_blank">Sample Flex</a></td></tr>
</table>

