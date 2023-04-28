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
	
	private static final int nameMaxLength = 500;
	private static final int textMaxLength = 1000;
	private static final int typeMaxLength = 9;
	
	
    public static boolean createTable(Statement s) {
        try {
            s.execute("create TABLE criteria(" +
                    "id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "name VARCHAR(500), " +
                    "text VARCHAR(1000), " +
                    "type VARCHAR(9), " +
                    "idProject INT, " +
                    "PRIMARY KEY (id), " +
                    "UNIQUE (name, idProject), " +
                    "CONSTRAINT PROJECT_FK FOREIGN KEY (idProject) REFERENCES project(id) ON DELETE CASCADE, " +
                    "CONSTRAINT type_chk CHECK (type IN ( 'inclusion', 'exclusion')))");
            System.out.println("Created table Criteria");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table Criteria exists");
            else
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

    public static void dropTable(Statement s) {
        try {
            s.execute("drop table criteria");
            System.out.println("Dropped table Criteria");
        }
        catch (SQLException sqlException) {
            System.out.println("Table Criteria not exist");
        }
    }

    public static String insert(String name, String text, String type, int idProject) throws SQLException {
        String query = "INSERT INTO criteria(name, text , type, idProject) VALUES (?, ?, ?, ?)";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(name, nameMaxLength));
        preparedStatement.setString(2, truncate(text, textMaxLength));
        preparedStatement.setString(3, truncate(type, typeMaxLength));
        preparedStatement.setInt(4, idProject);
        preparedStatement.execute();
        conn.commit();
        System.out.println("Inserted row in criteria");
        return null;
    }

    public static CriteriaDTO getById(Connection conn, int id) throws SQLException {
        String query = "SELECT * FROM criteria where id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertResultSetToCriteriaDTO(resultSet).get(0);
    }

    public static CriteriaDTO getCriteria(String name, int idProject) throws SQLException {
        String query = "SELECT * FROM criteria where name = ? and idProject = ?";
        
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setInt(2, idProject);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertResultSetToCriteriaDTO(resultSet).get(0);
    }

    public static List<CriteriaDTO> getAllCriteria(String type, int idProject) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        return type == null ? getAll(conn, idProject) : getAll(conn, type, idProject);
    }

    private static List<CriteriaDTO> getAll(Connection conn, String type, int idProject) throws SQLException {
        String query = "SELECT * FROM criteria WHERE TYPE = ? AND IDPROJECT = ? ORDER BY NAME ASC";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, type);
        preparedStatement.setInt(2, idProject);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertResultSetToCriteriaDTO(resultSet);
    }

    private static List<CriteriaDTO> getAll(Connection conn, int idProject) throws SQLException {
        String query = "SELECT * FROM criteria WHERE IDPROJECT = ? ORDER BY NAME ASC";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertResultSetToCriteriaDTO(resultSet);     
    }

    public static void update(String name, String text, int id) throws SQLException {
        String query = "UPDATE criteria SET name = ?, text = ? WHERE id = ?";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(name, nameMaxLength));
        preparedStatement.setString(2, truncate(text, textMaxLength));
        preparedStatement.setInt(3, id);
        preparedStatement.execute();
        System.out.println("Updated " + id + " row in criteria");
        conn.commit();
    }

    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM criteria WHERE id = ?";
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        conn.commit();
        System.out.println("deleted row in criteria");
    }

    private static List<CriteriaDTO> convertResultSetToCriteriaDTO(ResultSet resultSet) throws SQLException {
        List<CriteriaDTO> criteriaDTOList = new ArrayList<>();
        while (resultSet.next()) {
            criteriaDTOList.add(new CriteriaDTO(resultSet.getInt("id"), resultSet.getString("name"),
                    resultSet.getString("text"), resultSet.getString("type"), resultSet.getInt("idProject")));
        }
        return criteriaDTOList;
    }
    
    private static String truncate(String text, int maxValue) {
    	if (text.length() > maxValue) {
        	text = text.substring(0, maxValue - 1);	
        }
    	return text;
    }
}
