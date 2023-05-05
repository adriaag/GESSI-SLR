package com.webapp.gessi.domain.dto;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "user")
public class userDTO {
	    private String username;
	    private String password;

	    public userDTO(String username, String password) {
	        this.username = username;
	        this.password = password;
	    }

	    public String getUsername() {
	        return this.username;
	    }
	    
	    @XmlElement
	    public void setUsername(String username) {
	        this.username = username;
	    }

	    public String getPassword() {
	        return this.password;
	    }
	    
	    @XmlElement
	    public void setPassword(String password) {
	        this.password = password;
	    }
}
