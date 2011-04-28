package com.socialcomputing.wps.script
{
	import flash.geom.Point;
	
	public class JSONToWPSDecoder
	{
		public static function toEnv(json:Object):Env
		{
			var env:Env = new Env();
			env.m_flags = json.flags;
			env.m_inCol = toColorX(json.inColor);
			env.m_outColor = toColorX(json.outColor);
			env.m_filterColor = toColorX(json.filterColor);
			env.m_transfo = toTransfo(json.transfo);
			
/*			ObjectNode props = node.putObject("props");
			for (String key : (Set<String>) env.m_props.keySet()) {
				props.put(key, (String) env.m_props.get(key));
			}
			ObjectNode sel = node.putObject("selections");
			for (String key : (Set<String>) env.m_selections.keySet()) {
				putValue(sel, key, env.m_selections.get(key));
			}*/
			return env;
		}

		public static function toPlan(json:Object):Plan
		{
			var plan:Plan = new Plan();
			plan.m_links = new Array( json.links.length);
			for each (var item in json.links) { 
				plan.m_links.push( toLinkZone(item));
			} 
			plan.m_nodes = new Array( json.nodes.length);
			for each (var item in json.nodes) { 
				plan.m_nodes.push( toBagZone(item));
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
			var color:ColorX = new ColorX();
			color.m_color = json.color;
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
		}

		private static function _toActiveZone(zone:ActiveZone, json:Object) {
			zone.m_flags = json.flags;
			zone.m_curSwh = json.curSwatch;
			zone.m_restSwh = json.restSwatch;
			/*
			ObjectNode propsnode = node.putObject("props");
			for (String key : (Set<String>)zone.keySet()) {
			putValue(propsnode, key, zone.get(key));
			}*/
		}
		
		private static function toActiveZone(json:Object):ActiveZone {
			var zone:ActiveZone = new ActiveZone();
			_toActiveZone( zone, json);
			return zone;
		}
		
		private static function toBagZone(json:Object):BagZone {
			var zone:BagZone = new BagZone();
			_toActiveZone( zone, json);
			zone.m_subZones = new Array( json.subZones.length);
			for each (var z in json.subZones) { 
				zone.m_subZones.push( toZone(z));
			} 
			return zone;
		}
		
		private static function toLinkZone(json:Object):LinkZone {
			var zone:LinkZone = new LinkZone();
			_toActiveZone( zone, json);
			zone.m_from = toBagZone(json.from);
			zone.m_to = toBagZone(json.to);
			return zone;
		}
		
		private static function toSatellite(json:Object):Satellite {
			var satellite:Satellite = new Satellite();
			satellite.m_shape = toShapeX( json.shapex);
			satellite.m_slices = new Array( json.slices.length);
			for each (var slice in json.slices) { 
				satellite.m_slices.push( toSlice(slice));
			} 
			return satellite;
		}
		
		private static function toTransfo(json:Object):Transfo {
			var transfo:Transfo = new Transfo();
			transfo.m_dir = json.dir;
			transfo.m_po = json.pos;
			transfo.m_scl = json.scl;
			transfo.m_flags = json.flags;
			return transfo;
		}
		
		/*
		* ShapeX / Slice / Swatch / HTMLText / FontX / MenuX : Base subclasses
		*/
		private static function toShapeX(json:Object):ShapeX {
			var item:Transfo = new Transfo();
			return toBase( item, json);
		}
		
		private static function toSlice(json:Object):Slice {
			var item:Slice = new Slice();
			return toBase( item, json);
		}
		
		private static function toSwatch(json:Object):Swatch {
			var item:Swatch = new Swatch();
			/*ObjectNode refs = node.putObject("refs");
			for (String key : (Set<String>) swatch.m_refs.keySet()) {
				putValue(refs, key, swatch.m_refs.get(key));
			}*/
			item.m_satellites = new Array( json.satellites.length);
			for each (var z in json.satellites) { 
				item.m_satellites.push( toSatellite(z));
			} 
			return toBase( item, json);
		}
		
		private static function toHTMLText(json:Object):HTMLText {
			var item:HTMLText = new HTMLText();
			return toBase( item, json);
		}
		
		private static function toFontX(json:Object):FontX {
			var item:FontX = new FontX();
			return toBase( item, json);
		}
		
		private static function toMenuX(json:Object):MenuX {
			var item:MenuX = new MenuX();
			item.m_items = new Array( json.menu.length);
			for each (var z in json.menu) { 
				item.m_items.push( toMenuX(z));
			} 
			return toBase( item, json);
		}
		
		private static function toBase(base:Base, json:Object) {
			base.m_containers = new Array( json.containers.length);
			for each (var z in json.containers) { 
				base.m_containers.push( toVContainer(z));
			} 
		}
		
		private static function toVContainer(json:Object):VContainer {
			var item:VContainer = new VContainer();
			item.m_value = json.value;
			item.m_isBound = json.bound;
			return item;
		}
		
	}
}