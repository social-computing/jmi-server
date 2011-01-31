package com.socialcomputing.wps.server.persistence;


/**
 * Title:        Swatch Loader
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface Swatch
{
	public java.lang.String getName();
	public com.socialcomputing.wps.server.swatchs.XSwatch getSwatch() throws org.jdom.JDOMException;
	public void setDefinition(java.lang.String definition) throws org.jdom.JDOMException;
	public java.lang.String getDefinition();
}