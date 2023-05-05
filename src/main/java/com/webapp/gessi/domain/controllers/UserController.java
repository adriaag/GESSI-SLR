package com.webapp.gessi.domain.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
	
	public userDTO getUser(String username) throws SQLException {
		Connection con = iniConnection();
        Statement s = con.createStatement();
		return user.getUser(s, username);	
	}

}
