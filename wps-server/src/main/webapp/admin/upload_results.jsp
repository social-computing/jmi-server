<%@ page import="com.socialcomputing.wps.server.web.InternalReport" %>
<html>
<head>
<link rel="stylesheet" href="../css/wps.css">
</head>
<body bgcolor="ffffff">
 <table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" >
 <tr><td>
 <%
	int curLevel = 0;
	InternalReport report = (InternalReport) session.getValue("UploadDefinitionFileResults"); 
	for( int i = 0; i < report.size(); ++i)
	{
		InternalReport.Action action = report.get( i);
		for( int j = action.level; j < curLevel; ++j)
			out.print( "</ul>");
		if( action.delimiter)
			out.print( "<HR NOSHADE ALIGN='CENTER' WIDTH='50%' SIZE='1'>");
		else
		{
			for( int j = curLevel; j < action.level; ++j)
				out.print( "<ul type='disc'>");
			out.print( "<li><span class='blueText'>");
			out.print( action.action);
			out.print( "</span>");
 			if( action.result != null)
			{
				out.print( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class='subTitleBlue'>");
				out.print( action.result);
				out.println( "</span>");
			}
		}
		curLevel = action.level;
	}
 %>
 </td></tr>
 <tr><td>&nbsp;</td></tr>
 <tr>
  <td align="center" ><input type="button" value="Fermer" onclick="javascript: return window.close()"/></td>
 </tr>
 </table>
</body>
</html>

