package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import com.webapp.gessi.domain.dto.ProjectDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Project {
    public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE project(" +
                    "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "name VARCHAR(10000) NOT NULL UNIQUE, " +
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
            System.out.println("Tabla Project not exist");
        }
    }

    public static void insertRow(Statement s, String name) {
        String query = "INSERT INTO project(name) VALUES ( ? )";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            System.out.println("Inserted row " + name + " in Project");
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static void deleteRow(Statement s, int id) {
        String query = "DELETE FROM project where id = ?";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            System.out.println("Deleted row " + id + " in project");
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static List<ProjectDTO> getAll() {
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s = conn.createStatement();
            ResultSet resultSet = s.executeQuery("SELECT * FROM project");
            conn.commit();
            return convertResultSetToProjectDTO(resultSet);
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return null;
    }

    public static ProjectDTO getById (int id) {
        String query = "SELECT * FROM project WHERE id = ?";
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return convertResultSetToProjectDTO(resultSet).get(0);
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return null;
    }

    public static ProjectDTO getByName(String name) {
        String query = "SELECT * FROM project WHERE name = ?";
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            return convertResultSetToProjectDTO(resultSet).get(0);
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return null;
    }

    public static void updateName(int id, String name) {
        String query = "UPDATE project SET name = ? WHERE id = ?";
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            conn.commit();
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    private static List<ProjectDTO> convertResultSetToProjectDTO(ResultSet resultSet) throws SQLException {
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        while (resultSet.next()) {
            projectDTOList.add(new ProjectDTO(resultSet.getInt("id"), resultSet.getString("name")));
        }
        return projectDTOList;
    }
}
