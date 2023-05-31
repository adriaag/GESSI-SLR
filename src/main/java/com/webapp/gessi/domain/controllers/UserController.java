package com.webapp.gessi.domain.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.user;
import com.webapp.gessi.domain.dto.userDTO;

public class UserController {
	private static Connection iniConnection() throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        return conn;
    }
	
	public static userDTO getUser(String username) throws SQLException {
		Connection con = iniConnection();
        Statement s = con.createStatement();
		return user.getUser(s, username);	
	}
	
	public static void changePassword(userDTO actUser, String newPassword) throws SQLException {
		Connection con = iniConnection();
        Statement s = con.createStatement();
        String encPwd = new BCryptPasswordEncoder().encode(newPassword);
        System.out.println(newPassword + encPwd);
        user.changePassword(s, actUser, encPwd);
		
	}
	
	public static List<String> getAllUsernames() throws SQLException {
		Connection con = iniConnection();
        Statement s = con.createStatement();
        return user.getAllUsernames(s);
		
	}

}
