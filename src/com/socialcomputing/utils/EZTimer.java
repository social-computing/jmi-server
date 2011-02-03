package com.socialcomputing.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: EZTimer Description: Copyright: Copyright (c) 2001 Company: VOYEZ VOUS
 * 
 * @author
 * @version 1.0
 */

public final class EZTimer {

	Logger log = LoggerFactory.getLogger(EZTimer.class);

	/**
	 * Stores a previous time
	 */
	private long m_beg;

	public EZTimer() {
		reset();
	}

	/**
	 * Set the elapsed time to 0
	 */
	public void reset() {
		m_beg = System.currentTimeMillis();
	}

	/**
	 * Gets the time elapsed since the last reset in ms.
	 */
	public long getElapsedTime() {
		return System.currentTimeMillis() - m_beg;
	}

	/**
	 * Displays the time elapsed since the last reset preceded by a message and
	 * reset the timer.
	 */
	public void showElapsedTime(String msg) {
		long end = System.currentTimeMillis();
		log.debug("{} : {} ms", msg, end - m_beg);
		m_beg = System.currentTimeMillis();
	}

	public void showElapsedTimeIn(Object obj, String msg) {
		showElapsedTime(obj.getClass().getName()+ ':' + msg);
	}

	public void pause(String msg) {
		long end = System.currentTimeMillis();

		System.out.println(msg + "Press return");

		try {
			System.in.read();
		} catch (java.io.IOException e) {
		}

		m_beg += System.currentTimeMillis() - end;
	}
}