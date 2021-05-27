package com.example.tfgdefinitivo.presentation.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "researcher")
public class researcherDTO {
        private int idRes;
        private String name;

    public researcherDTO(int idRes, String name) {
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
