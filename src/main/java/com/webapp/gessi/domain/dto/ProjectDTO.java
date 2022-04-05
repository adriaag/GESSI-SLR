package com.webapp.gessi.domain.dto;

public class ProjectDTO {
    private int id;
    private String name;
    private int idDuplicateCriteria;

    public ProjectDTO(int id, String name, int idDuplicateCriteria) {
        this.id = id;
        this.name = name;
        this.idDuplicateCriteria = idDuplicateCriteria;
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

    public int getIdDuplicateCriteria() {
        return idDuplicateCriteria;
    }

    public void setIdDuplicateCriteria(int idDuplicateCriteria) {
        this.idDuplicateCriteria = idDuplicateCriteria;
    }
}
