package com.socialcomputing.wps.server.generator;

/**
 * <p>Title: ProtoEntity</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class ProtoEntity extends ProtoObject
{

	/**
	 * Can be a normal Entity, an Advertissment Entity or the Reference Entity.
	 */
	public static final int     NORMAL_ENTITY           = 0;
	public static final int     ADVERTISEMENT_ENTITY    = 1;
	public static final int     REFERENCE_ENTITY        = 2;

	/**
	 * Links between this and the attributes.
	 */
	public EntityLink[]     m_links = null;

	public ProtoEntity( String strId )
	{
		super( strId, NORMAL_ENTITY );
	}

	public ProtoEntity( String strId, int type )
	{
		super( strId, type );
	}

	public ProtoEntity( String strId, int type, int num )
	{
		super( strId, type, num );
	}

}
