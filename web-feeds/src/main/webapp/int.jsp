<%response.setContentType("text/javascript");%>
<jsp:include page="./client/swfobject.js" /> 
var d = new Date();
var mapid = "map" + d.getTime();
function getMap() {
	 if (navigator.appName.indexOf ("Microsoft") !=-1) {
	  return window[ mapid];
	 } else {
	  return document[ mapid];
	 }
}
function empty() {
 	alert( "Sorry, map is empty");
}
function error( error) {
 	alert( error);
}
function Navigate( url) {
	window.open( url, "_blank");
}
function NewWin( args)
{
	var parameters = {};
	parameters["entityId"] = args[0];
	parameters["feed"] = args[2];
	getMap().compute( parameters);
}
 function Discover( args)
 {
	var parameters = {};
	parameters["attributeId"] = args[0];
	parameters["analysisProfile"] = "DiscoveryProfile";
	parameters["feed"] = args[2];
 	getMap().compute( parameters);
 }
<%if(request.getParameter("url") != null && request.getParameter("url").length()>0){ %>
var flashvars = {};
flashvars.allowDomain = "*";
flashvars.wpsserverurl = "http://map.social-computing.com/";
flashvars.wpsplanname = "Feeds";
flashvars.analysisProfile = "GlobalProfile";
flashvars.feed = "<%=java.net.URLEncoder.encode(request.getParameter("url")) %>";
var params = {};
params.quality = "high";
params.bgcolor = "#FFFFFF";
params.allowscriptaccess = "always";
params.allowfullscreen = "true";
swfobject.embedSWF(
    "http://www.mapyourfeeds.com/client/wps-flex-1.0-SNAPSHOT.swf", mapid, 
    "<%=request.getParameter("w")%>", "<%=request.getParameter("h")%>", 
    "10.0.0", "http://www.mapyourfeeds.com/client/playerProductInstall.swf", 
    flashvars, params);
document.write( "<div id='" + mapid + "'>");
var pageHost = ((document.location.protocol == "https:") ? "https://" :	"http://"); 
document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
				+ pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
document.write( "</div>");
<%} %>