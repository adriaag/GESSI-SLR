package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.webapp.gessi.domain.dto.userDTO;

public class user {
		
	private static final int usernameMaxLength = 50;
	private static final int passwordMaxLength = 60;
    /* create table affiliations(name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("CREATE TABLE USERS (\n"
            		+ "USERNAME VARCHAR("+usernameMaxLength+"), "
            		+ "PASSWORD VARCHAR("+passwordMaxLength+"), "
            		+ "PRIMARY KEY (USERNAME))");
            System.out.println("Created table user");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table user exists");
            else {
            	System.out.println("Error en la creación de table user");
            	System.out.println(t.getCause());
            }
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table users");
        System.out.println("Dropped table users");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla user not exist");
            System.out.println(sqlException.getCause());
        }
    }
    
    public static userDTO getUser(Statement s, String username) throws SQLException {
    	String query ="SELECT * FROM USERS WHERE username = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,username);
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        if(rs.next()) 
        	return new userDTO(rs.getString("username"), rs.getString("password"));
        
        return null;
    	
    }
   

}