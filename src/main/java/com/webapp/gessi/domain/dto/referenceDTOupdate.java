package com.webapp.gessi.domain.dto;

import java.io.Serializable;

public class referenceDTOupdate implements Serializable {
    private int id;
    private String estado;
    private String applCriteria;

    public referenceDTOupdate() {}

    public referenceDTOupdate(int id, String estado, String applCriteria) {
        this.id = id;
        this.estado = estado;
        this.applCriteria = applCriteria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
