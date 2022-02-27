package com.webapp.gessi.domain.dto;


public class ExclusionDTO {

    private int idRef;
    private String idICEC;

    public ExclusionDTO(int idRef, String idICEC) {
        this.idRef = idRef;
        this.idICEC = idICEC;
    }

    public ExclusionDTO() {}

    public int getIdRef() {
        return idRef;
    }

    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }

    public String getIdICEC() { return idICEC; }

    public void setIdICEC(String idICEC) {
        this.idICEC = idICEC;
    }
}
