package com.socialcomputing.wps.script
{
	public class JSONToWPSDecoder
	{
		public static function toEnv(json:Object):Env
		{
			var env:Env = new Env();
			env.m_flags = json.flags;
/*			node.put("flags", env.m_flags);
			node.put("inColor", toJSON(env.m_inCol));
			node.put("outColor", toJSON(env.m_outCol));
			node.put("filterColor", toJSON(env.m_filterCol));
			node.put("transfo", toJSON(env.m_transfo));
			ObjectNode props = node.putObject("props");
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
/*			plan.m_links.
			// Add the array of nodes
			ArrayNode nodes = node.putArray("nodes");
			for (ActiveZone zone : plan.m_nodes) {
				nodes.add(toJSON(zone));
			}
			// Add the array of links
			ArrayNode links = node.putArray("links");
			for (ActiveZone zone : plan.m_links) {
				links.add(toJSON(zone));
			}*/
			return plan;
		}
		
/*		static private ObjectNode toJSON(Point point) {
			ObjectNode node = mapper.createObjectNode();
			node.put("x", point.x);
			node.put("y", point.y);
			return node;
		}
		
		// TODO à checker
		static private ObjectNode toJSON(ColorX color) {
			ObjectNode node = mapper.createObjectNode();
			node.put("color", color.m_color);
			if (color.m_scolor == null)
				node.putNull("scolor");
			else
				node.put("scolor", color.m_scolor);
			return node;
		}
		
		/*
		* LinkZone / BagZone : ActiveZone subclasses
		*/
		// Ok
		static private ObjectNode toJSON(ActiveZone zone) {
			ObjectNode node = null;
			if (zone == null)
				node = mapper.createObjectNode();
			else if (zone instanceof BagZone)
				node = toJSON((BagZone) zone);
			else if (zone instanceof LinkZone)
				node = toJSON((LinkZone) zone);
			else 
				node = mapper.createObjectNode();
			
			node.put("flags", zone.m_flags);
			node.put("curSwatch", toJSON(zone.getCurSwatch()));
			node.put("restSwatch", toJSON(zone.getRestSwatch()));
			ObjectNode propsnode = node.putObject("props");
			for (String key : (Set<String>)zone.keySet()) {
				putValue(propsnode, key, zone.get(key));
			}
			return node;
		}
		
		// Ok
		static private ObjectNode toJSON(BagZone zone) {
			ObjectNode node = mapper.createObjectNode();
			ArrayNode subzone = node.putArray("subZones");
			if (zone != null&& zone.m_subZones != null) {
				for (ActiveZone az : zone.m_subZones) {
					subzone.add(toJSON(az));
				}
			}
			return node;
		}
		
		// Ok
		static private ObjectNode toJSON(LinkZone zone) {
			ObjectNode node = mapper.createObjectNode();
			node.put("from", toJSON(zone.m_from));
			node.put("to", toJSON(zone.m_to));
			return node;
		}
		
		// Ok
		static private ObjectNode toJSON(Satellite satellite) {
			ObjectNode node = mapper.createObjectNode();
			node.put("shapex", toJSON(satellite.getShape()));
			ArrayNode slices = node.putArray("slices");
			for (Slice slice : satellite.getSlices()) {
				slices.add(toJSON(slice));
			}
			return node;
		}
		
		// Ok
		static private ObjectNode toJSON(Transfo transfo) {
			ObjectNode node = mapper.createObjectNode();
			node.put("dir", transfo.m_dir);
			node.put("pos", transfo.m_pos);
			node.put("scl", transfo.m_scl);
			node.put("flags", transfo.m_flags);
			return node;
		}
		
		/*
		* ShapeX / Slice / Swatch / HTMLText / FontX / MenuX : Base subclasses
		*/
		// Ok
		static private ObjectNode toJSON(ShapeX shape) {
			ObjectNode node = mapper.createObjectNode();
			return toJSON( shape, node);
		}
		
		// Ok
		static private ObjectNode toJSON(Slice slice) {
			ObjectNode node = mapper.createObjectNode();
			return toJSON( slice, node);
		}
		
		// Ok
		static private ObjectNode toJSON(Swatch swatch) {
			ObjectNode node = mapper.createObjectNode();
			ObjectNode refs = node.putObject("refs");
			for (String key : (Set<String>) swatch.m_refs.keySet()) {
				putValue(refs, key, swatch.m_refs.get(key));
			}
			ArrayNode sats = node.putArray("satellites");
			for (Satellite sat : swatch.getSatellites()) {
				sats.add(toJSON(sat));
			}
			return toJSON( swatch, node);
		}
		
		// Ok
		static private ObjectNode toJSON(HTMLText text) {
			ObjectNode node = mapper.createObjectNode();
			return toJSON( text, node);
		}
		
		// Ok
		static private ObjectNode toJSON(FontX font) {
			ObjectNode node = mapper.createObjectNode();
			return toJSON( font, node);
		}
		
		// Ok
		static private ObjectNode toJSON(MenuX menu) {
			ObjectNode node = mapper.createObjectNode();
			ArrayNode sats = node.putArray("menu");
			if( menu != null && menu.m_items != null) {
				for (MenuX submenu : menu.m_items) {
					sats.add(toJSON(submenu));
				}
			}
			return toJSON( menu, node);
		}
		
		// Ok
		static private ObjectNode toJSON(Base base, ObjectNode node) {
			ArrayNode containers = node.putArray("containers");
			for (VContainer container : base.m_containers) {
				if( container != null) {
					containers.add(toJSON(container));
				}
			}
			return node;
		}
		
		// TODO Presque OK !
		static private ObjectNode toJSON(VContainer container) {
			ObjectNode node = mapper.createObjectNode();
			// TODO à checker !!!
			if( container.m_value != null) {
				putValue(node, "value", container.m_value);
			}
			node.put("bound", container.isBound());
			return node;
		}
		*/
		
	}
}