package com.webapp.gessi.domain.dto;

public class projectUserInvolveDTO {
	private int idProject;
	private String username;
	private String involveInfo;
	
	public projectUserInvolveDTO(int idProject, String username, String involveInfo) {
		this.idProject = idProject;
		this.username = username;
		this.involveInfo = involveInfo;
	}
	
    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int id) {
        this.idProject = id;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getInvolveInfo() {
        return this.involveInfo;
    }
    
    public void setInvolveInfo(String involveInfo) {
        this.involveInfo = involveInfo;
    }

}
