package com.webapp.gessi.domain.dto;

public class userDesignationICECDTO {
	private String username;
	private int idRef;
	private int idICEC;
	
	public userDesignationICECDTO(String username, int idRef, int idICEC) {
		this.username = username;
		this.idRef = idRef;
		this.idICEC = idICEC;
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
	
	
	public int getIdICEC() {
		return this.idICEC;
	}
	
	public void serIdICEC(int idICEC) {
		this.idICEC = idICEC;
	}
	
	

}
