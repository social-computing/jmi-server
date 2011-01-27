package com.socialcomputing.wps.server.generator;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

//import com.socialcomputing.utils.*;
import com.socialcomputing.utils.geom.relax.*;
import com.socialcomputing.utils.math.*;
import com.socialcomputing.utils.geom.relax.GfxTester;
import com.socialcomputing.utils.geom.relax.NodeRelaxData;
import com.socialcomputing.utils.geom.relax.RelaxListener;
import com.socialcomputing.utils.geom.relax.RelaxParams;
import com.socialcomputing.utils.geom.relax.RelaxableLink;
import com.socialcomputing.utils.geom.relax.RelaxableNode;
import com.socialcomputing.utils.geom.relax.Relaxer;

/**
 * <p>Title: VisualMapper</p>
 * <p>Description: A Mapper that can interact with the relaxation process through a GUI.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */
public class VisualMapper extends Mapper implements RelaxListener
{
	/**
	 * Creates a VisualMapper using a ProtoPlan.
	 */
	public VisualMapper( ProtoPlan plan )
	{
		super( plan );
	}

	/**
	 * Generate a Plan , while waiting for the user at each stages.
	 * If the Plan is degenerated no visual feedback can be done.
	 */
	public void generatePlan() throws com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException
	{
		GfxTester   tester  = new GfxTester();
		Bounds2D	winBnds	= m_protoPlan.m_mapDat.m_winBnds;

		if ( !m_protoPlan.m_isDegenerated )
		{
			do
			{
				tester.resetUI( "relaxerNG", (int)winBnds.getWidth() , (int)winBnds.getHeight());
				tester.start( this );
			}
			while( tester.needRestart());
			toZones();
		}
		else
		{
			System.out.println( "Degenerate Plan, no Visual feedback!" );
			toDegeneratedZone();
		}

		System.out.println( "initNodesSwatchs" );
		initNodesSwatchs();

		System.out.println( "initLinksSwatchs" );
		initLinksSwatchs();

		System.out.println( "Plan generated" );
	}

	public RelaxableNode[] getNodes()
	{
		return m_nodes;
	}

	public NodeRelaxData getBase()
	{
		return m_base;
	}

	public RelaxableLink[] getLinks()
	{
		return m_links;
	}

	public String getStep()
	{
		return String.valueOf( m_step );
	}

	public String getIter()
	{
		return String.valueOf( m_iter );
	}

	public Relaxer getRelaxer()
	{
		return m_relaxer;
	}

	/**
	 * Gets the fields of this class that are editable with the GUI.
	 * The only one is the maximum iterration treshold.
	 * @return	A HashMap containing an entry corresponding to "com.socialcomputing.wps.server.generator.Mapper.m_trsh".
	 */
	public HashMap getEditableFields()
	{
		HashMap	fieldMap	= new HashMap();

		fieldMap.put( "max iter count treshold", "com.socialcomputing.wps.server.generator.Mapper.m_trsh" );

		return fieldMap;
	}

	public void updateRelaxParams( Field field )
	{
		RelaxParams	params	= m_protoPlan.m_mapDat.m_relaxParams[m_stage];

		m_relaxer.updateParams( params );
	}

	// update BBox aspect!
	protected void setStepParams( int curNode )
	{
		MapData	mapDat	= m_protoPlan.m_mapDat;

		m_curNode	= curNode;
		m_relaxer.evalBBox( 0, m_curNode, false );
		m_relaxer.getBounds().updateAspect( mapDat.m_winBnds );
		m_nodes[m_curNode].initPos( m_relaxer.getBounds().getCenter(), m_base );

		if ( m_step == m_stepCnt - 1 )
		{
			m_iterCnt	= mapDat.m_relaxParams[m_stage].m_lastItrs;
			m_trsh		= mapDat.m_relaxParams[m_stage].m_lastTrsh;
		}
	}

	/**
	 * Handles key events. A few keys are defined:
	 * <ul>
	 * <li>D : Dump relaxer params and data (nodes & links position, size...)</li>
	 * <li>L : Load relax params using a File Dialog. The file must be in XML format.
	 * <li>S : Save relax params in an XML file using a File Dialog.
	 * <li>F1 : Equalize and display the link width histogram.
	 * <li>F2 : Equalize and display the link length histogram.
	 * <li>F3 : Equalize and display the attribute size histogram.
	 * </ul>
	 * @param e			Key pressed.
	 * @param frame		The Frame of the GUI.
	 * @param g			Graphics to draw into.
	 */
	public void keyPressed( KeyEvent e, Frame frame, Graphics g )
	{
		FileDialog	fileDialog;

		switch ( e.getKeyCode())
		{
			case KeyEvent.VK_D :
				m_relaxer.dump();
				break;

			case KeyEvent.VK_F1 :
				m_protoPlan.EQHisto( ProtoPlan.L_ALLWIDTH_BND, g );
				break;

			case KeyEvent.VK_F2 :
				m_protoPlan.EQHisto( ProtoPlan.L_ALLLENGTH_BND, g );
				break;

			case KeyEvent.VK_F3 :
				m_protoPlan.EQHisto( ProtoPlan.A_ALLSIZE_BND, g );
				break;

			case KeyEvent.VK_L :
				fileDialog	= new FileDialog( frame, "Load XML Relax Params", FileDialog.LOAD );
				fileDialog.setVisible(true);
				readObject( fileDialog.getDirectory() + fileDialog.getFile());
				break;

			case KeyEvent.VK_S :
				fileDialog	= new FileDialog( frame, "Save XML Relax Params", FileDialog.SAVE );
				fileDialog.setVisible(true);
				writeObject( fileDialog.getDirectory() + fileDialog.getFile());
				break;
		}
	}

	/**
	 * Reads an XML relax definition.
	 * @param filename	Path of the File to load.
	 */
	private void readObject( String filename )
	{
		try
		{
			SAXBuilder			xmlIn		= new SAXBuilder();
			Element				root		= xmlIn.build( new File( filename )).getRootElement();
			MapData				mapDat		=  m_protoPlan.m_mapDat;
			if ( !root.getName().equals( "relax" ))
			{
				root	= root.getChild( "relax" );
			}

			mapDat.readObject( root );
			m_relaxer.setParams( mapDat.m_relaxParams[m_stage], m_base );
			System.out.println( "Relax Params : " + filename + " read." );
		}
		catch ( Exception e )
		{
			System.out.println( "Error reading Relax Params : " + e );
		}
	}

	/**
	 * Writes relax parameters to an XML file.
	 * @param filename	Path of the File to save.
	 */
	private void writeObject( String filename )
	{
		Element				root		= m_protoPlan.m_mapDat.writeObject();
		XMLOutputter    	xmlOut  	= new XMLOutputter( Format.getPrettyFormat() );
		FileOutputStream	os;

		try
		{
			os		= new FileOutputStream( filename );
			xmlOut.output( root, os );
			System.out.println( "Relax Params : " + filename + " written." );
		}
		catch ( Exception e )
		{
			System.out.println( "Error writting Relax Params : " + e );
		}
	}
}

