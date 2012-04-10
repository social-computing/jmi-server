package com.socialcomputing.wps.server.persistence.hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;

@Entity
@XmlRootElement
public class Track implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    @XmlElement
    private Long id;
    
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    @Index(name="nameIndex")
    private String      name;

    @XmlElement
    @Index(name="dateIndex")
    private Date        date;

    @XmlElement
    private long        duration;
    
    @XmlElement
    private boolean     success;

    @OneToOne(cascade = CascadeType.ALL)
    //@PrimaryKeyJoinColumn
    @JoinColumn(name="error")
    @XmlElement
    private Error       error;

    public Track(String name) {
        super();
        this.name = name;
        this.date = new Date();
        this.duration = System.currentTimeMillis();
        this.success = false;
    }
    
    public String getName() {
        return name;
    }
    
    public Date getDate() {
        return date;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    public void stop() {
        this.duration = System.currentTimeMillis() - this.duration;
        this.success = true;
        //this.error = null;
    }
    
    public void stop( Exception e, String agent, Hashtable<String, Object> params) {
        this.duration = System.currentTimeMillis() - this.duration;
        this.success = false;
        this.error = new Error(e, agent, params);
    }
}
