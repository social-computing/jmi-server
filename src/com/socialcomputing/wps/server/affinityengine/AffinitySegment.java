package com.socialcomputing.wps.server.affinityengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.socialcomputing.utils.database.DatabaseHelper;
import com.socialcomputing.utils.database.FileFastInserter;
import com.socialcomputing.utils.database.MultipleFastInserter;
import com.socialcomputing.utils.database.iFastInsert;
import com.socialcomputing.wps.server.plandictionary.FilteringProfile;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;
import com.socialcomputing.wps.server.plandictionary.connectors.iIdEnumerator;
import com.socialcomputing.wps.server.plandictionary.connectors.iProfileConnector;
import com.socialcomputing.wps.server.utils.AttributesPonderationMap;
import com.socialcomputing.wps.server.utils.MathLogBuffer;
import com.socialcomputing.wps.server.utils.ObjectToNumConverter;
import com.socialcomputing.wps.server.utils.StringAndFloat;


/**
  * describes object which compute (in background) affinity coef for a given rule/segment defining a set of tenties (may be a thread)
 */

public class AffinitySegment
{
	private WPSDictionary m_Dictionary=null;
	private iProfileConnector m_Connector=null;
	private iIdEnumerator m_Entities=null;
	private boolean m_InitializeProcess = true;
	private List<String> m_EntitiesToUpdate=null;
	private ObjectToNumConverter<ArrayList<String>> m_AttrConverter= new ObjectToNumConverter<ArrayList<String>>();
	private HashMap m_Profiles=new HashMap();
	private FilteringProfile m_FilteringProfile=null;

	private DatabaseHelper      m_DatabaseHelper = null;
	private FileFastInserter    m_FileInserter = null;
	private MultipleFastInserter  m_MultipleInserter = null;
	private Statement           m_Statement = null;
	private PreparedStatement   m_StmtDeleteCoef = null;

	/**
	* */
	public  AffinitySegment( Connection connection, WPSDictionary dictionary, FilteringProfile filteringProfile, iIdEnumerator enumerator ) throws WPSConnectorException
	{
		m_DatabaseHelper = new DatabaseHelper( connection, false);
		m_Dictionary=dictionary;
		m_FilteringProfile=filteringProfile;
		m_Connector=m_FilteringProfile.getConnector( m_Dictionary);
		m_Entities=enumerator;

		String table = WPSDictionary.getCoefficientTableName( dictionary.m_Name);
		try {
			if( filteringProfile.m_TmpDir != null)
			{   // On �crit les coefficients dans un fichier qui sera upload�
				m_FileInserter = new FileFastInserter( m_DatabaseHelper, table, ".coeff", filteringProfile.m_TmpDir);
			}
			else
			{   // Insertions multiples (sauf pour SQL Server)
				 m_MultipleInserter = new MultipleFastInserter( m_DatabaseHelper, "insert into " + table + m_DatabaseHelper.AddExtraTableLock() + "(id1, id2, ponderation) values");
				 m_StmtDeleteCoef = connection.prepareStatement( "delete from " + table + m_DatabaseHelper.AddExtraTableLock() + " where (id1 = ?) or (id2 = ?)");
			}
			m_Statement = connection.createStatement();
		}
		catch( Exception e)
		{
			throw new WPSConnectorException( "AffinitySegment init failure", e);
		}
	}
	/**
	*
	*  */
	public  AffinitySegment( Connection connection, WPSDictionary dictionary, FilteringProfile filteringProfile, iIdEnumerator enumerator, List<String> entitiesToUpdate) throws WPSConnectorException
	{
		this( connection,  dictionary,  filteringProfile,  enumerator);
		m_InitializeProcess = false;
		m_EntitiesToUpdate = entitiesToUpdate;
		Collections.sort( m_EntitiesToUpdate);
	}
	/**
	*  Compute the affinity coef for all the entities and for a given list of attributes
	*
	*/
	public Collection<String> compute(  ) throws WPSConnectorException
	{
		ArrayList<String> entities = new ArrayList<String>();
		ArrayList<String> computedEntities = new ArrayList<String>();
		int maxMapSize = m_FilteringProfile.m_AffProfileMaxAttrNb;
		AttributesPonderationMap map1, map2;
		float coef=0; int size; int strComp;
		String name1=null, name2=null;
		boolean relatedEntitesCompute=true;

		//int ii=0;
		for (String item : m_Entities) {
			if ((map1 = getAttributesMap(item)) != null) {
				size = map1.size();
				entities.add( item);
				if (maxMapSize < size)
					maxMapSize = size;
			}
		}

		// Choix du mode de projection en memoire
		if ((entities.size()>100000) || (maxMapSize>entities.size()))
		   relatedEntitesCompute=false;

		// Load Attributes and compute max
		int n = entities.size(), relatedEntSize;
		ArrayList<String> relatedEntities = null;
		float ponderAll=(float)MathLogBuffer.getLog(2) / (float)(maxMapSize+1);
		float threshold = (float)(m_FilteringProfile.m_AffinityThreshold / 100.0);

		// factorisation des insertions de coefficients (le + efficace)
		Vector coefficients  = new Vector( n/2, n/10);
		int j, interCard;
		for( int i = 0; i < n; ++i)
		{
			name1 = (String)entities.get( i);

			if( m_InitializeProcess || (Collections.binarySearch(m_EntitiesToUpdate, name1)>=0))
			{
				map1 = getAttributesMap(name1);
				coefficients.clear();
				computedEntities.add( name1);


				if (relatedEntitesCompute==false)
				   {
				   // optimisation possible si init car parcours exhaustif de la matrice diagonale
				   relatedEntities=entities;
				   j = m_InitializeProcess ? i+1 : 0;
				   relatedEntSize=n;
				   }
				else
					{
						relatedEntities=getRelatedEntities(name1);
						j=0; relatedEntSize=relatedEntities.size();
					}

				for( ; j < relatedEntSize; ++j)
				{
					name2 = (String)relatedEntities.get( j);
					strComp=name1.compareTo(name2);

					if ((strComp!=0) && ((!m_InitializeProcess) || (strComp>0)))
					{
						map2 = getAttributesMap( name2);
						interCard = map1.getIntersectionCardinality( map2);
						if( interCard > 0)
						{   // coef between 0 and 1
							coef = map1.getIntersectionPonderation(map2)*(float)(map1.getSymmetricalDifferenceCardinality(map2)+1)/(interCard*(float)MathLogBuffer.getLog(interCard+1))*ponderAll;
							//System.out.println( map1.getIntersectionPonderation(map2) + "*" + map1.getSymmetricalDifferenceCardinality(map2) + "/" + interCard);
							if( coef < threshold )
							{
								coefficients.add( new StringAndFloat( name2, coef));
							}
						}
					}
				}
				if( m_FileInserter != null)
					AddCoefficientsInFastInserter( m_FileInserter, name1, coefficients);
				else
					AddCoefficientsWithRequests( name1, coefficients);
			}
		}
		if( m_FileInserter != null)
			uploadCoefficientFile( computedEntities);

		try {
			if( m_Statement != null)
				m_Statement.close();
			if( m_StmtDeleteCoef != null)
				m_StmtDeleteCoef.close();
			m_Statement = null;
			m_StmtDeleteCoef = null;
			m_DatabaseHelper.close();
			m_DatabaseHelper = null;
		}
		catch( Exception e) { e.printStackTrace(); }
		return computedEntities;
	}

	private void AddCoefficientsWithRequests( String name1, Vector coefficients) throws WPSConnectorException
	{
		try {
			if( !m_InitializeProcess)
			{   // Lock (pour �viter les conflits avec les demandes de carte) + suppression
				m_DatabaseHelper.Lock( WPSDictionary.getCoefficientTableName( m_Dictionary.m_Name));
				// Suppression des 'vieux' coefficients
				m_StmtDeleteCoef.setString( 1, name1);
				m_StmtDeleteCoef.setString( 2, name1);
				m_StmtDeleteCoef.executeUpdate();
			}
			// Insertions
			AddCoefficientsInFastInserter( m_MultipleInserter, name1, coefficients);
			m_MultipleInserter.insertAll();
		}
		catch( SQLException e)
		{
			throw new WPSConnectorException( "AffinitySegment AddCoefficientsWithRequests failure", e);
		}
		finally {
			if( !m_InitializeProcess)
			{   // Unlock
				m_DatabaseHelper.Unlock( WPSDictionary.getCoefficientTableName( m_Dictionary.m_Name));
			}
		}
	}

	private void AddCoefficientsInFastInserter( iFastInsert inserter, String name1, Vector coefficients) throws WPSConnectorException
	{
		try {
			int size = coefficients.size();
			for( int ii = 0; ii < size; ++ii)
			{
				StringAndFloat saf = ( StringAndFloat) coefficients.get( ii);

				inserter.startLine();
				inserter.addParam( name1);
				inserter.addParam( saf.m_Id);
				inserter.addParam( saf.m_value);
				inserter.endLine();
			}
		}
		catch( Exception e)
		{
			throw new WPSConnectorException( "AffinitySegment AddCoefficientsInFastInserter failure", e);
		}
	}

	private void uploadCoefficientFile( ArrayList computed) throws WPSConnectorException
	{
		try {
			if( !m_InitializeProcess)
			{   // Lock (pour �viter les conflits avec les demandes de carte) + suppression
				m_DatabaseHelper.Lock( WPSDictionary.getCoefficientTableName( m_Dictionary.m_Name));

				// Suppression des 'vieux' coefficients
				int size = computed.size();
				for( int i = 0; i < size; i+=1)//200)
				{
					boolean first = true;
					StringBuffer deleteBuffer = new StringBuffer( 30*1024);
					deleteBuffer.append( '(');
					for( int j = i; j < size && j < i+200; ++j)
					{    // On d�truit par groupes de 200
						if( first)
							first = false;
						else
							deleteBuffer.append( ',');
						deleteBuffer.append( '\'');
						deleteBuffer.append( (String) computed.get( j));
						deleteBuffer.append( '\'');
					}
					deleteBuffer.append( ')');
					m_Statement.executeUpdate( "delete from " + WPSDictionary.getCoefficientTableName( m_Dictionary.m_Name) + m_DatabaseHelper.AddExtraTableLock()
											   + " where id1 in " + deleteBuffer.toString()
											   + "or id2 in " + deleteBuffer.toString());
				}
			}
			m_FileInserter.insertAll();
			if( !m_InitializeProcess)
			{   // Unlock
				m_DatabaseHelper.Unlock( WPSDictionary.getCoefficientTableName( m_Dictionary.m_Name));
			}
		}
		catch( Exception e)
		{
			throw new WPSConnectorException( "AffinitySegment uploadCoefficientFile failure", e);
		}
	}

	/**
	*
	*/
	private  AttributesPonderationMap getAttributesMap( String id ) throws WPSConnectorException
	{
		AttributesPonderationMap map;
		if ((map=(AttributesPonderationMap)m_Profiles.get(id))==null)
		{
			map = new AttributesPonderationMap ( m_Connector.getEnumerator(id), m_AttrConverter, id);
			if (map.size()==0)
			   return null;
			m_Profiles.put(id, map);
		}
		return map;
	}

	private ArrayList<String> getRelatedEntities (String id) throws WPSConnectorException
	{
		AttributesPonderationMap map = getAttributesMap( id );

		// On met � jour la table des attributs
		Set<String> set = new TreeSet<String>();
		for( Map.Entry<Integer, Float> entry : map.entrySet())
		{
			ArrayList<String> array = m_AttrConverter.getObject( entry.getKey());
			if(array.size() != 1)
				set.addAll( array);
		}
		return (new ArrayList<String>( set));
	}
}
