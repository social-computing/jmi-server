<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">	
<head>
    <title>Documentation - Map your feeds!</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="content-language" content="en" />
	<meta name="description" content="Map your feeds! documentation" />
	<meta name="keywords" content="documentation, rss, feeds, feed, map, cartography, visualization, social, blog, gadget, widget, social computing, category, representation, information" />
	<meta name="author" content="Social Computing" /> 
	<meta name="robots" content="all" /> 
	<link rel=StyleSheet href="./mapyourfeeds.css" type="text/css" media="screen" />
</head>
<body>
<div id="header">
<table id="bandeau" border="0">
<tr>
	<td id="logo" rowspan="3">
		<a href="./" title="Map your feeds!"><img border="0" width="144" height="70" title="Map your feeds!" src="./images/logo-sc-white.jpg" /></a>
	</td>
	<td><h1>How to use the service</h1>
	</td>
</tr>
</table>
</div>
<div id="documentation">
<h2>Overview</h2>
Map your feeds! displays your feed(s) items on a map. 
<h2>Kinds of URL(s)</h2>
<p>RSS 2.0 and Atom feeds are supported. The feeds items MUST contain categories; otherwise we won't be able to produce a map.</p>
<p>You also can give directly a website URL. All the feeds described in it will be mapped.</p>
<h3>One URL, two URL(s), ...</h3>
<p>You want to map more than one feed? Just separate them with a comma. You can mix RSS, Atom or website URLs.</p>
<h2>Add Map your feeds!</h2>
<h3>Add Map your feeds! to your webpage</h3>
You can place the map directly on any webpage. <a href="./integrate.jsp">Here it is</a>.
<h3>Add Map your feeds! to iGoogle</h3>
<a href="http://www.google.com/ig/adde?moduleurl=http://www.mapyourfeeds.com/google/igoogle-social-computing-feeds.xml"><img src="http://buttons.googlesyndication.com/fusion/add.gif" style="width:104px; height:17px;border:0px;" alt="Add to iGoogle" /></a>
<h3>Add Map your feeds! to Blogger</h3>
<p>Click on 'Add a gadget', then on 'Add your own' and enter this URL:</p>
<textarea readonly="true" style="width:100%">http://www.mapyourfeeds.com/google/blogger-social-computing-feeds.xml</textarea>
<h3>You need more customization?</h3>
<p>Contact us: <a href="mailto:mapyourfeeds at social-computing.com">mapyourfeeds at social-computing.com</a></p>
<h2>Troubleshooting</h2>
<h3>The map is empty</h3>
<p>Verify the feed contains items with categories.</p>
<h3>Flash is not supported</h3>
<p>The HTML5 client is coming soon...</p>
</div>
<jsp:include page="./footer.jsp" ></jsp:include>
</body>
</html>