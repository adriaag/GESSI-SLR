package com.webapp.gessi.domain.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.userDesignationICEC;
import com.webapp.gessi.domain.dto.userDesignationDTO;
import com.webapp.gessi.domain.dto.userDesignationICECDTO;

public class UserDesignationICECController {
	
    public static void insertRows(userDesignationDTO userDesignationDTO) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
    	String username = userDesignationDTO.getUsername();
    	int idRef = userDesignationDTO.getIdRef();
    	for (int idICEC: userDesignationDTO.getCriteriaList()) {
    		userDesignationICEC.insertRow(s, username, idRef, idICEC);
    	}
    	
    }

}
