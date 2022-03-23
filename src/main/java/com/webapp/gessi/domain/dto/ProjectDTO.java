package com.webapp.gessi.domain.dto;

public class ProjectDTO {
    private int id;
    private String name;

    public ProjectDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProjectDTO() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }
}
