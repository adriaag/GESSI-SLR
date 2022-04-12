package com.webapp.gessi.domain.dto;

import java.io.Serializable;
import java.util.List;

public class referenceDTOupdate implements Serializable {
    private int id;
    private String state;
    private List<Integer> applCriteria;

    public referenceDTOupdate() {}

    public referenceDTOupdate(int id, String state, List<Integer> applCriteria) {
        this.id = id;
        this.state = state;
        this.applCriteria = applCriteria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Integer> getApplCriteria() { return applCriteria; }

    public void setApplCriteria(List<Integer> applCriteria) { this.applCriteria = applCriteria;    }
}
