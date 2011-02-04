package com.socialcomputing.wps.server.plandictionary.connectors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class MultiProfileConnector implements iProfileConnector, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5339250131041549668L;
	private static final String _ATT_DEFAULT_PREFIX = "ATT_CL";
	private static final String _PROPERTY_ATTRIBUTE_PREFIX = "_ATT_PREFIX_"; // Pour les swatchs

	public static iProfileConnector getProfile( iEntityConnector entities, String full_profile ) throws WPSConnectorException
	{
		int count = 1;
		for( int pos = -1; ( pos = full_profile.indexOf( '&', pos+1)) != -1; count++) ;

		String prefixes[] = new String[ count];
		iProfileConnector profiles[] = new iProfileConnector[ count];

		StringTokenizer st = new StringTokenizer( full_profile, "&");
		for( int i = 0; st.hasMoreTokens(); ++i)
		{
			String profile = st.nextToken();
			int pos = profile.indexOf( "=");
			if( pos == -1)
				prefixes[ i] = _ATT_DEFAULT_PREFIX + String.valueOf( i);
			else
			{
				prefixes[ i] = profile.substring( pos+1);
				profile = profile.substring( 0, pos);
			}
			profiles[ i] = entities.getProfile( profile);
			if( profiles[ i] == null) throw new WPSConnectorException( "MultiProfileConnector '" + full_profile + "' profile '" + profile + "' not found");
		}
		return new MultiProfileConnector( prefixes, profiles);
	}

	private String m_Prefixes[] = null;
	private iProfileConnector m_Profiles[] = null;

	public MultiProfileConnector( String [] prefixes, iProfileConnector [] profiles)
	{
		m_Prefixes = prefixes;
		m_Profiles = profiles;
	}

	// iProfileConnector interface
	public  String getName()
	{
		return m_Profiles[0].getName();
	}

	public  String getDescription()
	{
		return m_Profiles[0].getDescription();
	}

	public iEnumerator getEnumerator( String entityId ) throws WPSConnectorException
	{
		return new MultiAttributeEnumerator( m_Prefixes, m_Profiles, entityId);
	}

	public Hashtable getAnalysisProperties( String attributeId, String entityId) throws WPSConnectorException
	{
		for( int i = 0; i < m_Profiles.length; ++i)
		{
			 if( attributeId.startsWith( m_Prefixes[i]))
				 return m_Profiles[i].getAnalysisProperties( attributeId.substring( m_Prefixes[i].length()), entityId);
		}
		throw new WPSConnectorException( "MultiProfileConnector getAnalysisProperties unknown attributeId origin '" + attributeId + "'");
	}

	public iEnumerator<String> getExclusionEnumerator( String entityId) throws WPSConnectorException
	{
		return new MultiEnumerator( m_Prefixes, m_Profiles, entityId);
	}

	public Hashtable getProperties( String attributeId, boolean bInBase, String entityId) throws WPSConnectorException
	{
		for( int i = 0; i < m_Profiles.length; ++i)
		{
			 if( attributeId.startsWith( m_Prefixes[i]))
			 {
				 Hashtable properties = m_Profiles[i].getProperties( attributeId.substring( m_Prefixes[i].length()), bInBase, entityId);
				 properties.put( _PROPERTY_ATTRIBUTE_PREFIX, m_Prefixes[i]);
				 return properties;
			 }
		}
		throw new WPSConnectorException( "MultiProfileConnector getProperties unknown attributeId origin '" + attributeId + "'");
	}

	public iSubAttributeConnector getSubAttribute() throws WPSConnectorException
	{
		return new MultiSubAttributeConnector( m_Prefixes, m_Profiles);
	}

	public Collection<iSelectionConnector> getSelections()
	{
		return null;
	}

	public iSelectionConnector getSelection( String selectionId) throws WPSConnectorException
	{
		return new MultiSelectionConnector( m_Prefixes, m_Profiles, selectionId);
	}


	public class MultiAttributeEnumerator implements iEnumerator
	{
		private String m_Prefixes[] = null;
		private iEnumerator<AttributeEnumeratorItem> m_Enumerators[] = null;
		private int index;

		public MultiAttributeEnumerator( String prefixes[], iProfileConnector [] profiles, String entityId) throws WPSConnectorException
		{
			m_Prefixes = prefixes;
			m_Enumerators = new iEnumerator[ profiles.length];
			for( int i = 0; i < profiles.length; ++i)
				 m_Enumerators[i] = profiles[i].getEnumerator( entityId);
			index = 0;
		}
		@Override
		public Iterator<AttributeEnumeratorItem> iterator() {
			return this;
		}
		@Override
		public boolean hasNext()
		{
			for( ; index < m_Enumerators.length; ++index)
			{
			   if( m_Enumerators[ index].hasNext())
				   return true;
			}
			return false;
		}
		@Override
		public AttributeEnumeratorItem next() {
			AttributeEnumeratorItem item = m_Enumerators[ index].next();
			item.m_Id = m_Prefixes[ index] + item.m_Id;
			return item;
		}
		@Override
		public void remove() {
		}
	}

	public class MultiEnumerator implements iEnumerator<String>
	{
		private String m_Prefixes[] = null;
		private List<iEnumerator<String>> m_Enumerators = new ArrayList<iEnumerator<String>>();
		private int index;

		public MultiEnumerator( String prefixes[], iProfileConnector [] profiles, String entityId) throws WPSConnectorException
		{
			m_Prefixes = prefixes;
			for( int i = 0; i < profiles.length; ++i)
				 m_Enumerators.add( profiles[i].getExclusionEnumerator( entityId));
			index = 0;
		}
		@Override
		public iEnumerator<String> iterator() {
			return this;
		}
		@Override
		public String next() 
		{
			String id = m_Enumerators.get( index).next();
			return m_Prefixes[ index] + id;
		}
		@Override
		public boolean hasNext() 
		{
			for( ; index < m_Enumerators.size(); ++index)
			{
			   if( m_Enumerators.get( index).hasNext())
				   return true;
			}
			return false;
		}
		@Override
		public void remove() {
		}
	}

	public class MultiSelectionConnector implements iSelectionConnector, java.io.Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2975504319460806260L;
		private String m_Prefixes[] = null;
		private iSelectionConnector m_Selections[] = null;
		//private int index;

		public MultiSelectionConnector( String prefixes[], iProfileConnector [] profiles, String selectionId) throws WPSConnectorException
		{
			m_Prefixes = prefixes;
			m_Selections = new iSelectionConnector[ profiles.length];
			for( int i = 0; i < profiles.length; ++i)
				 m_Selections[i] = profiles[i].getSelection( selectionId);
			//index = 0;
		}
		public String getName()
		{
			return "";
		}
		public  String getDescription()
		{
			return "";
		}
		public boolean isRuleVerified(String attributeId, boolean bInBase, String entityId) throws WPSConnectorException
		{
			for( int i = 0; i < m_Profiles.length; ++i)
			{
				 if( attributeId.startsWith( m_Prefixes[i]))
				 {
					if( m_Selections[i] != null)
						return m_Selections[i].isRuleVerified( attributeId.substring( m_Prefixes[i].length()), bInBase, entityId);
					else
						return false;
				 }
			}
			throw new WPSConnectorException( "isRuleVerified unknown attributeId origin '" + attributeId + "'");
		}
	}

	public class MultiSubAttributeConnector implements iSubAttributeConnector, Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3676312172241048460L;
		private String m_Prefixes[] = null;
		private iSubAttributeConnector m_SubAttributes[] = null;
		//private int index;

		public MultiSubAttributeConnector( String prefixes[], iProfileConnector [] profiles) throws WPSConnectorException
		{
			m_Prefixes = prefixes;
			m_SubAttributes = new iSubAttributeConnector[ profiles.length];
			for( int i = 0; i < profiles.length; ++i)
				 m_SubAttributes[i] = profiles[i].getSubAttribute();
			//index = 0;
		}
		public  String getName(  )
		{
			return "";
		}
		public  String getDescription(  )
		{
			return "";
		}

		public iEnumerator<SubAttributeEnumeratorItem> getEnumerator( String entity, String attributeId)  throws WPSConnectorException
		{
			for( int i = 0; i < m_Profiles.length; ++i)
			{
				 if( attributeId.startsWith( m_Prefixes[i]))
				 {
					if( m_SubAttributes[i] != null)
						return m_SubAttributes[i].getEnumerator( entity, attributeId.substring( m_Prefixes[i].length()));
					else
						return null;
				 }
			}
			throw new WPSConnectorException( "getEnumerator unknown attributeId origin '" + attributeId + "'");
		}

		public Hashtable<String, Object> getProperties( String subAttributeId, String attributeId, String entityId ) throws WPSConnectorException
		{
			for( int i = 0; i < m_Profiles.length; ++i)
			{
				 if( attributeId.startsWith( m_Prefixes[i]))
				 {
					if( m_SubAttributes[i] != null)
						return m_SubAttributes[i].getProperties( subAttributeId, attributeId.substring( m_Prefixes[i].length()), entityId);
					else
						return null;
				 }
			}
			throw new WPSConnectorException( "getProperties unknown attributeId origin '" + attributeId + "'");
		}
	}
}
