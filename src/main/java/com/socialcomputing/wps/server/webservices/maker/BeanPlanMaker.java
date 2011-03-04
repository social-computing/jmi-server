package com.socialcomputing.wps.server.webservices.maker;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.zip.GZIPOutputStream;

import org.hibernate.Session;

import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.utils.database.DatabaseHelper;
import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.affinityengine.RecommendationInterface;
import com.socialcomputing.wps.server.analysisengine.AnalysisProcess;
import com.socialcomputing.wps.server.generator.PlanContainer;
import com.socialcomputing.wps.server.generator.PlanGenerator;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.webservices.PlanRequest;

public class BeanPlanMaker implements PlanMaker {

    private class Steps {
        static final int PlanMakerStarted = 0x00000001;
        static final int DictionaryLoaded = 0x00000002;
        static final int DictionaryOpened = 0x00000004;
        static final int AffinityGroupComputed = 0x00000010;
        static final int AnalysisPassed = 0x00000100;
        static final int PlanGenerated = 0x00001000;
        static final int EnvInitialized = 0x00002000;
    }

    @Override
    public Hashtable<String, Object> createPlan(Hashtable<String, Object> params) throws RemoteException {
        Hashtable<String, Object> result = new Hashtable<String, Object>();
        try {
            EZTimer timer = new EZTimer();

            String mime = (String) params.get("PLAN_MIME");
            if (mime == null)
                mime = "application/octet-stream";
            PlanContainer planContainer = _createPlan(params, result);
            if (mime.equals("application/octet-stream")) {
                if (planContainer.m_env != null) {
                    ByteArrayOutputStream bout = new ByteArrayOutputStream(32768);
                    ObjectOutputStream objectOutStream = new ObjectOutputStream(new GZIPOutputStream(bout));
                    objectOutStream.writeObject(planContainer.m_env);
                    objectOutStream.writeObject(planContainer.m_plan);
                    objectOutStream.close();
                    result.put("PLAN", bout.toByteArray());
                }
                else {
                    result.put("PLAN", new byte[0]);
                }
                result.put("PLAN_MIME", mime);
            }
            else if (mime.equals("text/xml")) {
                result.put("PLAN", planContainer.m_protoPlan.getXML());
                result.put("PLAN_MIME", mime);
            }
            else if (mime.equals("text/java")) {
                result.put("PLAN", planContainer);
                result.put("PLAN_MIME", mime);
            }

            timer.showElapsedTime("ALL STEPS");
            return result;
        }
        catch (Exception e) {
            throw new RemoteException("WPS can't create plan " + (String) params.get("planName") + " : "
                    + e.getMessage());
        }
    }

    private PlanContainer _createPlan(Hashtable<String, Object> params, Hashtable<String, Object> results)
            throws RemoteException {
        int status = Steps.PlanMakerStarted;
        boolean isVisual = false;
        Connection connection = null;
        WPSDictionary dico = null;
        PlanContainer container = null;
        PlanRequest planRequest = null;

        long startTime = System.currentTimeMillis();

        String name = (String) params.get("planName");
        if (name == null)
            throw new RemoteException("WPS parameter 'planName' missing.");
        String x = (String) params.get("width");
        if (x != null && Integer.parseInt(x) == 0)
            throw new RemoteException("WPS parameter 'width' can't be 0.");
        x = (String) params.get("height");
        if (x != null && Integer.parseInt(x) == 0)
            throw new RemoteException("WPS parameter 'height' can't be 0.");

        String useragent = (String) params.get("User-Agent");
        if (useragent == null)
            useragent = "<unknown>";

        x = (String) params.get("wpsDebugRelaxation");
        if (x != null && Integer.parseInt(x) == 1)
            isVisual = true;

        try {
            connection = getConnection();

            // DICTIONARY LOADER
            DictionaryManagerImpl manager = new DictionaryManagerImpl();
            Dictionary dictionaryLoader = manager.findByName(name);
            if (dictionaryLoader == null)
                throw new RemoteException("WPS parameter can't find dictionary " + name);
            results.put("PLAN_NAME", name);

            // DICTIONARY RETRIEVAL
            dico = dictionaryLoader.getDictionary();
            status = Steps.DictionaryLoaded;

            // PLANREQUEST CREATION
            planRequest = new PlanRequest(connection, dico, params);
            switch (planRequest.getAnalysisProfile().m_planType) {
                case AnalysisProfile.PERSONAL_PLAN:
                    results.put("PLAN_TYPE", "PERSONAL");
                    break;
                case AnalysisProfile.GLOBAL_PLAN:
                    results.put("PLAN_TYPE", "GLOBAL");
                    break;
                case AnalysisProfile.DISCOVERY_PLAN:
                    results.put("PLAN_TYPE", "DISCOVERY");
                    break;
            }

            // PLANREQUEST CREATION
            dico.openConnections(planRequest.getAnalysisProfile().m_planType, params);
            status = Steps.DictionaryOpened;

            // AFFINITY GROUP RETRIEVAL
            RecommendationInterface affinity = new RecommendationInterface(planRequest);
            Collection<String> affinityGroup = affinity.retrieveAffinityGroup();
            status = Steps.AffinityGroupComputed;

            // ANALYSIS MOTOR
            AnalysisProcess analysisEngine = new AnalysisProcess(planRequest, affinityGroup, affinity);
            ProtoPlan proto = analysisEngine.getProtoPlan();
            status = Steps.AnalysisPassed;

            // PLAN GENERATOR
            PlanGenerator planGenerator = new PlanGenerator();
            planGenerator.generatePlan(proto, isVisual);
            status = Steps.PlanGenerated;

            container = new PlanContainer(planGenerator.getEnv(), planGenerator.getPlan());
            container.m_protoPlan = proto;
            status = Steps.EnvInitialized;

        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException(e.getMessage());
        }
        finally {
            try {
                if (connection != null)
                    connection.close();
                if (dico != null)
                    dico.closeConnections();
            }
            catch (Exception e) {}
        }

        return container;
    }

    private Connection getConnection() throws SQLException, RemoteException {
        Session session = HibernateUtil.currentSession();
        return session.connection();
    }

}
