<%@page import="com.socialcomputing.wps.server.persistence.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.persistence.DictionaryManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl"%>
<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
	return System.currentTimeMillis();
}%>
<%
response.setContentType( "text/xml");
//out.print( "<?xml version='1.0' encoding='ISO-8859-1'?>");
if( request.getParameter( "dictionary") != null)
{
	DictionaryManager managerD = new DictionaryManagerImpl();
	Dictionary dic = managerD.findByName( request.getParameter("dictionary"));
	out.print( dic.getDefinition());
}
else if( request.getParameter( "swatch") != null)
{
	SwatchManager managerS = new SwatchManagerImpl();
	Swatch swatch = managerS.findByName(request.getParameter("swatch"));
	out.print(swatch.getDefinition());
}
%>