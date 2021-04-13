package com.example.tfgdefinitivo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "company")
public class company implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idCom;
    private String name;

    public company(int idCom, String name) {
        this.idCom = idCom;
        this.name = name;
    }

    public int getIdCom() {
        return idCom;
    }
    @XmlElement
    public void setIdCom(int idCom) {
        this.idCom = idCom;
    }

    public String getName() {
        return name;
    }
    @XmlElement
    public void setName(String name) {
        this.name = name;
    }
}
