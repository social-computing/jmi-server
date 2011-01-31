<%@ page import="com.socialcomputing.wps.server.webservices.maker.PlanMaker" %>
<%@ page import="com.socialcomputing.wps.server.webservices.maker.BeanPlanMaker" %>
<%@ page import="java.util.*, java.sql.*, javax.sql.*, javax.naming.*, java.rmi.*, java.io.*" %>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}%>
<%@ include file="./applet/AppletVersion.jsp" %>

<%
PlanMaker planmaker = new BeanPlanMaker();
%>

<HTML>
<HEAD>
<title>View <%=request.getParameter( "dictionary")%> plan</title>
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
</HEAD>
<BODY bgcolor=7f9fdf topmargin=0 leftmargin=0 marginheight=0 marginwidth=0>
<%
boolean error = request.getParameter( "error") != null;
if( error)
{
	java.util.Date date = new java.util.Date();%>
	<h1>&nbsp;ERROR : <%=request.getParameter( "error")%></h1>
	 <table width="100%" border=0><tr><td>
	 <table width="100%" border=1>
		<tr><td width="20%"><span class="texblanc">Date</span></td><td><span class="texblanc"><%=date.toString()%></span></td></tr>
		<tr><td width="20%"><span class="texblanc">Remote host</span></td><td><span class="texblanc"><%=request.getRemoteHost()%></span></td></tr>
		<tr><td width="20%"><span class="texblanc">Local  host</span></td><td><span class="texblanc"><%=java.net.InetAddress.getLocalHost().getHostName()%></span></td></tr>
		<tr><td width="20%"><span class="texblanc">User Agent</span></td><td><span class="texblanc"><%=request.getHeader( "User-Agent")%></span></td></tr>
		<tr><td width="20%"><span class="texblanc">QueryString</span></td><td><span class="texblanc"><%=request.getQueryString()%></span></td></tr>
	</table>
	</td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td>
    <table width="100%" border=1>
		<%java.util.Enumeration params = request.getParameterNames();
		while( params.hasMoreElements())
		{
			String name = (String)params.nextElement();
			String value = request.getParameter( name);
			if( value.length() == 0) value="&nbsp;"; %>
			<tr><td valign="top" width="20%"><span class="texblanc"><%=name%></span></td><td><span class="texblanc"><%=value%></span></td></tr>
		<%}%>
 	</table>
 	</td></tr></table>
<%}
else if( request.getParameter( "internal")!= null && request.getParameter( "internal").equals( "y"))
{	
	Hashtable params = new Hashtable();
	params.put( "planName", request.getParameter( "dictionary"));
	java.util.StringTokenizer st = new java.util.StringTokenizer( request.getParameter( "appletparams"), "&");
	while( st.hasMoreTokens())
	{
		String val = st.nextToken();
		int pos = val.indexOf( "=");
		if( pos != -1)
			params.put( val.substring( 0, pos), val.substring( pos+1));
	}
	
    %><table width="100%" border=1>
    <tr><td colspan=2><span class="texblanc"><b>IN parameters</b></span></tr>
    <%for (Iterator it = params.entrySet().iterator() ; it.hasNext() ;) 
	{
		Map.Entry entry = ( Map.Entry)it.next();
		String name = ( String)entry.getKey();
		String value = ( String)entry.getValue();
		if( value.length() == 0) value = "&nbsp;"; %>
		<tr><td valign="top" width="20%"><span class="texblanc"><%=name%></span></td><td><span class="texblanc"><%=value%></span></td></tr>
	<%}
	%></table><br><br><%
	Hashtable results = null;
	try {
		results = planmaker.createPlan( params);
	}
	catch( Exception e)
	{%>
		<span class="texblanc"><b>ERROR : </b><%=e.getMessage()%></span>
	<%}
	if( results != null)
	{%>
		<table width="100%" border=1>
	    <tr><td colspan=2><span class="texblanc"><b>OUT results</b></span></tr>
	    <%for (Iterator it = results.entrySet().iterator() ; it.hasNext() ;) 
		{
			Map.Entry entry = ( Map.Entry)it.next();%>
			<tr><td valign="top" width="20%"><span class="texblanc"><%=entry.getKey()%></span></td>
			<td>
				<span class="texblanc"><%=entry.getValue()%>
				<%if( entry.getKey().equals( "PLAN"))
				{
					byte[] bplan = ( byte[])entry.getValue();
					out.print( " (" + bplan.length + " bytes)");
				}%>
				</span>
			</td></tr>
	<%}}%>
	</table><%
}
else
{	// APPLET
	StringBuffer appletParams = new StringBuffer();
	appletParams.append( "planName=");
	appletParams.append( request.getParameter( "dictionary"));
	appletParams.append( "&");
	appletParams.append( request.getParameter( "appletparams"));
	%>	
	<APPLET name="WPSApplet" archive="WPSApplet<%=APPLET_VERSION%>.jar" code="com.socialcomputing.wps.client.applet.WPSApplet.class" codebase="./applet/" MAYSCRIPT="" align="absmiddle" hspace="0" vspace="0" width="100%" height="100%">
		<PARAM NAME="WPSParameters"		VALUE="<%=appletParams.toString()%>" />
		<PARAM NAME="ServletUrl"		VALUE="../maker" />
		<PARAM NAME="VoidPlanUrl"    	VALUE="../view-applet.jsp?error=nodata&<%=appletParams.toString()%>" />
		<PARAM NAME="NoScriptUrl"     	VALUE="../view-applet.jsp?error=noscript" />
		<PARAM NAME="ErrorPlanUrl"    	VALUE="../view-applet.jsp?error=internal&<%=appletParams.toString()%>" />
		<PARAM NAME="ComputeMsg"      	VALUE="Calcul du plan..." />
		<PARAM NAME="DownloadMsg"    	VALUE="Chargement du plan..." />
		<PARAM NAME="InitColor"			VALUE="7f9fdf" />
		<PARAM NAME="HTTPHeaderName0" 	VALUE="COOKIE" />
		<PARAM NAME="HTTPHeaderSetValue0" VALUE="JSESSIONID=<%=session.getId()%>" />			
		<p align="center"><span class="texblanc"><br><br>
			Votre navigateur ne permet pas l'affichage d'applets java.<br><br>
		</span></p>
	</APPLET>
<%}%>
</BODY>
</HTML>
