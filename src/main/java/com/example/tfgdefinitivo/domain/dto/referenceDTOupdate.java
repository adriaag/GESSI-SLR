package com.example.tfgdefinitivo.domain.dto;

import java.io.Serializable;

public class referenceDTOupdate implements Serializable {
    private int idRef;
    private String estado;
    private String applCriteria;

    public referenceDTOupdate() {}

    public referenceDTOupdate(int aux, String estado, String applCriteria) {
        this.idRef = aux;
        this.estado = estado;
        this.applCriteria = applCriteria;
    }

    public int getIdRef() {
        return idRef;
    }

    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getApplCriteria() { return applCriteria; }

    public void setApplCriteria(String applCriteria) { this.applCriteria = applCriteria;    }
}
