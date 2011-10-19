<%response.setContentType("text/javascript");%>
<jsp:include page="./client/swfobject.js" /> 
var d = new Date();
var embedid = "embed" + d.getTime();
var messageid = "message" + d.getTime();
var mapid = "map" + d.getTime();
function display( message, error) {
<% String m=request.getParameter("m");
if( m != null && m.length() > 0) {%>
if( document.getElementById('<%=m%>'))
 document.getElementById('<%=m%>').innerHTML = message;
else
 if( error) alert( message);
<%} else { %>
 if( error) alert( message);
<%}%>
}
function empty() {
 display( "Sorry, the map is empty. Does the feed contains categories ?", true);
}
function error( error) {
 display( "Sorry, an error occured. Is this URL correct?", true);
}
function Navigate( url) {
	window.open( url, "_blank");
}
function NewWin( args)
{
	var parameters = {};
	parameters["entityId"] = args[0];
	parameters["feed"] = args[2];
	document.getElementById(mapid).compute( parameters);
	display( "<i>Focus on category:</i> " + args[1], false);
}
 function Discover( args)
 {
	var parameters = {};
	parameters["attributeId"] = args[0];
	parameters["analysisProfile"] = "DiscoveryProfile";
	parameters["feed"] = args[2];
 	document.getElementById(mapid).compute( parameters);
	display( "<i>Centered on item:</i> " + args[1], false);
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
<%} %>