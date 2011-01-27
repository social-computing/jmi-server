package com.socialcomputing.wps.server.generator;

import java.util.Collection;

/**
 * <p>Title: Recommendable</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public interface Recommendable
{
	public Collection getRecommendations( int type );
	public String getStrId();
	public boolean hasRecos();
	public boolean hasReco( int type );
	public boolean isRef();
}
