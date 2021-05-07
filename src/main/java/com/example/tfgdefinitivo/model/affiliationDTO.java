package com.example.tfgdefinitivo.model;

public class affiliationDTO {
    private static final long serialVersionUID = 1L;
    private int idCom;
    private String doi; //doi


    public affiliationDTO(int idCom, String idA) {
        this.idCom = idCom;
        this.doi = idA;
    }

    public int getIdCom() {
        return idCom;
    }

    public void setIdCom(int idCom) {
        this.idCom = idCom;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }
}
