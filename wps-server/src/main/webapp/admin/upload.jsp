<html>
<head> 
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
</head>
<body bgcolor="ffffff">

<br><br>

<% if( request.getParameter( "openresults") != null && session.getValue( "UploadDefinitionFileResults") != null)
{%>	
	<script language="javascript">
		var win = window.open( 'upload_results.jsp', 'mpstadminresults', 'width=600,height=600,scrollbars=yes,resizable=yes,dependent=yes');
		win.focus();
	</script>
<%}%>	
<form name="test" enctype="multipart/form-data" method="POST" action="upload">
	<input type="hidden" name="action" value="uploadSearchFile" />
	<input type="hidden" name="redirect" value="./upload.jsp?openresults=1" />
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >
	<tr>
	<td><span class="subTitleBlue">Load a defintion file (*.xml, *.zip) : </span></td> 
	<td><input type="file" name="definitionFile" size="50" ></td>
	</tr>
	<tr>
	<td />
	<td><input type="submit" value="Load" /></td>
	</tr>
	</table>
</form>
</body>
</html>

