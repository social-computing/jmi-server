<div id="footer">
<table><tr>
<td><a title="Social Computing" href="http://www.social-computing.com" target="_blank">Powered by Social Computing</a></td>
<!--  td><a href=".">Privacy Policies</a></td-->
<%String feed = request.getParameter("feed");
if( feed == null) feed = "";
if( feed.length() > 0) { %>
<td align="right"><a title="Add it to my webpage" href="./integrate.jsp?feed=<%=java.net.URLEncoder.encode(feed)%>">Add it to my webpage</a></td>
<%} else { %>
<td align="right"><a title="Add it to my webpage" href="./integrate.jsp">Add it to my webpage</a></td>
<%}%>
<!-- td><a href="./documentation.jsp#igoogle">Add it to iGoogle</a></td-->
</tr></table></div>
