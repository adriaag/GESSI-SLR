package com.example.tfgdefinitivo.model;

public class author {
    private static final long serialVersionUID = 1L;
    private int idRes;
    private String doi; //doi

    public author(int idRes, String doi) {
        this.idRes = idRes;
        this.doi = doi;
    }

    public int getIdRes() {
        return idRes;
    }

    public void setIdRes(int idRes) {
        this.idRes = idRes;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

}
