package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.CriteriaDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Criteria {
    public static boolean createTable(Statement s) {
        try {
            s.execute("create TABLE criteria(" +
                    "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "name VARCHAR(500), " +
                    "text VARCHAR(1000), " +
                    "type VARCHAR(9), " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE (name), " +
                    "CONSTRAINT type_chk CHECK (type IN ( 'inclusion', 'exclusion')))");
            System.out.println("Created table Criteria");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table Criteria exists");
            else System.out.println("Error en create table Criteria");
            return false;
        }
    }

    public static void dropTable(Statement s) {
        try {
        s.execute("drop table criteria");
        System.out.println("Dropped table Criteria");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla Criteria not exist");
        }
    }

    public static String insert(String name, String text, String type) {
        String query = "INSERT INTO criteria(name, text , type) VALUES (?, ?, ?)";
        try{
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, text);
            preparedStatement.setString(3, type);
            preparedStatement.execute();
            conn.commit();
            System.out.println("Inserted row in criteria");
            return null;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return "Identifier "+ name + " already exists";
            }
            else {
                while (e != null) {
                    System.err.println("\n----- SQLException -----");
                    System.err.println("  SQL State:  " + e.getSQLState());
                    System.err.println("  Error Code: " + e.getErrorCode());
                    System.err.println("  Message:    " + e.getMessage());
                    e = e.getNextException();
                }
                return "Error in inset criteria";
            }
        }
    }

    public static CriteriaDTO getById(Connection conn, int id) {
        String query = "SELECT * FROM criteria where id = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return convertResultSetToCriteriaDTO(resultSet).get(0);
        }
        catch (SQLException e) {
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

    public static List<CriteriaDTO> getAllCriteria(String type) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        return type == null ? getAll(conn) : getAll(conn, type);
    }

    private static List<CriteriaDTO> getAll(Connection conn, String type) {
        String query = "SELECT * FROM criteria where type = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, type);
            ResultSet resultSet = preparedStatement.executeQuery();
            return convertResultSetToCriteriaDTO(resultSet);
        }
        catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return new ArrayList<>();
    }

    private static List<CriteriaDTO> getAll(Connection conn) {
        try {
            Statement s = conn.createStatement();
            ResultSet resultSet = s.executeQuery("SELECT * FROM criteria");
            return convertResultSetToCriteriaDTO(resultSet);
        }
        catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return new ArrayList<>();
    }

    public static void update(String name, String text, int id) {
        String query = "UPDATE criteria SET name = ?, text = ? WHERE id = ?";
        try{
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, text);
            preparedStatement.setInt(3, id);
            preparedStatement.execute();
            System.out.println("Updated " + id + " row in criteria");
            conn.commit();
        }catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static void delete(int id) {
        String query = "DELETE FROM criteria WHERE id = ?";
        try{
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            conn.commit();
            System.out.println("deleted row in criteria");
        }catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static void insertRowIni(Connection conn, ArrayList<Statement> statements) throws SQLException {
        PreparedStatement psInsert;
        psInsert = conn.prepareStatement("insert into criteria(name , text , type) values (?, ?, ?)");
        statements.add(psInsert);

        psInsert.setString(1, "EC1");
        psInsert.setString(2, "Duplicated publication.");
        psInsert.setString(3, "exclusion");
        psInsert.executeUpdate();
    }

    private static List<CriteriaDTO> convertResultSetToCriteriaDTO(ResultSet resultSet) throws SQLException {
        List<CriteriaDTO> criteriaDTOList = new ArrayList<>();
        while (resultSet.next()) {
            criteriaDTOList.add(new CriteriaDTO(resultSet.getInt("id"), resultSet.getString("name"),
                    resultSet.getString("text"), resultSet.getString("type")));
        }
        return criteriaDTOList;
    }
}
