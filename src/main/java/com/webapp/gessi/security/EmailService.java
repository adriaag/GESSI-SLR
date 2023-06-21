package com.webapp.gessi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	    @Autowired 
	    private JavaMailSender javaMailSender;
	 
	    @Value("${spring.mail.username}") 
	    private String sender;
	 
	    public Boolean sendEmail(String recipient, String message, String subject)
	    {
	 
	        try {	       
	            SimpleMailMessage mailMessage = new SimpleMailMessage();
	            
	            mailMessage.setFrom(sender);
	            mailMessage.setTo(recipient);
	            mailMessage.setText(message);
	            mailMessage.setSubject(subject);
	 
	            // Sending the mail
	            javaMailSender.send(mailMessage);
	            return true;
	        }
	 
	        // Catch block to handle the exceptions
	        catch (MailException e) {
	        	e.printStackTrace();
	            return false;
	        }
	    }

}
