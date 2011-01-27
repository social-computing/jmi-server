package com.socialcomputing.wps.client.applet;

//import java.awt.*;
//import java.awt.image.*;
//import java.io.*;
//import java.util.zip.*;
//import java.net.*;
//import java.applet.*;
//import java.awt.event.*;
//import java.util.*;

/**
 * <p>Title: Waiter</p>
 * <p>Description: A Waiter is a waiting Thread that call a WaiterListener.<br>
 * It's usefull to create anonymous timer-like classes as a Server wake-up, a resize notifier,
 * a dblclick notifier or a tooltip timing manager.</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p>Company: MapStan (Voyez Vous)</p>
 * @author flugue@mapstan.com
 * @version 1.0
 */

public final class Waiter extends Thread
{
	/**
	 * A listener that is called by this Waiter.
	 */
	private WaitListener    m_listener;

	/**
	 * Delay before starting.
	 */
	private int             m_beg;

	/**
	 * Time to stay alive after starting.
	 */
	private int             m_len;

	/**
	 * Parameters that are passed to the listener.
	 */
	protected   Object[]    m_params;

	/**
	 * True while this should repeat it's waiting loop.
	 */
	protected   boolean     m_loop			= true;

	/**
	 * True if this should stop immediatly. It replace the interrupt() method that is not reliable in Java1.1!
	 */
	protected   boolean     m_isInterrupted = false;

	/**
	 * True if this should not execute the End step. This shortcut should be used with m_isInterrupted.
	 */
	protected   boolean     m_hasFinished   = false;

	/**
	 * Create a Waiter that will call a listener if one of the next 4 cases happen:<br>
	 * <ul>
	 * <li>INIT			When this is created.</li>
	 * <li>START		When this starts, after beg ms.</li>
	 * <li>INTERRUPTED	When this is interrupted because m_isInterrupted is set to true.</li>
	 * <li>END			When this ends, after beg + loops x len or after being interrupted and not finished.</li>
	 * </ul>
	 * @param listener	A listener that will received the 4 possible messages and the params array.
	 * @param params	An array of value that are stored to be passed to the listener.
	 * 					This is usefull to remind the initial value of a parameter that change with time (ex:mouse position).
	 * @param beg		The delay in ms before the waiting loop begins.
	 * @param len		The time to wait between each loop.
	 */
	public Waiter( WaitListener listener, Object[] params, int beg, int len )
	{
		super( "WPSWaiter" );

		m_listener  = listener;
		m_params    = params;
		m_beg       = beg;
		m_len       = len;

		m_listener.stateChanged( m_params, WaitListener.INIT );
	}

	/**
	 * This Thread main method.
	 */
	public void run()
	{
		while ( m_loop )
		{
			try
			{
				int i, n;

				for ( i = 0, n = m_beg / 20; i < n && !m_isInterrupted; i ++ )
				{
					sleep( 20 );
				}

				if ( !m_isInterrupted )
				{
					m_listener.stateChanged( m_params, WaitListener.START );
				}

				do
				{
					m_loop = false;

					for ( i = 0, n = m_len / 20; ( i < n || m_len == -1 )&& !m_isInterrupted; i ++ )
					{
						sleep( 20 );
					}
				}
				while ( m_loop );

			}
			catch ( InterruptedException e )
			{
				m_isInterrupted = true;
			}

			if ( !m_hasFinished )
			{
				m_listener.stateChanged( m_params, m_isInterrupted ? WaitListener.INTERRUPTED : WaitListener.END );
			}
		}
	}

	/**
	 * Kills this Waiter by calling the listener END state. In fact the thread will die "quietly" later.
	 */
	public void finish()
	{
		m_hasFinished = true;
		m_listener.stateChanged( m_params, WaitListener.END );
	}
}
