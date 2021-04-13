package com.example.tfgdefinitivo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "researcher")
public class researcher {
        private int idRes;
        private String name;

    public researcher(int idRes, String name) {
        this.idRes = idRes;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public int getIdRes() {
        return idRes;
    }
    @XmlElement
    public void setIdRes(int idRes) {
        this.idRes = idRes;
    }
}
