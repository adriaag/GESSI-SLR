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

import org.assertj.core.util.Arrays;

import com.webapp.gessi.domain.dto.ProjectDTO;
import com.webapp.gessi.domain.dto.projectDigitalLibraryDTO;
import com.webapp.gessi.domain.dto.projectUserInvolveDTO;

public class projectDigitalLibrary {
	private static final int searchStringMaxLength = 5000;
	public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE projectDigitalLibrary(" +
                    "idProject INT, " +
                    "idDL INT, " +
                    "searchString VARCHAR("+searchStringMaxLength+"), "+
                    "numSearchResults INT, "+
                    "PRIMARY KEY(idProject, idDL),"+
                    "CONSTRAINT DL_FK_PDL FOREIGN KEY (idDL) REFERENCES digitalLibraries (idDL) ON DELETE CASCADE)");
            System.out.println("Created table projectDigitalLibrary");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table projectDigitalLibrary exists");
            else System.out.println("Error en create table projectDigitalLibrary");
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
            s.execute("DROP TABLE projectDigitalLibrary");
            System.out.println("Dropped table projectDigitalLibrary");
        }
        catch (SQLException sqlException) {
        	sqlException.printStackTrace();
        }
    }

    public static void insertRow(Connection conn, int idProject, int idDL, String searchString, int numSearchResults) throws SQLException {
        String query = "INSERT INTO projectDigitalLibrary(idProject, idDL, searchString, numSearchResults) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        preparedStatement.setInt(2, idDL);
        if (searchString != null)
        	preparedStatement.setString(3, Reference.truncate(searchString, searchStringMaxLength));
        else preparedStatement.setNull(3, java.sql.Types.VARCHAR);
        preparedStatement.setInt(4, numSearchResults);
        preparedStatement.execute();
        conn.commit();
        System.out.println("Inserted row " + idProject + ", " + idDL + " in Project");
    }

    public static void deleteRow(Statement s, int idProject, int idDL) throws SQLException {
        String query = "DELETE FROM projectDigitalLibrary where idProject = ? AND idDL = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        preparedStatement.setInt(2, idDL);
        preparedStatement.execute();
        conn.commit();
        System.out.println("Deleted row " + idProject + ", " + idDL + " in project");
    }
    
    public static void updateRow(Connection conn, int idProject, int idDL, String searchString, int numSearchResults) throws SQLException {
        String query = "UPDATE projectDigitalLibrary SET searchString = ?, numSearchResults = ? WHERE idProject = ? AND idDL = ?";
        
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        if (searchString != null)
        	preparedStatement.setString(1, Reference.truncate(searchString, searchStringMaxLength));
        else preparedStatement.setNull(1, java.sql.Types.VARCHAR);
        preparedStatement.setInt(2, numSearchResults);
        preparedStatement.setInt(3, idProject);
        preparedStatement.setInt(4, idDL);
        preparedStatement.executeUpdate();
        conn.commit();
        System.out.println("Updated row " + idProject + ", " + idDL + " in Project");
    }
    
    public static projectDigitalLibraryDTO[] getByIdProject(Statement s, int idProject) throws SQLException {
    	 String query = "SELECT * FROM projectDigitalLibrary where idProject = ?";
    	 Connection conn = s.getConnection();
         PreparedStatement preparedStatement = conn.prepareStatement(query);
         preparedStatement.setInt(1, idProject);
         return convertResultSetToDTO(preparedStatement.executeQuery());
             	
    }
    
    public static void updateByIdProject(Statement s, projectDigitalLibraryDTO[] pDLList, int idProject) throws SQLException {
    	projectDigitalLibraryDTO[] storedPDLList = getByIdProject(s, idProject);
    	Map<Integer, Integer> dict = new HashMap<Integer,Integer>();
    	
    	int i = 0;
    	for(projectDigitalLibraryDTO pDL : storedPDLList) {
    		dict.put(pDL.getIdDL(), i);
    		++i;
    	}
    	
    	for(projectDigitalLibraryDTO pDL : pDLList) {
    		Integer ind = dict.get(pDL.getIdDL());
    		if(ind == null) insertRow(s.getConnection(), idProject, pDL.getIdDL(), pDL.getSearchString(), pDL.getNumSearchResults());
    		else {
    			updateRow(s.getConnection(), idProject, pDL.getIdDL(), pDL.getSearchString(), pDL.getNumSearchResults());
    			dict.remove(pDL.getIdDL());
    		}
    		
    	}
    	for(Integer key : dict.keySet()) {
    		deleteRow(s,idProject, key);
    	}
    	
    	System.out.println("ProjectDigitalLibrary of project "+ idProject + " updated");
    	
    	
    	
    }
    
    private static projectDigitalLibraryDTO[] convertResultSetToDTO(ResultSet resultSet) throws SQLException {
    	List<projectDigitalLibraryDTO> pDL = new ArrayList<>();
        while (resultSet.next()) {
            pDL.add(new projectDigitalLibraryDTO(resultSet.getInt("idProject"), resultSet.getInt("idDL"), 
            		resultSet.getString("searchString"), resultSet.getInt("numSearchResults")));
        }
        return pDL.toArray(new projectDigitalLibraryDTO[0]);
    }
    
    


}
