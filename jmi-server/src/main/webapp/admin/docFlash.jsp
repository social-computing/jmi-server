<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<title>Just Map It! Administration</title>
	<link rel="stylesheet" href="../css/main.css"/>
	<link rel="stylesheet" href="../css/wps.css"/>
</head>
<body>
<div id="top"><jsp:include page="top.jsp" /></div>
<div id="menu"><jsp:include page="menu.jsp" /></div>
<div id="content" class="documentation">
<span class="subTitleBlue" >Just Map It! Flash client</span><br/><br/>
For object and embed tag or for any other method (such as the <a href="http://code.google.com/p/swfobject/" target="_blank">swfobject</a>)
<table width="100%" class="tableau" >
<tr><td nowrap>&lt;param name="movie"</td><td>value=".../jmi-flex-1.0-SNAPSHOT.swf" /&gt;</td></tr>
<tr><td nowrap>&lt;param name="flashvars"</td><td>value="<a href="#jmiparameters" >Just Map It! parameters</a>" /&gt;</td></tr>
<tr><td nowrap>&lt;param name="bgcolor"</td><td>value="initial background color" /&gt;</td></tr>
<tr><td nowrap>&lt;param name="allowscriptaccess"</td><td>value="always" /&gt;</td></tr>
<tr><td nowrap>&lt;param name="..."</td><td>value="..." /&gt;</td></tr>
</table>
<br/>
<a name="jmiparameters" ></a><span class="subTitleBlue" >Just Map It! parameters (refer to dictionary DTD)</span>
<table width="100%" class="tableau" >
<tr>
	<td width="30%" >allowDomain</td>
	<td>Just Map It! Server domain or * in order to bypass flash security sandbox</td>
</tr>
<tr>
	<td>wpsserverurl *</td>
	<td>Just Map It! Server URL</td>
</tr>
<tr>
	<td>wpsplanname *</td>
	<td>dictionary name</td>
</tr>
<tr>
	<td>waiterurl</td>
	<td>Image or swf played during map computation</td>
</tr>
<tr>
	<td>entityId</td>
	<td>id of an entity; mandatory for a personal plan</td>
</tr>
<tr>
	<td>attributeId</td>
	<td>id of an attribute; mandatory for a discovery plan</td>
</tr>
<tr>
	<td>analysisProfile</td>
	<td>Name of an analysis profile</td>
</tr>
<tr>
	<td>affinityReaderProfile</td>
	<td>Name of an affinity reader profile</td>
</tr>
<tr>
	<td>displayProfile</td>
	<td>Name of a display profile</td>
</tr>
<tr>
	<td>language</td>
	<td>Set language for language segmentation</td>
</tr>
<tr>
	<td>readyCallback</td>
	<td>javascript callback function; default : ready()</td>
</tr>
<tr>
	<td>emptyCallback</td>
	<td>javascript callback function; default : empty()</td>
</tr>
<tr>
	<td>errorCallback</td>
	<td>javascript callback function; default : error( errorMsg)</td>
</tr>
<tr>
	<td>statusCallback</td>
	<td>javascript callback function; default : status( statusMsg)</td>
</tr>
<tr>
	<td>navigateCallback</td>
	<td>javascript callback function; default : navigate( url, target)</td>
</tr>
<tr><td colspan=2>Any other parameters are forwarded to Just Map It! Server as global parameters.</td></tr>
<tr><td colspan=2>(*) mandatory</td></tr>
</table>
<br/>
<span class="subTitleBlue"> javascript API</span>
<table width="100%" class="tableau" >
<tr>
	<td width="30%" >void compute( parameters)</td>
	<td>compute a new map</td>
</tr>
<tr>
	<td width="30%" >String getProperty( name)</td>
	<td>returns a global property</td>
</tr>
<tr>
	<td width="30%" >void uploadAsImage( url, name, 'image/png', width, height, true, extraParameters)</td>
	<td>Post the map as an image to a server. Image is streched to the new size, but if keepProportions is true the image is centered on the axis clipped</td>
</tr>
</table>
</div>
</body>
</html>
 
 
