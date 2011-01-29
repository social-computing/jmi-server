package com.socialcomputing.wps.server.plandictionary.loader;

import java.io.Serializable;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.socialcomputing.wps.server.plandictionary.WPSDictionary;

@Entity
@Table(name = "dictionaries")
@org.hibernate.annotations.Table(appliesTo="dictionaries", indexes = {@Index(name="PRIMARY", columnNames={"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Dictionary implements Serializable, DictionaryLoader {

	public Dictionary() {
		
	}
	
	public Dictionary(String name, String dictionary, String filteringdate) {
		super();
		this.dictionary = dictionary;
		this.name = name;
		this.filteringdate = filteringdate;
	}
	
	private static String s_DateFormat = "yyyy/MM/dd HH:mm:ss";
	
	@Id
	@Column(name = "name", columnDefinition="varchar(100) default ''")
	String name;
	
	@Column(name = "dictionary", columnDefinition="TEXT")
	String dictionary;
	
	@Column(name = "filteringdate", nullable=false, columnDefinition="varchar(50) default ''")
	
	String filteringdate;
	
	@Transient
	private WPSDictionary m_Dico = null; // Speeder
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDictionaryDefinition() throws RemoteException {
		return dictionary;
	}

	public void setDictionaryDefinition(String dictionary) throws RemoteException, JDOMException {
		this.dictionary = dictionary;
	}

	public String getFilteringdate() {
		return filteringdate;
	}

	public void setFilteringdate(String filteringdate) {
		this.filteringdate = filteringdate;
	}
	
	public WPSDictionary getDictionary() throws RemoteException {
		try {
			if (m_Dico == null)	{
				SAXBuilder builder = new SAXBuilder(false);
				Document doc = builder.build(new StringReader(dictionary));
				Element root = doc.getRootElement();
				m_Dico = WPSDictionary.readObject( root);
			}
		} catch (Exception e) {
			throw new RemoteException ( "getDictionary failed : " + e.getMessage());
		}
		return m_Dico;
	}
	
	public void setDictionaryDefinitionApplyDTD(String definition) throws RemoteException, JDOMException {
		// TODO Auto-generated method stub
	}
	
	public Date getNextFilteringDate(){
		if (filteringdate==null || filteringdate.length() == 0) return null;
		try {
			SimpleDateFormat format = new SimpleDateFormat(s_DateFormat);
			return format.parse( filteringdate);
		} catch (Exception e) {
			filteringdate = "";
			System.out.println( "getNextFilteringDate error: " + e.getMessage());
		}
		return null;
	}
	
	public Date computeNextFilteringDate() {
		try {
			WPSDictionary dico = this.getDictionary();
			if( dico.m_FilteringSchedule == null) {
				filteringdate = "";
				return null;
			}
			GregorianCalendar curDate = new java.util.GregorianCalendar();
			SimpleDateFormat format = new SimpleDateFormat(s_DateFormat);
			if (dico.m_FilteringSchedule.time != null) {
				// Sur le jour d'apres ou non
				StringTokenizer st = new StringTokenizer(dico.m_FilteringSchedule.time, ":");
				GregorianCalendar nextDate = new GregorianCalendar();
				nextDate.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt( st.nextToken()));
				nextDate.set(GregorianCalendar.MINUTE, Integer.parseInt( st.nextToken()));
				nextDate.set(GregorianCalendar.SECOND, 0);

				if (curDate.getTime().compareTo(nextDate.getTime()) >= 0)
					nextDate.add(GregorianCalendar.DAY_OF_YEAR, 1);
				filteringdate = format.format(nextDate.getTime());
				return this.getNextFilteringDate();
			} else if( dico.m_FilteringSchedule.timer != null) {
				// On ajoute le delta a la date courante
				curDate.add(GregorianCalendar.MINUTE, dico.m_FilteringSchedule.timer.intValue());
				curDate.set(GregorianCalendar.SECOND, 0);
				filteringdate = format.format(curDate.getTime());
				return this.getNextFilteringDate();
			}
		} catch( Exception e) {
			System.out.println("computeNextFilteringDate error: " + e.getMessage());
			filteringdate = "";
		}
		return null;
	}
}
