<%@ page import="com.socialcomputing.wps.server.plandictionary.loader.*,com.socialcomputing.wps.server.swatchs.loader.*" %>

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
	DictionnaryLoaderDao dld = new DictionnaryLoaderDao();
	BeanDictionaryLoader dic = dld.findByName( request.getParameter( "dictionary") );
	//dic = dicHome.findByPrimaryKey( request.getParameter( "dictionary"));
	out.print( dic.getDictionaryDefinition());
}
else if( request.getParameter( "swatch") != null)
{
	SwatchLoaderDao sld = new SwatchLoaderDao();
	BeanSwatchLoader sw = sld.findByName( request.getParameter( "swatch") );

	//sw = swHome.findByPrimaryKey( request.getParameter( "swatch"));
	out.print( sw.getSwatchDefinition());
}
%>