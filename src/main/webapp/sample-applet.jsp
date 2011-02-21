<%@ page import="java.util.*, java.sql.*, javax.sql.*, javax.naming.*, java.rmi.*, java.io.*" %>
<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}%>
<%@ include file="applet/AppletVersion.jsp" %>
<HTML>
<HEAD>
<link rel="stylesheet" href="./wps.css">
<script language="JavaScript" src="./applet/jquery.js" ></script>
<script language="JavaScript" src="./applet/jquery.wpsmap.js" ></script>
<script language="JavaScript" >
	function onAppletReady()
	{
		var	doc = parent._appletFrame.document;
		if( doc.applets == null) return null;
		
		var applet = doc.applets["WPSApplet"];
		if( applet == null) return null;
		if( !applet.isReady()) return;
		
		var sep = "$$;.,:$$";
		doc = parent._toolsFrame.document;
		var n = applet.getAttCount();
		var nameLst = new Array( n);
		for (var i = 0; i < n; i++)
		{ 
			nameLst[i] = applet.getAttProp( i, "NAME") + sep + i;//applet.getAttProp( i, "ID");
		}
		nameLst.sort();
		for (var i = 0; i < n; i++)
		{ 
			var pos = nameLst[i].lastIndexOf( sep);
			doc.Tools.group.options[i+1] = new Option( nameLst[i].substring( 0, pos), nameLst[i].substring( pos + sep.length), false, false);
		}
		
		n = applet.getAttCount();
		var max = 0;
		nameLst = new Array( 30);
		idLst = new Array( 30);
		for (var i = 0; i < n; i++)
		{ 
			var props = applet.getAttProp( i, "POSS_NAME");
			if( props != null)
			{
				props = props + ''; // String transformation
				var names = props.split( '\n');
				for( var j = 0; j < names.length; ++j)
				{			
					var found = false;
					for (var k = 0; k < max && !found; k++)
					{
						if( nameLst[k] == names[j])
						{
							found = true;
							idLst[k] += ',' + i;
						}
					}
					if( !found)
					{
						//alert( props + '\n ' + j + '/' + props.length + ' ' + names[j]);
						nameLst[max] = names[j];
						idLst[max++] = names[j] + sep + i;
					}
				}
			}
		}
		idLst.sort();
		for (var i = 0; i < max; i++)
		{ 
			var pos = idLst[i].lastIndexOf( sep);
			doc.Tools.accord.options[i+1] = new Option( idLst[i].substring( 0, pos), idLst[i].substring( pos + sep.length), false, false);
		}
	}
	function NewWin( newgroup, name)
	{
		parent.location = "./sample.jsp?group=" + escape( newgroup) + "&name=" + escape( name);
	}
	function Discover( id, name)
	{
		parent.location = "./sample.jsp?discover=" + id + "&name=" + escape( name);
	}
</SCRIPT>
</HEAD>
<BODY bgcolor=7f9fdf topmargin=0 leftmargin=0 marginheight=0 marginwidth=0>
<%
boolean error = request.getParameter("error") != null;
if( !error)
{
	StringBuffer appletParams = new StringBuffer();
	
	// Mysql based dictionary by default  
	appletParams.append("{planName:'sample'");
	
	// Uncomment to switch to Solr based dictionary
	//appletParams.append("{planName:'Solr_sample'");
	
	// Uncomment to switch to XML based dictionary
	//appletParams.append("{planName:'Xml_sample'");
	
	String group = request.getParameter("group");
	if( group != null)
	{
		appletParams.append(",entityId:'").append( group).append( "'");
	}
	else
	{
		String discover = request.getParameter( "discover");
		if( discover != null)
		{
			appletParams.append( ",analysisProfile:'DiscoveryProfile',attributeId:'").append( discover).append( "'");
		}
		else
		{		
			appletParams.append( ",analysisProfile:'GlobalProfile'");
		}
	}
	appletParams.append( "}");
	%>	
	<script type="text/javascript"> 
		$(document).ready(function(){
			$("#map").wpsmap( {wps: <%=appletParams.toString()%>, display: {color:'336699'}});
		});
	</script>
	<div id="map" width="100%" height="100%"></div>
<%}
else {	%>
	<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
		<%if( request.getParameter( "error").equalsIgnoreCase( "nodata"))
		{%>
			<tr>
				<td align="center" valign="middle" height="100">					
					<span class="texblanc">Le plan est vide</span>
				</td>
			</tr>
		<%}
		else
		{%>
			<tr>
				<td align="center" valign="middle" height="100">					
					<span class="texblanc">
						<b>MapStan ne peut pas afficher votre plan en raison d'un problème technique.<br></b>
					</span>
				</td>
			</tr>
			<tr>
				<td>
					<p align="justify">		
					<span class="texblanc">
					<%
					String	version	= request.getParameter( "version" ),
							header	= request.getParameter( "header" ),
							stack	= request.getParameter( "stack" ),
							javaCls	= request.getParameter( "java" ),
							agent	= request.getHeader( "User-Agent"),
							subject	= "plan creation failure warning";
							
					if( version == null) version = "";
					if( header == null) header = "";
					if( stack == null) stack = "";
					if( javaCls == null) javaCls = "";
					if( agent == null) agent = "";
					
					if ( !version.equals( APPLET_VERSION ))
					{
						subject	= "wrong version";
						%>
							Le cache de votre navigateur contient une ancienne version de l'applet.<br>
							<ul>
							<li>Si vous avez Internet Explorer, appuyez sur CTRL + F5 ou CTRL + bouton 'rafraîchir' ou relancez IE.<br>
								Vérifiez également que la mise à jour de votre cache n'est pas réglée sur 'jamais'.</li>
							<li>Sinon videz le cache de votre navigateur.</li>
							</ul>
					<%}
					else if ( request.getParameter( "pb" ) != null && request.getParameter( "pb" ).startsWith( "offscreenInit" ))
					{
						subject	= "bitmap alloc";
						%>
							Votre navigateur ne peut plus allouer de mémoire graphique.<br>
							Pour en libérer vous fermez toutes les fenêtres de votre navigateur puis relancez le.<br>
					<%}
					else if ( agent.indexOf( "AOL" )!= -1 && stack.indexOf( "java.io.IOException" )!= -1 )
					{
						subject	= "io AOL";
						%>
							Les transferts de données ne fonctionnent pas en Java lorsque le navigateur AOL compresse les images.<br>
							Pour y remédier, veuillez décocher l'option 'graphiques compressés' en allant dans :<br> 
							MyAOL -> WWW -> Graphiques web. Refermez la boite de préférences puis rafraîchissez la page
							à l'aide du boutton 'rafraîchir'. Déloguez vous et quittez le navigateur AOL.
							Enfin redémarrez votre navigateur et retournez à cette page.<br>
					<%}
					else if ( header.indexOf( "403" )!= -1 || header.indexOf( "404" )!= -1 ||( header.indexOf( "null" )!= -1 && stack.indexOf( "java.io.IOException" )!= -1 ))
					{
						subject	= "io generic";
						%>
							La communication avec notre serveur a été rompue, veuillez réessayer ultérieurement.<br>
					<%}
					else if ( stack.indexOf( "Unable to check hostname" )!= -1 )
					{
						subject	= "proxy IE";
						%>
							L'applet ne peut charger le plan car vous utilisez un proxy.<br>
							Nous n'avons pour l'instant pas de solution pour palier à ce problème.
							Renseignez vous auprès de votre administrateur réseau pour savoir s'il peut authoriser l'accès à "societe.mapstan.com".<br>
					<%}
					else if (( javaCls.indexOf( "46" )!= -1 || javaCls.indexOf( "47" )!= -1 || javaCls.indexOf( "48" )!= -1 )&&( header.indexOf( "407" )!= -1  || stack.indexOf( "access denied" )!= -1 ))
					{
						subject	= "proxy Sun";
						%>
							Pour utiliser un proxy avec authentification et le Java Plugin de Sun vous devez le paramétrer.<br>
							Ouvrez le Java Plugin (il se trouve dans le panneau de configuration) et cliquez l'onglet 'proxy'.
							Demandez à votre administrateur réseau l'adresse IP et le numéro de port du proxy.
							Lorsque vous utiliserez cette Applet, une boite de dialogue vous demandera alors d'entrer 
							votre nom d'utilisateur et votre mot de passe.<br>
					<%}
					else if ( stack.indexOf( "algorithm SHA not available" )!= -1 )
					{
						subject	= "class SHA";
						%>
							Il manque à votre machine virtuelle Java (JVM) certaines classes nécessaires au bon fonctionnement de notre Applet.<br>
							Pour y remédier réinstallez la JVM d'Internet Explorer ou installez le Java Plugin de Sun.
							Pour plus d'informations visitez <a href="http://java-virtual-machine.net/download.html">java-virtual-machine.net</a>.<br>
					<%}%>
					</span>
					</p>
				</td>
			</tr>
			<%}%>
		</table>
		<%}%>
	</BODY>
</HTML>
