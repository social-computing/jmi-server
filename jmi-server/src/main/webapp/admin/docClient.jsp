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
<span class="subTitleBlue" >Just Map It! Html5/Flash client</span><br/>
	....<br/>
	var client = JMI.Map({<br/>
				parent: 'mapId', <br/>
				swf: '/jmi-client/jmi-flex-1.0-SNAPSHOT.swf', <br/>
				parameters: parameters<br/>
			});<br/>
	....<br/>
	&lt;/head&gt;&lt;body&gt;
	....<br/>
	&lt;div id="mapId" style="border:1px solid black ;  width: 640px; height: 480px" &gt;&lt;/div&gt;

<table width="100%" class="tableau" >
<tr><td nowrap>parent *</td><td>parent id or node</td><td></td><td></td></tr>
<tr><td nowrap>server</td><td>JMI server</td><td>'http://server.just-map-it.com/'</td><td></td></tr>
<tr><td nowrap>swf *</td><td>path to JMI swf client</td><td></td><td>Flash</td></tr>
<tr><td nowrap>client</td><td>none for auto-detect, JMI.Map.SWF or JMI.Map.CANVAS ito force one of them</td><td></td><td></td></tr>
<tr><td nowrap>touchMenuDelay</td><td>menu delay for touch screen (in ms)</td><td>1000</td><td>Html5</td></tr>
<tr><td nowrap>backgroundColor</td><td>initial background color</td><td>parent background color</td><td></td></tr>
<tr><td nowrap>parameters</td><td><a href="#jmiparameters" >Just Map It! parameters</a></td><td></td></tr>
</table>
<br/>
<a name="jmiparameters" ></a><span class="subTitleBlue" >Just Map It! parameters (refer to dictionary DTD)</span>
<table width="100%" class="tableau" >
<tr>
	<td>map *</td>
	<td>dictionary name</td>
	<td></td>
</tr>
<tr>
	<td>entityId</td>
	<td>id of an entity; mandatory for a personal plan</td>
	<td></td>
</tr>
<tr>
	<td>attributeId</td>
	<td>id of an attribute; mandatory for a discovery plan</td>
	<td></td>
</tr>
<tr>
	<td>analysisProfile</td>
	<td>Name of an analysis profile</td>
	<td></td>
</tr>
<tr>
	<td>affinityReaderProfile</td>
	<td>Name of an affinity reader profile</td>
	<td></td>
</tr>
<tr>
	<td>displayProfile</td>
	<td>Name of a display profile</td>
	<td></td>
</tr>
<tr>
	<td>language</td>
	<td>Set language for language segmentation</td>
	<td></td>
</tr>
<tr>
	<td width="30%" >allowDomain</td>
	<td>JMI Server domain (default * in order to bypass flash security sandbox)</td>
	<td>Flash</td>
</tr>
<tr><td colspan=3>Any other parameters are forwarded to Just Map It! Server as global parameters.</td></tr>
<tr><td colspan=3>(*) mandatory</td></tr>
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
 
 
