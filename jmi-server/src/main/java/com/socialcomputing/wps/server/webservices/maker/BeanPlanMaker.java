package com.socialcomputing.wps.server.webservices.maker;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.EZTimer;
import com.socialcomputing.utils.database.HibernateUtil;
import com.socialcomputing.wps.server.affinityengine.RecommendationInterface;
import com.socialcomputing.wps.server.analysisengine.AnalysisProcess;
import com.socialcomputing.wps.server.generator.PlanContainer;
import com.socialcomputing.wps.server.generator.PlanGenerator;
import com.socialcomputing.wps.server.generator.ProtoPlan;
import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.hibernate.DictionaryManagerImpl;
import com.socialcomputing.wps.server.persistence.hibernate.Track;
import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.plandictionary.AnalysisProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.webservices.PlanRequest;

public class BeanPlanMaker implements PlanMaker {

    private final static Logger LOG = LoggerFactory.getLogger(BeanPlanMaker.class);
    
    private enum Steps {  PlanMakerStarted , DictionaryLoaded, DictionaryOpened, 
        AffinityGroupComputed, AnalysisPassed, PlanGenerated, EnvInitialized };

    @Override
    public Hashtable<String, Object> createPlan(Hashtable<String, Object> params) throws JMIException {
        Hashtable<String, Object> result = new Hashtable<String, Object>();
            EZTimer timer = new EZTimer();

            String mime = (String) params.get( PlanMaker.PLAN_MIME);
            if (mime == null)
                mime = "application/octet-stream";
            
            PlanContainer planContainer = _createPlan(params, result);
            if (mime.equals("application/octet-stream")) {
                result.put( PlanMaker.PLAN, planContainer.toBinary());
                result.put( PlanMaker.PLAN_MIME, mime);
            }
            else if (mime.equals("application/json")) {
                result.put( PlanMaker.PLAN, planContainer.toJson());
                result.put( PlanMaker.PLAN_MIME, mime);
            }
            else if (mime.equals("text/xml")) {
                //result.put("PLAN", planContainer.m_protoPlan.getXML());
                result.put( PlanMaker.PLAN_MIME, mime);
            }
            result.put( PlanMaker.DURATION, timer.getElapsedTime());

            timer.showElapsedTime("ALL STEPS");
            return result;
    }

    private PlanContainer _createPlan(Hashtable<String, Object> params, Hashtable<String, Object> results) throws JMIException {
        Steps status = Steps.PlanMakerStarted;
        boolean isVisual = false;
        WPSDictionary dico = null;
        PlanContainer container = null;
        PlanRequest planRequest = null;
        Session session = null;
        
        String name = (String) params.get("planName");
        if (name == null)
            throw new JMIException(JMIException.ORIGIN.PARAMETER,"JMI parameter 'planName' missing.");
        // Track
        Track track = new Track( name);
        JMIException exception = null;
        
        String x = (String) params.get("width");
        if (x != null && Integer.parseInt(x) == 0)
            throw new JMIException(JMIException.ORIGIN.PARAMETER,"JMI parameter 'width' can't be 0.");
        x = (String) params.get("height");
        if (x != null && Integer.parseInt(x) == 0)
            throw new JMIException(JMIException.ORIGIN.PARAMETER,"JMI parameter 'height' can't be 0.");

        String useragent = (String) params.get("User-Agent");
        if (useragent == null)
            useragent = "<unknown>";

        x = (String) params.get("wpsDebugRelaxation");
        if (x != null && Integer.parseInt(x) == 1)
            isVisual = true;

        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            
            // DICTIONARY LOADER
            DictionaryManagerImpl manager = new DictionaryManagerImpl();
            Dictionary dictionaryLoader = manager.findByName(name);
            if (dictionaryLoader == null)
                throw new JMIException(JMIException.ORIGIN.PARAMETER,"JMI server unknown map '" + name + "'");
            results.put( PlanMaker.PLAN_NAME, name);

            // DICTIONARY RETRIEVAL
            dico = dictionaryLoader.getDictionary();
            status = Steps.DictionaryLoaded;

            // PLANREQUEST CREATION
            planRequest = new PlanRequest(session.connection(), dico, params);
            switch (planRequest.getAnalysisProfile().m_planType) {
                case AnalysisProfile.PERSONAL_PLAN:
                    results.put( PlanMaker.TYPE, "personal");
                    break;
                case AnalysisProfile.GLOBAL_PLAN:
                    results.put( PlanMaker.TYPE, "global");
                    break;
                case AnalysisProfile.DISCOVERY_PLAN:
                    results.put( PlanMaker.TYPE, "discovery");
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
            planGenerator.generatePlan( proto, isVisual);
            status = Steps.PlanGenerated;

            container = new PlanContainer(planGenerator.getEnv(), planGenerator.getPlan());
            status = Steps.EnvInitialized;
            track.stop();
        }
        catch (JMIException e) {
            exception = e;
            track.stop( e, useragent, params);
            throw e;
        }
        finally {
            try {
                if (dico != null)
                    dico.closeConnections();
                if( session != null) {
                    session.save( track);
                    exception.setTrack( track.getId());
                }
            }
            catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return container;
    }

}
