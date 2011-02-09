package com.socialcomputing.utils.geom.triangle;

import java.util.Comparator;

import com.socialcomputing.utils.geom.Localisable;
import com.socialcomputing.utils.geom.Vertex;

/**
 * Title:        Triangle
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      VoyezVous
 * @author
 * @version 1.0
 */

public class NodeTriData
{
	protected   static final    Comparator  s_comp  = new Comparator()
	{
		public int compare( Object o1, Object o2 )
		{
			Vertex  pos1    = ((NodeTriData)o1).m_pos,
					pos2    = ((NodeTriData)o2).m_pos;
			float   dx = pos1.x - pos2.x,
					dy = pos1.y - pos2.y;

			return ( dx < 0 ||( dx == 0 && dy < 0 ))? -1 :(( dx == 0 && dy == 0 )? 0 : 1);
		}
	};

	protected   Localisable m_node;
	protected   Vertex      m_pos;

	public NodeTriData( Localisable node )
	{
		m_node  = node;
		m_pos   = node.getPos();
	}

	public NodeTriData( Vertex pos )
	{
		m_node  = null;
		m_pos   = pos;
	}
}