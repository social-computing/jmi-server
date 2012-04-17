package com.socialcomputing.wps.server.generator;

//import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Hashtable;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.socialcomputing.utils.EZParams;
import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.utils.math.EZMath;
import com.socialcomputing.wps.client.applet.Env;
import com.socialcomputing.wps.client.applet.Plan;
import com.socialcomputing.wps.server.affinityengine.RecommendationInterface;
import com.socialcomputing.wps.server.analysisengine.AnalysisProcess;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.webservices.PlanRequest;

//import com.socialcomputing.wps.server.generator.*;

/**
 * <p>
 * Title: PlanGenerator
 * </p>
 * <p>
 * Description: A generator of graphical Plan that can be displayed with the
 * WPSApplet.<br>
 * A valid protoPlan is needed as input and a Plan and an Env can be retrieved
 * as output. It has also a few debug features like the visualisation and
 * modification of the relaxation process, the saving of a plan (standalone and
 * serialized stream) and a simple test loop.
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
public class PlanGenerator {
	/**
	 * Output type for a raw serialized Env/Plan (not readable by the
	 * WPSApplet).
	 */
	private static final int SERIAL_OUT = 0;

	/**
	 * Output type for a GZIP serialized Env/Plan (readable by the WPSApplet).
	 */
	private static final int SERIALZ_OUT = 1;

	protected static final int XML_OUT = 2; // XML plan
	// protected static final int SVG_OUT = 3; // SVG : Adobe open format
	// protected static final int SVGZ_OUT = 4; // GZIP SVG
	// protected static final int SWFML_OUT = 5; // SWFML : XML version of SWF
	// protected static final int SWF_OUT = 6; // SWF : Flash4 format

	/**
	 * Manage the main() args.
	 */
	private static EZParams s_params;

	/**
	 * The real Plan generator class.
	 */
	private Mapper m_mapper;

	/**
	 * Creates a new PlanGenerator and reset its random generator seed.
	 */
	public PlanGenerator() {
		EZMath.resetSeed();
	}

	/**
	 * Generate a new Plan using a ProtoPlan and eventualy display the
	 * relaxation process. Before generating the new Plan, the ProtoPlan datas
	 * are process to extract statistical information and ensure its integrity.
	 * 
	 * @param plan
	 *            A ProtoPlan holding the plan information and a PlanRequest to
	 *            connect to the DB.
	 * @param isVisual
	 *            True to display the relaxation GUI.
	 */
	public void generatePlan(ProtoPlan plan, boolean isVisual)
			throws com.socialcomputing.wps.server.planDictionnary.connectors.JMIException {
		if (plan.init()) {
			plan.evalBounds();
			m_mapper = isVisual ? new VisualMapper(plan) : new Mapper(plan);
			m_mapper.generatePlan();
		}
	}

	/**
	 * Gets the WPSApplet Plan previously generated.
	 **/
	public Plan getPlan() {
		return m_mapper == null ? null : m_mapper.m_plan;
	}

	/**
	 * Gets the WPSApplet Env previously generated.
	 **/
	public Env getEnv() {
		return m_mapper == null ? null : m_mapper.m_protoPlan.m_env;
	}

	/**
	 * Pre step to retrieve a ProtoPlan using PlanParams.
	 * 
	 * @param planPrm
	 *            Parameters of the Plan to generate.
	 * @return A new ProtoPlan defined by planPrm.
	 */
	public ProtoPlan preGenerate(PlanParams planPrm)
			throws com.socialcomputing.wps.server.planDictionnary.connectors.JMIException {
		EZTimer timer = new EZTimer();

		planPrm.init();

		// SwatchLoaderHome swatchLoaderHome = (SwatchLoaderHome)
		// PortableRemoteObject.narrow( planPrm.m_loader, SwatchLoaderHome.class
		// );
		PlanRequest planRequest = new PlanRequest(planPrm.m_connection, planPrm.m_dico, planPrm.m_params);
		timer.showElapsedTime("create PlanRequest");

		// AFFINITY GROUP RETRIEVAL
		RecommendationInterface affinity = new RecommendationInterface(planRequest);
		Collection<String> affinityGroup = affinity.retrieveAffinityGroup();
		timer.showElapsedTime("retrieve AffinityGroup");

		// ANALYSIS ENGINE
		AnalysisProcess analysisEngine = new AnalysisProcess(planRequest, affinityGroup, affinity);
		timer.showElapsedTime("create analysisEngine");

		ProtoPlan proto = analysisEngine.getProtoPlan();
		timer.showElapsedTime("getProtoPlan");

		return proto;
	}

	/**
	 * Post step to save the Plan generated to a file. The file format depends
	 * on the 'output' command line param.
	 * 
	 * @param planPrm
	 *            Parameters of the generated Plan.
	 */
	public void postGenerate(PlanParams planPrm) {
		Env env = getEnv();
		Plan plan = getPlan();

		if (env != null && plan != null) {
			switch (s_params.getIntParameter("output", SERIALZ_OUT)) {
			case (SERIAL_OUT):
				writeObject(env, plan, planPrm, false);
				break;
			case (SERIALZ_OUT):
				writeObject(env, plan, planPrm, true);
				break;
			case (XML_OUT):
				writeXML(env, plan, planPrm);
				break;
			default:
				System.out.println("Wrong output type! [0-2]");
			}
		} else {
			System.out.println("Error : Env = " + env + ", Plan = " + plan);
		}
	}

	/**
	 * Gets a JDOM XML element that encapsulate a serialized standalone Plan.
	 * 
	 * @return A new 'properties' element describing a connection to scharon.
	 */
	public Element getProperties() {
		Element properties = new Element("properties"), parameters = new Element("parameters"), parameter = new Element(
				"parameter"), redirect = new Element("redirect");

		properties.setAttribute("name", "MapStan Search/MapStan Search");
		properties.setAttribute("server", "http://scharon:80/");
		properties.setAttribute("servlet", "/sengine");
		properties.setAttribute("url", "http://scharon:80/sengine");

		parameters.setAttribute("file", "fileName");

		parameter.setAttribute("attribute", "action");
		parameter.setAttribute("value", "uploadPlan");

		redirect.setAttribute("path", "/fr/splan.jsp?planId=");
		redirect.setAttribute("extra", "q=mapstan");

		parameters.addContent(parameter);
		properties.addContent(parameters);
		properties.addContent(redirect);

		return properties;
	}

	/**
	 * Magic header to identify a MapStan Plan.
	 */
	public static final byte[] s_header = { 'M', 'a', 'p', 'S', 't', 'a', 'n' };

	/**
	 * Gets a buffer containing a raw serialized Env+Plan.
	 * 
	 * @param env
	 *            The Env to serialize.
	 * @param plan
	 *            The Plan to serialize.
	 * @return A byte array containing raw serialized Objects.
	 * @throws IOException
	 */
	public byte[] getSerialPlan(Env env, Plan plan) throws IOException {
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(new GZIPOutputStream(baOut));

		objOut.writeObject(env);
		objOut.writeObject(plan);
		objOut.close();

		byte[] serPlan = baOut.toByteArray();

		baOut.close();

		return serPlan;
	}

	/**
	 * Writes a couple Env+Plan as a standalone MapStan plan and as a serialized
	 * Object compressed or not.
	 * 
	 * @param env
	 *            The Env to Write.
	 * @param plan
	 *            The Plan to write.
	 * @param planPrm
	 *            Parameters of this plan containing its ID.
	 * @param needComp
	 *            True if the serialized WPSApplet Plan should be compressed
	 *            using GZIP.
	 */
	public void writeObject(Env env, Plan plan, PlanParams planPrm, boolean visual) {
		try {
			String outPath = "C:/Documents and Settings/flugue/Desktop/temp/";
			FileOutputStream out = new FileOutputStream(outPath + planPrm.getPlanName() + ".mapstan");
			byte[] serPlan = getSerialPlan(env, plan);

			if (out != null) {
				out.write(s_header);

				ZipOutputStream zipOut = new ZipOutputStream(out);
				Element props = getProperties();
				XMLOutputter xmlOut = new XMLOutputter();
				Hashtable table = new Hashtable();

				table.put("PLAN_ID", String.valueOf(planPrm.m_id));
				table.put("PLAN", serPlan);

				zipOut.setLevel(Deflater.BEST_COMPRESSION);

				zipOut.putNextEntry(new ZipEntry("properties"));
				xmlOut.output(props, zipOut);

				zipOut.putNextEntry(new ZipEntry("data"));
				ObjectOutputStream objOut = new ObjectOutputStream(zipOut);
				objOut.writeObject(table);

				zipOut.flush();
				zipOut.close();
				out.close();
			}

			outPath = "../../../../../../classes/";
			out = new FileOutputStream(outPath + "plan.bin");

			if (out != null) {
				DataOutputStream outData = new DataOutputStream(out);
				outData.writeInt(serPlan.length);
				outData.write(serPlan);
				outData.flush();

				outData.close();
				out.close();
			}
		} catch (java.io.IOException e) {
			System.out.println("Serialisation to Client IO error " + e);
		}
	}

	public void writeXML(Env env, Plan plan, PlanParams planPrm) {
	}

}

/**
 * 
 * <p>
 * Title: PlanParams
 * </p>
 * <p>
 * Description: This simple container holds the parameters used to create
 * retrieve a ProtoPlan.
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

class PlanParams {
	/**
	 * URL of the server that will produce the ProtoPlan after analysis.
	 */
	protected String m_server;

	/**
	 * Name of the Plan (SEngine, Boosol...)
	 */
	protected String m_name;

	/**
	 * Type of the Plan (AnalysisProfile.XXX_PLAN).
	 */
	protected int m_type;

	/**
	 * Attribute or entity ID depending on the Plan type.
	 */
	protected int m_id;

	/**
	 * Connection to the WPS server.
	 */
	protected Connection m_connection;

	/**
	 * Dictionary holding the Plan description.
	 */
	protected WPSDictionary m_dico;

	/**
	 * SwatchLoader.
	 */
	protected Object m_loader;

	/**
	 * WPS Server parameters.
	 */
	protected Hashtable m_params;

	/**
	 * Creates a new PlanParams knowing the WPS server, the name and type of the
	 * Plan and the entity/attribute id.
	 * 
	 * @param server
	 *            WPS Server URL.
	 * @param name
	 *            Plan name (SEngine, Boosol...)
	 * @param type
	 *            Plan type (AnalysisProfile.XXX_PLAN)
	 * @param id
	 *            Id of the entity/attribute to make a Plan about.
	 */
	public PlanParams(String server, String name, int type, int id) {
		m_server = server;
		m_name = name;
		m_type = type;
		m_id = id;
	}

	/**
	 * Gets a filename describing this plan.
	 * 
	 * @return A string representation of this Plan.
	 */
	protected String getPlanName() {
		String type = (String) m_params.get("analysisProfile");

		return type == null ? m_name + "_" + m_id : m_name + "_" + type + "_" + m_id;
	}

	/**
	 * Gets this Plan parameters for the WPS Server.
	 * 
	 * @return A parameter table.
	 */
	protected Hashtable<String, Object> getParams() {
		Hashtable<String, Object> params = new Hashtable<String, Object>();

		params.put("language", "en");
		params.put("width", "800");
		params.put("height", "600");

		if (m_type == AnalysisProfile.PERSONAL_PLAN) {
			params.put("entityId", String.valueOf(m_id));
		} else if (m_type == AnalysisProfile.GLOBAL_PLAN) {
			params.put("entityId", String.valueOf(m_id));
			params.put("analysisProfile", "dailyPlan");
		} else if (m_type == AnalysisProfile.DISCOVERY_PLAN) {
			params.put("attributeId", String.valueOf(m_id));
			params.put("analysisProfile", "discoveryPlan");
		}

		return params;
	}

	/**
	 * Gets a connection to the WPS Server.
	 * 
	 * @return a new Conection to the Server.
	 */
	protected Connection getConnection() {
		String connectUrl = "jdbc:mysql://" + m_server + ":3306/WPS?user=boosol&password=boosol";

		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			return DriverManager.getConnection(connectUrl);
		} catch (java.lang.ClassNotFoundException e) {
			System.out.println("Can't find Driver " + e);
			return null;
		} catch (java.sql.SQLException e) {
			System.out.println("DriverManager can't get connection " + e);
			return null;
		}
	}

	/**
	 * Gets a Dictionary corresponding to the Plan name.
	 * 
	 * @return a new WPSDictionary.
	 */
	protected WPSDictionary getDictionnary() {
		return WPSDictionary.CreateTestInstance(m_name);
	}

	/**
	 * Gets a SwatchLoader using the WPS server URL.
	 * 
	 * @return a SwatchLoader using RMI.
	 */
	protected Object getSwatchLoader() {
		Hashtable envCtx = new Hashtable();

		envCtx.put(Context.INITIAL_CONTEXT_FACTORY, "com.evermind.server.rmi.RMIInitialContextFactory");
		envCtx.put(Context.PROVIDER_URL, "ormi://" + m_server + "/WPS");
		envCtx.put(Context.SECURITY_PRINCIPAL, "admin");
		envCtx.put(Context.SECURITY_CREDENTIALS, "youarehere");

		try {
			Context ctx = new InitialContext(envCtx);

			return ctx.lookup("WPSSwatchLoader");
		} catch (javax.naming.NamingException e) {
			System.out.println("Can't initialize Context " + e);

			return null;
		}
	}

	/**
	 * Initialize this inner fields using the constructor parameters.
	 * 
	 * @return True if the connection to the WPS Server is opened.
	 */
	protected void init() throws com.socialcomputing.wps.server.planDictionnary.connectors.JMIException {
		m_params = getParams();
		m_connection = getConnection();
		m_dico = getDictionnary();

		m_dico.openConnections(0, null);
		m_loader = getSwatchLoader();
	}
}
