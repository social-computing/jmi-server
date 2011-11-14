<%!// return current time to proxy server request
    public long getLastModified(HttpServletRequest request) {
        return System.currentTimeMillis();
}%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
<title>Just Map It! Administration</title>
<meta http-equiv="content-language" content="en" />
<link rel="stylesheet" href="../css/main.css" />
<link rel="stylesheet" href="../css/wps.css" />
<script type="text/javascript" src="../client/applet/jquery.js" ></script>
<script type="text/javascript" src="./fancybox/jquery.fancybox-1.3.4.pack.js"></script>
<link rel="stylesheet" href="./fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
</head>
<body>
<script>
$(document).ready(function() {
    /* This is basic - uses default settings */
    $("a#single_image").fancybox();
        
    /* Using custom settings */
    $("a#inline").fancybox({
        'hideOnContentClick': true
    });

    /* Apply fancybox to multiple items */
    parent.$.fancybox.close();
    $("a.iframe").fancybox({
        'width'                : '75%',
        'height'            : '75%',
        'autoScale'         : false,
        'transitionIn'        : 'none',
        'transitionOut'        : 'none',
        'type'                : 'iframe'
    });
});
</script>
<div id="top"><jsp:include page="top.jsp" /></div>
	<div id="menu"><jsp:include page="menu.jsp" /></div>
	<div id="content">
<table width="100%" border=0>
	<tr>
		<td colspan=2><span class="subTitleBlue">Dictionary DTD</span></td>
	</tr>
	<tr>
		<td><a class="iframe" title="View DTD" href="view_DTD.jsp?dtd=<%=java.net.URLEncoder.encode( "dico","UTF-8")%>">dictionary</a></td>
		<td>main definition</td>
	</tr>
	<tr>
		<td><a class="iframe" title="View DTD" href="view_DTD.jsp?dtd=<%=java.net.URLEncoder.encode( "jdbc","UTF-8")%>">JDBC connector</a></td>
		<td>SGBD data</td>
	</tr>
	<tr>
		<td><a class="iframe" title="View DTD" href="view_DTD.jsp?dtd=<%=java.net.URLEncoder.encode( "rest","UTF-8")%>">REST connector</a></td>
		<td>Xml/Json data</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan=2><span class="subTitleBlue">Swatch DTD</span></td>
	</tr>
	<tr>
		<td><a class="iframe" title="View DTD" href="view_DTD.jsp?dtd=<%=java.net.URLEncoder.encode( "swatch","UTF-8")%>">swatch</a></td>
		<td>main definition</td>
	</tr>
</table>
</div>
</body>
</html>


