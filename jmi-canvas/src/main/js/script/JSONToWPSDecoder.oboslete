package com.socialcomputing.jmi.script
{
	import flash.geom.Point;
	
	public class JSONToWPSDecoder
	{
		public static function toEnv(json:Object):Env
		{
			var env:Env = new Env();
			env.m_flags = json.flags;
			env.m_inCol = toColorX(json.inColor);
			env.m_outCol = toColorX(json.outColor);
			env.m_filterCol = toColorX(json.filterColor);
			env.m_transfo = toTransfo(json.transfo);

			env.m_props = new Array();
			for(var i:Object in json.props){
				if( json.props[i] is Array) {
					env.m_props[i] = new Array();
					for each (var z:Object in json.props[i]) { 
						env.m_props[i].push( z);
					} 
				}
				else {
					env.m_props[i] = json.props[i];
				}
			}			
			env.m_selections = new Array();
			for(i in json.selections){
				env.m_selections[i] = json.selections[i];
			}			
			return env;
		}

		public static function toPlan(json:Object):Plan
		{
			var plan:Plan = new Plan();
			plan.m_linksCnt = json.linksCnt;
			plan.m_links = new Array();
			for each (var item:Object in json.links) { 
				plan.m_links.push( toLinkZone(item));
			} 
			plan.m_nodesCnt = json.nodesCnt;
			plan.m_nodes = new Array();
			for each (item in json.nodes) { 
				plan.m_nodes.push( toZone(item));
			} 
			var n:int = json.nodes.length; 
			for( var i:int=0; i < n; ++i) {
				// BagZone : append ActiveZone
				for each (var az:ActiveZone in plan.m_nodes[i].m_subZones) { 
					plan.m_nodes.push( az);
				}			
			}
			// Résolution des références
			// Convert from et to index to Bagzone reference
			for each (item in plan.m_links) { 
				var link:LinkZone = item as LinkZone;
				if( link.m_fromIndex != -1) {
					link.m_from = plan.m_nodes[ link.m_fromIndex];
					link.m_props["_VERTICES"][0] = link.m_from.m_props["_VERTICES"][0];
				}
				if( link.m_toIndex != -1) {
					link.m_to = plan.m_nodes[ link.m_toIndex];
					link.m_props["_VERTICES"][1] = link.m_to.m_props["_VERTICES"][0];
				}
			} 
			return plan;
		}
		
		private static function toPoint(json:Object):Point {
			var point:Point = new Point();
			point.x = json.x;
			point.y = json.y;
			return point;
		}
		
		private static function toColorX(json:Object):ColorX {
			var color:ColorX = new ColorX( json.color);
			if( json.hasOwnProperty("scolor"))
				color.m_scolor = json.scolor;
			return color;
		}
		
		/*
		* LinkZone / BagZone : ActiveZone subclasses
		*/
		private static function toZone(json:Object):ActiveZone {
			if( json.cls == "ActiveZone") {
				return toActiveZone(json);				
			}
			else if( json.cls == "BagZone") {
				return toBagZone(json);				
			}
			else if( json.cls == "LinkZone") {
				return toLinkZone(json);				
			}
			trace( "Error toZone");
			return null;
		}

		private static function _toActiveZone(zone:ActiveZone, json:Object):void {
			zone.m_flags = json.flags;
			zone.m_curSwh = toSwatch(json.curSwatch);
			zone.m_restSwh = toSwatch(json.restSwatch);
			zone.m_props = new Array();
			for(var i:String in json.props){
				if( json.props[i] is Array) {
					zone.m_props[i] = new Array();
					for each (var z:Object in json.props[i]) { 
						zone.m_props[i].push( toObject(z));
					} 
				}
				else {
					zone.m_props[i] = toObject(json.props[i]);
				}
			}			
		}
		
		private static function toActiveZone(json:Object):ActiveZone {
			var zone:ActiveZone = new ActiveZone();
			_toActiveZone( zone, json);
			return zone;
		}
		
		private static function toBagZone(json:Object):BagZone {
			var zone:BagZone = new BagZone( new Array());
			_toActiveZone( zone, json);
			for each (var z:Object in json.subZones) { 
				zone.m_subZones.push( toZone(z));
			} 
			return zone;
		}
		
		private static function toLinkZone(json:Object):LinkZone {
			var zone:LinkZone = new LinkZone(json.from, json.to);
			_toActiveZone( zone, json);
			return zone;
		}
		
		private static function toTransfo(json:Object):Transfo {
			var transfo:Transfo = new Transfo(0,0,0,0);
			transfo.m_dir = json.dir;
			transfo.m_pos = json.pos;
			transfo.m_scl = json.scl;
			transfo.m_flags = json.flags;
			return transfo;
		}
		
		/*
		* Satellite / ShapeX / Slice / Swatch / HTMLText / FontX / MenuX : Base subclasses
		*/
		private static function toSatellite(json:Object):Satellite {
			var satellite:Satellite = new Satellite(toShapeX( json.shapex), new Vector.<Slice>());
			for each (var slice:Object in json.slices) { 
				satellite.m_slices.push( toSlice(slice));
			} 
			toBase( satellite, json);
			return satellite;
		}
		
		private static function toShapeX(json:Object):ShapeX {
			var item:ShapeX = new ShapeX();
			toBase( item, json);
			return item;
		}
		
		private static function toSlice(json:Object):Slice {
			var item:Slice = new Slice();
			toBase( item, json);
			return item;
		}
		
		private static function toSwatch(json:Object):Swatch {
			if( json == null) return null;
			var item:Swatch = new Swatch( new Vector.<Satellite>());
			for each (var z:Object in json.satellites) { 
				item.m_satellites.push( toSatellite(z));
			} 
			item.m_refs = new Array();
			for(var i:String in json.refs){
				item.m_refs[i] = toObject( json.refs[i]);
			}			
			toBase( item, json);
			return item;
		}
		
		private static function toHTMLText(json:Object):HTMLText {
			var item:HTMLText = new HTMLText();
			toBase( item, json);
			return item;
		}
		
		private static function toFontX(json:Object):FontX {
			var item:FontX = new FontX();
			toBase( item, json);
			return item;
		}
		
		private static function toMenuX(json:Object):MenuX {
			var item:MenuX = new MenuX( new Vector.<MenuX>());
			for each (var z:Object in json.menu) { 
				item.m_items.push( toMenuX(z));
			} 
			toBase( item, json);
			return item;
		}
		
		private static function toBase(base:Base, json:Object):void {
			base.m_containers = new Array();
			for each (var z:Object in json.containers) { 
				base.m_containers.push( toVContainer(z));
			} 
		}
		
		private static function toVContainer(json:Object):VContainer {
			if( json == "null")
				return null;
			var item:VContainer = new VContainer( toObject( json.value), json.bound);
			return item;
		}

		private static function toObject(json:Object):Object {
			if( !json.hasOwnProperty("cls")) {
				return json;
			}
			else if( json.cls == "Transfo") {
				return toTransfo(json);				
			}
			else if( json.cls == "Slice") {
				return toSlice(json);				
			}
			else if( json.cls == "ColorX") {
				return toColorX(json);				
			}
			else if( json.cls == "MenuX") {
				return toMenuX(json);				
			}
			else if( json.cls == "HtmlText") {
				return toHTMLText(json);				
			}
			else if( json.cls == "FontX") {
				return toFontX(json);				
			}
			else if( json.cls == "Point") {
				return toPoint(json);				
			}
			trace( "Error toZone: " + json.cls);
			return null;
		}
		
	}
}