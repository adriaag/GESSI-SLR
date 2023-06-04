package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webapp.gessi.domain.dto.projectDigitalLibraryDTO;
import com.webapp.gessi.domain.dto.projectUserInvolveDTO;

public class projectUserInvolve {
	private static final int involveInfoMaxLength = 5000;
	public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE projectUserInvolve(" +
                    "idProject INT, " +
                    "username VARCHAR("+user.getUsernameMaxLength()+"), " +
                    "involveInfo VARCHAR("+involveInfoMaxLength+"), "+
                    "PRIMARY KEY(idProject, username))");
            System.out.println("Created table projectUserInvolve");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table projectUserInvolve exists");
            else System.out.println("Error en create table projectUserInvolve");
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
            s.execute("DROP TABLE projectUserInvolve");
            System.out.println("Dropped table projectUserInvolve");
        }
        catch (SQLException sqlException) {
        	sqlException.printStackTrace();
        }
    }

    public static void insertRow(Connection conn, int idProject, String username, String involveInfo) throws SQLException {
        String query = "INSERT INTO projectUserInvolve(idProject, username, involveInfo) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, Reference.truncate(involveInfo, involveInfoMaxLength));
        preparedStatement.execute();
        System.out.println("Inserted row " + idProject + ", " + username + " in Project");
    }

    public static void deleteRow(Statement s, int idProject, String username) throws SQLException {
        String query = "DELETE FROM projectUserInvolve where idProject = ? AND username = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        preparedStatement.setString(2, username);
        preparedStatement.execute();
        System.out.println("Deleted row " + idProject + ", " + username + " in project");
    }
    
    public static void updateRow(Connection conn, int idProject, String username, String involveInfo) throws SQLException {
        String query = "UPDATE projectUserInvolve SET involveInfo = ? WHERE idProject = ? AND username = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, Reference.truncate(involveInfo, involveInfoMaxLength));
        preparedStatement.setInt(2, idProject);
        preparedStatement.setString(3, username);
        preparedStatement.execute();
        System.out.println("Updated row " + idProject + ", " + username + " in Project");
    }
    
    public static projectUserInvolveDTO[]  getByIdProject(Statement s, int idProject) throws SQLException {
    	 String query = "SELECT * FROM projectUserInvolve where idProject = ?";
    	 Connection conn = s.getConnection();
         PreparedStatement preparedStatement = conn.prepareStatement(query);
         preparedStatement.setInt(1, idProject);
         return convertResultSetToDTO(preparedStatement.executeQuery());
             	
    }
    
    public static void updateByIdProject(Statement s, projectUserInvolveDTO[] pUIList, int idProject) throws SQLException {
    	projectUserInvolveDTO[] storedPUIList = getByIdProject(s, idProject);
    	Map<String,Integer> dict = new HashMap<String,Integer>();
    	
    	int i = 0;
    	for(projectUserInvolveDTO pUI : storedPUIList) {
    		dict.put(pUI.getUsername(), i);
    		++i;
    	}
    	
    	for(projectUserInvolveDTO pUI : pUIList) {
    		Integer ind = dict.get(pUI.getUsername());
    		if(ind == null) insertRow(s.getConnection(), idProject, pUI.getUsername(), pUI.getInvolveInfo());
    		else {
    			updateRow(s.getConnection(), idProject, pUI.getUsername(), pUI.getInvolveInfo());
    			dict.remove(pUI.getUsername());
    		}
    		
    	}
    	for(String key : dict.keySet()) {
    		deleteRow(s,idProject, key);
    	}
    	
    	System.out.println("ProjectDigitalLibrary of project "+ idProject + " updated");  	
    	
    }
    
    private static projectUserInvolveDTO[] convertResultSetToDTO(ResultSet resultSet) throws SQLException {
    	List<projectUserInvolveDTO>  pUI = new ArrayList<>();
        while (resultSet.next()) {
            pUI.add(new projectUserInvolveDTO(resultSet.getInt("idProject"), resultSet.getString("username"), 
            		resultSet.getString("involveInfo")));
        }
        return pUI.toArray(new projectUserInvolveDTO[pUI.size()]);
    }


}
