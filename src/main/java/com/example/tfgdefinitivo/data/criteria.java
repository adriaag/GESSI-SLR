package com.example.tfgdefinitivo.data;

import com.example.tfgdefinitivo.config.DBConnection;
import com.example.tfgdefinitivo.domain.dto.criteriaDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                System.out.println("ICEC exists");
            }
            else {
                while (e != null) {
                    System.err.println("\n----- SQLException -----");
                    System.err.println("  SQL State:  " + e.getSQLState());
                    System.err.println("  Error Code: " + e.getErrorCode());
                    System.err.println("  Message:    " + e.getMessage());
                    e = e.getNextException();
                }
            }
        }
        return null;
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
            criList = new ArrayList<>();
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
}
