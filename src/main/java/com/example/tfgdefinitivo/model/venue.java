package com.example.tfgdefinitivo.model;

public class venue {
    private int idVen;
    private String name;
    private String acronym;

    public venue(int idVen, String name) {
        this.idVen = idVen;
        this.name = name;
    }
    public venue(int idVen, String name, String acronym) {
        this.idVen = idVen;
        this.name = name;
        this.acronym = acronym;
    }

    public int getIdVen() {
        return idVen;
    }

    public void setIdVen(int idVen) {
        this.idVen = idVen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
}
