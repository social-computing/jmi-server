<%@page import="java.net.URLEncoder" %>

<%!// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}
%>
<%
String group = request.getParameter( "group");
if( group == null) group = "EU";
%>
<HTML>
<HEAD>
<link rel="stylesheet" href="./wps.css">
<SCRIPT LANGUAGE="JavaScript" >
	function groupChanged()
	{
		var	doc = parent._appletFrame.document;
		if( doc.applets == null) return null;
		
		var applet = doc.applets["WPSApplet"];
		if( applet == null) return null;
		if( !applet.isReady()) return;
		
		document.Tools.accord.selectedIndex = 0;
		if( document.Tools.group.selectedIndex == 0)
			ClearSelection( applet);
		else
			ShowOnPlan( applet, document.Tools.group[ document.Tools.group.selectedIndex].value, '');
	}
	function ShowOnPlan( applet, ida, idl)
	{
		if( applet == null) return;
		applet.clearAttSelection( "search" );
		applet.clearLinkSelection( "search" );
		applet.setAttSelection( ida, "search");
		applet.setLinkSelection( idl, "search");
		applet.setSelection( "search" );
	}
	function ClearSelection( applet)
	{
		if( applet == null) return;
		applet.clearLinkSelection( "search" );
		applet.clearAttSelection( "search" );
		applet.setSelection( "search" );
	}
	function accordChanged()
	{
		var	doc = parent._appletFrame.document;
		if( doc.applets == null) return null;
		
		var applet = doc.applets["WPSApplet"];
		if( applet == null) return null;
		if( !applet.isReady()) return;
		
		document.Tools.group.selectedIndex = 0;
		if( document.Tools.accord.selectedIndex == 0)
			ClearSelection( applet);
		else
		{
			var id = document.Tools.accord[ document.Tools.accord.selectedIndex].text;
			var n = applet.getLinkCount();
			var linkIds = '';
			for (var i = 0; i < n; i++)
			{ 
				var props = applet.getLinkProp( i, "REC_NAME");
				if( props != null)
				{
					props = props + '';
					var names = props.split( '\n');
					for( var j = 0; j < names.length; ++j)
					{			
						if( names[ j] == id)
						{
							linkIds += (i + ',');
							break;
						}
					}
				}
			}
			ShowOnPlan( applet, document.Tools.accord[ document.Tools.accord.selectedIndex].value, linkIds);
		}
	}
</SCRIPT>
</HEAD>
<BODY bgcolor="30659b" topmargin="4" leftmargin="0" marginheight="4" marginwidth="0" >
<form name="Tools" onSubmit="return findChanged()"> 
<table border="0" cellpadding="0" cellspacing="0" width="100%">				
 <tr>
   <td align="left" valign="middle" nowrap>
   <%
   String name = request.getParameter( "name");
   if( name == null) name ="All";%>
   <h1>&nbsp;<%=name%></h1>
   </td>
   <td align="left" valign="top" nowrap>
		<SELECT NAME="accord" style="font-family:Arial;font-size:8pt" onChange="accordChanged()">
			<OPTION VALUE="-1">---------- choose organization ----------</OPTION>
		</SELECT>
   </td>
   <td align="left" valign="top" nowrap>
		<SELECT NAME="group" style="font-family:Arial;font-size:8pt" onChange="groupChanged()">
			<OPTION VALUE="-1">---------- choose country ----------</OPTION>
		</SELECT>
   </td>
 </tr>
</table>
</form>
</BODY>
</HTML>
