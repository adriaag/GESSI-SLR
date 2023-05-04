package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class user {
		
	private static final int usernameMaxLength = 50;
	private static final int passwordMaxLength = 50;
    /* create table affiliations(name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("CREATE TABLE USERS (\n"
            		+ "USERNAME VARCHAR("+usernameMaxLength+"), "
            		+ "PASSWORD VARCHAR("+passwordMaxLength+"), "
            		+ "CONSTRAINT PRIMARY KEY (USERNAME))");
            System.out.println("Created table user");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table user exists");
            else System.out.println("Error en la creación de table user");
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table user");
        System.out.println("Dropped table user");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla user not exist");
        }
    }
    
    public static ResultSet getUser(Statement s, String username) throws SQLException {
    	String query ="SELECT * FROM AFFILIATIONS WHERE username = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,username);
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        return rs;
    	
    }
   

}
