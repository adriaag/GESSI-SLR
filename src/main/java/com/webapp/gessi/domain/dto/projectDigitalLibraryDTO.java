package com.webapp.gessi.domain.dto;


public class projectDigitalLibraryDTO {
	private int idProject;
	private int idDL;
	private String searchString;
	private int numSearchResults;
	
	public projectDigitalLibraryDTO(int idProject, int idDL, String searchString, int numSearchResults) {
		this.idProject = idProject;
		this.idDL = idDL;
		this.searchString = searchString;
		this.numSearchResults = numSearchResults;
	}
	
    public int getIdProject() {
        return idProject;
    }

    public void setIdProject(int id) {
        this.idProject = id;
    }
    
    
    public int getIdDL() {
        return idDL;
    }

    
    public void setidDL(int idDL) {
        this.idDL = idDL;
    }
    
    public String getSearchString() {
    	return this.searchString;
    }
    
    public void setSearchString(String searchString) {
    	this.searchString = searchString;
    }
    
    public int getNumSearchResults() {
        return numSearchResults;
    }

    
    public void setNumSearchResults(int numSearchResults) {
        this.numSearchResults = numSearchResults;
    }
    

}
