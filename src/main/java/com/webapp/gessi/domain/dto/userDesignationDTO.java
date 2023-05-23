package com.webapp.gessi.domain.dto;

import java.util.List;

public class userDesignationDTO {
	private String username;
	private int idRef;
	private boolean processed;
	private int numDesignation;
	private List<Integer> criteriaList;
	
	public userDesignationDTO(String username, int idRef, int numDesignation, boolean processed, List<Integer> criteriaList) {
		this.username = username;
		this.idRef = idRef;
		this.processed = processed;
		this.criteriaList = criteriaList;
		this.numDesignation = numDesignation;
	}
	
	public userDesignationDTO(String username, int idRef, int numDesignation, boolean processed, int criteriaList[]) {
		this.username = username;
		this.idRef = idRef;
		this.processed = processed;
		//this.criteriaList = criteriaList;
		this.numDesignation = numDesignation;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public int getIdRef() {
		return this.idRef;
	}
	
	public void setIdRef(int idRef) {
		this.idRef = idRef;
	}
	
	public boolean getProcessed() {
		return this.processed;
	}
	
	public void setProcessed(boolean processed) {
		this.processed = processed;
	}
	
	public List<Integer> getCriteriaList() {
		return this.criteriaList;
	}
	
	public void setCriteriaList(List<Integer> criteriaList) {
		this.criteriaList = criteriaList;
	}
	
	public int getNumDesignation() {
		return this.numDesignation;
	}
	
	public void setNumDesignation(int numDesignation) {
		this.numDesignation = numDesignation;
	}

}
