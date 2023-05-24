package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.webapp.gessi.domain.dto.userDesignationDTO;

public class userDesignation {
		public static boolean createTable (Statement s) {
	        try {
	            s.execute("create TABLE UserDesignations(" +
	                    "username VARCHAR("+user.getUsernameMaxLength()+"), " +
	                    "idRef INT, " +
	                    "numDesignation INT NOT NULL, " +
	                    "processed BOOLEAN DEFAULT false, " +
	                    "CONSTRAINT username_FK FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE, " +
	                    "CONSTRAINT crit_FK FOREIGN KEY (idRef) REFERENCES referencias(idRef) ON DELETE CASCADE, " +
	                    "CONSTRAINT numDes_chk CHECK (numDesignation IN ( 1, 2)), "+
	                    "PRIMARY KEY(username, idRef))");
	            System.out.println("Created table UserDesignation");
	            return true;

	        } catch (SQLException t ) {
	            if (t.getSQLState().equals("X0Y32")) System.out.println("Table UserDesignation exists");
	            else System.out.println("Error en create UserDesignation");
	            while (t != null) {
	                System.err.println("\n----- SQLException -----");
	                System.err.println("  SQL State:  " + t.getSQLState());
	                System.err.println("  Error Code: " + t.getErrorCode());
	                System.err.println("  Message:    " + t.getMessage());
	                t = t.getNextException();
	            }
	            return false;
	        }
	    }

	    public static void dropTable(Statement s) throws SQLException {
	        try{
	            s.execute("DROP TABLE UserDesignations");
	            System.out.println("Dropped table UserDesignation");
	        }
	        catch (SQLException sqlException) {
	            System.out.println("Tabla UserDesignation not exist");
	        }
	    }
	    
	    public static void insertRow(Statement s,String username, int idRef, int numDesignation) throws SQLException {
	        String query = "INSERT INTO userDesignations(username, idRef, numDesignation) VALUES (?, ?, ?)";
	        Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1, username);
	        preparedStatement.setInt(2, idRef);
	        preparedStatement.setInt(3, numDesignation);
	        preparedStatement.execute();
	        System.out.println("Inserted row in userDesignation");
	        
	    }
	    
	    public static void deleteRow(Statement s, String username, int idRef) throws SQLException {
	        String query = "DELETE FROM userDesignations WHERE username = ? and idRef = ?";
	        Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1, username);
	        preparedStatement.setInt(2, idRef);
	        preparedStatement.execute();
	        System.out.println("Deleted row in userDesignation");

	    }
	    
	    public static void setUserDesignation(Statement s, int numDesignation, String username, int idRef) throws SQLException {
	    	
	    	String userAct = getUserDesignation(s, numDesignation, idRef);

	    	if (userAct != null) 
	    		deleteRow(s, userAct, idRef);

	    	insertRow(s, username, idRef, numDesignation);	    	
	    }
	    
	    public static String getUserDesignation(Statement s, int numDesignation, int idRef) throws SQLException {
	    	String query = "SELECT username FROM userDesignations WHERE idRef = ? and numDesignation = ?";
	        
	        Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setInt(1, idRef);
	        preparedStatement.setInt(2, numDesignation);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        if(resultSet.next()) return resultSet.getString("username");
	        return null;	
	    	
	    }
	    
	    public static void addCriteria(Statement s, userDesignationDTO designation) throws SQLException {
	    	int idRef = designation.getIdRef();
	    	String username = designation.getUsername();
	    	if (designation.getProcessed())
	    		setProcessed(s, username, idRef);
	    	
	    	for (int idICEC: designation.getCriteriaList()) {
	    		userDesignationICEC.insertRow(s, username, idRef, idICEC);
	    	}
	    	
	    	
	    }
	    
	    public static void setProcessed(Statement s, String username, int idRef) throws SQLException {
	    	String query = "UPDATE userDesignations set processed = true WHERE username = ? and idRef = ?";
	    	Connection conn = s.getConnection();
	    	PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1, username);
	        preparedStatement.setInt(2, idRef);
	        preparedStatement.execute();
	    }
	    
	    public static userDesignationDTO getByIdRefNumDes(Statement s, int idRef, int numDesignation) throws SQLException {
	    	String query = "SELECT * FROM userDesignations WHERE idRef = ? and numDesignation = ?";
	        
	        Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setInt(1, idRef);
	        preparedStatement.setInt(2, numDesignation);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        if(resultSet.next()) {
	        	String username = resultSet.getString("username");
	        	boolean processed = resultSet.getBoolean("processed");
	        	List<Integer> criterias = userDesignationICEC.getICECs(s, username, idRef);
	        	return new userDesignationDTO(username, idRef, numDesignation, processed, criterias);
	        }
	        return null;
	    }	        	 	  
}
