package com.socialcomputing.wps.server.generator;

/**
 *
 * <p>Title: EntityLink</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
 public class EntityLink
{
	/**
	 * Handles type identifier or flags.
	 */
	public int          m_type  = 0;

	/**
	 * Link length.
	 */
	public int          m_length;

	/**
	 * Index of the linked entity in the ProtoPlan table.
	 */
	public ProtoObject  m_from;

	/**
	 * Index of the linked attribute in the ProtoPlan table.
	 */
	public ProtoAttribute  m_to;

}
