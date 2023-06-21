package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.webapp.gessi.domain.dto.userDTO;

public class user {
		
	private static final int usernameMaxLength = 100;
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
            
            insertUser(s, "None", "1234567890"); //Usuari especial que es fa servir quan
            									//es vol classificar una referència a partir de els IC/EC
            									//d'un sol usuari. Mai es podrà iniciar sessió, ja que les contrasenyes a la 
            									//base de dades estan encriptades i tenen un altre format
            System.out.println("Special user None inserted");
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
    
    public static void changePassword(Statement s, userDTO user, String newPassword) throws SQLException {
    	String query ="UPDATE USERS SET password = ? WHERE username = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,newPassword);
        preparedStatement.setString(2, user.getUsername());
        preparedStatement.executeUpdate();
        conn.commit();
    }
    
    public static int getUsernameMaxLength() {
    	return usernameMaxLength;
    }
    
    public static List<String> getAllUsernames(Statement s) throws SQLException {
    	String query ="SELECT username from users WHERE username != 'None'";
    	Connection conn = s.getConnection();
    	PreparedStatement preparedStatement = conn.prepareStatement(query);
    	ResultSet rs = preparedStatement.executeQuery();
    	List<String> usernames = new ArrayList<>();
    	
    	while(rs.next()) {
    		usernames.add(rs.getString("username"));
    	}
    	
    	return usernames;
    	
    	
    }
    
    private static void insertUser(Statement s, String username, String password) throws SQLException {
    	String query ="INSERT INTO USERS(username, password) VALUES (?,?)";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,username);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
        conn.commit();
    	
    }
  
    
}
