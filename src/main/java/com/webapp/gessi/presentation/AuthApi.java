package com.webapp.gessi.presentation;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.http.parser.Authorization;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.gessi.domain.controllers.UserController;
import com.webapp.gessi.domain.dto.userDTO;
import com.webapp.gessi.security.EmailService;
import com.webapp.gessi.security.TokenBuilder;

@RestController
@RequestMapping("/auth/")
public class AuthApi {
	
	@Autowired
	private EmailService mailService;
	
	@PostMapping(value=("/changePasswordRequest")) 
	public ResponseEntity<?> changePassword(@RequestParam("username") String username) {

		try {
			userDTO user = UserController.getUser(username);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
			}
			
			String changePwdToken = TokenBuilder.buildChangePwdToken(user.getUsername());
			
			String mail = user.getUsername();
			
			if (sendEmailForgotPassword(mail, changePwdToken)) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
		}
		catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }
		return internalServerError();
    			
	}
	
	@PutMapping(value=("/password"))
	public ResponseEntity<?> updatePassword(@RequestParam("token") String token, @RequestParam("password") String newPassword) {
		try {
			String username = TokenBuilder.decodeUsername(token);
			userDTO user = UserController.getUser(username);
			System.out.println(username);
			
			if (user != null) {
				UserController.changePassword(user, newPassword);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
		}
		catch(SQLException e) {
			return sqlExcHandler(e);
		}
		
		return internalServerError();
	}
	
	private Boolean sendEmailForgotPassword(String mail, String token) {
		String subject = "GESSI-SLR password recovery";
		String body = "You have requested a password recovery from GESSI-SLR webapp."
				+ "\nClick on the link to proceed:\nhttps://localhost:4200/forgotPassword?token="+token;
	
		return mailService.sendEmail(mail, body, subject);
	}
	
	private ResponseEntity<?> sqlExcHandler(SQLException e) {
    	boolean conflict = false;
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e.printStackTrace();
            
            switch(e.getSQLState()) {
            case "23505": //duplicate key contraint violation
            	conflict = true;
            	break;
            //més casos si es desitja
            }
            e = e.getNextException();
        }
        if (conflict) {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body("");	
        }
        else {
        	return internalServerError();
        }
    }
    
    private ResponseEntity<?> internalServerError() {
    	JSONObject returnData = new JSONObject();
    	returnData.put("message", "Resource not found");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(returnData.toString());
    }

}
