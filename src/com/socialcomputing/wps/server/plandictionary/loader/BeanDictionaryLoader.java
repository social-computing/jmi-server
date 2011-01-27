package com.socialcomputing.wps.server.plandictionary.loader;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.StringTokenizer;

import org.jdom.JDOMException;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;
import com.socialcomputing.wps.server.plandictionary.connectors.WPSConnectorException;

public class BeanDictionaryLoader implements DictionaryLoader{

	private static String s_DateFormat = "yyyy/MM/dd HH:mm:ss";

	public String        dictionary = null;
	public String        name = null;
	public String        filteringdate = "";

	private WPSDictionary m_Dico = null; // Speeder

	public WPSDictionary getDictionary() throws RemoteException {
		try {
			if( m_Dico == null)
			{
				org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder( false);
				org.jdom.Document doc = builder.build( new StringReader( dictionary));
				org.jdom.Element root = doc.getRootElement();
				m_Dico = WPSDictionary.readObject( root);
			}
		}
		catch( Exception e)
		{
			throw new RemoteException ( "getDictionary failed : " + e.getMessage());
		}
		return m_Dico;
	}

	

	public String getFilteringdate() {
		return filteringdate;
	}

	public void setFilteringdate(String filteringdate) {
		this.filteringdate = filteringdate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.util.Date getNextFilteringDate()
	{
		if( filteringdate==null || filteringdate.length() == 0) return null;
		try {
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat( s_DateFormat);
			return format.parse( filteringdate);
		}
		catch( Exception e)
		{
			filteringdate = "";
			System.out.println( "getNextFilteringDate error: " + e.getMessage());
		}
		return null;
	}

	public BeanDictionaryLoader( String name, String dictionary, String filteringdate) {
		super();
		this.dictionary = dictionary;
		this.name = name;
		this.filteringdate = filteringdate;
	}






	public String getDictionaryDefinition() throws RemoteException {
		return this.dictionary;
	}



	public void setDictionaryDefinition(String definition) throws RemoteException, JDOMException {
		this.dictionary = definition;
		
	}

	public void setDictionaryDefinitionApplyDTD(String definition) throws RemoteException, JDOMException {
		// TODO Auto-generated method stub
		
	}
	public java.util.Date computeNextFilteringDate()
	{
		try {
			
			WPSDictionary dico = this.getDictionary();
			if( dico.m_FilteringSchedule == null)
			{
				filteringdate = "";
				return null;
			}
			java.util.GregorianCalendar curDate = new java.util.GregorianCalendar();
			java.text.SimpleDateFormat format = new java.text.SimpleDateFormat( s_DateFormat);
			if( dico.m_FilteringSchedule.time != null)
			{   // Sur le jour d'après ou non
				StringTokenizer st = new StringTokenizer( dico.m_FilteringSchedule.time, ":");
				java.util.GregorianCalendar nextDate = new java.util.GregorianCalendar();
				nextDate.set( java.util.GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( st.nextToken()));
				nextDate.set( java.util.GregorianCalendar.MINUTE, Integer.parseInt( st.nextToken()));
				nextDate.set( java.util.GregorianCalendar.SECOND, 0);

				if( curDate.getTime().compareTo( nextDate.getTime()) >= 0)
					nextDate.add( java.util.GregorianCalendar.DAY_OF_YEAR, 1);
				filteringdate = format.format( nextDate.getTime());
				return this.getNextFilteringDate();
			}
			else if( dico.m_FilteringSchedule.timer != null)
			{   // On ajoute le delta à la date courante
				curDate.add( java.util.GregorianCalendar.MINUTE, dico.m_FilteringSchedule.timer.intValue());
				curDate.set( java.util.GregorianCalendar.SECOND, 0);
				filteringdate = format.format( curDate.getTime());
				return this.getNextFilteringDate();
			}
		}
		catch( Exception e)
		{
			System.out.println( "computeNextFilteringDate error: " + e.getMessage());
			filteringdate = "";
		}
		return null;
	}

}
