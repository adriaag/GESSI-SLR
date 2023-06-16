package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.Project;
import com.webapp.gessi.domain.dto.ProjectDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ProjectController {

    public static void insertRows(List<ProjectDTO> projectDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        for(ProjectDTO project: projectDTOList) {
        	Project.insertRow(conn, project.getName());
        }
        conn.commit();
    }

    public static void deleteRows(List<ProjectDTO> projectDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        for(ProjectDTO project: projectDTOList) {
        	Project.deleteRow(s, project.getId());
        }
        conn.commit();
    }

    public static List<ProjectDTO> getAll() throws SQLException {
        return Project.getAll();
    }

    public static ProjectDTO getById(int id) throws SQLException {
        return Project.getById(id);
    }

    public static ProjectDTO getByName(String name) throws SQLException {
        return Project.getByName(name);
    }

    public static void updateName(int id, String name) throws SQLException {
        Project.updateName(id, name);
    }

    public static void updateName(int id, int idICEC) throws SQLException {
        Project.updateIdDuplicateCriteria(id, idICEC);
    }
    
    public static void updateProject(ProjectDTO projects) throws SQLException, IOException {
    	Project.updateProject(projects);
    }

	public static void updateProtocolImg(int idProj, MultipartFile img) throws SQLException, IOException {
		Project.updateProtocolImg(idProj, img.getBytes());
		
	}

	public static byte[] getProtocolImg(int idProj) throws SQLException {
		return Project.getProtocolImg(idProj);
	}
	
	public static void updateOrder(int idProj, String orderColSearch, String orderDirSearch, 
    		String orderColScreen, String orderDirScreen) throws SQLException {
		Project.updateOrder(idProj, orderColSearch, orderDirSearch, orderColScreen, orderDirScreen);
	}
}
