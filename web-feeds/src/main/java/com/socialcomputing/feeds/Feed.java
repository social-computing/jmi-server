package com.socialcomputing.feeds;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Feed {

    @Id
    @XmlElement
    private String      url;
    @XmlElement
    private String      title;
    @XmlAttribute
    private int         count;
    @XmlAttribute
    private boolean     success;
    @XmlAttribute
    private Date        created;
    @XmlAttribute
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
        created = new Date();
    }
    
    public void increment() {
        count = count + 1;
        updated = new Date();
    }
    
}
