package com.socialcomputing.utils.geom.relax;

import java.io.*;

import org.jdom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socialcomputing.utils.*;

public class RelaxParams implements Serializable
{
	private static final Logger log = LoggerFactory.getLogger(RelaxParams.class); 
	
	static final long serialVersionUID  	= 5845865399115455453L;

	public	float           m_scale     	= 0.5f;
	public	float           m_nodeRep   	= 0.2f;
	public	float           m_repMix  		= 1.f;
	public	float           m_linkTen   	= 0.2f;
	public	float           m_tensMix  		= 1.f;
	public	float           m_linkRot   	= 0.2f;
	public	float           m_linkRep   	= 0.2f;
	public	float           m_linkRepMix	= 1.f;
	public	float           m_crossRep  	= 0.2f;

	public	float           m_trsh 			= 0.001f;
	public	float           m_lastTrsh  	= 0;
	public	int           	m_iters  		= 50;
	public	int           	m_lastItrs		= 0;
	public	String         	m_name			= null;

	public RelaxParams()
	{
	}

	public RelaxParams( float scale, float nodeRep, float repMix, float linkTen, float tensMix, float linkRep, float linkRepMix, float linkRot, float crossRep )
	{
		setParams( scale, nodeRep, repMix, linkTen, tensMix, linkRep, linkRepMix, linkRot, crossRep, 0, 0, 0, 0 );
	}

	public RelaxParams( float scale, float nodeRep, float repMix, float linkTen, float tensMix, float linkRep, float linkRepMix, float linkRot, float crossRep, int iters, float trsh )
	{
		setParams( scale, nodeRep, repMix, linkTen, tensMix, linkRep, linkRepMix, linkRot, crossRep, iters, trsh, 0, 0 );
	}

	public RelaxParams( float scale, float nodeRep, float repMix, float linkTen, float tensMix, float linkRep, float linkRepMix, float linkRot, float crossRep, int iters, float trsh, int lastItrs, float lastTrsh )
	{
		setParams( scale, nodeRep, repMix, linkTen, tensMix, linkRep, linkRepMix, linkRot, crossRep, iters, trsh, lastItrs, lastTrsh );
	}

	public void setParams( float scale, float nodeRep, float repMix, float linkTen, float tensMix, float linkRep, float linkRepMix, float linkRot, float crossRep, int iters, float trsh, int lastItrs, float lastTrsh )
	{
		setParams( scale, nodeRep, repMix, linkTen, tensMix, linkRep, linkRepMix, linkRot, crossRep );

		m_iters			= iters;
		m_trsh			= trsh;
		m_lastItrs		= lastItrs;
		m_lastTrsh		= lastTrsh;
	}

	public void setParams( float scale, float nodeRep, float repMix, float linkTen, float tensMix, float linkRep, float linkRepMix, float linkRot, float crossRep )
	{
		m_scale     	= scale;
		m_nodeRep   	= nodeRep;
		m_repMix   		= repMix;
		m_linkTen   	= linkTen;
		m_tensMix   	= tensMix;
		m_linkRep   	= linkRep;
		m_linkRepMix	= linkRepMix;
		m_linkRot   	= linkRot;
		m_crossRep  	= crossRep;
	}

//	public void setIters( int iters, int trsh, int lastItrs, int lastTrsh )
//	{
//		m_iters		= iters;
//		m_trsh		= trsh;
//		m_lastItrs	= lastItrs;
//		m_lastTrsh	= lastTrsh;
//	}

	public float getScale()
	{
		return m_scale;
	}

	public float getNodeRep()
	{
		return m_nodeRep;
	}

	public float getRepMix()
	{
		return m_repMix;
	}

	public float getLinkTen()
	{
		return m_linkTen;
	}

	public float getTensMix()
	{
		return m_tensMix;
	}

	public float getLinkRot()
	{
		return m_linkRot;
	}

	public float getLinkRep()
	{
		return m_linkRep;
	}

	public float getLinkRepMix()
	{
		return m_linkRepMix;
	}

	public float getCrossRep()
	{
		return m_crossRep;
	}

	// ne pas oublier le SetPlan(plan) apr�s (la ou il doit y avoir .... = new MapData(plan))
	public static RelaxParams readObject( Element elem )
	throws JDOMException
	{
		RelaxParams	relaxPrm = new RelaxParams();

		if ( elem != null )
		{
			//String  valueStr;

			try
			{
				relaxPrm.m_name			= elem.getAttributeValue( "name" );
				relaxPrm.m_scale		= EZDom.readFloat( elem, "scale", relaxPrm.m_scale );
				relaxPrm.m_nodeRep		= EZDom.readFloat( elem, "nodeRep", relaxPrm.m_nodeRep );
				relaxPrm.m_repMix		= EZDom.readFloat( elem, "nodeRepMix", relaxPrm.m_repMix );
				relaxPrm.m_linkTen		= EZDom.readFloat( elem, "linkTens", relaxPrm.m_linkTen );
				relaxPrm.m_tensMix		= EZDom.readFloat( elem, "linkTensMix", relaxPrm.m_tensMix );
				relaxPrm.m_linkRep		= EZDom.readFloat( elem, "linkRep", relaxPrm.m_linkRep );
				relaxPrm.m_linkRepMix	= EZDom.readFloat( elem, "linkRepMix", relaxPrm.m_linkRepMix );
				relaxPrm.m_linkRot		= EZDom.readFloat( elem, "linkRot", relaxPrm.m_linkRot );
				relaxPrm.m_crossRep		= EZDom.readFloat( elem, "crossRep", relaxPrm.m_crossRep );
				relaxPrm.m_iters		= EZDom.readInt( elem, "iterCnt", relaxPrm.m_iters );
				relaxPrm.m_trsh			= EZDom.readFloat( elem, "errorTrsh", relaxPrm.m_trsh );
				relaxPrm.m_lastItrs		= EZDom.readInt( elem, "lastIterCnt", relaxPrm.m_lastItrs );
				relaxPrm.m_lastTrsh		= EZDom.readFloat( elem, "lastErrorTrsh", relaxPrm.m_lastTrsh );
			}
			catch ( NumberFormatException e )
			{
				log.debug("error parsing Number in RelaxParams.readObject(): {}", elem);
			}
		}

		return relaxPrm;
	}

	public void writeObject( Element root )
	{
		Element	elem	= new Element( "stage" );

		root.addContent( elem );

		elem.setAttribute( "name",  m_name );
		elem.setAttribute( "scale",  String.valueOf( m_scale ));
		elem.setAttribute( "nodeRep",  String.valueOf( m_nodeRep ));
		elem.setAttribute( "nodeRepMix",  String.valueOf( m_repMix ));
		elem.setAttribute( "linkTens",  String.valueOf( m_linkTen ));
		elem.setAttribute( "linkTensMix",  String.valueOf( m_tensMix ));
		elem.setAttribute( "linkRep",  String.valueOf( m_linkRep ));
		elem.setAttribute( "linkRepMix",  String.valueOf( m_linkRepMix ));
		elem.setAttribute( "linkRot",  String.valueOf( m_linkRot ));
		elem.setAttribute( "crossRep",  String.valueOf( m_crossRep ));
		elem.setAttribute( "iterCnt",  String.valueOf( m_iters ));
		elem.setAttribute( "errorTrsh",  String.valueOf( m_trsh ));
		elem.setAttribute( "lastIterCnt",  String.valueOf( m_lastItrs ));
		elem.setAttribute( "lastErrorTrsh",  String.valueOf( m_lastTrsh ));
	}

	public String toString()
	{
		String	msg		= "relaxParam : " + m_name + '\n';

		msg += "m_scale = " + m_scale + '\n';
		msg += "m_nodeRep = " + m_nodeRep + '\n';
		msg += "m_repMix = " + m_repMix + '\n';
		msg += "m_linkTen = " + m_linkTen + '\n';
		msg += "m_tensMix = " + m_tensMix + '\n';
		msg += "m_linkRep = " + m_linkRep + '\n';
		msg += "m_linkRepMix = " + m_linkRepMix + '\n';
		msg += "m_linkRot = " + m_linkRot + '\n';
		msg += "m_crossRep = " + m_crossRep + '\n';
		msg += "m_trsh = " + m_trsh + '\n';
		msg += "m_lastTrsh = " + m_lastTrsh + '\n';
		msg += "m_iters = " + m_iters + '\n';
		msg += "m_lastItrs = " + m_lastItrs;

		return msg;
	}

}