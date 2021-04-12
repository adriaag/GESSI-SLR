package com.example.tfgdefinitivo.model;

public class researcher {
        private int idRes;
        private String name;

    public researcher(int idRes, String name) {
        this.idRes = idRes;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdRes() {
        return idRes;
    }

    public void setIdRes(int idRes) {
        this.idRes = idRes;
    }
}
