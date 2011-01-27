package com.socialcomputing.wps.server.swatchs.loader;

import java.io.Serializable;
import java.io.StringReader;
import java.rmi.RemoteException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.socialcomputing.wps.server.swatchs.XSwatch;

@Entity
@Table(name = "swatchs")
@org.hibernate.annotations.Table(appliesTo="swatchs", indexes = {@Index(name="PRIMARY", columnNames={"name"})})
public class Swatch implements Serializable, SwatchLoader {

	public Swatch() {
		
	}
	
	public Swatch(String name, String swatch) {
		super();
		this.name = name;
		this.swatch = swatch;
	}
	
	@Id
	@Column(name = "name")
	String name;
	
	@Column(name = "swatch", columnDefinition="TEXT")
	String swatch;
	
	@Transient
	private XSwatch m_Swatch = null;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSwatchDefinition() throws RemoteException {
		return swatch;
	}

	public void setSwatch(String swatch) {
		this.swatch = swatch;
		this.m_Swatch = null;
	}
	
	public XSwatch getSwatch() throws RemoteException {
		try {
			if( m_Swatch == null) {
				SAXBuilder builder = new SAXBuilder( false);
				Document doc = builder.build( new StringReader(swatch));
				Element root = doc.getRootElement();
				m_Swatch = XSwatch.readObject( root);
			}
		} catch( Exception e) {
			throw new RemoteException ( "getSwatch failed : " + e.getMessage());
		}
		return m_Swatch;
	}
}
