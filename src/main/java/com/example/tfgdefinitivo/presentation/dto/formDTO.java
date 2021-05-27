package com.example.tfgdefinitivo.presentation.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "formDTO")
public class formDTO implements Serializable {
    private String path;
    private String dlNum;

    public formDTO(String path, String dl) {
        this.path = path;
        this.dlNum = dl;
    }
    public formDTO() { }

    public String getPath() { return path; }

    @XmlElement
    public void setPath(String path) {
        this.path = path;
    }

    public String getDl() {
        return dlNum;
    }

    @XmlElement
    public void setDl(String dlNum) {
        this.dlNum = dlNum;
    }
}
