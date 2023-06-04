package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.controllers.criteriaController;
import com.webapp.gessi.domain.dto.ProjectDTO;
import com.webapp.gessi.domain.dto.projectDigitalLibraryDTO;
import com.webapp.gessi.domain.dto.projectUserInvolveDTO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Project {
	
	private static final int nameMaxLength = 10000;
	
    public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE project(" +
                    "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "name VARCHAR(1000) NOT NULL UNIQUE, " +
                    "idDuplicateCriteria INT, " +
                    "topic VARCHAR(500), " +
                    "researchQuestion VARCHAR(5000), "+
                    "protocol VARCHAR(5000), "+
                    "protocolImg BLOB(1M), "+
                    "comments VARCHAR(5000), "+
                    "PRIMARY KEY(id))");
            System.out.println("Created table Project");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table Project exists");
            else System.out.println("Error en create table Project");
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
            s.execute("DROP TABLE project");
            System.out.println("Dropped table Project");
        }
        catch (SQLException sqlException) {
        	sqlException.printStackTrace();
        }
    }

    public static void insertRow(Connection conn, String name) throws SQLException {
        String query = "INSERT INTO project(name) VALUES (?)";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(name, nameMaxLength));
        preparedStatement.execute();
        System.out.println("Inserted row " + name + " in Project");
        ProjectDTO projectDTO = getByName(name);
        int idDuplicateCriteria = criteriaController.insertDuplicateCriteria(projectDTO.getId());
        updateIdDuplicateCriteria(projectDTO.getId(), idDuplicateCriteria);
    }

    public static void deleteRow(Statement s, int id) throws SQLException {
        String query = "DELETE FROM project where id = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        System.out.println("Deleted row " + id + " in project");
    }

    public static List<ProjectDTO> getAll() throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        ResultSet resultSet = s.executeQuery("SELECT * FROM project");
        conn.commit();
        return convertResultSetToProjectDTO(s, resultSet);
    }

    public static ProjectDTO getById (int id) throws SQLException {
        String query = "SELECT * FROM project WHERE id = ?";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertResultSetToProjectDTO(s, resultSet).get(0);
    }

    public static ProjectDTO getByName(String name) throws SQLException {
        String query = "SELECT * FROM project WHERE name = ?";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<ProjectDTO> projectDTOList = convertResultSetToProjectDTO(s, resultSet);
        return projectDTOList.size() > 0 ? projectDTOList.get(0) : null;
    }

    public static void updateName(int id, String name) throws SQLException {
        String query = "UPDATE project SET name = ? WHERE id = ?";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(name, nameMaxLength));
        preparedStatement.setInt(2, id);
        preparedStatement.execute();
        conn.commit();
    }

    public static void updateIdDuplicateCriteria(int id, int idICEC) throws SQLException {
        String query = "UPDATE project SET idDuplicateCriteria = ? WHERE id = ?";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idICEC);
        preparedStatement.setInt(2, id);
        preparedStatement.execute();
        conn.commit();
    }
    
    public static void updateProject(ProjectDTO project) throws SQLException {
    	String query = "UPDATE project SET name = ?, topic = ?, researchQuestion = ?, protocol = ?, protocolImg = ?, comments = ? WHERE id = ?";
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, project.getName());
        preparedStatement.setString(2, project.getTopic());
        preparedStatement.setString(3, project.getResearchQuestion());
        preparedStatement.setString(4, project.getProtocol());
        if (project.getProtocolImg() != null) {
            Blob imgBlob = conn.createBlob();
            imgBlob.setBytes(1, project.getProtocolImg());
            preparedStatement.setBlob(5, imgBlob);
        }
        else preparedStatement.setNull(5, java.sql.Types.BLOB);

        preparedStatement.setString(6, project.getComments());
        preparedStatement.setInt(7, project.getId());
        preparedStatement.execute();
        conn.commit();
        
        projectDigitalLibrary.updateByIdProject(s, project.getDigitalLibraries(), project.getId());
        projectUserInvolve.updateByIdProject(s, project.getInvolvedUsers(), project.getId());
    	
    }

    private static List<ProjectDTO> convertResultSetToProjectDTO(Statement s, ResultSet resultSet) throws SQLException {
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        while (resultSet.next()) {
        	byte[] img = resultSet.getBytes("protocolImg");
        	projectUserInvolveDTO[] pUIList = projectUserInvolve.getByIdProject(s, resultSet.getInt("id"));
        	projectDigitalLibraryDTO[] pDLList = projectDigitalLibrary.getByIdProject(s, resultSet.getInt("id"));
            projectDTOList.add(new ProjectDTO(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("idDuplicateCriteria"),
            		resultSet.getString("topic"), resultSet.getString("researchQuestion"), resultSet.getString("protocol"), 
            		img, resultSet.getString("comments"),pUIList,pDLList));
        }
        
        return projectDTOList;
    }
    
    private static String truncate(String text, int maxValue) {
    	if (text.length() > maxValue) {
        	text = text.substring(0, maxValue - 1);	
        }
    	return text;
    }
}
