package com.socialcomputing.wps.server.persistence.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class Accounts {

    @Id
    @Column(name = "id")
    int id;

    @Column(name = "name", columnDefinition = "varchar(255)")
    String name;

    public Accounts() {
        
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
