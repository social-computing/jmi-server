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
public class Site {

    @Id
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    private String      url;
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    private String      feed;
    @XmlAttribute
    @Index(name="countIndex")
    private int         count;
    @XmlAttribute
    private Date        created;
    @XmlAttribute
    private Date        updated;
    
    public Site() {
        url = null;
        feed = null;
        count = 0;
    }
    
    public Site(String url, String feed) {
        super();
        this.url = url;
        this.feed = feed;
        this.count = 1;
        this.created = new Date();
        this.updated = new Date();
    }

    public void incrementUpdate(String feed) {
        this.feed = feed;
        this.count++;
        this.updated = new Date();
    }
}
