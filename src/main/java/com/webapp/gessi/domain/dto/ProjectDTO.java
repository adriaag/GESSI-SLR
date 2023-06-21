package com.webapp.gessi.domain.dto;


public class ProjectDTO {
    private int id;
    private String name;
    private int idDuplicateCriteria;
    private String topic;
    private String researchQuestion;
    private String protocol;
    private String comments;
    private projectUserInvolveDTO[] involvedUsers;
    private projectDigitalLibraryDTO[] digitalLibraries;
    private String orderColSearch;
    private String orderDirSearch;
    private String orderColScreen;
    private String orderDirScreen;
    
    public ProjectDTO(int id, String name, int idDuplicateCriteria, String topic, String researchQuestion,
    		String protocol, byte[] protocolImg, String comments, projectUserInvolveDTO[] involvedUsers, 
    		projectDigitalLibraryDTO[] digitalLibraries, String orderColSearch, String orderDirSearch, 
    		String orderColScreen, String orderDirScreen) {
        this.id = id;
        this.name = name;
        this.idDuplicateCriteria = idDuplicateCriteria;
        this.topic = topic;
        this.researchQuestion = researchQuestion;
        this.protocol = protocol;
        this.comments = comments;
        this.involvedUsers = involvedUsers;
        this.digitalLibraries = digitalLibraries;
        this.orderColSearch = orderColSearch;
        this.orderDirSearch = orderDirSearch;
        this.orderColScreen = orderColScreen;
        this.orderDirScreen = orderDirScreen;
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
    
    public String getOrderColSearch() {return this.orderColSearch;}
    
    public String getOrderDirSearch() {return this.orderDirSearch;}
    
    public String getOrderDirScreen() {return this.orderDirScreen;}
    
    public String getOrderColScreen() {return this.orderColScreen;}
    
    
    
    
}
