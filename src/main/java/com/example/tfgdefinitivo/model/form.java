package com.example.tfgdefinitivo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "form")
public class form implements Serializable {
    private String path;
    private int dlNum;

    public form(String path, int dl) {
        this.path = path;
        this.dlNum = dl;
    }

    public String getPath() {
        return path;
    }
    @XmlElement
    public void setPath(String path) {
        this.path = path;
    }

    public int getDl() {
        return dlNum;
    }
    @XmlElement
    public void setDl(int dl) {
        this.dlNum = dl;
    }
}
