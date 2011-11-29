package com.socialcomputing.wps.server.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import java.util.Hashtable;

import com.socialcomputing.utils.geom.relax.CrossMgr;
import com.socialcomputing.utils.geom.relax.LinkRelaxData;
import com.socialcomputing.utils.geom.relax.Node;
import com.socialcomputing.utils.geom.relax.NodeRelaxData;
import com.socialcomputing.utils.geom.relax.RelaxerNG;
import com.socialcomputing.utils.geom.triangle.Delaunay;
import com.socialcomputing.utils.geom.triangle.QuadEdge;
import com.socialcomputing.wps.client.applet.ActiveZone;
import com.socialcomputing.wps.client.applet.BagZone;
import com.socialcomputing.wps.client.applet.LinkZone;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.client.applet.Swatch;
import com.socialcomputing.wps.server.swatchs.XSwatch;
import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds;
import com.socialcomputing.utils.math.Bounds2D;
import com.socialcomputing.utils.math.EZMath;

/**
 * <p>
 * Title: Mapper
 * </p>
 * <p>
 * Description: This manages the Plan generation through different stages.<br>
 * The stages are:
 * <ul>
 * <li>Base relax : Relaxation of the base without much constraints to give more
 * freedom to attributes.</li>
 * <li>Base filter : Quick relaxation of the base with higher constraints to
 * avoid covering and intersections. This stage give the final position of the
 * base attributes so they are locked.</li>
 * <li>Ext relax : Relaxation of the external attributes without much
 * constraints to give more freedom to attributes. The external attributes are
 * repulsed by the circle surrounding the base.</li>
 * <li>Ext filter : Relaxation of the external attributes with more constraints.
 * </li>
 * <li>Ext last : Relaxation of the external attributes with highest constraints
 * to avoid covering and intersections. The external attributes are repulsed by
 * the base attributes, not the surounding circle.</li>
 * <li>Tesselation : Delaunay triangulation is applied to creates new links
 * including fake ones.</li>
 * <li>Zonisation : Converts this graphical objects to their WPSApplet zone
 * equivalent. This is not a graphical stage.</li>
 * <li>init swatchs : Retrieve swatchs and properties for each zone created.
 * This is not a graphical stage.</li>
 * </ul>
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
public class Mapper {

	private static final Logger log = LoggerFactory.getLogger(Mapper.class);

	/**
	 * Name of each stages as they will appears in the GUI.
	 */
	private static final String[] s_stages = new String[] { "Base relax", "Base filter", "Ext relax", "Ext filter",
			"Ext last", "Tesselation", };

	/**
	 * Index of the Base relax stage in the Stage table.
	 */
	protected static final int BASEREL_STG = 0;

	/**
	 * Index of the Base filter stage in the Stage table.
	 */
	protected static final int BASEFLT_STG = 1;

	/**
	 * Index of the Ext relax stage in the Stage table.
	 */
	protected static final int EXTREL_STG = 2;

	/**
	 * Index of the Ext filter stage in the Stage table.
	 */
	protected static final int EXTFLT_STG = 3;

	/**
	 * Index of the Ext last stage in the Stage table.
	 */
	protected static final int EXTLST_STG = 4;

	/**
	 * Index of the Tesselation stage in the Stage table.
	 */
	protected static final int TESSELATION_STG = 5;

	/**
	 * Plan data given by the Analysis. This data is used to create this Mapper.
	 */
	protected ProtoPlan m_protoPlan;

	/**
	 * WPSApplet Plan equivalent to this or m_protoplan. Raw analysis data =
	 * m_protoPlan ==> graphical relaxation data = this Mapper ==> WPSApplet
	 * final data = m_plan
	 */
	protected Plan m_plan;

	/**
	 * Graphical relaxation ProtoAttributes equivalent table.
	 */
	protected NodeMapData[] m_nodes;

	/**
	 * Graphical relaxation AttributeLinks equivalent table.
	 */
	protected LinkMapData[] m_links;

	/**
	 * Fake node used to simulate a repulsive circle around the base attributes.
	 */
	protected NodeRelaxData m_base;

	/**
	 * A temp copy of m_links used by the tesselation stage.
	 */
	protected LinkRelaxData[] m_oldLinksDat;

	/**
	 * Number of nodes in base.
	 */
	protected int m_baseNodeCnt;

	/**
	 * Number of subNodes (clusterized children) in this Plan.
	 */
	protected int m_subNodeCnt;

	/**
	 * Number of relaxable links in base.
	 */
	protected int m_baseLinkCnt;

	/**
	 * A reference to a relaxer that will do the real relaxation job.
	 */
	protected RelaxerNG m_relaxer;

	/**
	 * A reference to a Delaunay that will do the real tesselation job.
	 */
	protected Delaunay m_delaunay;

	/**
	 * A reference to a Cross manager that will do the real intersection
	 * detection / uncrossing job.
	 */
	protected CrossMgr m_crossMgr;

	/**
	 * Number of steps of the current stage.
	 */
	protected int m_stepCnt;

	/**
	 * Maximum number of iterations of the current step.
	 */
	protected int m_iterCnt;

	/**
	 * Current stage ID.
	 */
	protected int m_stage;

	/**
	 * Current step count of the current stage.
	 */
	protected int m_step;

	/**
	 * Current iteration count of the current step.
	 */
	protected int m_iter;

	/**
	 * Current last node to relax. That means all nodes of the table are relaxed
	 * from the first to the m_curNode one.
	 */
	protected int m_curNode;

	/**
	 * Current relaxation error evaluated at each relax steps.
	 */
	private float m_err;

	/**
	 * Relax error treshold for this stage.
	 */
	protected float m_trsh = .00001f;// .05f;

	/**
	 * Fake links (those going outside of the Plan) table.
	 */
	protected LinkFakeData[] m_fakeLinks;

	/**
	 * Creates a new Mapper using a ProtoPlan. This initialize graphical data
	 * according to the ProtoPlan data.
	 * 
	 * @param plan
	 *            A ProtoPlan holding the Attributes and Links used by the
	 *            Mapper.
	 */
	public Mapper(ProtoPlan plan) {
		EZTimer timer = new EZTimer();

		m_protoPlan = plan;
		m_plan = new Plan();

		plan.m_mapDat.init(this);

		if (!m_protoPlan.m_isDegenerated) {
			m_crossMgr = new CrossMgr();
			m_relaxer = new RelaxerNG(m_crossMgr);
		}

		timer.showElapsedTime("Mapper initialized");
	}

	/**
	 * Gets the name of the current stage. This is used for debug purpose.
	 * 
	 * @return The name of the current stage or "" if the current stage is not
	 *         defined.
	 */
	public String getStage() {
		return m_stage < 0 ? "" : s_stages[m_stage];
	}

	/**
	 * Generate a new Plan by executing all the stages.
	 */
	public void generatePlan() throws com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException {
		EZTimer timer = new EZTimer();

		if (!m_protoPlan.m_isDegenerated) {
			run();
			toZones();
		} else {
			toDegeneratedZone();
		}
		setIntrinsicProps();
		timer.showElapsedTime("All stages");

		initNodesSwatchs();
		timer.showElapsedTime("initNodesSwatchs");

		if (!m_protoPlan.m_isDegenerated) {
			initLinksSwatchs();
			timer.showElapsedTime("initLinksSwatchs");
		}
	}

	/**
	 * Initialize graphical nodes and links. Sorts nodes using their weight.
	 * 
	 * @param winBnds
	 *            Not used in this implementation.
	 */
	public void initData(Bounds2D winBnds) {
		EZMath.resetSeed();

		MapData mapDat = m_protoPlan.m_mapDat;

		initNodes();
		initLinks();

		int i, n = m_nodes.length;

		for (i = 0; i < n; i++) {
			m_nodes[i].initLinks();
		}

		for (i = 0; i < n; i++) {
			m_nodes[i].init(mapDat);
		}

		// Nodes reordering
		Comparator comp = new Comparator() {
			public int compare(Object o1, Object o2) {
				Node n1 = (Node) o1, n2 = (Node) o2;
				float o = n2.getWeight() - n1.getWeight();

				return o > 0 ? 1 : (o < 0 ? -1 : 0);
			}
		};

		Arrays.sort(m_nodes, 0, m_baseNodeCnt, comp);

		m_delaunay = null;
		m_stage = -1;
		m_base = null;
	}

	/**
	 * Initialize graphical data and execute all graphical stages. For each
	 * stage, execute all the steps. For each steps, execute all the iterations.
	 */

	private void run() {
		EZTimer timer = new EZTimer();
		EZFlags flags = new EZFlags();

		initData(m_protoPlan.m_mapDat.m_winBnds);

		while (initStage()) {
			while (initStep()) {
				while (initIter()) {
					iterate(flags);
				}
			}
			timer.showElapsedTime(getStage());
		}
	}

	/**
	 * Initialize data specific to the current stage (counters...). The current
	 * stage is first incremented.
	 * 
	 * @return False if the current stage is undefined (to end the process).
	 */
	public boolean initStage() {
		// MapData mapDat = m_protoPlan.m_mapDat;

		m_stage++;
		m_step = -1;

		switch (m_stage) {
		case BASEREL_STG:
			setStageParams(m_baseNodeCnt - 1, null, true);
			m_nodes[0].initPos(new Vertex()); // We must init the initial node
			break;

		case BASEFLT_STG:
			setStageParams(1, null, false);
			break;

		case EXTREL_STG:
			m_base = new NodeRelaxData(null, 1, 1, 1, 1, true, true, "base");
			setStageParams(m_nodes.length - m_baseNodeCnt, m_base, false);
			m_relaxer.updateBoundingBase(0, m_baseNodeCnt);
			m_relaxer.setLinksFlags(0, m_protoPlan.m_baseLinkCnt, LinkRelaxData.LOCKED_BIT);
			m_relaxer.setNodesLock(0, m_baseNodeCnt, true, false);
			break;

		case EXTFLT_STG:
			setStageParams(1, m_base, false);
			break;

		case EXTLST_STG:
			m_relaxer.setNodesLock(0, m_baseNodeCnt, true, true);
			setStageParams(1, null, false);
			break;

		case TESSELATION_STG:
			m_stepCnt = 2;
			m_relaxer.alignData(m_protoPlan.m_mapDat.m_winBnds, m_base);
			m_delaunay = new Delaunay(m_relaxer.getDataNodes(), m_relaxer.getBounds(), 5);
			m_oldLinksDat = m_relaxer.getDataLinks();
			break;

		default:
			return false;
		}

		return true;
	}

	/**
	 * Initialize data specific to the current step (counters...). The current
	 * step is first incremented.
	 * 
	 * @return False if the current step has reach its maximum (to go to the
	 *         next stage).
	 */
	public boolean initStep() {
		m_step++;

		if (m_step < m_stepCnt) {
			m_iter = 0;

			switch (m_stage) {
			case BASEREL_STG:
				setStepParams(m_step + 1);
				return true;

			case BASEFLT_STG:
				m_curNode = m_baseNodeCnt - 1;
				return true;

			case EXTREL_STG:
				setStepParams(m_baseNodeCnt + m_step);
				return true;

			case EXTFLT_STG:
				m_curNode = m_nodes.length - 1;
				return true;

			case EXTLST_STG:
				return true;

			case TESSELATION_STG:
				m_iter = 0;
				m_iterCnt = 1;
				return true;
			}
		}

		return false;
	}

	/**
	 * Increment the current iteration.
	 * 
	 * @return False if the iteration as reach its maximum or the relax error is
	 *         less than its treshold.
	 */
	public boolean initIter() {
		return m_iter++ < m_iterCnt && (m_iter < 5 || m_err > m_trsh);
	}

	/**
	 * Execute the current iteration.
	 * 
	 * @flags Not used by this implementation.
	 */
	public void iterate(EZFlags flags) {
		switch (m_stage) {
		case BASEREL_STG:
			setIterParams(0, false);
			break;

		case BASEFLT_STG:
			setIterParams(0, true);
			break;

		case EXTREL_STG:
			setIterParams(0, false);
			break;

		case EXTFLT_STG:
			setIterParams(0, true);
			break;

		case EXTLST_STG:
			setIterParams(0, true);
			break;

		case TESSELATION_STG:
			try {
				if (m_step == 0)
					tesselate();
				else
					normalizeData();
			} catch (com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	// =====================================================================================================
	// ================================ PROTECTED
	// ==========================================================
	// =====================================================================================================

	/**
	 * Initialize the Mapper node table by creating an equivalent of each
	 * cluster attributes. It also evaluate the number of base clusters.
	 */
	protected void initNodes() {

		ProtoAttribute[] atts = m_protoPlan.m_attributes;
		ProtoAttribute att;
		NodeMapData nodeDat;
		MapData mapDat = m_protoPlan.m_mapDat;
		int i, j, n = atts.length;
		// boolean isInBase = true;
		NodeMapData[] nodes = new NodeMapData[n];

		m_baseNodeCnt = 0;

		for (i = j = 0; i < n; i++) {
			att = atts[i];

			if (att.m_parent == null) {
				nodeDat = new NodeMapData(att, j, mapDat);
				nodes[j++] = nodeDat;
				att.setMapData(nodeDat);

				if (att.isBase()) {
					m_baseNodeCnt = j;
				}
			}
			// else this att is clusterized and so useless for the relaxation!
		}

		m_nodes = new NodeMapData[j];
		System.arraycopy(nodes, 0, m_nodes, 0, j);

		log.info("{} NodeMapData were created, {} in base", String.valueOf(m_nodes.length), m_baseNodeCnt);
	}

	/**
	 * Initialize the Mapper link table by creating an equivalent of each
	 * AttributeLinks.
	 */
	protected void initLinks() {
		MapData mapDat = m_protoPlan.m_mapDat;
		AttributeLink[] links = m_protoPlan.m_relaxLinks;
		AttributeLink link;
		LinkMapData linkDat;
		int i, n = links.length;

		m_links = new LinkMapData[n];

		for (i = 0; i < n; i++) {
			link = links[i];
			linkDat = new LinkMapData(link, mapDat);
			m_links[i] = linkDat;
		}

		log.info("links created");
	}

	/**
	 * Tesselate the nodes and adds the resulting links to the existing ones if
	 * they don't intersect. Fake links are also added and stored in
	 * m_fakeLinks, by creating fake nodes outside screen before tesselation.
	 * Before tesselating, all remaining crossing links are filtered. New links
	 * are added only if they exists in the ProtoPlan and the angle between them
	 * and previous links is > PI/8.
	 */
	protected void tesselate() throws com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException {
		ArrayList edges = m_delaunay.process().edges(), fakeEdges = new ArrayList();
		QuadEdge edge;
		NodeRelaxData fromDat, toDat;
		NodeMapData from, to;
		Localisable fromLoc, toLoc;
		AttributeLink attLink;
		LinkRelaxData[] tessLinks = new LinkRelaxData[3 * m_nodes.length], links = m_oldLinksDat;
		int[] inters = new int[1];
		LinkRelaxData linkDat;
		LinkMapData link;
		MapData mapDat = m_protoPlan.m_mapDat;
		int i, j, n = edges.size(), m = 0, size, linkCnt = m_links.length;
		Bounds widthBnd = m_protoPlan.m_bounds[ProtoPlan.L_ALLWIDTH_BND], lenBnd = m_protoPlan.m_bounds[ProtoPlan.L_ALLLENGTH_BND];
		boolean isFakeFrom, isFakeTo;
		// Node fromNode, toNode;
		float alpha = EZMath.PI / 8; // Minimal angle between Triangulation and
										// relaxation links
		boolean displayFakeLinks = this.m_protoPlan.m_planReq.getModel().m_DisplayFakeLinks;

		m_crossMgr.clearInters();
		m_crossMgr.evalInters(m_relaxer.getDataNodes(), m_relaxer.getDataLinks(), true);
		m_crossMgr.filter(true);

		for (i = m = 0; i < n; i++) {
			edge = (QuadEdge) edges.get(i);
			fromLoc = edge.getFrom();
			toLoc = edge.getTo();
			isFakeFrom = fromLoc == null;
			isFakeTo = toLoc == null;

			if (!isFakeFrom && !isFakeTo) {
				fromDat = (NodeRelaxData) fromLoc;
				toDat = (NodeRelaxData) toLoc;
				linkDat = fromDat.getLinkDataTo(toDat);
				inters[0] = -1;

				if ((linkDat == null)// || linkDat.isDead()) // this link
										// doesn't already exists or has been
										// removed
						&& m_crossMgr.hasOneInterMax(fromDat, toDat, links, inters)) {
					from = (NodeMapData) fromDat.getNode();
					to = (NodeMapData) toDat.getNode();
					attLink = from.getLinkTo(to);

					if (attLink != null) // this link exists but is too weak to
											// be relaxable, let's add it.
					{
						j = inters[0];
						size = j == -1 ? 0 : ((LinkMapData) (links[j].getSource())).m_link.m_size;

						if (j == -1 || size < attLink.m_size) {
							link = new LinkMapData(attLink, mapDat);
							linkDat = new LinkRelaxData(link);

							if (linkDat.hasSpace(alpha)) {
								if (j == -1)
									tessLinks[m++] = linkDat;
								else
									links[j] = linkDat;
								link.setRelaxData(linkDat);
								widthBnd.check(attLink.m_size);
								lenBnd.check(attLink.m_length);
							}
						}
					}
				}
			} else {
				if ((!isFakeFrom || !isFakeTo) && displayFakeLinks) {
					fakeEdges.add(new LinkFakeData(edge, isFakeFrom));
				}
			}
		}

		m_fakeLinks = (LinkFakeData[]) fakeEdges.toArray(new LinkFakeData[fakeEdges.size()]);
		n = linkCnt + m;
		links = new LinkRelaxData[n];

		System.arraycopy(m_relaxer.getDataLinks(), 0, links, 0, linkCnt);
		System.arraycopy(tessLinks, 0, links, linkCnt, m);
		m_relaxer.setDataLinks(links);

		AttributeLink[] attLinks = new AttributeLink[n];

		for (i = 0; i < n; i++) {
			attLinks[i] = ((LinkMapData) links[i].getSource()).m_link;
		}

		m_protoPlan.m_relaxLinks = attLinks;
	}

	/**
	 * Renormalize graphics value of nodes and links by equalizing their
	 * histogram. This transformation keep order but lessen differences between
	 * high and low values. Link width and lenght and Node size are transformed.
	 */
	protected void normalizeData() {
		AttributeLink[] links = m_protoPlan.m_relaxLinks;
		MapData mapDat = m_protoPlan.m_mapDat;
		LinkMapData link;
		int i, n = links.length;

		// FRV m_protoPlan.EQHisto( ProtoPlan.L_ALLWIDTH_BND, null );
		m_protoPlan.EQHisto(ProtoPlan.L_ALLLENGTH_BND, null);
		m_protoPlan.EQHisto(ProtoPlan.A_ALLSIZE_BND, null);

		// renormalize the links width
		for (i = 0; i < n; i++) {
			link = links[i].getMapData();
			link.syncRelaxData(mapDat);
		}
	}

	/**
	 * Creates the WPSApplet Plan in the degenerate case (only one cluster in
	 * the Plan).
	 */
	protected void toDegeneratedZone() {
		NodeMapData node = new NodeMapData(m_protoPlan.m_attributes[0], m_protoPlan.m_mapDat.m_winBnds);

		m_nodes = new NodeMapData[] { node };
		m_subNodeCnt = m_protoPlan.m_attributes.length - 1;
		m_plan.m_nodes = new ActiveZone[m_protoPlan.m_attributes.length];
		m_curNode = 1;
		m_plan.m_nodes[0] = node.toZone(this, 0);
		m_plan.m_links = new ActiveZone[0];
		m_plan.m_linksCnt = 0;
	}

	/**
	 * Creates the WPSApplet Plan using the Mapper nodes and links. Links are
	 * sorted using the LinkMapData.s_comp comparator.
	 */
	protected void toZones() throws com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException {
		int i, j, n = m_nodes.length, m = m_fakeLinks.length;
		// ActiveZone zone;
		// NodeMapData node;

		m_relaxer.updateData(m_protoPlan.m_mapDat.m_winBnds);

		m_subNodeCnt = m_protoPlan.m_attributes.length - m_nodes.length;
		m_plan.m_nodes = new ActiveZone[m_protoPlan.m_attributes.length];
		m_curNode = n;

		for (i = 0; i < n; i++) {
			m_plan.m_nodes[i] = m_nodes[i].toZone(this, i);
		}

		LinkRelaxData[] linksDat = m_relaxer.getDataLinks();
		// LinkRelaxData linkDat;

		n = linksDat.length;
		m_links = new LinkMapData[n];
		m_plan.m_links = new ActiveZone[n + m];

		for (i = 0; i < n; i++) {
			m_links[i] = (LinkMapData) linksDat[i].getSource();
		}

		Arrays.sort(m_links, LinkMapData.s_comp);

		boolean display = this.m_protoPlan.m_planReq.getModel().m_DisplayEmptyLinks;
		for (i = 0; i < n; i++) {
			m_plan.m_links[i] = m_links[i].toZone(this, display);
		}

		// Number of real links (excluding fakes links)
		m_plan.m_linksCnt = n;

		for (j = i, i = 0; i < m; i++, j++) {
			m_plan.m_links[j] = m_fakeLinks[i].toZone(this);
		}
	}

	private void setIntrinsicProps() {
		int n = m_plan.m_nodes.length;
		for (int i = 0; i < n; ++i) {
			m_plan.m_nodes[i].put("_INDEX", new Integer(i));
		}
		n = m_plan.m_links.length;
		for (int i = 0; i < n; ++i) {
			LinkZone linkZone = (LinkZone) m_plan.m_links[i];
			linkZone.put("_INDEX", new Integer(i));
			if (linkZone.m_from != null) {
				BagZone bagZone = (BagZone) linkZone.m_from;
				int nbindexes = 1 + (bagZone.m_subZones != null ? bagZone.m_subZones.length : 0);
				Integer indexes[] = new Integer[nbindexes];
				indexes[0] = (Integer) bagZone.get("_INDEX");
				for (int j = 1; j < nbindexes; ++j)
					indexes[j] = (Integer) bagZone.m_subZones[j - 1].get("_INDEX");
				linkZone.put("_NODE1", indexes);
			}
			if (linkZone.m_to != null) {
				BagZone bagZone = (BagZone) linkZone.m_to;
				int nbindexes = 1 + (bagZone.m_subZones != null ? bagZone.m_subZones.length : 0);
				Integer indexes[] = new Integer[nbindexes];
				indexes[0] = (Integer) bagZone.get("_INDEX");
				for (int j = 1; j < nbindexes; ++j)
					indexes[j] = (Integer) bagZone.m_subZones[j - 1].get("_INDEX");
				linkZone.put("_NODE2", indexes);
			}
		}
	}

	/**
	 * Initialize the node Swatchs and properties. This includes the WPSApplet
	 * BagZones (cluster attributes) and ActiveZones (clusterized attributes).
	 * While the "normal" properties are set, the "inter" properties bounds are
	 * evaluated. Then the automatic "inter" properties can be set.
	 */
	protected void initNodesSwatchs()
			throws com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException {
		EZTimer timer = new EZTimer();
		// NodeMapData sNode;
		ProtoAttribute att;
		int i, m = m_nodes.length, n = m_plan.m_nodes.length;
		XSwatch[] nodeSSwh;
		XSwatch[][] serverSwatchs = new XSwatch[][] { { // Normal Node
														// ServerSwatchs
						m_protoPlan.getNodeSwatch(0), m_protoPlan.getNodeSwatch(2) }, { // Reference
																						// Node
																						// ServerSwatchs
						m_protoPlan.getNodeSwatch(1), m_protoPlan.getNodeSwatch(3) } };

		for (i = 0; i < n; i++) {
			att = i < m ? (ProtoAttribute) m_nodes[i].m_att : (ProtoAttribute) m_plan.m_nodes[i].get("ATT");
			nodeSSwh = getSSwatchs(serverSwatchs, att.isRef());

			m_protoPlan.m_planReq.putAttributeProps(att, nodeSSwh[0], nodeSSwh[1], m_plan.m_nodes[i]);

			m_protoPlan.m_props.put(att.m_strId, m_plan.m_nodes[i]);
		}

		for (i = 0; i < n; i++) {
			att = i < m ? (ProtoAttribute) m_nodes[i].m_att : (ProtoAttribute) m_plan.m_nodes[i].get("ATT");
			nodeSSwh = getSSwatchs(serverSwatchs, att.isRef());

			m_protoPlan.m_planReq.updateInterProps(att, nodeSSwh[0], nodeSSwh[1], m_plan.m_nodes[i]);
		}

		Swatch[] nodeSwh;
		Swatch[][] clientSwatchs = new Swatch[][] { { // Normal Node
														// ClientSwatchs
						(Swatch) serverSwatchs[0][0].toClient(), (Swatch) serverSwatchs[0][1].toClient() }, { // Reference
																												// Node
																												// ClientSwatchs
						(Swatch) serverSwatchs[1][0].toClient(), (Swatch) serverSwatchs[1][1].toClient() } };

		for (i = 0; i < n; i++) {
			att = i < m ? (ProtoAttribute) m_nodes[i].m_att : (ProtoAttribute) m_plan.m_nodes[i].get("ATT");
			nodeSwh = getSwatchs(clientSwatchs, att.isRef());

			m_plan.m_nodes[i].setSwatchs(nodeSwh[0], nodeSwh[1]);

			if (n >= m) {
				m_plan.m_nodes[i].remove("ATT");
			}
		}
		m_plan.m_nodesCnt = m;

		timer.showElapsedTime("Nodes Swatch&Props init");
	}

	/**
	 * Initialize the link Swatchs and properties for the WPSApplet (LinkZones).
	 */
	protected void initLinksSwatchs()
			throws com.socialcomputing.wps.server.planDictionnary.connectors.WPSConnectorException {
		EZTimer timer = new EZTimer();
		LinkMapData sLink;
		AttributeLink link;
		int i, n = m_links.length;
		XSwatch[] linkSSwh;
		XSwatch[][] serverSwatchs = new XSwatch[][] { { // Normal Link
														// ServerSwatchs
				m_protoPlan.getLinkSwatch(0), m_protoPlan.getLinkSwatch(2), }, { // Reference
																					// Link
																					// ServerSwatchs
				m_protoPlan.getLinkSwatch(1), m_protoPlan.getLinkSwatch(3), } };

		for (i = 0; i < n; i++) {
			sLink = m_links[i];
			link = (AttributeLink) sLink.m_link;
			linkSSwh = getSSwatchs(serverSwatchs, link.isRef());

			m_protoPlan.m_planReq.putAttLinkProps(link, linkSSwh[0], linkSSwh[1], m_plan.m_links[i]);
		}

		linkSSwh = getSSwatchs(serverSwatchs, false);

		for (n = i + m_fakeLinks.length; i < n; i++) {
			m_protoPlan.m_planReq.putAttLinkProps(null, linkSSwh[0], linkSSwh[1], m_plan.m_links[i]);
		}

		Swatch[] linkSwh;
		Swatch[][] clientSwatchs = new Swatch[][] { { // Normal Node
						// ClientSwatchs
						(Swatch) serverSwatchs[0][0].toClient(),
						serverSwatchs[0][1] == null ? null : (Swatch) serverSwatchs[0][1].toClient() }, { // Reference
						// Node
						// ClientSwatchs
						(Swatch) serverSwatchs[1][0].toClient(),
						serverSwatchs[1][1] == null ? null : (Swatch) serverSwatchs[1][1].toClient() } };

		n = m_links.length;

		for (i = 0; i < n; i++) {
			sLink = m_links[i];
			linkSwh = getSwatchs(clientSwatchs, sLink.isRef());

			m_plan.m_links[i].setSwatchs(linkSwh[0], linkSwh[1]);
		}

		linkSwh = getSwatchs(clientSwatchs, false);

		for (n = i + m_fakeLinks.length; i < n; i++) {
			m_plan.m_links[i].setSwatchs(linkSwh[0], linkSwh[1]);
		}

		timer.showElapsedTime("Links Swatch&Props init");

	}

	// =====================================================================================================
	// ================================ PRIVATE
	// ============================================================
	// =====================================================================================================

	private void setStageParams(int stepCnt, NodeRelaxData base, boolean needData) {
		MapData mapDat = m_protoPlan.m_mapDat;

		m_stepCnt = stepCnt;
		m_iterCnt = mapDat.m_relaxParams[m_stage].m_iters;
		m_trsh = mapDat.m_relaxParams[m_stage].m_trsh;

		if (needData) {
			m_relaxer.setParams(mapDat.m_relaxParams[m_stage], base, m_nodes, m_links);
		} else {
			m_relaxer.setParams(mapDat.m_relaxParams[m_stage], base);
		}
	}

	protected void setStepParams(int curNode) {
		MapData mapDat = m_protoPlan.m_mapDat;

		m_curNode = curNode;
		m_relaxer.evalBBox(0, m_curNode, false);
		m_nodes[m_curNode].initPos(m_relaxer.getBounds().getCenter(), m_base);

		if (m_step == m_stepCnt - 1) {
			m_iterCnt = mapDat.m_relaxParams[m_stage].m_lastItrs;
			m_trsh = mapDat.m_relaxParams[m_stage].m_lastTrsh;
		}
	}

	private void setIterParams(int beg, boolean isFiltered) {
		m_err = m_relaxer.relaxe(beg, m_curNode, 1);
		m_relaxer.postProcess(beg, m_curNode, isFiltered, isFiltered);
	}

	private XSwatch[] getSSwatchs(XSwatch[][] swatchs, boolean isRef) {
		return isRef ? swatchs[1] : swatchs[0];
	}

	private Swatch[] getSwatchs(Swatch[][] swatchs, boolean isRef) {
		return isRef ? swatchs[1] : swatchs[0];
	}
}
