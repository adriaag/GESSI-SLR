package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.criteriaDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class criteria {
    public static boolean createTable(Statement s) {
        try {
            s.execute("create TABLE criteria( idICEC VARCHAR(5) , text VARCHAR(1000), type VARCHAR(9), " +
                    "PRIMARY KEY (idICEC), CONSTRAINT type_chk CHECK (type IN ( 'inclusion', 'exclusion')))");
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

    public static String insert(String idICEC, String text, String type) {
        try{
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s;
            String query = "INSERT INTO criteria(idICEC, text , type ) VALUES ('" + idICEC+ "', \'" + text + "\','"+ type+"')";
            System.out.println(query);
            s = conn.createStatement();
            s.execute(query);
            System.out.println("Inserted row in criteria");
            s.close();
            return idICEC;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return "Identifier "+ idICEC+" already exists";
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
    public static List<criteriaDTO> getAllCriteria(String t) {
        List<criteriaDTO> criList = null;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        try {
            Statement s = conn.createStatement();
            ResultSet rs;
            if (t.equals("IC")) rs = getAllIC(s);
            else if (t.equals("EC")) rs = getAllEC(s);
            else rs = getAll(s);
            int number = 1;
            criList = new ArrayList<criteriaDTO>();
            while(rs.next()) {
                System.out.println(number++);
                System.out.println(rs.getFetchSize());

                String idICEC = rs.getString(1);
                String text = rs.getString(2);
                String type = rs.getString(3);
                System.out.println(idICEC + " " + text + " " + type);

                criteriaDTO NewCri = new criteriaDTO(idICEC,text,type);

                criList.add(NewCri);
            }
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return criList;
    }

    private static ResultSet getAll(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM criteria");
        return rs;
    }

    private static ResultSet getAllIC(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM criteria where type = 'inclusion'");
        return rs;
    }

    private static ResultSet getAllEC(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM criteria where type='exclusion'");
        return rs;
    }

    public static void update(String idICEC, String text, String type, String oldIdICEC) {
        try{
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s;
            String query ="";
            if(!idICEC.isEmpty() && !text.isEmpty() && !type.isEmpty()) {
                 query = "UPDATE criteria SET idICEC= '" + idICEC + "', text= '" + text
                        + "' , TYPE='" + type + "' WHERE idICEC = '" + oldIdICEC + "' ";
            }else if (!text.isEmpty() && !type.isEmpty()) {
                query = "UPDATE criteria SET  text= '" + text + "' , TYPE='" + type + "' WHERE idICEC = '" + oldIdICEC + "' ";
            } else if(!idICEC.isEmpty() &&  !type.isEmpty()) {
                query = "UPDATE criteria SET idICEC= '" + idICEC + "', TYPE='" + type + "' WHERE idICEC = '" + oldIdICEC + "' ";
            } else if(!idICEC.isEmpty() &&  !text.isEmpty()) {
                query = "UPDATE criteria SET idICEC= '" + idICEC + "', text= '" + text + "' WHERE idICEC = '" + oldIdICEC + "' ";
            } else if (!type.isEmpty()) {
                query = "UPDATE criteria SET TYPE='" + type + "' WHERE idICEC = '" + oldIdICEC + "' ";
            } else if(!idICEC.isEmpty()) {
                query = "UPDATE criteria SET idICEC= '" + idICEC + "' WHERE idICEC = '" + oldIdICEC + "' ";
            }else if (!text.isEmpty()) {
                query = "UPDATE criteria SET  text= '" + text + "' WHERE idICEC = '" + oldIdICEC + "' ";
            }
            //System.out.println(query);
            s = conn.createStatement();
            if (!query.isEmpty()) s.execute(query);
            System.out.println("Inserted row in criteria");
            s.close();
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

    public static void delete(String idICEC) {
        try{
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            Connection conn = ctx.getBean(Connection.class);
            Statement s;
            String query = "DELETE FROM criteria WHERE idICEC = '"+ idICEC+"'";
            //System.out.println(query);
            s = conn.createStatement();
            s.execute(query);
            System.out.println("Inserted row in criteria");
            s.close();
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
        psInsert = conn.prepareStatement("insert into criteria values (?, ?, ?)");
        statements.add(psInsert);

        psInsert.setString(1, "EC1");
        psInsert.setString(2, "Duplicated publication.");
        psInsert.setString(3, "exclusion");
        psInsert.executeUpdate();
    }
}
