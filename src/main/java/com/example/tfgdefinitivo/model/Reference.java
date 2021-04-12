package com.example.tfgdefinitivo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@XmlRootElement(name = "reference")
public class Reference implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idRef;
    private String doi;
    private int idDL;
    private article art;
    private digitalLibrary dl;

    public Reference(){}

    public Reference(int aux, String aux2, int aux3) {
        this.idRef = aux;
        this.doi = aux2;
        this.idDL = aux3;
    }
    public Reference(int aux, String aux2, int aux3, digitalLibrary dl) {
        this.idRef = aux;
        this.doi = aux2;
        this.idDL = aux3;
        this.dl = dl;
    }
    public int getIdRef() {
            return idRef;
        }

        @XmlElement
        public void setIdRef(int idRef) {
            this.idRef = idRef;
        }
        public String getDoi() {
            return doi;
        }
        @XmlElement
        public void setDoi(String doi) {
            this.doi = doi;
        }
        public int getidDL() {
            return idDL;
        }
        @XmlElement
        public void setidDL(int idDL) {
            this.idDL = idDL;
        }


    public article getArt() {
        return art;
    }

    public void setArt(article art) {
        this.art = art;
    }

    public digitalLibrary getDl() {
        return dl;
    }
    @XmlElement
    public void setDl(digitalLibrary dl) {
        this.dl = dl;
    }
}
