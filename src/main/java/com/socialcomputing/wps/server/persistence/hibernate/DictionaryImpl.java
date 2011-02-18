package com.socialcomputing.wps.server.persistence.hibernate;

import java.io.Serializable;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.Swatch;
import com.socialcomputing.wps.server.plandictionary.WPSDictionary;

@Entity
@Table(name = "dictionaries")
@org.hibernate.annotations.Table(appliesTo="dictionaries", indexes = {@Index(name="PRIMARY", columnNames={"name"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DictionaryImpl implements Serializable, Dictionary {

	private static String s_DateFormat = "yyyy/MM/dd HH:mm:ss";
	
	@Id
	@Column(name = "name", columnDefinition="varchar(255) default ''")
	//@Column(name="name")
	String name;
	
	@Column(name = "dictionary", columnDefinition="text")
	//@Column(name = "dictionary")
	String dictionary;
	
	@Column(name = "filteringdate", nullable=false, columnDefinition="varchar(50) default ''")
	//@Column(name = "filteringdate")
	String filteringdate;
	
	@Transient
	private WPSDictionary m_Dico = null; // Speeder
	
	//@OneToMany(cascade=CascadeType.ALL, targetEntity=SwatchImpl.class )
	@OneToMany(mappedBy="dictionary", targetEntity=SwatchImpl.class)
	private List<Swatch> swatchs;
	
	public DictionaryImpl() {
		this.dictionary = null;
		this.name = null;
		this.filteringdate = null;
	}
	
	public DictionaryImpl(String name, String dictionary, String filteringdate) {
		this.dictionary = dictionary;
		this.name = name;
		this.filteringdate = filteringdate;
	}
	
	public List<Swatch> getSwatchs() {
		return swatchs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDefinition() {
		return dictionary;
	}

	@Override
	public void setDefinition(String dictionary) {
		this.dictionary = dictionary;
		this.m_Dico = null;
	}

	public String getFilteringdate() {
		return filteringdate;
	}

	public void setFilteringdate(String filteringdate) {
		this.filteringdate = filteringdate;
	}
	
	public WPSDictionary getDictionary() {
		try {
			if (m_Dico == null)	{
				SAXBuilder builder = new SAXBuilder(false);
				Document doc = builder.build(new StringReader(dictionary));
				Element root = doc.getRootElement();
				m_Dico = WPSDictionary.readObject( root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m_Dico;
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
