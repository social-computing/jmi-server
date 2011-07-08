<%@page import="com.socialcomputing.wps.server.persistence.Swatch"%>
<%@page import="com.socialcomputing.wps.server.persistence.SwatchManager"%>
<%@page import="com.socialcomputing.wps.server.persistence.hibernate.SwatchManagerImpl"%>

<%!
// return current time to proxy server request
public long getLastModified(HttpServletRequest request) {
    return System.currentTimeMillis();
}
%>

<html>
<head>
<link rel="stylesheet" href="./wps.css">
<link rel="stylesheet" href="./result.css">
<script type="text/javascript" src="../client/applet/jquery.js" ></script>
<script type="text/javascript" src="./javascript/jquery.fancybox-1.3.4.pack.js"></script>
<link rel="stylesheet" href="./css/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
<base target="main">
<SCRIPT LANGUAGE="JavaScript1.2" > 
    function SubmitForm(resetStart) {
        if( resetStart) ResetStart();
        document.test.submit();
        return true;
    }
    function Delete() {
        if( confirm("Are you sure you want to delete selected dictionnaires ?")) {
            document.test.confirmdelete.value = 'y';
            document.test.submit();
        }
        return false;
    }
    function OnExport( content, contentType) {
        document.test.content.value = content;
        document.test.contentType.value = contentType;
        document.test.submit();
        return false;
    }
</SCRIPT>
</head>
<body bgcolor=7f9fdf>
<script>
$(document).ready(function() {

    /* This is basic - uses default settings */
    $("a#single_image").fancybox();
        
    /* Using custom settings */
    $("a#inline").fancybox({
        'hideOnContentClick': true
    });

    /* Apply fancybox to multiple items */
        
    $("a.iframe").fancybox({
        'width'                : '75%',
        'height'            : '75%',
        'autoScale'         : false,
        'transitionIn'        : 'none',
        'transitionOut'        : 'none',
        'type'                : 'iframe'
    });
});
</script>
<!--iframe height="0" width="0" src="../exportrequest.jsp"></iframe-->
<%
if(request.getParameter("swatch") == null) return;
if(request.getParameter("dictionary") == null) return;
SwatchManager manager = new SwatchManagerImpl();
Swatch sw = manager.findByName(request.getParameter("swatch"), request.getParameter("dictionary"));
%>
<table width="100%">
<tr>
<td><h1>swatch : <%=sw.getSwatchPk().getName()%></h1></td>
</tr>
<tr>
<td><a class="iframe" href="view_def.jsp?dictionary=<%=request.getParameter("dictionary")%>&swatch=<%=java.net.URLEncoder.encode(sw.getSwatchPk().getName(), "UTF-8")%>" target="_blank"><span class="texblanc">View definition</span></a></td>
</tr>
<tr>
<td><a class="iframe" href="edit_def.jsp?dictionary=<%=request.getParameter("dictionary")%>&swatch=<%=java.net.URLEncoder.encode(sw.getSwatchPk().getName(), "UTF-8")%>" target="_blank"><span class="texblanc">Edit definition</span></a></td>
</tr>
<tr><td>&nbsp;</td></tr>
</table>


</body>
</html>
