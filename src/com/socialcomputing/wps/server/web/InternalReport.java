package com.socialcomputing.wps.server.web;

import java.util.ArrayList;
/**
 * <p>Title: MapStan Search Engine</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: VOYEZ VOUS</p>
 * @author unascribed
 * @version 1.0
 */

public class InternalReport implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1863749407618505266L;
	public class Action implements java.io.Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7966592832459990527L;
		public int level = 0;
		public String action = null, result = null;
		public boolean delimiter = false;

		public Action( int l, String a)
		{
			level = l;
			action = a ;
		}
		public Action()
		{
			level = 0;
			delimiter = true;
		}
	}

	public ArrayList lst = new ArrayList();
	private Action lastAction = null;

	public void addAction( int level, String action)
	{
		lastAction = new Action( level, action);
		lst.add( lastAction);
	}

	public void skipLine()
	{
		lastAction = new Action();
		lst.add( lastAction);
	}

	public void setLastActionResult( String result)
	{
		if( lastAction != null)
			lastAction.result = result;
	}
	public int size()
	{
		return lst.size();
	}
	public Action get( int i)
	{
		return (Action) lst.get( i);
	}
}