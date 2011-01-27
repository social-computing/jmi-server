package com.socialcomputing.wps.server.plandictionary;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iClassifierConnector;
import com.socialcomputing.wps.server.plandictionary.connectors.iEntityConnector;
import com.socialcomputing.wps.server.webservices.RequestingClassifyId;

public class WPSDictionary implements java.io.Serializable
{
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

	static final long serialVersionUID = 165666346154822634L;

	public String m_Name = null;
	public String m_Description = null;

	// Le connecteur principal
	public iEntityConnector m_EntitiesConnector = null;

	// Filtering
	public Schedule m_FilteringSchedule = new Schedule();
	public TreeMap m_FilteringProfiles = new TreeMap(); // FilteringProfile
	public ClassifierMapper m_FilteringMapper = new ClassifierMapper(); // Segmentation

	// Analysis
	public TreeMap m_AnalysisProfiles = new TreeMap();  // AnalysisProfile
	public ClassifierMapper m_AnalysisMapper = new ClassifierMapper(); // Segmentation

	// Affinity Reader
	public TreeMap m_AffinityReaderProfiles = new TreeMap();  // AffinityReaderProfile
	public AffinityReaderMapper m_AffinityReaderMapper = new AffinityReaderMapper(); // Segmentation

	// Models
	public TreeMap m_Models = new TreeMap();    // Model
	public ModelMapper m_AnalysisLanguageModelMapper = new ModelMapper(); // Segmentation : Analysis / Language / Classifier / Model

	// Global Env Properties
	public Hashtable m_EnvProperties = new Hashtable();

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

	static public WPSDictionary readObject( org.jdom.Element element) throws org.jdom.JDOMException,  WPSConnectorException
	{
		WPSDictionary dico = new WPSDictionary( element.getAttributeValue( "name"));
		dico.m_Description = element.getChildText( "comment");

		// Connecteur d'entites
		org.jdom.Element entities = element.getChild( "entities");
		if( entities == null)
			throw new org.jdom.JDOMException( dico.m_Name + " : No Entities Specified");
		entities = (org.jdom.Element) entities.getChildren().get( 0);
		if( entities == null)
			throw new org.jdom.JDOMException( dico.m_Name + " : No Entities entry found");
		String className = entities.getAttributeValue( "class");
		if( className == null)
			throw new org.jdom.JDOMException( dico.m_Name + " : No Entities class name specified");
		try
		{
			Class cl = Class.forName( className);
			Class parms[] = new Class[1];
			parms[0] = Class.forName( "org.jdom.Element");
			java.lang.reflect.Method met = cl.getDeclaredMethod( "readObject", parms);
			Object parms2[] = new Object[1];
			parms2[0] = entities;
			met.setAccessible(true);
			Object ec = met.invoke( null, parms2);
			dico.m_EntitiesConnector = (iEntityConnector) ec;
		}
		catch( ClassNotFoundException e)
		{ throw new org.jdom.JDOMException( dico.m_Name + " : Unknown Entities Class '" +  className + "'", e);}
		catch( IllegalAccessException e)
		{ throw new org.jdom.JDOMException( dico.m_Name + " : Invalid Entities Class Access '" +  className + "'", e);}
		catch( NoSuchMethodException e)
		{ throw new org.jdom.JDOMException( dico.m_Name + " : Unknown Entities Class Method 'readObject' in '" +  className + "'", e);}
		catch( InvocationTargetException e)
		{ throw new org.jdom.JDOMException( dico.m_Name + " : Invalid Entities Class Method 'readObject' in '" +  className + "'", e);}

		{   // Global Env Properties
			java.util.List lst = element.getChildren( "env-property");
			int size = lst.size();
			for( int i = 0; i < size; ++i)
			{
				org.jdom.Element elem = ( org.jdom.Element)lst.get( i);
				dico.m_EnvProperties.put( elem.getAttributeValue( "name"), elem.getAttributeValue( "value"));
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

	public void openConnections( Hashtable wpsparams) throws WPSConnectorException
	{
		// V\uFFFDrifications de base
		if( m_EntitiesConnector == null)
			throw new WPSConnectorException( "No entities connector in WPSDictionary");

		// Information des models sur les entit\uFFFDs utilis\uFFFDes
		Iterator it = m_Models.values().iterator();
		while( it.hasNext())
		{
			Model model = ( Model) it.next();
			model.setEntitiesConnector( m_EntitiesConnector);
		}
		// Ouverture des bases
		m_EntitiesConnector.openConnections( wpsparams);
	}

	public void closeConnections()  throws WPSConnectorException
	{
		Iterator it = m_Models.values().iterator();
		while( it.hasNext())
		{
			Model model = ( Model) it.next();
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
		return (FilteringProfile) m_FilteringProfiles.get( name);
	}

	public  iClassifierConnector getFilteringClassifier()  throws WPSConnectorException
	{
		String name = m_FilteringMapper.m_ClassifierName;
		return m_EntitiesConnector.getClassifier( name);
	}

	private  String getAnalysisProfileName( RequestingClassifyId classifyId )  throws WPSConnectorException
	{
		return m_AnalysisMapper.getAssociatedName( m_EntitiesConnector, classifyId);
	}

	public AnalysisProfile getAnalysisProfile( String name )  throws WPSConnectorException
	{
		return ( AnalysisProfile)m_AnalysisProfiles.get( name);
	}

	public AnalysisProfile getAnalysisProfile( RequestingClassifyId classifyId )  throws WPSConnectorException
	{
		return getAnalysisProfile( getAnalysisProfileName( classifyId));
	}

	public AffinityReaderProfile getAffinityReaderProfile( String name )
	{
		return ( AffinityReaderProfile)m_AffinityReaderProfiles.get( name);
	}

	public AffinityReaderProfile getAffinityReaderProfile( String analysisProfile, RequestingClassifyId classifyId )  throws WPSConnectorException
	{
		ClassifierMapper mapper = m_AffinityReaderMapper.getClassifier( analysisProfile);
		String name = mapper.getAssociatedName( m_EntitiesConnector, classifyId);
		return this.getAffinityReaderProfile( name);
	}

	public  Model getModel( String name)
	{
		return (Model) m_Models.get( name);
	}

	public  Model getModel( String analysisProfile, String language, RequestingClassifyId classifyId )  throws WPSConnectorException
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

	public static void main(String[] args)
	{
		//WPSDictionary dico = null;
		WPSDictionary.CreateTestInstance( "BooSol");
		WPSDictionary.CreateTestInstance( "SEngine");
	}
}
