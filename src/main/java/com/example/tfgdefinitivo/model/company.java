package com.example.tfgdefinitivo.model;

public class company {
    private int idCom;
    private String name;


    public company(int idCom, String name) {
        this.idCom = idCom;
        this.name = name;
    }

    public int getIdCom() {
        return idCom;
    }

    public void setIdCom(int idCom) {
        this.idCom = idCom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
