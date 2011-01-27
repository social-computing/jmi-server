package com.socialcomputing.wps.server.swatchs;

import java.util.*;
import java.io.Serializable;

import org.jdom.*;
//import org.jdom.input.*;

import com.socialcomputing.wps.client.*;
import com.socialcomputing.wps.client.applet.Slice;
import com.socialcomputing.wps.client.applet.VContainer;

/**
  * **********************************************************
  *  Java Class Name : ServerSlice
  *  ---------------------------------------------------------
  *  Filetype: (SOURCE)
  *  Filepath: E:\Dvpt\src\com\voyezvous\wps\server\SwatchEditor\ServerSlice.java
  *
  *
  *  GDPro Properties
  *  ---------------------------------------------------
  *   - GD Symbol Type    : CLD_Class
  *   - GD Method         : UML ( 5.0 )
  *   - GD System Name    : WPS
  *   - GD Diagram Type   : Class Diagram
  *   - GD Diagram Name   : Server-side Swatch Classes
  *  ---------------------------------------------------
  *   Author         : flugue
  *   Creation Date  : Tues - Jan 23, 2001
  *
  *   Change Log     :
  *
  * ********************************************************** */

public class XTooltip extends XMLBase implements Serializable
{
	static final long serialVersionUID = 2654579821654846435L;

	private XSlice  m_slice;

	public static XTooltip readObject( Element elem, Element root, Hashtable refs )
	throws JDOMException
	{
		XTooltip    tooltip = new XTooltip();

		tooltip.m_slice = XSlice.readObject( elem, root, refs );

		tooltip.putAttRef( "delay", Integer.class, elem, root, refs, CDATA | IDREF );
		tooltip.putAttRef( "length", Integer.class, elem, root, refs, CDATA | IDREF );

		return tooltip;
	}

	public void addPropsToList( ArrayList list )
	{
		super.addPropsToList( list );

		m_slice.addPropsToList( list );
	}

	public Object toClient( Hashtable refs )
	{
		Slice           slice       = (Slice)m_slice.toClient( refs );
		int             n           = slice.m_containers.length;
		VContainer[]    containers  = new VContainer[n+2];

		System.arraycopy( slice.m_containers, 0, containers, 0, n );

		containers[n]   = toClientCont( "delay", refs );
		containers[n+1] = toClientCont( "length", refs );

		slice.m_containers = containers;

		return slice;
	}
}
