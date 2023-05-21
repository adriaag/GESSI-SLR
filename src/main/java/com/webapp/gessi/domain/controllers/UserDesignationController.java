package com.webapp.gessi.domain.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.userDesignation;
import com.webapp.gessi.domain.dto.userDesignationDTO;

public class UserDesignationController {
	
	public static void insertRow(String username, int idRef, int numDesignation) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        userDesignation.insertRow(s, username, idRef, numDesignation);
        s.getConnection().commit();
    }
	
	public static void addCriteria(userDesignationDTO designation) throws SQLException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        
		
	}


}
