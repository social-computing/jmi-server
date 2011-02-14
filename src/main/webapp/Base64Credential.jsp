<%@page import="sun.misc.BASE64Encoder"%>
<html>
<head>
<title>Basic authentication</title>
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
</head>
<body>
<body bgcolor=7f9fdf>
<%
String login = request.getParameter( "login");
if( login == null) login = "";
String password = request.getParameter( "password");
if( password == null) password = "";
String encoded = null;
if( login.length()>0 || password.length()>0) {
	String lp = login + ":" + password;
	BASE64Encoder encoder = new BASE64Encoder();
	encoded = encoder.encode(lp.getBytes());
}
%>

<form action="Base64Credential.jsp" >
<table border=0>
 <tr> 
  <td>
	<table width="100%" border=0>
	 <tr>
	  <td nowrap><span class="subTitleBlue">Login</span></td>
	  <td align="left" nowrap><input name="login" value="<%=login%>" ></td>
	 </tr>
	 <tr>
	  <td nowrap><span class="subTitleBlue">Password</span></td>
	  <td align="left" nowrap><input name="password" value="<%=password%>" ></td>
	 </tr>
<%if( encoded != null) {%>
	<tr>
	  <td colspan=2 align="left" nowrap><b>&lt;PARAM NAME="HTTPHeaderName0" VALUE="Authorization" /&gt;</b></td>
	</tr>
	<tr>
	  <td colspan=2 align="left" nowrap><b>&lt;PARAM NAME="HTTPHeaderSetValue0" VALUE="Basic <%=encoded%>" /&gt;</b></td>
	</tr>
<%}%>
	 <tr>
	 <td>&nbsp;</td>
	  <td align="left" nowrap><input type="submit" value="Encode"></td>
	 </tr>
	</table>
  </td>
 </tr>
</table>
</form>
</body>
</html>