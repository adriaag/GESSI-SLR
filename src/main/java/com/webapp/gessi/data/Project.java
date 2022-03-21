package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Project {
    public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE project(" +
                    "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name VARCHAR(10000), " +
                    "PRIMARY KEY(id, name))");
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
        try {
            String query = "INSERT INTO project(id, name) VALUES ('" + name + "')";
            s.execute(query);
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

    public static void deleteRow(Statement s, String name) {
        try {
            s.execute("DELETE FROM project where name = '" + name + "'");
            System.out.println("Deleted row " + name + " in project");
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

    public static List<ExclusionDTO> getById(Statement s, int id) {
        try {
            ResultSet resultSet = s.executeQuery("SELECT * FROM project WHERE id = " + id);
            return convertResultSetToExclusionDTO(resultSet);
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

    public static List<ExclusionDTO> getByName(Statement s, String name) {
        try {
            ResultSet resultSet = s.executeQuery("SELECT * FROM project WHERE name = '" + name + "'");
            return convertResultSetToExclusionDTO(resultSet);
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

    public static void deleteCriteriaFK() {
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s;
            s = conn.createStatement();
            s.execute("ALTER TABLE exclusion DROP CONSTRAINT CRITERIA_FK");
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

    public static void addCriteriaFK() {
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s;
            s = conn.createStatement();
            s.execute("ALTER TABLE exclusion ADD CONSTRAINT criteria_FK FOREIGN KEY (idICEC) REFERENCES criteria(idICEC) ON UPDATE RESTRICT ON DELETE CASCADE");
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

    public static void updateIdICEC(String oldIdICEC, String newIdICEC) {
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s;
            s = conn.createStatement();
            s.execute("UPDATE exclusion SET idICEC = '" + newIdICEC + "' WHERE idICEC = '" + oldIdICEC + "'");
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

    private static List<ExclusionDTO> convertResultSetToExclusionDTO(ResultSet resultSet) throws SQLException {
        List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
        while (resultSet.next()) {
            exclusionDTOList.add(new ExclusionDTO(resultSet.getInt(2), resultSet.getString(1)));
        }
        return exclusionDTOList;
    }
}
