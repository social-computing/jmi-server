package com.socialcomputing.wps.server.plandictionary.connectors;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * <p>Title: WPS Connectors</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2000</p>
 * <p>Company: VOYEZ VOUS</p>
 * @author Franck Valetas
 * @version 1.0
 */

public class WPSConnectorException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6564839622930056614L;
	protected Throwable cause;

	public WPSConnectorException()
	{
		super("Error occurred in WPS connector.");
	}

	public WPSConnectorException( String message)
	{
		super( message);
	}

	public WPSConnectorException(String message, Throwable cause)
	{
		super(message);
		this.cause = cause;
	}

	public String getMessage()
	{
		if (cause != null) {
			return super.getMessage() + ": " + cause.getMessage();
		} else {
			return super.getMessage();
		}
	}

	public void printStackTrace()
	{
		super.printStackTrace();
		if (cause != null) {
			System.err.print("Root cause: ");
			cause.printStackTrace();
		}
	}

	public void printStackTrace(PrintStream s)
	{
		super.printStackTrace(s);
		if (cause != null) {
			s.print("Root cause: ");
			cause.printStackTrace(s);
		}
	}

	public void printStackTrace(PrintWriter w)
	{
		super.printStackTrace(w);
		if (cause != null) {
			w.print("Root cause: ");
			cause.printStackTrace(w);
		}
	}

	public Throwable getCause()
	{
		return cause;
	}
}