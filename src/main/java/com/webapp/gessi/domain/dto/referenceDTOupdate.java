package com.webapp.gessi.domain.dto;

import java.io.Serializable;

public class referenceDTOupdate implements Serializable {
    private String estado;
    private String applCriteria;

    public referenceDTOupdate() {}

    public referenceDTOupdate(String estado, String applCriteria) {
        this.estado = estado;
        this.applCriteria = applCriteria;
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
