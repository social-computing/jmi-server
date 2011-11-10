<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%><html>
<head>
	<title>Just Map It! Administration</title>
	<link rel="stylesheet" href="../css/main.css"/>
	<link rel="stylesheet" href="../css/wps.css">
	<script>
		function Auth()
		{
			var win = window.open( 'Base64Credential.jsp', 'base64', 'width=700,height=200,scrollbars=yes,resizable=yes,dependent=yes');
			win.focus();
			return false;
		}
	</script>
</head>
<body>
<div id="top"><jsp:include page="top.jsp" /></div>
<div id="menu"><jsp:include page="menu.jsp" /></div>
<div id="content">
<span class="subTitleBlue" >Just Map It! Flash template</span>
<table width="100%" border=0>
<tr><td>&lt;object type="application/x-shockwave-flash" data=".../wps-flex-1.0-SNAPSHOT.swf" width="100%" height="100%"&gt;</td></tr>
<tr><td>
	<table width="90%" align="center" class="tableau" >
	<tr><td nowrap>&lt;param name="quality"</td><td>value="high" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="bgcolor"</td><td>value="initial background color" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="allowscriptaccess"</td><td>value="always" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="allowDomain"</td><td>value="*" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="allowfullscreen"</td><td>value="true" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="wmode"</td><td>value="opaque" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="flashvars"</td><td>value="<a href="#jmiparameters" >Just Map It! parameters</a>" /&gt;</td></tr>
	<tr><td nowrap>&lt;param name="movie"</td><td>value=".../wps-flex-1.0-SNAPSHOT.swf" /&gt;</td></tr>
	</table>
</td></tr>
<tr><td>&lt;/object&gt;</tr>
</table>
<br>
<a name="jmiparameters" ></a><span class="subTitleBlue" >Just Map It! parameters (refer to WPS-dictionary.dtd)</span>
<table width="100%" class="tableau" >
<tr>
	<td width="30%" >wpsserverurl</td>
	<td>Just Map It! Server URL</td>
</tr>
<tr>
	<td width="30%" >wpsplanname</td>
	<td>dictionary name</td>
</tr>
<tr>
	<td>entityId (*)</td>
	<td>id of an entity; mandatory for a personal plan</td>
</tr>
<tr>
	<td>attributeId (*)</td>
	<td>id of an attribute; mandatory for a discovery plan</td>
</tr>
<tr>
	<td>analysisProfile (*)</td>
	<td>Name of an analysis profile</td>
</tr>
<tr>
	<td>affinityReaderProfile (*)</td>
	<td>Name of an affinity reader profile</td>
</tr>
<tr>
	<td>displayProfile (*)</td>
	<td>Name of a display profile</td>
</tr>
<tr>
	<td>language (*)</td>
	<td>Set language for language segmentation</td>
</tr>
<tr>
	<td>readyCallback</td>
	<td>javascript callback function. Default : ready()</td>
</tr>
<tr>
	<td>emptyCallback</td>
	<td>javascript callback function. Default : empty()</td>
</tr>
<tr>
	<td>errorCallback</td>
	<td>javascript callback function. Default : error( errorMsg)</td>
</tr>
<tr>
	<td>statusCallback</td>
	<td>javascript callback function. Default : status( statusMsg)</td>
</tr>
<tr>
	<td>navigateCallback</td>
	<td>javascript callback function. Default : navigate( url, target)</td>
</tr>
<tr><td colspan=2>Any other parameters are forwarded to Just Map It! Server as global parameters.</td></tr>
<tr><td colspan=2>(*) optional</td></tr>
</table>
<br/>
<span class="subTitleBlue"> javascript API</span>
<table width="100%" class="tableau" >
<!-- tr>
	<td width="30%" >boolean isReady()</td>
	<td>true if a plan is initialized</td>
</tr-->
</table>
</div>
</body>
</html>
 
 
