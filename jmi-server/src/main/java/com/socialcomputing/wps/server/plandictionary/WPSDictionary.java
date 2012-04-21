package com.socialcomputing.wps.server.plandictionary;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;
import com.socialcomputing.wps.server.planDictionnary.connectors.utils.NameValuePair;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.webservices.RequestingClassifyId;

public class WPSDictionary implements java.io.Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(WPSDictionary.class);
    
	public class Schedule implements java.io.Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4996938584804561500L;
		public String time = null;
		public Integer timer = null;
		public void readObject( org.jdom.Element elem)
		{
			org.jdom.Element timeElem = elem.getChild( "filtering-scheduling-time");
			if( timeElem != null)
				time = timeElem.getAttributeValue( "time");
			timeElem = elem.getChild( "filtering-scheduling-timer");
			if( timeElem != null)
				timer = Integer.valueOf( timeElem.getAttributeValue( "timer"));
		}
	}

	public static final int APPLY_TO_ALL                    = 0x00000000;
	public static final int APPLY_TO_BASE                   = 0x00000001;
	public static final int APPLY_TO_NOT_BASE               = 0x00000002;

	public static final String DEFAULT_NAME					= "<default>";
	
	static final long serialVersionUID = 165666346154822634L;

	public String m_Name = null;
	public String m_Description = null;

	// Le connecteur principal
	public iEntityConnector m_EntitiesConnector = null;

	// Filtering
	public Schedule m_FilteringSchedule = new Schedule();
	public TreeMap<String,FilteringProfile> m_FilteringProfiles = new TreeMap<String,FilteringProfile>(); // FilteringProfile
	public ClassifierMapper m_FilteringMapper = new ClassifierMapper(); // Segmentation

	// Analysis
	public TreeMap<String,AnalysisProfile> m_AnalysisProfiles = new TreeMap<String,AnalysisProfile>();  // AnalysisProfile
	public ClassifierMapper m_AnalysisMapper = new ClassifierMapper(); // Segmentation

	// Affinity Reader
	public TreeMap<String,AffinityReaderProfile> m_AffinityReaderProfiles = new TreeMap<String,AffinityReaderProfile>();  // AffinityReaderProfile
	public AffinityReaderMapper m_AffinityReaderMapper = new AffinityReaderMapper(); // Segmentation

	// Models
	public TreeMap<String,Model> m_Models = new TreeMap<String,Model>();    // Model
	public ModelMapper m_AnalysisLanguageModelMapper = new ModelMapper(); // Segmentation : Analysis / Language / Classifier / Model

	// Global Env Properties
	public List<NameValuePair> m_EnvProperties = new ArrayList<NameValuePair>();
	
	static public String getCoefficientTableName( String name)
	{
		return name + "_coef";
	}

	static public String getCoefficientQueuingTableName( String name)
	{
		return name + "_coef_queued";
	}

	static public String getHistoryTableName( String name)
	{
		return name + "_history";
	}

    static public WPSDictionary readObject(Element element) throws JMIException, JDOMException {
        
        String dictionnaryName = element.getAttributeValue("name");

		WPSDictionary dico = new WPSDictionary(dictionnaryName);
		
		dico.m_Description = element.getChildText( "comment");

		// Connecteur d'entites
		Element entities = element.getChild( "entities");

		
		if(entities == null)
		    throw new JMIException(JMIException.ORIGIN.DEFINITION,dico.m_Name + " : No Entities Specified");
		
		entities = (org.jdom.Element) entities.getChildren().get( 0);
		
		// TODO : Do not throw JDOMExceptions !! They are specific to the JDOM Framework and
		// shouldn't be thrown up
		if( entities == null)
			throw new JMIException(JMIException.ORIGIN.DEFINITION, dico.m_Name + " : No Entities entry found");
		String className = entities.getAttributeValue( "class");
		if( className == null)
			throw new JMIException(JMIException.ORIGIN.DEFINITION, dico.m_Name + " : No Entities class name specified");
		
		try {
		    // Use reflection to get a method named "readObject" 
		    // with a parameter that has a jdom element type
		    // TODO : replace this by dependency injection
			Class<?> cl = Class.forName(className);
			Method met = cl.getDeclaredMethod("readObject", Class.forName("org.jdom.Element"));
			met.setAccessible(true);
			
			// Invoke the readObject method with the entities read in the XML dictionary file
			dico.m_EntitiesConnector = (iEntityConnector) met.invoke(null, entities);
		}
		catch(ClassNotFoundException e) { 
		    throw new JMIException(JMIException.ORIGIN.DEFINITION, dico.m_Name + " : Unknown Entities Class '" +  className + "'", e);
		}
		catch(IllegalAccessException e) { 
            throw new JMIException(JMIException.ORIGIN.DEFINITION, dico.m_Name + " : Invalid Entities Class Access '" +  className + "'", e);
        }
		catch(NoSuchMethodException e) {
		    throw new JMIException(JMIException.ORIGIN.DEFINITION, dico.m_Name + " : Unknown Entities Class Method 'readObject' in '" +  className + "'", e);
		}
		catch( InvocationTargetException e) {
		    throw new JMIException(JMIException.ORIGIN.DEFINITION, dico.m_Name + " : Invalid Entities Class Method 'readObject' in '" +  className + "'", e);
		}

		{   // Global Env Properties
			List<Element> lst = (List<Element>) element.getChildren("env-property");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				org.jdom.Element elem = ( org.jdom.Element)lst.get( i);
				dico.m_EnvProperties.add( new NameValuePair(elem.getAttributeValue( "name"), elem.getAttributeValue( "value")));
			}
		}

		{   // Filtering scheduling
			org.jdom.Element schedule = element.getChild( "filtering-scheduling");
			if( schedule != null)
			{
				dico.m_FilteringSchedule.readObject( schedule);
			}
		}

		{   // Filtering Profiles
			java.util.List lst = element.getChildren( "filtering-profile");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				FilteringProfile profile = FilteringProfile.readObject( ( org.jdom.Element)lst.get( i));
				profile.checkIntegrity( dico.m_Name, dico.m_EntitiesConnector);
				dico.m_FilteringProfiles.put( profile.m_Name, profile);
			}
		}
		{   // Affinity Reader Profiles
			java.util.List lst = element.getChildren( "affinity-reader-profile");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				AffinityReaderProfile profile = AffinityReaderProfile.readObject( ( org.jdom.Element)lst.get( i));
				profile.checkIntegrity( dico.m_Name, dico.m_EntitiesConnector);
				dico.m_AffinityReaderProfiles.put( profile.m_Name, profile);
			}
		}
		{   // Analysis Profiles
			java.util.List lst = element.getChildren( "analysis-profile");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				AnalysisProfile profile = AnalysisProfile.readObject( ( org.jdom.Element)lst.get( i));
				profile.checkIntegrity( dico.m_Name, dico.m_EntitiesConnector);
				dico.m_AnalysisProfiles.put( profile.m_Name, profile);
			}
		}
		{   // Mod\uFFFDles
			java.util.List lst = element.getChildren( "display-profile");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				Model model = Model.readObject( ( org.jdom.Element)lst.get( i));
				dico.m_Models.put( model.m_Name, model);
			}
		}
		{   // Segmentation
			org.jdom.Element segmentation = element.getChild( "segmentation");
			if( segmentation.getChild( "filtering-segmentation") != null)
			{	// Filtering Segmentation
				dico.m_FilteringMapper = ClassifierMapper.readObject( segmentation.getChild( "filtering-segmentation"));
				// V\uFFFDrification
				dico.m_FilteringMapper.checkIntegrityForFilteringProfiles( dico.m_Name + ", Filtering Segmentation", dico);
			}

			if( segmentation.getChild( "affinity-reader-segmentation") != null)
			{	// AffinityReader Segmentation
				dico.m_AffinityReaderMapper = AffinityReaderMapper.readObject( segmentation.getChild( "affinity-reader-segmentation"));
				// V\uFFFDrification
				dico.m_AffinityReaderMapper.checkIntegrity( dico.m_Name + ", Affinity Reader Segmentation", dico);
			}

			// Analysis Segmentation
			dico.m_AnalysisMapper = ClassifierMapper.readObject( segmentation.getChild( "analysis-segmentation"));
			// V\uFFFDrification
			dico.m_AnalysisMapper.checkIntegrityForAnalysisProfiles( dico.m_Name + ", Analysis Segmentation", dico);

			// Model Segmentation
			dico.m_AnalysisLanguageModelMapper = ModelMapper.readObject( segmentation.getChild( "display-segmentation"));
			// V\uFFFDrification
			dico.m_AnalysisLanguageModelMapper.checkIntegrity( dico.m_Name + ", Display Profile Segmentation", dico);
		}

		return dico;
	}

	public WPSDictionary( String name)
	{
		m_Name = name;
	}

	public void openConnections( int planType, Hashtable<String, Object> wpsparams) throws JMIException
	{
		// V\uFFFDrifications de base
		if( m_EntitiesConnector == null)
			throw new JMIException(JMIException.ORIGIN.DEFINITION,"No entities connector in WPSDictionary");

		// Information des models sur les entit\uFFFDs utilis\uFFFDes
		for( Model model : m_Models.values())
		{
			model.setEntitiesConnector( m_EntitiesConnector);
		}
		// Ouverture des bases
		m_EntitiesConnector.openConnections( planType, wpsparams);
	}

	public void closeConnections()  throws JMIException
	{
		for( Model model : m_Models.values())
		{
			model.setEntitiesConnector( null);
		}
		m_EntitiesConnector.closeConnections();
	}

	public iEntityConnector getEntityConnector()
	{
		return m_EntitiesConnector;
	}

	public FilteringProfile getFilteringProfile( String classifierName)
	{
		String name = m_FilteringMapper.getAssociatedName( classifierName);
		return m_FilteringProfiles.get( name);
	}

	public  iClassifierConnector getFilteringClassifier()  throws JMIException
	{
		String name = m_FilteringMapper.m_ClassifierName;
		return m_EntitiesConnector.getClassifier( name);
	}

	private  String getAnalysisProfileName( RequestingClassifyId classifyId )  throws JMIException
	{
		return m_AnalysisMapper.getAssociatedName( m_EntitiesConnector, classifyId);
	}

	public AnalysisProfile getAnalysisProfile( String name )  throws JMIException
	{
		return m_AnalysisProfiles.get( name);
	}

	public AnalysisProfile getAnalysisProfile( RequestingClassifyId classifyId )  throws JMIException
	{
		return getAnalysisProfile( getAnalysisProfileName( classifyId));
	}

	public AffinityReaderProfile getAffinityReaderProfile( String name )
	{
		return m_AffinityReaderProfiles.get( name);
	}

	public AffinityReaderProfile getAffinityReaderProfile( String analysisProfile, RequestingClassifyId classifyId )  throws JMIException
	{
		ClassifierMapper mapper = m_AffinityReaderMapper.getClassifier( analysisProfile);
		String name = mapper.getAssociatedName( m_EntitiesConnector, classifyId);
		return this.getAffinityReaderProfile( name);
	}

	public  Model getModel( String name)
	{
		return m_Models.get( name);
	}

	public  Model getModel( String analysisProfile, String language, RequestingClassifyId classifyId )  throws JMIException
	{
		LanguageMapper languageMapper = m_AnalysisLanguageModelMapper.getClassifier( analysisProfile);
		ClassifierMapper mapper = languageMapper.getClassifier( language);
		String name = mapper.getAssociatedName( m_EntitiesConnector, classifyId);
		return this.getModel( name);
	}
	
	// Starter helper
	public static WPSDictionary CreateTestInstance( String name)
	{
		WPSDictionary dico = null;

		try
		{
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( true);
			org.jdom.Document doc = null;

			if( name.equalsIgnoreCase( "BooSol"))
				doc = builder.build( new File( "..\\plandictionary\\mapstan_net.xml"));
			if( name.equalsIgnoreCase( "SEngine"))
				doc = builder.build( new File( "..\\plandictionary\\mapstan_search.xml"));
			if( name.equalsIgnoreCase( "Test"))
				doc = builder.build( new File( "..\\plandictionary\\test.xml"));

			if( doc != null)
			{
				org.jdom.Element root = doc.getRootElement();
				dico = WPSDictionary.readObject( root);
				if( dico != null)
					System.out.println( dico.m_Name + " created");
				else
					System.out.println( "Dico failed");

			}
			else
				System.out.println( "Unknown dico failed");
		}
		catch (org.jdom.JDOMException se)
		{
			System.err.println(se.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return dico;
	}

}
