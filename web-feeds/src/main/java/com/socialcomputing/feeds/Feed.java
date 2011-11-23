package com.socialcomputing.feeds;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
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
    @Lob
    private byte[]      thumbnail;
    @XmlAttribute
    private int         thumbnail_width;
    @XmlAttribute
    private int         thumbnail_height;
    @XmlAttribute
    private String      thumbnail_mime;
    @XmlAttribute
    private Date        thumbnail_date;
    
    public Feed() {
        this.url = null;
        this.title = null;
        this.success = false;
        this.count = 0;
        this.thumbnail = null;
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

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getCount() {
        return count;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setThumbnail_width(int thumbnail_width) {
        this.thumbnail_width = thumbnail_width;
    }

    public void setThumbnail_height(int thumbnail_height) {
        this.thumbnail_height = thumbnail_height;
    }

    public void setThumbnail_mime(String thumbnail_mime) {
        this.thumbnail_mime = thumbnail_mime;
    }

    public void setThumbnail_date(Date thumbnail_date) {
        this.thumbnail_date = thumbnail_date;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }
    
}
