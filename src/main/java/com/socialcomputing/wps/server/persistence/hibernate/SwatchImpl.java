package com.socialcomputing.wps.server.persistence.hibernate;

import java.io.Serializable;
import java.io.StringReader;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.socialcomputing.wps.server.persistence.Dictionary;
import com.socialcomputing.wps.server.persistence.Swatch;
import com.socialcomputing.wps.server.swatchs.XSwatch;

@Entity
@Table(name = "swatchs")
@org.hibernate.annotations.Table(appliesTo = "swatchs",
                                 indexes = { @Index(name = "PRIMARY", columnNames = { "name" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class SwatchImpl implements Serializable, Swatch {

    @Id
    @Column(name = "name", columnDefinition = "varchar(255) default ''")
    // @Column(name = "name")
    String name;

    @Column(name = "swatch", columnDefinition = "text")
    // @Column(name = "swatch")
    String swatch;

    @Transient
    private XSwatch m_Swatch = null;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "swatch_dictionaryName")
    private DictionaryImpl dictionary;

    public SwatchImpl() {
        this.name = null;
        this.swatch = null;
    }

    public SwatchImpl(String name, String swatch) {
        this.name = name;
        this.swatch = swatch;
    }
    
    public SwatchImpl(String name, String swatch, Dictionary dictionary) {
        this.name = name;
        this.swatch = swatch;
        this.dictionary = (DictionaryImpl) dictionary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDefinition() {
        return swatch;
    }

    @Override
    public void setDefinition(String definition) throws JDOMException {
        this.swatch = definition;
        this.m_Swatch = null;
    }
    
    @Override
    public void setDictionary(Dictionary dictionary) throws JDOMException {
        this.dictionary = (DictionaryImpl) dictionary;
    }

    @Override
    public XSwatch getSwatch() {
        try {
            if (m_Swatch == null) {
                SAXBuilder builder = new SAXBuilder(false);
                Document doc = builder.build(new StringReader(swatch));
                Element root = doc.getRootElement();
                m_Swatch = XSwatch.readObject(root);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return m_Swatch;
    }

}
