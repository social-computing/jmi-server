package com.socialcomputing.feeds;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;

@Entity
@XmlRootElement
public class Feed {

    @Id
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    private String      url;
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    private String      title;
    @XmlAttribute
    @Index(name="countIndex")
    private int         count;
    @XmlAttribute
    private boolean     success;
    @XmlAttribute
    private Date        created;
    @XmlAttribute
    @Index(name="updateIndex")
    private Date        updated;

    public Feed() {
        this.url = null;
        this.title = null;
        this.success = false;
        this.count = 0;
    }
    
    public Feed(String url, String title, boolean success) {
        super();
        this.url = url;
        this.title = title;
        this.success = success;
        this.count = 1;
        this.created = new Date();
        this.updated = created;
    }
    
    public void incrementUpdate(String title, boolean success) {
        this.title = title;
        this.success = success;
        this.count++;
        this.updated = new Date();
    }
    
}
