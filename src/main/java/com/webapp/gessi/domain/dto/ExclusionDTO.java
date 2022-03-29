package com.webapp.gessi.domain.dto;


public class ExclusionDTO {

    private int idRef;
    private int idICEC;
    private String nameICEC;

    public ExclusionDTO(int idRef, int idICEC, String nameICEC) {
        this.idRef = idRef;
        this.idICEC = idICEC;
        this.nameICEC = nameICEC;
    }

    public ExclusionDTO() {}

    public int getIdRef() {
        return idRef;
    }

    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }

    public String getNameICEC() {
        return nameICEC;
    }

    public void setNameICEC(String nameICEC) {
        this.nameICEC = nameICEC;
    }

    public int getIdICEC() { return idICEC; }

    public void setIdICEC(int idICEC) {
        this.idICEC = idICEC;
    }
}
