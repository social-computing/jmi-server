package com.socialcomputing.wps.server.persistence.hibernate;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.socialcomputing.wps.server.planDictionnary.connectors.JMIException;

@Entity
@XmlRootElement
public class Error implements Serializable {

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    @XmlElement
    private Long id;

    @XmlElement
    @Column(columnDefinition = "varchar(64)")
    private String      origin;

    @XmlElement
    private Long        code;
    
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    private String      message;

    @XmlElement
    @Column(columnDefinition="text")
    private String      trace;
    
    @XmlElement
    @Column(columnDefinition = "varchar(512)")
    private String      agent;
    
    @XmlElement
    @Column(columnDefinition="text")
    private String      parameters;
    
    public Error() {
    }
    
    public Error(JMIException e, String agent, Hashtable<String, Object> params) {
        super();
        this.origin = e.getOrigin().toString();
        this.code = e.getCode();
        this.message = e.getMessage();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw);
        e.printStackTrace(pw);
        this.trace = sw.toString();
        this.agent = agent;
        List<String> keys = new ArrayList<String>( params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for( String key : keys) {
            if( !key.equalsIgnoreCase("User-Agent"))  {
                sb.append( key).append('=').append(params.get(key)).append(',');
            }
        }
        this.parameters = sb.toString();
    }
    
}
