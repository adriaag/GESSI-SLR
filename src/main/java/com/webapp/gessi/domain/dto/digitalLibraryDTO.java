package com.webapp.gessi.domain.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "digitalLibrary")
public class digitalLibraryDTO implements Serializable{

    private static final long serialVersionUID = 1L;
    private int idDL;
    private String name;
    private String url;
    private int priority;

    public digitalLibraryDTO(int idDL, String name, String url, int p){
        this.idDL = idDL;
        this.name = name;
        this.url = url;
        this.priority = p;
    }

    public int getIdDL() {
        return idDL;
    }
    @XmlElement
    public void setidDL(int idDL) {
        this.idDL = idDL;
    }
    public String getName() {
        return name;
    }
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }
    public String geturl() {
        return url;
    }
    @XmlElement
    public void seturl(String url) {this.url = url; }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
