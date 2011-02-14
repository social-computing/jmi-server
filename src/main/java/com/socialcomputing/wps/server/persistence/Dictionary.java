package com.socialcomputing.wps.server.persistence;


/**
 * Title:        DictionaryLoader
 * Description:
 * Copyright:    Copyright (c) 2000
 * Company:      VOYEZ VOUS
 * @author Franck Valetas
 * @version 1.0
 */

public interface Dictionary
{
	public java.lang.String getName();
	public com.socialcomputing.wps.server.plandictionary.WPSDictionary getDictionary();
	public void setDefinition( java.lang.String definition);
	public java.lang.String getDefinition();
	public void setFilteringdate(String filteringdate);
	public java.util.Date getNextFilteringDate();
	public java.util.Date computeNextFilteringDate();
}