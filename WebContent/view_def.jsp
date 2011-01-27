<%@page import="com.socialcomputing.wps.server.swatchs.loader.Swatch"%>
<%@page import="com.socialcomputing.wps.server.swatchs.loader.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.plandictionary.loader.Dictionary"%>
<%@page import="com.socialcomputing.wps.server.plandictionary.loader.DictionaryManager"%>
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
	DictionaryManager managerD = new DictionaryManager();
	Dictionary dic = managerD.findByName( request.getParameter("dictionary"));
	//dic = dicHome.findByPrimaryKey( request.getParameter( "dictionary"));
	out.print( dic.getDictionaryDefinition());
}
else if( request.getParameter( "swatch") != null)
{
	SwatchManager managerS = new SwatchManager();
	Swatch swatch = managerS.findByName(request.getParameter("swatch"));

	//sw = swHome.findByPrimaryKey( request.getParameter( "swatch"));
	out.print(swatch.getSwatchDefinition());
}
%>