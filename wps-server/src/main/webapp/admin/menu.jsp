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
-<tr><td height=20 ><a href="welcome.jsp">Home</a></td></tr>
-<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><a href="dictionaries.jsp" >Dictionaries (<%=dics.size()%>)</a></td></tr>
<tr>
	<td>
		<ul>
			<% for(Dictionary d :dics) { %>
			<li><a href="dictionary-detail.jsp?dictionary=<%=d.getName() %>"><%=d.getName() %></a></li>
			<%} %>
		</ul>
	</td>
</tr>
<!-- 
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><a href="swatches.jsp" >Swatches</a></td></tr>
 -->
<!-- 
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><a href="upload.jsp" >Upload</a></td></tr>
-->
<tr><td height=20 >&nbsp;</td></tr>
<tr><td height=20 ><hr noshade align="center" width="50%" size="1"></td></tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><span class="blueText"><b>Documentation</b></span></td></tr>
<!--   <tr><td align="left" nowrap><a href="doc/WPS.PDF" target="_blank"><font size="-2" >&nbsp;&nbsp;&nbsp;&nbsp;Technical guide</font></a></td></tr> --> 
<tr><td align="left" nowrap><a href="./docDTD.jsp" target="_wpsdtd"><font size="-2" >&nbsp;&nbsp;&nbsp;&nbsp;DTD definition</font></a></td></tr>
<tr><td align="left" nowrap><a href="./docApplet.jsp" ><font size="-2" >&nbsp;&nbsp;&nbsp;&nbsp;Applet</font></a></td></tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td height=20 ><hr noshade align="center" width="50%" size="1"></td></tr>
<tr><td height=20 >&nbsp;</td></tr>
<tr><td align="left" nowrap><a href="../demo/sample-applet.jsp">Sample Applet</a></td></tr>
<tr><td align="left" nowrap><a href="../demo/sample-flex.jsp">Sample Flex</a></td></tr>
</table>

