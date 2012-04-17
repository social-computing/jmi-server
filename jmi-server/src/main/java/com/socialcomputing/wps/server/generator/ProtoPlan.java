package com.socialcomputing.wps.server.generator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.server.swatchs.XSwatch;
import com.socialcomputing.wps.server.webservices.PlanRequest;
import com.socialcomputing.utils.math.Bounds;
import com.socialcomputing.utils.math.EZMath;

/**
 * <p>
 * Title: ProtoPlan
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2001-2003
 * </p>
 * <p>
 * Company: MapStan (Voyez Vous)
 * </p>
 * 
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class ProtoPlan {

	Logger log = LoggerFactory.getLogger(ProtoPlan.class);
	/**
	 * A Plan Request to reach the DB information.
	 */
	public PlanRequest m_planReq;

	/**
	 * The Attributes list.
	 */
	public ProtoAttribute[] m_attributes;

	/**
	 * The Entities list.
	 */
	public ProtoEntity[] m_entities;

	/**
	 * The AttributeLinks list.
	 */
	public AttributeLink[] m_attLinks;

	/**
	 * The EntityLinks list.
	 */
	public EntityLink[] m_entLinks;

	/**
	 * A reference to the MapData of the Mapper.
	 */
	protected MapData m_mapDat;

	/**
	 * The links that are relaxed.
	 */
	protected AttributeLink[] m_relaxLinks;

	/**
	 * the properties of nodes
	 */
	Hashtable m_props = new Hashtable();

	/**
	 * Index of the bounds of the external Attributes size in the bounds list.
	 */
	public static final int A_SIZE_BND = 0;

	/**
	 * Index of the bounds of the base Attributes size in the bounds list.
	 */
	public static final int AB_SIZE_BND = 1;

	/**
	 * Index of the bounds of the external Attributes weight in the bounds list.
	 */
	public static final int A_WEIGHT_BND = 2;

	/**
	 * Index of the bounds of the base Attributes weight in the bounds list.
	 */
	public static final int AB_WEIGHT_BND = 3;

	/**
	 * Index of the bounds of the Links length in the bounds list.
	 */
	public static final int L_LENGTH_BND = 4;

	/**
	 * Index of the bounds of the mixed Links length in the bounds list.
	 */
	public static final int LM_LENGTH_BND = 5;

	/**
	 * Index of the bounds of the base Links length in the bounds list.
	 */
	public static final int LB_LENGTH_BND = 6;

	/**
	 * Index of the bounds of the external Links width in the bounds list.
	 */
	public static final int L_WIDTH_BND = 7;

	/**
	 * Index of the bounds of the mixed Links width in the bounds list.
	 */
	public static final int LM_WIDTH_BND = 8;

	/**
	 * Index of the bounds of the base Links width in the bounds list.
	 */
	public static final int LB_WIDTH_BND = 9;

	/**
	 * Index of the bounds of all Attributes size in the bounds list.
	 */
	public static final int A_ALLSIZE_BND = 10;

	/**
	 * Index of the bounds of all Links width in the bounds list.
	 */
	public static final int L_ALLWIDTH_BND = 11;

	/**
	 * Index of the bounds of all Links length in the bounds list.
	 */
	public static final int L_ALLLENGTH_BND = 12;

	/**
	 * Name of the bounds for debug purpose.
	 */
	protected static final String[] s_bndNames = new String[] {
			"Attribute size", "Base Attribute size", "Attribute weight",
			"Base Attribute weight", "Link length", "Mixed Link length",
			"Base Link length", "Link width", "Mixed Link width",
			"Base Link width", "All attributes size", "All links width",
			"All links length", };

	/**
	 * Array of bounds for each link/attribute parameters.
	 */
	public Bounds[] m_bounds;

	/**
	 * Number of attributes (including clusterized) in the base.
	 */
	protected int m_baseAttCnt = 0;

	/**
	 * Number of links in the base.
	 */
	protected int m_baseLinkCnt = 0;

	/**
	 * Env for the WPSApplet.
	 */
	protected Env m_env;

	/**
	 * True if thsi plan has just one Cluster (and no links).
	 */
	protected boolean m_isDegenerated;

	/**
	 * A link Comparator to sort links from the best to the worst. First are the
	 * base links then the mixed one and then the ext links. In each category,
	 * links are sorted accordingly to their size/length ratio and filtering
	 * state. So the thick short links are before the tight long ones that are
	 * before the filtered ones.
	 */
	protected static Comparator s_linkCmp = new Comparator() {
		public int compare(Object o1, Object o2) {
			AttributeLink l1 = (AttributeLink) o1, l2 = (AttributeLink) o2;

			if (l2.isBase()) {
				if (l1.isBase())
					return compareSizeAndLength(l1, l2);
				else
					return 1;
			} else if (l2.isMixed()) {
				if (l1.isMixed())
					return compareSizeAndLength(l1, l2);
				else if (l1.isBase())
					return -1;
				else
					return 1;
			} else // l2.isExt())
			{
				if (l1.isExt())
					return compareSizeAndLength(l1, l2);
				else
					return -1;
			}
		}
	};

	/**
	 * Creates a new ProtoPlan and store its associated Plan Request.
	 * 
	 * @param planReq
	 *            The Plan Request corresponding to this.
	 */
	public ProtoPlan(PlanRequest planReq) {
		int i, n = s_bndNames.length;

		m_planReq = planReq;
		m_bounds = new Bounds[n];

		for (i = 0; i < n; i++) {
			m_bounds[i] = new Bounds();
		}
	}

	private Hashtable getprop(String att) {
		return (Hashtable) m_props.get(att);
	}

	/**
	 * Return a XML file describing Protoplan
	 */
	public String getXML() {
		String result = new String();
		int i, j, n;

		result = "<nodes>\n";
		// result +=" <request>"+m_planReq.toString()+"</request>\n";

		n = m_attributes.length;
		for (i = 0; i < n; i++) {
			ProtoAttribute attr = m_attributes[i];

			result += "<node id='" + attr.m_strId + "' >\n";
			result += "<string builtin='elementType'>" + attr.m_strId
					+ "</string>\n";
			result += "<string builtin='elementSize'>" + attr.m_size
					+ "</string>\n";
			result += "<string builtin='isBase'>" + attr.m_isBase
					+ "</string>\n";
			result += "<string builtin='isRef'>" + attr.m_isRef + "</string>\n";

			NodeMapData md = attr.getMapData();
			if (md != null) {// this is a base node
				Point p = md.m_clientPos;
				result += "<integer builtin='x'>" + p.x + "</integer>\n";
				result += "<integer builtin='y'>" + p.y + "</integer>\n";
			} else {// this is a child node
				String pid = attr.m_parent.m_strId;
				result += "<string builtin='parent'>" + pid + "</string>\n";
			}

			if (attr.m_isRef == true)
				result += "<integer builtin='elementColor'>0xFC9000</integer>\n";
			else
				result += "<integer builtin='elementColor'>0x3030A0</integer>\n";

			Hashtable ht = getprop(attr.m_strId);
			Set keys = ht.keySet();
			Iterator iter = keys.iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Object value = ht.get(key);
				boolean b = value.getClass().isArray();
				if (b) {
					Object[] t = (Object[]) value;
					result += "<array builtin='" + key + "'>";
					String sep = "";
					for (j = 0; j < t.length; j++) {
						result += sep + t[j].toString();
						sep = "; ";
					}
					result += "</array>\n";
				} else
					result += "<string builtin='" + key + "'>"
							+ value.toString() + "</string>\n";
			}

			result += "</node>\n\n";
		}
		result += "</nodes>\n";

		result += "<links>\n";
		n = m_relaxLinks.length;
		for (i = 0; i < n; i++) {
			AttributeLink link = m_relaxLinks[i];
			LinkMapData md = link.getMapData();
			float w = md.getWidth();

			if (link.m_isWeak)
				continue;
			if (w <= 1.0)
				continue;

			result += "<link id='b" + i + "'>";
			result += "<string builtin='atomRef'>" + link.m_from.m_strId
					+ "</string>\n";
			result += "<string builtin='atomRef'>" + link.m_to.m_strId
					+ "</string>\n";
			result += "<string builtin='order'>" + w + "</string>\n";

			Collection coll = link
					.getRecommendations(RecommendationGroup.ENTITIES_RECOM);
			String[] c = (String[]) link.getRecommendations(
					RecommendationGroup.ENTITIES_RECOM).toArray(new String[0]);
			if (c.length != 0) {
				result += "<string builtin='recom'>";
				String sep = "";
				for (j = 0; j < c.length; j++) {
					result += sep + c[j];
					sep = ";";
				}
				result += "</string>\n";
			}

			// RecommendationGroup[] rg = link.m_recomGroups;

			result += "</link>\n\n";
		}

		result += "</links>\n";
		/*
		 * n=m_relaxLinks.length; for ( i = 0; i < n; i ++ ) { AttributeLink
		 * link = m_relaxLinks[i];
		 * 
		 * result += "<link "; result += "from='"+link.m_from+"' "; result +=
		 * "to='"+link.m_to+"'"; result += "size='"+link.m_size+"'"; result +=
		 * " />\n"; }
		 */

		return result;
	}

	/**
	 * Returns wether this is a degenerated Plan.
	 * 
	 * @return True there is just one cluster (no link).
	 */
	protected boolean isDegenerated() {
		return m_isDegenerated;
	}

	/**
	 * Display a message when a value is strictly positiv.
	 * 
	 * @param n
	 *            Value to test.
	 * @param msg
	 *            A (warning) message to print.
	 * @return True if n > 0.
	 */
	private boolean showNotNull(int n, String msg) {
		if (n > 0)
			log.debug("{} {}", String.valueOf(n), msg);

		return n > 0;
	}

	/**
	 * Compares both size and length as a ratio. Filtered links are always worst
	 * than the others.
	 * 
	 * @param l1
	 *            A link in the list to sort.
	 * @param l2
	 *            A link different from l1 in the list to sort.
	 * @return 1 if l2 is better than l1, -1 for the opposite and 0 if they are
	 *         equal.
	 */
	private static int compareSizeAndLength(AttributeLink l1, AttributeLink l2) {
		if (l2.isFiltered() == l1.isFiltered()) {
			float r1 = l1.m_size / l1.m_length, r2 = l2.m_size / l2.m_length;
			return r2 > r1 ? 1 : (r2 < r1 ? -1 : 0);
		} else
			return l2.isFiltered() ? -1 : 1;
	}

	// Compare length first then size
	/*
	 * private int compareLength( AttributeLink l1, AttributeLink l2 ) { if (
	 * l2.isFiltered() == l1.isFiltered()) return l1.m_length > l2.m_length ? 1
	 * :( l1.m_length < l2.m_length ? -1 : l2.m_size - l1.m_size ); else return
	 * l2.isFiltered() ? -1 : 1; }
	 * 
	 * // Compare size first then length private int compareSize( AttributeLink
	 * l1, AttributeLink l2 ) { if ( l2.isFiltered() == l1.isFiltered()) return
	 * l2.m_size > l1.m_size ? 1 :( l2.m_size < l1.m_size ? -1 : l1.m_length -
	 * l2.m_length ); else return l2.isFiltered() ? -1 : 1; }
	 */

	// to test the degenerate case by removing all but the first node.
	protected void degenerate() {
		ProtoAttribute[] atts;
		ProtoAttribute att = m_attributes[0];
		int i, j, n = m_attributes.length;

		att.m_links.clear();

		for (i = 0, j = 1; i < n; i++) {
			if (m_attributes[i].m_parent == att)
				m_attributes[j++] = m_attributes[i];
		}

		atts = new ProtoAttribute[j];
		System.arraycopy(m_attributes, 0, atts, 0, j);

		m_attributes = atts;
		m_attLinks = new AttributeLink[0];
	}

	/**
	 * Check Plan integrity and filter links to keep only the most interesting
	 * for the relaxation.
	 * 
	 * @return True if this Plan is valid (at least one cluster and if more than
	 *         one, no cluster without link).
	 */
	protected boolean init()
			throws com.socialcomputing.wps.server.planDictionnary.connectors.JMIException {
		int i, n = m_attributes.length, clustered = 0, nullLinks = 0, voidLinks = 0, extraLinks = 0, extraChild = 0, extraBase = 0, refNodes = 0, nodeLinks = 0;
		boolean isInBase = true, isBase;
		ProtoAttribute att;

		m_env = m_planReq.initEnv();
		m_mapDat = m_planReq.getAnalysisProfile().m_mapDat;

		for (i = 0; i < n; i++) {
			att = m_attributes[i];

			if (att.m_parent == null) {
				clustered++;
				if (att.m_links == null) {
					log.info("NULL LINKS ATT {}", att);
					nullLinks++;
				} else {
					if (att.m_links.size() == 0) {
						log.info("NO LINK ATT {}", att);
						voidLinks++;
					} else {
						nodeLinks += att.m_links.size();
					}
				}
				if (att.m_isRef)
					refNodes++;
			} else {
				if (att.m_links != null && att.m_links.size() > 0) {
					log.info("LINK IN CLUST.ATT {}", att);
					extraLinks++;
				}
				if (att.m_children != null) {
					log.info("CHILD IN CLUST.ATT {}", att);
					extraChild++;
				}
			}

			isBase = att.isBase();

			if (isInBase) {
				if (isBase)
					m_baseAttCnt++;
				else
					isInBase = false;
			} else if (isBase)
				extraBase++;
		}

		if (nodeLinks % 2 != 0)
			log.debug("Odd number of referenced links in nodes : {}", nodeLinks);

		log.info("attributes = {}", n);
		log.info("ref atts = {}", refNodes);
		log.info("base atts = {}", m_baseAttCnt);
		log.info("parent atts = {}", clustered);

		showNotNull(nullLinks, " atts with null links");
		showNotNull(voidLinks, " atts without links");
		showNotNull(extraLinks, " clustatts with links");
		showNotNull(extraChild, " clustatts with children");
		showNotNull(extraBase, " base atts out of base");

		isInBase = true;
		extraBase = 0;
		n = m_attLinks.length;

		AttributeLink[] relaxLinks = new AttributeLink[n];
		AttributeLink link;
		int deadLinks = 0, invalidLinks = 0, junkLinks = 0, m = 0;

		for (i = 0; i < n; i++) {
			link = m_attLinks[i];

			if (link.m_from == null || link.m_to == null) {
				log.info("DEAD LINK {}", link);
				deadLinks++;
			}

			if (!link.isValid()) {
				log.info("INVALID LINK {}", link);
				invalidLinks++;
			}

			// if ( link.isRelaxable())
			{
				// if ( !link.m_isFiltered )
				{
					isBase = link.isBase();

					if (isInBase) {
						if (isBase)
							m_baseLinkCnt++;
						else
							isInBase = false;
					} else if (isBase) {
						log.info("EXTRA BASE LINK {}", link);
						extraBase++;
					}

					if (link.m_from.m_parent != null
							|| link.m_to.m_parent != null) {
						log.info("JUNK LINK {}", link);
						junkLinks++;
						continue; // to avoid copying this junk link!
					}
					relaxLinks[m++] = link;
				}
			}
		}

		log.info("links = {}", n);
		log.info("base links = {}", m_baseLinkCnt);

		m_relaxLinks = new AttributeLink[m];
		System.arraycopy(relaxLinks, 0, m_relaxLinks, 0, m);

		showNotNull(deadLinks, " links with null atts ");
		showNotNull(invalidLinks, " invalid links");
		showNotNull(junkLinks, " links between clusterized node(s)");
		showNotNull(extraBase, " base links out of base");

		nodeLinks /= 2;

		if (nodeLinks != m_attLinks.length)
			log.debug(
					"Different number of links in nodes ( {} ) and link table ( {} )",
					nodeLinks, m_attLinks.length);

		// To put in analysis engine?
		if (m > 1)
			filterLinks();

		n = m;
		isInBase = true;
		m_baseLinkCnt = 0;

		for (i = 0, m = 0; i < n; i++) {
			link = m_relaxLinks[i];

			if (!link.m_isFiltered) {
				relaxLinks[m++] = link;

				if (isInBase) {
					if (link.isBase())
						m_baseLinkCnt++;
					else
						isInBase = false;
				}
			}
		}

		m_relaxLinks = new AttributeLink[m];
		System.arraycopy(relaxLinks, 0, m_relaxLinks, 0, m);

		if (clustered == 0) {
			return false;
		} else if (clustered == 1) {
			m_isDegenerated = true;

			return true;
		} else {
			return nullLinks == 0 && voidLinks == 0;
		}
	}

	/**
	 * Evaluate bounds of analysis values.
	 */
	public void evalBounds() {
		if (!m_isDegenerated) {
			int i, n = m_attributes.length;
			ProtoAttribute att;
			AttributeLink link;

			for (i = 0; i < n; i++) {
				att = m_attributes[i];

				if (att.m_parent == null) // clusterized
				{
					if (att.isBase()) {
						m_bounds[AB_SIZE_BND].check(att.m_size);
						m_bounds[AB_WEIGHT_BND].check(att.m_weight);
					} else {
						m_bounds[A_SIZE_BND].check(att.m_size);
						m_bounds[A_WEIGHT_BND].check(att.m_weight);
					}
				}
			}

			m_bounds[A_ALLSIZE_BND].setBounds(Math.min(
					m_bounds[A_SIZE_BND].m_min, m_bounds[AB_SIZE_BND].m_min),
					Math.max(m_bounds[A_SIZE_BND].m_max,
							m_bounds[AB_SIZE_BND].m_max));

			n = m_relaxLinks.length;

			for (i = 0; i < n; i++) {
				link = m_relaxLinks[i];

				if (link.isBase()) {
					m_bounds[LB_LENGTH_BND].check(link.m_length);
					m_bounds[LB_WIDTH_BND].check(link.m_size);
				} else if (link.isMixed()) {
					m_bounds[LM_LENGTH_BND].check(link.m_length);
					m_bounds[LM_WIDTH_BND].check(link.m_size);
				} else {
					m_bounds[L_LENGTH_BND].check(link.m_length);
					m_bounds[L_WIDTH_BND].check(link.m_size);
				}
			}

			float widthMin = Math.min(m_bounds[LB_WIDTH_BND].m_min,
					m_bounds[LM_WIDTH_BND].m_min), widthMax = Math.max(
					m_bounds[LB_WIDTH_BND].m_max, m_bounds[LM_WIDTH_BND].m_max), lenMin = Math
					.min(m_bounds[LB_LENGTH_BND].m_min,
							m_bounds[LM_LENGTH_BND].m_min), lenMax = Math.max(
					m_bounds[LB_LENGTH_BND].m_max,
					m_bounds[LM_LENGTH_BND].m_max);

			m_bounds[L_ALLWIDTH_BND].setBounds(
					Math.min(m_bounds[L_WIDTH_BND].m_min, widthMin),
					Math.max(m_bounds[L_WIDTH_BND].m_max, widthMax));
			m_bounds[L_ALLLENGTH_BND].setBounds(
					Math.min(m_bounds[L_LENGTH_BND].m_min, lenMin),
					Math.max(m_bounds[L_LENGTH_BND].m_max, lenMax));

			n = s_bndNames.length;

			for (i = 0; i < n; i++) {
				log.info("{} bounds = {}", s_bndNames[i], m_bounds[i]);
			}

		}
	}

	/**
	 * Equalize an histogram from a value knowing its bounds and eventualy
	 * display it. The given bound ID is also used to retrieve the values to
	 * equalize.
	 * 
	 * @see #renormalizeHisto
	 * @param boundId
	 *            Index of the bound in the bound table.
	 * @param g
	 *            A Graphics to draw into or null to avoid visual feedback.
	 */
	protected void EQHisto(int boundId, Graphics g) {
		int[] histo = new int[10];
		int i, n = histo.length, max, j = 0;
		Bounds bounds = m_bounds[boundId], histoBnds = new Bounds(0, n - 1);
		Object[] objs = m_attributes;
		Class cls = ProtoAttribute.class;
		Field field = null;

		if (bounds.getWidth() > 5) {
			try {
				switch (boundId) {
				case A_WEIGHT_BND:
				case AB_WEIGHT_BND:
					field = cls.getDeclaredField("m_weight");
					break;

				case A_SIZE_BND:
				case AB_SIZE_BND:
				case A_ALLSIZE_BND:
					field = cls.getDeclaredField("m_size");
					break;

				case L_LENGTH_BND:
				case LM_LENGTH_BND:
				case LB_LENGTH_BND:
				case L_ALLLENGTH_BND:
					cls = AttributeLink.class;
					field = cls.getDeclaredField("m_length");
					objs = m_relaxLinks;
					break;

				case L_WIDTH_BND:
				case LM_WIDTH_BND:
				case LB_WIDTH_BND:
				case L_ALLWIDTH_BND:
					cls = AttributeLink.class;
					field = cls.getDeclaredField("m_size");
					objs = m_relaxLinks;
					break;
				}

				n = objs.length;
				Arrays.fill(histo, 0);

				for (i = 0; i < n; i++) {
					j = (int) bounds.project(field.getInt(objs[i]), histoBnds);
					if (j < 0)
						j = 0;
					histo[j]++;
				}

				max = 0;
				n = histo.length;

				for (i = 0; i < n; i++) {
					if (histo[i] > max)
						max = histo[i];
				}

				if (g != null) {
					int histoWidth = 200, histoHeight = 200, dx = 10, dy = 10, x, y = 0, h, w = (int) Math
							.ceil(histoWidth / (float) n);

					g.clearRect(0, 0, histoWidth + dx, histoHeight + dy + 50);
					g.setColor(Color.white);

					for (i = 0; i < n; i++) {
						x = (i * histoWidth) / n;
						h = (histoHeight * histo[i]) / max;
						g.fillRect(dx + x, dy + histoHeight + y - h, w, h);
					}
					g.drawString(s_bndNames[boundId] + bounds, dx, dy
							+ histoHeight + 20);
				}

				// cliping of the values
				renormalizeHisto(histo, objs, field, bounds, max);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("displayHisto [{}] error : {}", boundId, e);
			}
		}
	}

	/**
	 * Renormalize an histogram to lessen variations.
	 * 
	 * @param histo
	 *            An histogram array containing the number of value for each
	 *            intervals.
	 * @param objs
	 *            An array of Objects holding the value to renormalize matching
	 *            that generates histo.
	 * @param field
	 *            The value of the Objects to transform.
	 * @param bounds
	 *            Min and max of the values to remap.
	 * @param hMax
	 *            Maximum number of value in an interval of the histogram.
	 * @throws IllegalAccessException
	 */
	private void renormalizeHisto(int[] histo, Object[] objs, Field field,
			Bounds bounds, int hMax) throws IllegalAccessException {
		int i, j, n = histo.length, min = (int) bounds.m_min;
		float[] newBnds = new float[n];
		float q = min, pow = .5f, alpha, beta, dq, r, beg, end, qMax = .999f * n;

		// Hardcoded to .5 now!
		// if ( pow >= .9999f ) return; // don't remap anything
		// if ( pow < 0.f ) pow = 0.f; // collapse small values!

		alpha = 1.f / (1.f - pow);
		beta = alpha - 1.f;
		dq = bounds.getWidth() / n;
		r = dq / (alpha * hMax);
		beta *= hMax;

		for (i = 0; i < n; i++) {
			q += r * (histo[i] + beta);
			newBnds[i] = q;
		}

		log.info("filtering {} using a {} values histogram", bounds,
				histo.length);
		bounds.m_max = q;

		n = objs.length;
		int val;

		dq = 1.f / dq;

		for (i = 0; i < n; i++) {
			val = field.getInt(objs[i]);
			q = val <= min ? 0 : dq * (val - min);
			if (q > qMax)
				q = qMax;
			j = (int) q;
			q -= j;
			beg = j > 0 ? newBnds[j - 1] : min;
			end = newBnds[j];

			field.setInt(objs[i], (int) EZMath.interLin(q, beg, end));
		}

		log.info("new max bound is {}", bounds.m_max);
	}

	/**
	 * Returns a node Swatch from the server with a specified style.
	 * 
	 * @param style
	 *            an index to identifies a variant of a swatch:
	 *            <ul>
	 *            <li>[0] : normal rest swatch.</li>
	 *            <li>[1] : reference rest swatch.</li>
	 *            <li>[2] : normal current swatch.</li>
	 *            <li>[3] : reference current swatch.</li>
	 *            </ul>
	 * @return A server node swatch matching the specified style.
	 */
	protected XSwatch getNodeSwatch(int style)
			throws com.socialcomputing.wps.server.planDictionnary.connectors.JMIException {
		return m_planReq.getSwatch(XSwatch.NODE_TYP, style);
	}

	/**
	 * Returns a link Swatch from the server with a specified style.
	 * 
	 * @param style
	 *            an index to identifies a variant of a swatch:
	 *            <ul>
	 *            <li>[0] : normal rest swatch.</li>
	 *            <li>[1] : reference rest swatch.</li>
	 *            <li>[2] : normal current swatch.</li>
	 *            <li>[3] : reference current swatch.</li>
	 *            </ul>
	 * @return A server link swatch matching the specified style.
	 */
	protected XSwatch getLinkSwatch(int style)
			throws com.socialcomputing.wps.server.planDictionnary.connectors.JMIException {
		return m_planReq.getSwatch(XSwatch.LINK_TYP, style);
	}

	/**
	 * Filters links to keep only the most interesting for the relaxation. This
	 * magical formula should be teleported in the Analysis process...one day...
	 * First, links are separated in 3 categories : base, mixed and ext and
	 * counted. Then the same is done with attributes and their inner links
	 * table. Each attribute link table is sorted so base is first then mix and
	 * ext. In each subcategory, links are sorted accordingly to their
	 * 'importance'. Second, for each attributes, the best links are choosen.
	 * The number of links to choose depend on the XXXLinkMin Dictionary
	 * settings. Then all relaxable links of the Plan are sorted accordingly to
	 * their 'importance'. To ensure not too much links were created by the
	 * second step, try to remove the links in base attributes that are more
	 * numerous than baseLinkMax Dictionary settings. If parts of the base are
	 * separated, add necessary links to ensure connexity. Finaly, adds the best
	 * links of each categories. The number of links to add depends on the
	 * XXXLinkKeep Dictionary settings.
	 * 
	 * @see #s_linkCmp
	 */
	private void filterLinks() {
		int i, j, m, n = m_relaxLinks.length, baseCnt = 0, mixCnt = 0, extCnt = 0;
		AttributeLink link;

		// count the base|mixed|ext links
		for (i = 0; i < n; i++) {
			link = m_relaxLinks[i];

			if (link.isBase()) {
				baseCnt++;
			} else if (link.isMixed()) {
				mixCnt++;
			} else {
				extCnt++;
			}
		}

		ProtoAttribute att;
		ProtoAttribute[] atts = new ProtoAttribute[m_attributes.length];
		int attBaseCnt = 0, attExtCnt = 0;

		n = m_attributes.length;

		// Eval the max|min number of base|mixed|ext links / att. And count the
		// base|ext atts
		for (i = j = 0; i < n; i++) {
			att = m_attributes[i];

			if (att.m_parent == null) {
				if (att.isBase())
					attBaseCnt++;
				else
					attExtCnt++;

				// Sort the links of the atts and count the different types
				// (base, mixed, ext)
				att.setLinksCnts(m_mapDat);
				atts[j++] = att;
			}
		}

		n = j;

		// For each att, select the best min links
		for (i = 0; i < n; i++) {
			atts[i].setMinLinks();
		}

		n = m_relaxLinks.length;

		// To ensure the best links are kept, they are sorted accordingly.
		Arrays.sort(m_relaxLinks, 0, n, s_linkCmp);

		// For each links, check if it is not supernumerary for one of its atts.
		// If so, then remove it and find another one if necessary
		for (i = n - 1; i >= 0; i--) {
			link = m_relaxLinks[i];
			if (!link.m_isFiltered && !link.isMixed())
				link.updateSupernumerary();
		}

		// Find the link filtered that shouldn't if we want the base to stay
		// connex then "unfilter" them.
		for (i = 0; i < baseCnt; i++) {
			link = m_relaxLinks[i];
			if (link.m_isFiltered && !link.isLinkFiltrable(this))
				link.m_isFiltered = false;
		}

		m = (int) (m_mapDat.m_baseLinkKeep * attBaseCnt);
		n = m > baseCnt ? baseCnt : m;

		// Add the best links of the base
		for (i = 0; i < n; i++) {
			m_relaxLinks[i].activateIfPossible();
		}

		m = (int) (m_mapDat.m_mixedLinkKeep * attExtCnt);
		n = baseCnt + m > mixCnt ? mixCnt : m;

		// Add the best links of the mixed
		for (i = baseCnt; i < n; i++) {
			m_relaxLinks[i].activateIfPossible();
		}

		m = (int) (m_mapDat.m_extLinkKeep * attExtCnt);
		n = baseCnt + mixCnt + m > extCnt ? extCnt : m;

		// Add the best ext links
		for (i = baseCnt + mixCnt; i < n; i++) {
			m_relaxLinks[i].activateIfPossible();
		}
	}

	/**
	 * Sets a marker in each relaxable links so it's easier to visit the graph.
	 * 
	 * @param isMarked
	 *            True to enable the marker, false to disable it.
	 */
	protected void setLinksMarker(boolean isMarked) {
		int i, n = m_relaxLinks.length;

		for (i = 0; i < n; i++) {
			m_relaxLinks[i].m_isMarked = isMarked;
		}
	}
}
