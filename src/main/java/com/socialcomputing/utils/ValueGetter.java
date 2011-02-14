package com.socialcomputing.utils;

/**
 * Title:        Plan Generator
 * Description:  classes used to generate a Plan that will be sent to the applet.
 * Copyright:    Copyright (c) 2001
 * Company:      Voyez Vous
 * @author
 * @version 1.0
 */

public interface ValueGetter
{
	public float getValue( Object obj );
	public void  setValue( Object obj, float val );
}