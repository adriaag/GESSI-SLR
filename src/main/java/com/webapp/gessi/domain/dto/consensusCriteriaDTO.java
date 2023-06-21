package com.webapp.gessi.domain.dto;

import java.util.List;

public class consensusCriteriaDTO {

    private int idRef;
    private List<Integer> idICEC;

    public consensusCriteriaDTO(int idRef,  List<Integer> idICEC){
        this.idRef = idRef;
        this.idICEC = idICEC;
    }

    public consensusCriteriaDTO() {}

    public int getIdRef() {
        return idRef;
    }

    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }

    public List<Integer> getIdICEC() { return idICEC; }

    public void setIdICEC(List<Integer> idICEC) {
        this.idICEC = idICEC;
    }
}
