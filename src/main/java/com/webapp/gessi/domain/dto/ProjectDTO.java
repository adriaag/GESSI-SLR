package com.webapp.gessi.domain.dto;

import java.util.List;

public class ProjectDTO {
    private int id;
    private String name;
    private int idDuplicateCriteria;
    private String topic;
    private String researchQuestion;
    private String protocol;
    private byte[] protocolImg;
    private String comments;
    private projectUserInvolveDTO[] involvedUsers;
    private projectDigitalLibraryDTO[] digitalLibraries;

    public ProjectDTO(int id, String name, int idDuplicateCriteria, String topic, String researchQuestion,
    		String protocol, byte[] protocolImg, String comments, projectUserInvolveDTO[] involvedUsers, 
    		projectDigitalLibraryDTO[] digitalLibraries) {
        this.id = id;
        this.name = name;
        this.idDuplicateCriteria = idDuplicateCriteria;
        this.topic = topic;
        this.researchQuestion = researchQuestion;
        this.protocol = protocol;
        this.protocolImg = protocolImg;
        this.comments = comments;
        this.involvedUsers = involvedUsers;
        this.digitalLibraries = digitalLibraries;
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
    
    public String getTopic() { return topic; }

    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public String getResearchQuestion() { return researchQuestion; }

    public void setResearchQuestion(String researchQuestion) {
        this.researchQuestion = researchQuestion;
    }
    
    public String getProtocol() { return protocol; }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public byte[] getProtocolImg() { return protocolImg; }

    public void setProtocolImg(byte[] protocolImg) {
        this.protocolImg = protocolImg;
    }
    
    public String getComments() { return comments; }

    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public projectUserInvolveDTO[] getInvolvedUsers() { return involvedUsers; }

    public void setInvolvedUsers(projectUserInvolveDTO[] involvedUsers) {
        this.involvedUsers = involvedUsers;
    }
    
    public projectDigitalLibraryDTO[] getDigitalLibraries() { return digitalLibraries; }

    public void setDigitalLibraries(projectDigitalLibraryDTO[] digitalLibraries) {
        this.digitalLibraries = digitalLibraries;
    }
    
    
    
}
