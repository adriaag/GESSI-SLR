package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.Exclusion;
import com.webapp.gessi.data.Project;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import com.webapp.gessi.domain.dto.ProjectDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ProjectController {

    public static void insertRows(List<ProjectDTO> projectDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        projectDTOList.forEach(value -> Project.insertRow(s, value.getName()));
        conn.commit();
    }

    public static void deleteRows(List<ProjectDTO> projectDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        projectDTOList.forEach(value -> Project.deleteRow(s, value.getId()));
        conn.commit();
    }

    public static List<ProjectDTO> getAll() {
        return Project.getAll();
    }

    public static ProjectDTO getByIdRef(int idRef) {
        return Project.getById(idRef);
    }

    public static ProjectDTO getByName(String name) {
        return Project.getByName(name);
    }

    public static void updateName(int id, String name) {
        Project.updateName(id, name);
    }
}
