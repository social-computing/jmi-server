<html>
	<head>
		<title>WPS Administration</title>
		<meta http-equiv="content-type" content="text/html;charset=ISO-8859-1" />
		<meta http-equiv="content-language" content="fr-FX" />
		<link rel="stylesheet" href="css/main.css"/>
		<link rel="stylesheet" href="css/wps.css">
		<script type="text/javascript" src="./applet/jquery.js" ></script>
		<script type="text/javascript" src="./applet/jquery.wpsmap.js" ></script>
		<script type="text/javascript" >
			function attributeChanged()
			{
				var applet = document.applets["WPSApplet"];
				if( applet == null) return null;
				if( !applet.isReady()) return;
				
				$('#entity')[0].selectedIndex = 0;
				if( $('#attribute')[0].selectedIndex == 0)
					ClearSelection( applet);
				else
					ShowOnPlan( applet, $('#attribute')[0][ $('#attribute')[0].selectedIndex].value, '');
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
			function entityChanged()
			{
				var applet = document.applets["WPSApplet"];
				if( applet == null) return null;
				if( !applet.isReady()) return;
				
				$('#attribute')[0].selectedIndex = 0;
				if( $('#entity')[0].selectedIndex == 0)
					ClearSelection( applet);
				else
				{
					var id = $('#entity')[0][ $('#entity')[0].selectedIndex].text;
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
					ShowOnPlan( applet, $('#entity')[0][$('#entity')[0].selectedIndex].value, linkIds);
				}
			}
			function onMapReady()
			{
				var applet = document.applets["WPSApplet"];
				if( applet == null) return null;
				if( !applet.isReady()) return;
				
				var sep = "$$;.,:$$";
				var n = applet.getAttCount();
				var nameLst = new Array( n);
				for (var i = 0; i < n; i++)
				{ 
					nameLst[i] = applet.getAttProp( i, "NAME") + sep + i;//applet.getAttProp( i, "ID");
				}
				nameLst.sort();
				$('#attribute')[0].options.length=0;
				$('#attribute')[0].options[0] = new Option( "---------- choose country ----------", -1, false, false);
				for (var i = 0; i < n; i++)
				{ 
					var pos = nameLst[i].lastIndexOf( sep);
					$('#attribute')[0].options[i+1] = new Option( nameLst[i].substring( 0, pos), nameLst[i].substring( pos + sep.length), false, false);
				}
				
				n = applet.getAttCount();
				var max = 0;
				nameLst = new Array( 30);
				idLst = new Array( 30);
				for (var i = 0; i < n; i++)
				{ 
					var props = applet.getAttProp( i, "POSS_NAME");
					if( props != null)
					{
						props = props + ''; // String transformation
						var names = props.split( '\n');
						for( var j = 0; j < names.length; ++j)
						{			
							var found = false;
							for (var k = 0; k < max && !found; k++)
							{
								if( nameLst[k] == names[j])
								{
									found = true;
									idLst[k] += ',' + i;
								}
							}
							if( !found)
							{
								//alert( props + '\n ' + j + '/' + props.length + ' ' + names[j]);
								nameLst[max] = names[j];
								idLst[max++] = names[j] + sep + i;
							}
						}
					}
				}
				idLst.sort();
				$('#entity')[0].options.length=0;
				$('#entity')[0].options[0] = new Option( "---------- choose organization ----------", -1, false, false);
				for (var i = 0; i < max; i++)
				{ 
					var pos = idLst[i].lastIndexOf( sep);
					$('#entity')[0].options[i+1] = new Option( idLst[i].substring( 0, pos), idLst[i].substring( pos + sep.length), false, false);
				}
			}
			function setMap(params) {
				// Mysql based dictionary by default  
				// params['planName'] = 'sample';
				
				// Uncomment to switch to Solr based dictionary
				// params['planName'] = 'Solr_sample';
				// params['maxResults'] = '35';
				// params['q'] = 'fulltext:retraite AND assetTagNames:[* TO *]';
				// params['invert'] = false;
				
				// Uncomment to enable search by document proximity
				// params['searchDocumentId'] = '15_PORTLET_10156_FIELD_47260';
				// params['attributeId'] = '15_PORTLET_10156_FIELD_47260';
				// params['analysisProfile'] = 'DiscoveryProfile';
		
				// Uncomment to switch to XML based dictionary
				params['planName'] = 'Xml_sample';
		
				$("#map").wpsmap({
					wps: params, 
					display: {color:'336699'},
					plugin: {noscript:'../noscript.jsp'}
				});
			}
			// Fired from applet
			function NewWin( id, name) {
				setMap( {entityId:id});
				$('#titre').html( name);
			}
			// Fired from applet
			function Discover( id, name) {
				setMap( {analysisProfile:'DiscoveryProfile',attributeId:id});
				$('#titre').html( name);
			}
		</script>
	</head>
	
	<body>	
	<div id="top"><jsp:include page="top.jsp" /></div>
	<div id="menu"><jsp:include page="menu.jsp" /></div>
	<div id="content">




	<script type="text/javascript"> 
		$(document).ready(function(){
			$("#map").bind('ready', function(e) {
				onMapReady();
				});
			$("#map").bind('void', function(e) {
				$("#map").html("<h1>Void map</h1>");
				});
			$("#map").bind('error', function(e, context) {
				error = "<h1>Error !</h1><br/>";
				jQuery.each( context, function(name, value) {
						error = error + name + ": " + value + "<br/>"
					});
				$("#map").html( error);
				});
			setMap( {analysisProfile:'GlobalProfile'});
			$('#titre').html( "All");
		});
	</script>
	<div id="tools">
		<table border="0" cellpadding="0" cellspacing="0" width="100%">				
		 <tr>
		   <td align="left" valign="middle" nowrap><h1 id="titre"></h1></td>
		   <td align="left" valign="top" nowrap>
				<select id="entity" style="font-family:Arial;font-size:8pt" onChange="entityChanged()"> </select>
		   </td>
		   <td align="left" valign="top" nowrap>
				<SELECT id="attribute" style="font-family:Arial;font-size:8pt" onChange="attributeChanged()"> </select>
		   </td>
		 </tr>
		</table>
	</div>	
	<div id="map" style="width:100%;height:90%" ></div>
	</div>
</body>
</html>
