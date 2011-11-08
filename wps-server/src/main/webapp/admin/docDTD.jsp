<%!// return current time to proxy server request
    public long getLastModified(HttpServletRequest request) {
        return System.currentTimeMillis();
    }%>

<html>
<head>
<title>Just Map It! Administration</title>
<meta http-equiv="content-language" content="en">
<link rel="stylesheet" href="../css/main.css" />
<link rel="stylesheet" href="../css/wps.css">
</head>
<body>
<body>
<div id="top"><jsp:include page="top.jsp" /></div>
	<div id="menu"><jsp:include page="menu.jsp" /></div>
	<div id="content">

<table width="100%" border=0>
	<tr>
		<td colspan=2><span class="subTitleBlue">Dictionary DTD</span></td>
	</tr>
	<tr>
		<td><a href="../dtd/WPS-dictionary.dtd" target="_blank">dictionary</a></td>
		<td>main definition</td>
	</tr>
	<tr>
		<td><a href="../dtd/JDBC-connector.dtd" target="_blank">JDBC connector</a></td>
		<td>SGBD data</td>
	</tr>
	<tr>
		<td><a href="../dtd/REST-connector.dtd" target="_blank">REST connector</a></td>
		<td>Xml/Json data</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td colspan=2><span class="subTitleBlue">Swatch DTD</span></td>
	</tr>
	<tr>
		<td><a href="../dtd/swatch.dtd" target="_blank">swatch</a></td>
		<td>main definition</td>
	</tr>
</table>
</div>
</body>


</html>


