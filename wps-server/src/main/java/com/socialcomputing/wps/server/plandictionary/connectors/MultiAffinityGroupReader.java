package com.socialcomputing.wps.server.plandictionary.connectors;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.socialcomputing.wps.server.utils.StringAndFloat;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: MapStan</p>
 * @author unascribed
 * @version 1.0
 */

public class MultiAffinityGroupReader implements iAffinityGroupReader, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4985693827455829443L;
	private static final String _ATT_DEFAULT_PREFIX = "_ATT_PREFIX";

	public static iAffinityGroupReader GetMultiAffinityGroupReader( iEntityConnector entities, String full_reader) throws WPSConnectorException
	{
		int count = 1;
		for( int pos = -1; ( pos = full_reader.indexOf( '&', pos+1)) != -1; count++);

		String prefixes[] = new String[ count];
		iAffinityGroupReader readers[] = new iAffinityGroupReader[ count];

		StringTokenizer st = new StringTokenizer( full_reader, "&");
		for( int i = 0; st.hasMoreTokens(); ++i)
		{
			String reader = st.nextToken();
			int pos = reader.indexOf( "=");
			if( pos == -1)
				prefixes[ i] = _ATT_DEFAULT_PREFIX + String.valueOf( i);
			else
			{
				prefixes[ i] = reader.substring( pos+1);
				reader = reader.substring( 0, pos);
			}
			readers[ i] = entities.getAffinityGroupReader( reader);
			if( readers[ i] == null) throw new WPSConnectorException( "MultiAffinityGroupReader '" + full_reader + "' affreader '" + reader + "' not found");
		}
		return new MultiAffinityGroupReader( prefixes, readers);
	}

	private String m_Prefixes[] = null;
	private iAffinityGroupReader m_AffReaders[] = null;

	public MultiAffinityGroupReader( String [] prefixes, iAffinityGroupReader [] profiles)
	{
		m_Prefixes = prefixes;
		m_AffReaders = profiles;
	}

	public StringAndFloat[] retrieveAffinityGroup( String id, int affinityThreshold, int max) throws WPSConnectorException
	{
		for( int i = 0; i < m_AffReaders.length; ++i)
		{
			 if( id.startsWith( m_Prefixes[i]))
				 return m_AffReaders[i].retrieveAffinityGroup( id.substring( m_Prefixes[i].length()), affinityThreshold, max);
		}
		throw new WPSConnectorException( "MultiAffinityGroupReader retrieveAffinityGroup unknown attributeId origin '" + id + "'");
	}
}