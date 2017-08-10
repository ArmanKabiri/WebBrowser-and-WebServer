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
@Table(name = "CacheWebPage")
public class CacheWebPage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String host;
    private String file;
    private String htmlContent;
    private String dateTime;

    public CacheWebPage(int id, String host, String file, String htmlContent, String dateTime) {
        this.id = id;
        this.host = host;
        this.file = file;
        this.htmlContent = htmlContent;
        this.dateTime = dateTime;
    }

    public CacheWebPage(String host, String file, String htmlContent, String dateTime) {
        this.host = host;
        this.file = file;
        this.htmlContent = htmlContent;
        this.dateTime = dateTime;
    }

    public CacheWebPage() {
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

}
