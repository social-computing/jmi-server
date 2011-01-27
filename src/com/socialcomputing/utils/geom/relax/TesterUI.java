package com.socialcomputing.utils.geom.relax;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
//import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.math.Bounds2D;

public class TesterUI extends Frame implements ActionListener, ItemListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final    Hashtable   s_initVars;

	static
	{
		s_initVars  = new Hashtable();
		s_initVars.put( "GfxTester.m_nodeCnt", "FAKE" );
		s_initVars.put( "GfxTester.m_linkCnt", "FAKE" );
	}

	// Displayed objects colors
	protected static final Color      BACK_COL    = Color.black;

	protected static final Color      NODE_COL    = Color.blue;
	protected static final Color      NODEID_COL  = Color.white;//new Color( 0, 0, 127 );
	protected static final Color      NODEEXT_COL = Color.white;//new Color( 63, 0, 63 );
	protected static final Color      SHIELD_COL  = new Color( 255, 127, 0 );
	protected static final Color      SEPSHIELD_COL	= new Color( 159, 0, 0 );
	protected static final Color      LOCK_COL    = Color.red;

	protected static final Color      LINK_COL    = Color.green;
	protected static final Color      LINKID_COL  = Color.white;//new Color( 0, 127, 0 );
	protected static final Color      LINKEXT_COL = Color.white;//new Color( 63, 63, 0 );
	protected static final Color      INTER_COL   = Color.red;
	protected static final Color      TESS_COL      = new Color( 191, 191, 191 );

	protected static final Color      TENS_COL    = Color.orange;
	protected static final Color      NREP_COL    = Color.cyan;
	protected static final Color      ROT_COL     = Color.magenta;
	protected static final Color      LREP_COL    = Color.gray;
	protected static final Color      CROSS_COL   = Color.black;

	private Image           m_image;
	private boolean         m_isReady       = false;
	private GfxTester       m_tester;
//	private boolean         m_isPaused      = true;
	private HashMap			m_fieldTab;

	public void paint( Graphics g )
	{
		update( g );
	}

	public void update( Graphics g )
	{
		if ( g != null && m_isReady )
		{
			g = m_imagePnl.getGraphics();
			g.drawImage( m_image, 0, 0, null );
		}
	}

	public void drawImage()
	{
		m_imagePnl.getGraphics().drawImage( m_image, 0, 0, null );
	}

	private static final ButData[]  s_checkDat =
	{
		new ButData( "swap" ),
		new ButData( "filter" ),
		new ButData( "tess" ),

		new ButData( "nodes",   true,   NODE_COL ),
		new ButData( "nids",    false,  NODEID_COL ),
		new ButData( "nExt",    false,  NODEEXT_COL ),
		new ButData( "shield",  false,  SHIELD_COL ),

		new ButData( "links",   true,   LINK_COL ),
		new ButData( "lids",    false,  LINKID_COL ),
		new ButData( "lExt",    false,  LINKEXT_COL ),
		new ButData( "inters",  false,  INTER_COL ),

		new ButData( "tens",    false,  TENS_COL ),
		new ButData( "nrep",    false,  NREP_COL ),
		new ButData( "rot",     false,  ROT_COL ),
		new ButData( "lrep",    false,  LREP_COL ),
		new ButData( "cross",   false,  CROSS_COL ),

//		new ButData( "all",     false ),
//		new ButData( "node",    true ),
//		new ButData( "none",    false ),
	};

	private Panel       m_imagePnl;
	private Panel       m_commandPnl;
	private Panel       m_stepPnl;
//	private Panel       m_varPnl;
	private Box         m_playBox;

	private Button      m_unlockBut;
	//private Button      m_dumpBut;

	private HashMap     m_checks    = new HashMap();

	private Checkbox    m_centerChk;
	private Checkbox    m_dblbufChk;
	private Button      m_firstBut;
	private Button      m_playBut;
	private Button      m_pauseBut;
	private Button      m_iterBut;
	private Button      m_stepBut;
	private Button      m_stageBut;
	private Button      m_freeBut;
//	private Label       m_nodeCntLbl;
	private Label       m_stageLbl;
	private Label       m_stepLbl;
	private Label       m_iterLbl;

	private Choice      m_varLst;
	private Button      m_bigDecBut;
	private Button      m_decBut;
	private TextField   m_valueFld;
	private Button      m_incBut;
	private Button      m_bigIncBut;

	public TesterUI( String title, int width, int height )
	{
		super( title );

		setBackground( BACK_COL );
		setSize( width, height );
	}

	public void init( GfxTester tester )
	{
		int             i, n        = 4;

		m_tester        = tester;
		m_imagePnl      = new Panel();
		m_commandPnl    = new Panel();
		m_stepPnl       = new Panel();
		m_playBox       = new Box( BoxLayout.Y_AXIS );

		m_commandPnl.setBackground( Color.lightGray );

		Box[]   boxes   = new Box[n];

		for ( i = 0; i < n; i ++ )
		{
			boxes[i]    = new Box( BoxLayout.Y_AXIS );
			m_stepPnl.add( boxes[i] );
		}

		// STEP PANEL
		//
		m_checks.putAll( ButData.addChecks( s_checkDat, 0, 3, boxes[0], this, false ));
		m_checks.putAll( ButData.addChecks( s_checkDat, 3, 7, boxes[1], this, false ));
		m_checks.putAll( ButData.addChecks( s_checkDat, 7, 11, boxes[2], this, false ));
		m_checks.putAll( ButData.addChecks( s_checkDat, 11, 16, boxes[3], this, false ));
//		m_checks.putAll( ButData.addChecks( s_checkDat, 16, 19, boxes[4], this, true ));

//		m_unlockBut  = new Button( "unlock" );
//		m_unlockBut.addActionListener( this );
//
//		m_dumpBut    = new Button( "dump" );
//		m_dumpBut.addActionListener( this );

		// PLAY PANEL
		//
		n       = 4;
		boxes   = new Box[n];

		for ( i = 0; i < n; i ++ )
		{
			boxes[i]    = new Box( BoxLayout.X_AXIS );
			m_playBox.add( boxes[i] );
		}

		m_firstBut  = new Button( "|<" );
		m_firstBut.addActionListener( this );

		m_pauseBut  = new Button( "||" );
		m_pauseBut.addActionListener( this );

		m_playBut  = new Button( ">" );
		m_playBut.addActionListener( this );

		m_stageBut  = new Button( "=>" );
		m_stageBut.addActionListener( this );

		m_stepBut  = new Button( "->" );
		m_stepBut.addActionListener( this );

		m_iterBut  = new Button( "|>" );
		m_iterBut.addActionListener( this );

		m_freeBut  = new Button( "~>" );
		m_freeBut.addActionListener( this );

		boxes[0].add( m_firstBut );
		boxes[0].add( m_pauseBut );
		boxes[0].add( m_playBut );
		boxes[0].add( m_stageBut );
		boxes[0].add( m_stepBut );
		boxes[0].add( m_iterBut );
		boxes[0].add( m_freeBut );


		m_centerChk = new Checkbox( "center", true );
		m_centerChk.addItemListener( this );

		m_dblbufChk = new Checkbox( "dblbuf", true );
		m_dblbufChk.addItemListener( this );

		m_stageLbl      = new Label( "Ready", Label.RIGHT );

		m_stepLbl       = new Label( "   ", Label.RIGHT );

		m_iterLbl       = new Label( "   ", Label.RIGHT );

		boxes[1].add( m_centerChk );
		boxes[1].add( m_dblbufChk );
		boxes[1].add( m_stageLbl );
		boxes[1].add( m_stepLbl );
		boxes[1].add( m_iterLbl );

		m_varLst	= new Choice();
		m_varLst.addItemListener( this );

		HashMap	relaxFlds		= m_tester.getRelaxListener().getRelaxer().getEditableFields();
		addVarItems( relaxFlds, m_varLst );

		HashMap	listenerFlds	= m_tester.getRelaxListener().getEditableFields();
		addVarItems( listenerFlds, m_varLst );

		HashMap	testerFlds		= m_tester.getEditableFields();
		addVarItems( testerFlds, m_varLst );

		m_fieldTab	= new HashMap();
		m_fieldTab.putAll( relaxFlds );
		m_fieldTab.putAll( listenerFlds );
		m_fieldTab.putAll( testerFlds );

		m_bigDecBut     = new Button( "<<" );
		m_bigDecBut.addActionListener( this );

		m_decBut        = new Button( "<" );
		m_decBut.addActionListener( this );

		m_valueFld      = new TextField();
		m_valueFld.addActionListener( this );

		m_incBut        = new Button( ">" );
		m_incBut.addActionListener( this );

		m_bigIncBut     = new Button( ">>" );
		m_bigIncBut.addActionListener( this );

		boxes[2].add( m_bigDecBut );
		boxes[2].add( m_decBut );
		boxes[2].add( m_valueFld );
		boxes[2].add( m_incBut );
		boxes[2].add( m_bigIncBut );

		boxes[3].add( m_varLst );

		// Update flags according to buttons state.
		updateFlags();

		// FRAME
		//
		m_commandPnl.add( m_stepPnl );
		m_commandPnl.add( m_playBox );

		add( m_imagePnl, BorderLayout.CENTER );
		add( m_commandPnl, BorderLayout.SOUTH );

//		setBackground( BACK_COL );
//		setSize( width, height );
		setVisible( true );

		Dimension   dim = m_imagePnl.getSize();

		m_image     = createImage( dim.width, dim.height );
		m_isReady   = true;

		m_imagePnl.addMouseListener( m_tester );
		m_imagePnl.addMouseMotionListener( m_tester );

		addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent e )
			{
				System.exit( 0 );
			}
		});

		m_imagePnl.addKeyListener( this );
	}

	private void addVarItems( HashMap fieldMap, Choice varLst )
	{
		//int			i,n		= fieldMap.size();
		//Map.Entry[]	fields  	= (Map.Entry[])fieldMap.entrySet().toArray( new Map.Entry[n]);
		Iterator	it			= fieldMap.keySet().iterator();

		while ( it.hasNext())
		{
			varLst.addItem((String)it.next());
		}
	}

	private Field getCurField()
	{
		String  varLbl      = m_varLst.getSelectedItem(),
				varNam		= (String)m_fieldTab.get( varLbl );
		int     sepPos      = varNam.lastIndexOf( '.' );
		String  clsNam    	= varNam.substring( 0, sepPos ),
				fieldNam    = varNam.substring( sepPos + 1 );
		Class   cls     	= getInstance( clsNam ).getClass();

		while ( cls != null )
		{
			try
			{
				return cls.getDeclaredField( fieldNam );
			}
			catch ( NoSuchFieldException e )
			{
				cls	= cls.getSuperclass();
			}
		}

		return null;
	}

	private Object getInstance( String clsNam )
	{
		try
		{
			Class   		cls     	= Class.forName( clsNam );
			RelaxListener	listener	= m_tester.getRelaxListener();
			Relaxer			relaxer		= listener.getRelaxer();

			if ( cls.isInstance( m_tester ))		return m_tester;
			else if ( cls.isInstance( listener ))	return listener;
			else if ( cls.isInstance( relaxer ))	return relaxer;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		return null;
	}

	private Object getCurInstance( Field field )
	{
		Class   cls     = field.getDeclaringClass();
		String  clsNam  = cls.getName();

		return getInstance( clsNam );
	}

//	private Object getCurInstance( Field field )
//	{
//		Class   cls     = field.getDeclaringClass();
//		String  clsNam  = cls.getName();
//		int     sepPos  = clsNam.lastIndexOf( '.' );
//
//		clsNam  = clsNam.substring( sepPos + 1 );
//
//		return clsNam.equals( "TestMapper" )? (Object)m_listener : (Object)m_listener.getRelaxer();
//	}

	private void incCurrentValue( boolean isDec, boolean isBig )
	{
		Field   field   = getCurField();

		try
		{
			if ( field.getType()== Integer.TYPE )
			{
				int     value   = field.getInt( getCurInstance( field )),
						inc     = isBig ? 10 : 1;

				value   = isDec ? value - inc : value + inc;
				field.setInt( getCurInstance( field ), value );
				m_valueFld.setText( String.valueOf( value ));
			}
			else if ( field.getType()== Float.TYPE )
			{
				float   value   = field.getFloat( getCurInstance( field )),
						inc     = isBig ? 1.f : 0.1f;

				value   = isDec ? value - inc : value + inc;
				field.setFloat( getCurInstance( field ), value );
				m_valueFld.setText( String.valueOf( value ));
			}

			String  varNam  = m_varLst.getSelectedItem();

			if ( s_initVars.containsKey( varNam ))
			{
				m_tester.reset();
			}

			m_tester.updateCurrentValue( field );
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}

	private void setCurrentValue( float value )
	{
		Field   field   = getCurField();

//		getCurInstance( field );
//		m_valueFld.setText( String.valueOf( value ));

		try
		{
			if ( field.getType()== Integer.TYPE )
			{
				field.setInt( getCurInstance( field ), (int)value );
			}
			else if ( field.getType()== Float.TYPE )
			{
				field.setFloat( getCurInstance( field ), value );
			}

			String  varNam  = m_varLst.getSelectedItem();

			if ( s_initVars.containsKey( varNam ))
			{
				m_tester.reset();
			}

			updateCurrentValue();

			m_tester.updateCurrentValue( field );
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}

	private void updateCurrentValue()
	{
		Field   field   = getCurField();

		try
		{
			if ( field.getType()== Integer.TYPE )
			{
				m_valueFld.setText( String.valueOf( field.getInt( getCurInstance( field ))));
			}
			else if ( field.getType()== Float.TYPE )
			{
				m_valueFld.setText( String.valueOf( field.getFloat( getCurInstance( field ))));
			}
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
		}
	}

	public void actionPerformed( ActionEvent e )
	{
		Object  source  = e.getSource();
		//float   value;

		if ( source == m_valueFld )
		{
			setCurrentValue( Float.parseFloat( m_valueFld.getText()));
		}
		else if ( source == m_bigDecBut )
		{
			incCurrentValue( true, true );
		}
		else if ( source == m_decBut )
		{
			incCurrentValue( true, false );
		}
		else if ( source == m_incBut )
		{
			incCurrentValue( false, false );
		}
		else if ( source == m_bigIncBut )
		{
			incCurrentValue( false, true );
		}
		else if ( source == m_firstBut )
		{
			m_tester.halt();
//			m_tester.reset();
		}
		else if ( source == m_playBut )
		{
			if ( m_tester.isEvolving( ))
			{
				m_tester.m_butFlg.enable( GfxTester.NEXTITER_BIT | GfxTester.NEXTSTEP_BIT | GfxTester.NEXTSTAGE_BIT );
				m_tester.go();
			}
		}
		else if ( source == m_pauseBut )
		{
			m_tester.m_butFlg.enable( GfxTester.PAUSED_BIT );
			m_tester.m_butFlg.disable( GfxTester.FREE_BIT );
		}
		else if ( source == m_iterBut )
		{
			if ( m_tester.isEvolving())
			{
				m_tester.m_butFlg.disable( GfxTester.NEXTITER_BIT );
				m_tester.go();
			}
		}
		else if ( source == m_stepBut )
		{
			if ( m_tester.isEvolving())
			{
				m_tester.m_butFlg.enable( GfxTester.NEXTITER_BIT );
				m_tester.m_butFlg.disable( GfxTester.NEXTSTEP_BIT );
				m_tester.go();
			}
		}
		else if ( source == m_stageBut )
		{
			if ( m_tester.isEvolving())
			{
				m_tester.m_butFlg.enable( GfxTester.NEXTITER_BIT | GfxTester.NEXTSTEP_BIT );
				m_tester.m_butFlg.disable( GfxTester.NEXTSTAGE_BIT );
				m_tester.go();
			}
		}
		else if ( source == m_freeBut )
		{
			m_tester.m_butFlg.enable( GfxTester.FREE_BIT );
//			m_tester.m_butFlg.disable( GfxTester.NEXTSTEP_BIT | GfxTester.NEXTNODE_BIT );
			m_tester.go();
		}
		else if ( source == m_unlockBut )
		{
//			m_tester.unlockNodes();
		}
/*		else if ( source == m_filterBut )
		{
//			triangulate();
//			CrossMgr    cm  = ((RelaxerNG)m_relaxer).m_crossMgr;
//
//			cm.evalInters( m_relaxer.m_nodes, m_relaxer.m_links );
//			cm.filter( m_nodes, m_links );
		}*/
//		else if ( source == m_dumpBut )
//		{
//			m_tester.dump();
//		}
	}

	protected boolean getCheckState( HashMap checks, String label )
	{
		return ((Checkbox)checks.get( label )).getState();
	}

	private void setFlagState( int bit, HashMap checks, String label )
	{
		m_tester.m_flags.setEnabled( bit, getCheckState( checks, label ));
	}

	private void updateFlags()
	{
		setFlagState( GfxTester.NODE_BIT, m_checks, "nodes" );
		setFlagState( GfxTester.NODEID_BIT, m_checks, "nids" );
		setFlagState( GfxTester.NODEEXT_BIT, m_checks, "nExt" );
		setFlagState( GfxTester.SHIELD_BIT, m_checks, "shield" );

		setFlagState( GfxTester.LINK_BIT, m_checks, "links" );
		setFlagState( GfxTester.LINKID_BIT, m_checks, "lids" );
		setFlagState( GfxTester.LINKEXT_BIT, m_checks, "lExt" );
		setFlagState( GfxTester.INTER_BIT, m_checks, "inters" );

		setFlagState( GfxTester.TENS_BIT, m_checks, "tens" );
		setFlagState( GfxTester.NREP_BIT, m_checks, "nrep" );
		setFlagState( GfxTester.ROT_BIT, m_checks, "rot" );
		setFlagState( GfxTester.LREP_BIT, m_checks, "lrep" );
		setFlagState( GfxTester.CROSS_BIT, m_checks, "cross" );

		setFlagState( CrossMgr.SWAP_BIT, m_checks, "swap" );
		setFlagState( CrossMgr.FILTER_BIT, m_checks, "filter" );
		setFlagState( GfxTester.TESS_BIT, m_checks, "tess" );

		m_tester.m_flags.setEnabled( GfxTester.DBLBUF_BIT, m_dblbufChk.getState());
		m_tester.m_flags.setEnabled( GfxTester.CENTER_BIT, m_centerChk.getState());

//		if ( getCheckState( m_checks, "all" ))          m_tester.m_display  = GfxTester.STAGE_DSP;
//		else if ( getCheckState( m_checks, "node" ))    m_tester.m_display  = GfxTester.STEP_DSP;
//		else if ( getCheckState( m_checks, "none" ))    m_tester.m_display  = GfxTester.ITER_DSP;
	}

	public void itemStateChanged( ItemEvent e )
	{
		Object  source  = e.getSource();

		updateFlags();

		if ( source == m_varLst )
		{
			updateCurrentValue();
		}
/*		else if ( source == m_allStepChk || source == m_nodeStepChk || source == m_oneStepChk )
		{
//			computeAll();
		}*/

		if ( m_tester.m_butFlg.isDisabled( GfxTester.FREE_BIT ))
		{
			Graphics    imageGfx    = m_image.getGraphics();
			//Dimension   dim         = m_imagePnl.getSize();

			m_tester.paint( imageGfx );
		}
	}

	protected Graphics getImageGfx()
	{
		return m_image.getGraphics();
	}

	protected Graphics getPanelGfx()
	{
		return m_imagePnl.getGraphics();
	}

	protected Bounds2D getImageBnds()
	{
		Dimension   dim = m_imagePnl.getSize();

		return new Bounds2D( 0, dim.width, 0, dim.height );
	}

//	protected void showNodesCnt( int n )
//	{
//		m_nodeCntLbl.setText( String.valueOf( n + 1 )+ 'n' );
//	}
	protected void showStage( String msg )
	{
		m_stageLbl.setText( msg );
	}

	protected void showStep( String msg )
	{
		m_stepLbl.setText( msg );
	}

	protected void showIter( String msg )
	{
		m_iterLbl.setText( msg );
	}

	protected void showDataPopup( RelaxData data, int x, int y )
	{
		PopupMenu   menu    = data.getMenu();

		menu.addActionListener( m_tester );
		add( menu );
		menu.show( m_imagePnl, x, y );
	}

	protected void showDataFieldDialog( RelaxData data, String fieldNam, int x, int y )
	{
		try
		{
			final Dialog    dialog      = new Dialog( this, fieldNam, true );
			final Field     field       = data.getClass().getDeclaredField( fieldNam );
			final TextField textFld     = new TextField( field.get( data ).toString());
			final Panel     buttonPnl   = new Panel();
			final Button    okBut       = new Button( "OK" ),
							cancelBut   = new Button( "CANCEL" );
			final RelaxData innerDat  	= data;

			ActionListener  listener    = new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					if ( e.getSource()== okBut )
					{
						String  value   = textFld.getText();

						try
						{
							if ( field.getType()== Float.TYPE )
							{
								field.setFloat( innerDat, Float.parseFloat( value ));
							}
							else if ( field.getType()== int.class )
							{
								field.setInt( innerDat, Integer.parseInt( value ));
							}
							else if ( field.getType()== boolean.class )
							{
								field.setBoolean( innerDat, Boolean.getBoolean( value ));
							}
							else if ( field.getType()== EZFlags.class )
							{
								EZFlags	flags	= (EZFlags)field.get( innerDat );
								flags.parseBoolean( value );
							}
							//dialog.hide();
							dialog.setVisible(true);
						}
						catch ( Exception ex )
						{
							System.out.println( "PBBBB" );
						}
					}
					else
					{
						System.out.println( "CANCEL" );
					}
				}
			};
			okBut.addActionListener( listener );
			cancelBut.addActionListener( listener );
			buttonPnl.add( okBut );
			buttonPnl.add( cancelBut );
			dialog.add( textFld, BorderLayout.CENTER );
			dialog.add( buttonPnl, BorderLayout.SOUTH );
			dialog.pack();
			//dialog.show();
			dialog.setVisible(true);
		}
		catch ( Exception ex )
		{
			System.out.println( "pb" );
		}
	}

	public void keyPressed( KeyEvent e )
	{
		m_tester.keyPressed( e, m_imagePnl.getGraphics());
/*		switch ( e.getKeyCode())
		{
			case KeyEvent.VK_D :
				m_tester.dump();
				break;
		}*/
	}

	public void keyReleased( KeyEvent e ){}

	public void keyTyped( KeyEvent e ){}
}

class ButData
{
	private String  m_label;
	private boolean m_state;
	private Color   m_color;

	public ButData( String label )
	{
		this( label, false, null );
	}

	public ButData( String label, boolean state )
	{
		this( label, state, null );
	}

	public ButData( String label, boolean state, Color color )
	{
		m_label = label;
		m_state = state;
		m_color = color;
	}

	public static HashMap addChecks( ButData[] datas, int beg, int end, Container cont, ItemListener listener, boolean isGroup )
	{
		int             i;
		HashMap         checks  = new HashMap( end - beg );
		Checkbox        check;
		CheckboxGroup   group   = isGroup ? new CheckboxGroup(): null;

		for ( i = beg; i < end; i ++ )
		{
			check   = datas[i].createCheckbox( group );
			check.addItemListener( listener );
			cont.add( check );
			checks.put( datas[i].m_label, check );
		}

		return checks;
	}

	public Button createButton()
	{
		Button  but = new Button( m_label );

		but.setEnabled( m_state );

		return but;
	}

	public Checkbox createCheckbox( CheckboxGroup group )
	{
		Checkbox    check = group == null ? new Checkbox( m_label, m_state ):
							new Checkbox( m_label, group, m_state );

		if ( m_color != null )  check.setForeground( m_color );

		return check;
	}
}

