package com.socialcomputing.utils.geom.relax;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.socialcomputing.utils.EZFlags;
import com.socialcomputing.utils.geom.Vertex;
import com.socialcomputing.utils.math.Bounds2D;

public class GfxTester implements Runnable, MouseListener, MouseMotionListener, ActionListener
{
	// Flags
	protected   static final    int     NODE_BIT    = 0x00000001;
	protected   static final    int     NODEID_BIT  = 0x00000002;
	protected   static final    int     NODEEXT_BIT = 0x00000004;
	protected   static final    int     SHIELD_BIT  = 0x00000008;

	protected   static final    int     LINK_BIT    = 0x00000010;
	protected   static final    int     LINKID_BIT  = 0x00000020;
	protected   static final    int     LINKEXT_BIT = 0x00020000;
	protected   static final    int     INTER_BIT   = 0x00000040;

	protected   static final    int     TENS_BIT    = 0x00000080;
	protected   static final    int     NREP_BIT    = 0x00000100;
	protected   static final    int     ROT_BIT     = 0x00000200;
	protected   static final    int     LREP_BIT    = 0x00000400;
	protected   static final    int     CROSS_BIT   = 0x00000800;

//	protected   static final    int     SWAP_BIT    = 0x00001000;
//	protected   static final    int     FILTER_BIT  = 0x00002000;
	protected   static final    int     TESS_BIT    = 0x00004000;

	public      static final    int     CENTER_BIT  = 0x00008000;
	protected   static final    int     DBLBUF_BIT  = 0x00010000;

	// Buttons flags
	protected   static final    int     PAUSED_BIT      = 0x0001;
	protected   static final    int     NEXTITER_BIT    = 0x0002;
	protected   static final    int     NEXTSTEP_BIT    = 0x0004;
	protected   static final    int     NEXTSTAGE_BIT   = 0x0008;
	protected   static final    int     FREE_BIT        = 0x0010;
	protected   static final    int     RESET_BIT       = 0x0020;

	protected   int             m_delay;                // slow motion
	protected   EZFlags         m_flags         = new EZFlags();
	protected   EZFlags         m_butFlg        = new EZFlags( PAUSED_BIT );

	private     RelaxData  		m_pickedObj;
	private     TesterUI        m_ui			= null;
	private     boolean         m_continue;
	private     boolean         m_isEvolving;
	private		boolean			m_isWaiting;
	private		boolean			m_restart;
	private     Thread          m_thread;

	private     RelaxListener   m_listener;
	protected   Bounds2D        m_winBnds;

	private static final Font   s_font          = new Font( "SansSerif", Font.PLAIN, 10 );

	public void resetUI( String title, int width, int height )
	{
		if ( m_ui != null )	m_ui.dispose();

		m_ui	= new TesterUI( title, width + 8, height + 162 );
	}

	public synchronized void run()
	{
		Graphics        imageGfx    = m_ui.getImageGfx(),
						panelGfx	= m_ui.getPanelGfx();

//		while ( true )
//		{
			reset();

			while (( m_butFlg.isEnabled( FREE_BIT )|| m_listener.initStage())&& waitForUI( NEXTSTAGE_BIT ))
			{
				m_ui.showStage( m_listener.getStage());

				while (( m_butFlg.isEnabled( FREE_BIT )|| m_listener.initStep())&& waitForUI( NEXTSTEP_BIT ))
				{
					m_ui.showStep( m_listener.getStep());

					while (( m_butFlg.isEnabled( FREE_BIT )|| m_listener.initIter())&& waitForUI( NEXTITER_BIT ))
					{
						m_ui.showIter( m_listener.getIter());
						m_listener.iterate( m_flags );

						paint( m_flags.isEnabled( DBLBUF_BIT )? imageGfx : panelGfx );
					}
				}

				while ( m_butFlg.isEnabled( FREE_BIT )|| waitForUI( FREE_BIT ))
				{
					m_listener.iterate( m_flags );

					paint( m_flags.isEnabled( DBLBUF_BIT )? imageGfx : panelGfx );
				}
			}

//			m_isEvolving = false;
//
//			while ( m_butFlg.isEnabled( FREE_BIT )|| waitForUI( FREE_BIT ))
//			{
//				m_listener.iterate( m_flags );
//
//				paint( m_flags.isEnabled( DBLBUF_BIT )? imageGfx : panelGfx );
//			}
//
//			System.out.println( "LETS START AGAIN!" );
//		}
	}

	public void mousePressed( MouseEvent e )
	{
//		if ( m_butFlg.isEnabled( FREE_BIT ) )
		{
			m_pickedObj	= getObjectAt( e.getX(), e.getY());

			if ( m_pickedObj != null )// && m_pickedObj instanceof NodeRelaxData )
			{
				if (( e.getModifiers()& MouseEvent.BUTTON2_MASK )!= 0 )
				{
					lockCurObj( true );
					m_pickedObj	= null;
				}
				else if (( e.getModifiers()& MouseEvent.BUTTON1_MASK )!= 0 )
				{
					lockCurObj( true );
				}
				else
				{
					m_ui.showDataPopup( m_pickedObj, e.getX(), e.getY());
					System.out.println( "popup" );
				}
			}
		}
	}

	private void lockCurObj( boolean isLocked)
	{
		if (  m_pickedObj instanceof NodeRelaxData )
		{
			((NodeRelaxData)m_pickedObj).m_isLocked			= isLocked;
		}
		else
		{
			((LinkRelaxData)m_pickedObj).m_from.m_isLocked	= isLocked;
			((LinkRelaxData)m_pickedObj).m_to.m_isLocked	= isLocked;
		}
	}

	public void mouseReleased( MouseEvent e )
	{
		if ( m_pickedObj != null && ( e.getModifiers()& MouseEvent.BUTTON3_MASK )== 0 )
		{
			lockCurObj( false );
			m_pickedObj	= null;
		}
	}

	public void mouseDragged( MouseEvent e )
	{
		if ( m_butFlg.isEnabled( FREE_BIT ) && m_pickedObj != null &&( e.getModifiers()& MouseEvent.BUTTON1_MASK )!= 0 )
		{
			setCurObjPos( e.getX(), e.getY());
		}
	}

	private void setCurObjPos( int x, int y )
	{
		Relaxer	relaxer	= m_listener.getRelaxer();
		Vertex	curPos	= relaxer.m_vertexBuf[Relaxer.LAST_VTX].setLocation( x, y ),
				pos		= relaxer.unproject( curPos, m_winBnds );

		if (  m_pickedObj instanceof NodeRelaxData )
		{
			((NodeRelaxData)m_pickedObj).m_pos.setLocation( pos );
		}
		else
		{
			Vertex	fromPos	= ((LinkRelaxData)m_pickedObj).m_from.m_pos,
					toPos	= ((LinkRelaxData)m_pickedObj).m_to.m_pos;

			pos.subThis( Vertex.center( fromPos, toPos ));
			fromPos.addThis( pos);
			toPos.addThis( pos);
		}
	}

	public void mouseClicked( MouseEvent e ){}
	public void mouseEntered( MouseEvent e ){}
	public void mouseExited( MouseEvent e ){}
	public void mouseMoved( MouseEvent e ){}

	public void actionPerformed( ActionEvent e )
	{
		String  field   = e.getActionCommand().substring( 0, e.getActionCommand().indexOf( '=' )- 1 );

		m_ui.showDataFieldDialog( m_pickedObj, field, 200, 200 );
	}

	protected RelaxListener getRelaxListener()
	{
		return m_listener;
	}

	protected HashMap getEditableFields()
	{
		HashMap	fieldMap	= new HashMap();

		fieldMap.put( "refresh delay", "com.socialcomputing.utils.geom.relax.GfxTester.m_delay" );

		return fieldMap;
	}

	protected synchronized void paint( Graphics g )
	{
		g.clearRect( 0, 0, (int)m_winBnds.getWidth(), (int)m_winBnds.getHeight());
		g.setFont( s_font );

		m_listener.getRelaxer().paint( g, m_flags.isEnabled( CENTER_BIT ), m_winBnds, m_flags );

//		g.setColor( Color.red );
//		int len = (int)( 1000.f * ((TestMapper)m_listener).m_err );
//		g.fillRect( 50, 100, 5, len );

		if ( m_delay > 0 )
		{
			try{ wait( m_delay );}
			catch ( InterruptedException e ){}
		}

		if ( m_flags.isEnabled( DBLBUF_BIT ))
		{
			m_ui.drawImage();
		}
	}

	public RelaxData getObjectAt( int x, int y )
	{
		RelaxableNode[] nodes       = m_listener.getNodes();
		RelaxableLink[] links       = m_listener.getLinks();
		NodeRelaxData   nodeDat;
		LinkRelaxData   linkDat;
		Relaxer			relaxer		= m_listener.getRelaxer();
		Vertex			pos			= relaxer.m_vertexBuf[Relaxer.LAST_VTX].setLocation( x, y ),
						mousePos    = relaxer.unproject( pos, m_winBnds );
		int             i, n        = nodes.length;
		//float           size;

		// Nodes
		for ( i = 0; i < n; i ++ )
		{
			nodeDat = nodes[i].getRelaxData();

			if ( nodeDat.isReady()&& nodeDat.contains( mousePos ))	return nodeDat;
		}

		// Links
		n	= links.length;

		for ( i = 0; i < n; i ++ )
		{
			linkDat = links[i].getRelaxData();

			if ( linkDat.isReady()&& linkDat.contains( mousePos ))	return linkDat;
		}

		// Base
		nodeDat = m_listener.getRelaxer().m_base;

		if ( nodeDat != null && nodeDat.contains( mousePos ))	return nodeDat;

		return null;
	}

	public void pause()
	{
		m_butFlg.enable( PAUSED_BIT );
	}

	public void halt()
	{
		m_butFlg.enable( GfxTester.PAUSED_BIT );
		m_butFlg.disable( GfxTester.FREE_BIT );

		while ( !m_isWaiting )
		{
			try{ Thread.sleep( 1000 );}
			catch ( InterruptedException e ){ break;}
		}

		m_continue	= false;
		m_restart	= true;
/*		m_continue = false;
//		m_butFlg.enable( RESET_BIT );
		m_thread.interrupt();*/
	}

	protected void reset()
	{
		m_listener.initData( m_winBnds );

		m_ui.showIter( m_listener.getIter());
		m_ui.showStep( m_listener.getStep());
		m_ui.showStage( m_listener.getStage());

		m_continue      = true;
		m_isEvolving    = true;
		m_restart		= false;
	}

	protected void go()
	{
		m_butFlg.disable( PAUSED_BIT );
		m_thread.interrupt();
	}

	protected boolean isEvolving()
	{
		return m_isEvolving;
	}

	public boolean needRestart()
	{
		return m_restart;
	}

	public void keyPressed( KeyEvent e, Graphics g )
	{
		m_listener.keyPressed( e, m_ui, g );
	}
//	protected void dump()
//	{
//		m_listener.dump();
//	}

	protected void updateCurrentValue( Field field )
	{
		m_listener.updateRelaxParams( field );
	}

	private synchronized boolean waitForUI( int nextBit )
	{
		m_isWaiting	= true;

		while (( m_butFlg.isDisabled( nextBit )|| m_butFlg.isEnabled( PAUSED_BIT ))&& m_continue )
		{
			try{ wait( 100 );}
			catch ( InterruptedException e ){ break;}
		}

		m_isWaiting = false;

		return nextBit == FREE_BIT ? m_butFlg.isEnabled( FREE_BIT ): m_continue;
	}

	public synchronized void start( RelaxListener listener )
	{
		m_listener	= listener;
		m_ui.init( this );
		m_winBnds   = m_ui.getImageBnds();

		if ( m_listener != null )
		{
			m_ui.repaint();
			m_thread = new Thread( this );
			m_thread.start();

			try
			{
				while ( m_thread.isAlive())
				{
					wait( 100 );
				}
			}
			catch ( InterruptedException e )
			{
			}

			if ( !m_restart )	System.out.println( "Gfx Finished!" );
		}
	}

	public static void main( String[] args )
	{
		GfxTester   tester  	= new GfxTester();
		TestMapper	listener	= new TestMapper( tester );
//		float		x			= (0.f / 0.f),
//					y			= (float)Math.sqrt( -1 ),
//					z			= 0.f * Float.POSITIVE_INFINITY,
//					w			= Float.POSITIVE_INFINITY / Float.POSITIVE_INFINITY;
//
//		System.out.println( "x = " + x + ", y = " + y + ", z = " + z + ", w = " + w );

		do
		{
			tester.resetUI( "relaxerNG", 800, 600 );
			tester.start( listener );
		}
		while( tester.needRestart());
	}
}
