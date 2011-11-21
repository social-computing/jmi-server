<div id="footer">
<table><tr>
<!--  td><a href=".">Privacy Policies</a></td-->
<%String feed = request.getParameter("feed");
if( feed == null) feed = "";
if( feed.length() > 0) { %>
<td><a title="Add it to my webpage" href="./integrate.jsp?feed=<%=java.net.URLEncoder.encode(feed, "UTF-8")%>">Add it to your webpage</a></td>
<%} else { %>
<td><a title="Add it to my webpage" href="./integrate.jsp">Add it to your webpage</a></td>
<%}%>
<!-- td><a href="./documentation.jsp#igoogle">Add it to iGoogle</a></td-->
<td align="center"><a id="howtouse" title="How to use the service" href="./documentation.jsp">How to use the service</a></td>
<td align="right">Powered by <a title="Just Map It!" href="http://www.social-computing.com/offre/cartographie-just-map-it/" target="_blank">Just Map It!</a> by <a title="Social Computing" href="http://www.social-computing.com" target="_blank">Social Computing</a> &copy; 2011</td>
</tr></table></div>
