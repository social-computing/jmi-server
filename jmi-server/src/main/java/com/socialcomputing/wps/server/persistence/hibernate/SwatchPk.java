package com.socialcomputing.wps.server.persistence.hibernate;

import java.io.Serializable;

import javax.persistence.Column;

import org.jdom.JDOMException;

public class SwatchPk implements Serializable {

    @Column(name = "name", columnDefinition = "varchar(50) default ''")
    //@Index(name="name")
    String name;
    
    @Column(name = "dictionaryName", unique = false, nullable = false, insertable = true, updatable = true, columnDefinition = "varchar(255) default ''")
    String dictionary;
    
    public SwatchPk() {
        this.name = null;
        this.dictionary = null;
    }
    
    public SwatchPk(String name, String dictionary) {
        this.name = name;
        this.dictionary = dictionary;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setDictionary(String dictionary) throws JDOMException {
        this.dictionary = dictionary;
    }
    
    public String getDictionaryName() {
        return dictionary;
    }
}
