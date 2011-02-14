<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>

<html>
<head>
<link rel="stylesheet" href="./wps.css">
<base target="main">
</head>
<body bgcolor=7f9fdf>
<table width="100%" border=0>
<tr><td colspan=2><span class="subTitleBlue" >Dictionary DTD</span></td></tr>
<tr><td><a href="dtd/WPS-dictionary.dtd" target="_blank">dictionary</a></td><td>main definition</td></tr>
<tr><td><a href="dtd/JDBC-connector.dtd" target="_blank">JDBC dictionary connector</a></td><td>SGBD data, access thru JDBC</td></tr>
<tr><td><a href="dtd/JAVA-connector.dtd" target="_blank">JAVA dictionary connector</a></td><td>data in java classes or loaded from an xml file into java classes</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td colspan=2><span class="subTitleBlue" >Swatch DTD</span></td></tr>
<tr><td><a href="dtd/swatch.dtd" target="_blank">swatch</a></td><td>main definition</td></tr>
</table>
</body>
</html>
 
 
