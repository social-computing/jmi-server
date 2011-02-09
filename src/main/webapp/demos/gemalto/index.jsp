<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}%>
<%
String q = request.getQueryString();
if( q == null) q = "";
%>
<HTML>
<frameset rows="40, *, 0" border="0" frameborder="no">
	<frame name="_toolsFrame" src="./sample-tools.jsp?<%=q%>" scrolling="no" />
	<frame name="_appletFrame" src="./sample-applet.jsp?<%=q%>" scrolling="no" />
	<noframes>
		<body>
			<p>
				Cette page utilise des cadres, mais votre navigateur ne les prend pas en charge.
			</p>
		</body>
	</noframes>
</frameset>
</HTML>
