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
			env.m_outCol = toColorX(json.outColor);
			env.m_filterCol = toColorX(json.filterColor);
			env.m_transfo = toTransfo(json.transfo);

			env.m_props = new Array( json.props.length);
			for(var i:String in json.props){
				env.m_props[i] = json.props[i];
			}			
			env.m_selections = new Array( json.props.length);
			for(var i:String in json.selections){
				env.m_selections[i] = json.selections[i];
			}			
			return env;
		}

		public static function toPlan(json:Object):Plan
		{
			var plan:Plan = new Plan();
			plan.m_links = new Array( json.links.length);
			for each (var item:Object in json.links) { 
				plan.m_links.push( toLinkZone(item));
			} 
			plan.m_nodes = new Array( json.nodes.length);
			for each (var item:Object in json.nodes) { 
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
			trace( "Error toZone");
			return null;
		}

		private static function _toActiveZone(zone:ActiveZone, json:Object):void {
			zone.m_flags = json.flags;
			zone.m_curSwh = toSwatch(json.curSwatch);
			zone.m_restSwh = toSwatch(json.restSwatch);
			for(var i:String in json.props){
				if( json.props[i] is Array) {
					zone[i] = new Array(json.props[i].length);
					for each (var z:Object in json.props[i]) { 
						zone[i].push( z);
					} 
				}
				else {
					if( i != "ID")
					zone[i] = json.props[i];
				}
			}			
		}
		
		private static function toActiveZone(json:Object):ActiveZone {
			var zone:ActiveZone = new ActiveZone();
			_toActiveZone( zone, json);
			return zone;
		}
		
		private static function toBagZone(json:Object):BagZone {
			var zone:BagZone = new BagZone( new Array( json.subZones.length));
			_toActiveZone( zone, json);
			for each (var z:Object in json.subZones) { 
				zone.m_subZones.push( toZone(z));
			} 
			return zone;
		}
		
		private static function toLinkZone(json:Object):LinkZone {
			var zone:LinkZone = new LinkZone(toBagZone(json.from), toBagZone(json.to));
			_toActiveZone( zone, json);
			return zone;
		}
		
		private static function toSatellite(json:Object):Satellite {
			var satellite:Satellite = new Satellite(toShapeX( json.shapex), new Array( json.slices.length));
			for each (var slice:Object in json.slices) { 
				satellite.m_slices.push( toSlice(slice));
			} 
			return satellite;
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
		* ShapeX / Slice / Swatch / HTMLText / FontX / MenuX : Base subclasses
		*/
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
			var item:Swatch = new Swatch( new Array( json.satellites.length));
			for each (var z:Object in json.satellites) { 
				item.m_satellites.push( toSatellite(z));
			} 
			item.m_refs = new Array( json.refs.length);
			for(var i:String in json.refs){
				item.m_refs[i] = json.refs[i];
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
			var item:MenuX = new MenuX( new Array( json.menu.length));
			for each (var z:Object in json.menu) { 
				item.m_items.push( toMenuX(z));
			} 
			toBase( item, json);
			return item;
		}
		
		private static function toBase(base:Base, json:Object):void {
			base.m_containers = new Array( json.containers.length);
			for each (var z:Object in json.containers) { 
				base.m_containers.push( toVContainer(z));
			} 
		}
		
		private static function toVContainer(json:Object):VContainer {
			var item:VContainer = new VContainer(json.value, json.bound);
			return item;
		}
		
	}
}