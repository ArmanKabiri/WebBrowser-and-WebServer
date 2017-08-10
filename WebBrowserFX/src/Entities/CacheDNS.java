/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author arman
 */
@Entity
@Table(name = "CacheDNS")
public class CacheDNS {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String host;
    private String ip;
//    private String ips;

//    public CacheDNS(String host, String ip) {
//        this.host = host;
//        this.ip = ip;
//    }

    public CacheDNS(int id, String host, String ip) {
        this.id = id;
        this.host = host;
        this.ip = ip;
    }

    public CacheDNS(String host, String ip) {
        this.host = host;
        this.ip = ip;
    }
    
    
    
    public CacheDNS() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
