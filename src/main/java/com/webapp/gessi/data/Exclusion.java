package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Exclusion {
    public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE exclusion(" +
                    "idICEC VARCHAR(5), idRef INT , " +
                    "CONSTRAINT criteria_FK FOREIGN KEY (idICEC) REFERENCES criteria(idICEC) ON UPDATE RESTRICT ON DELETE CASCADE, " +
                    "CONSTRAINT referencies_FK FOREIGN KEY (idRef) REFERENCES referencias(idRef) ON DELETE CASCADE, " +
                    "PRIMARY KEY(idICEC, idRef))");
            System.out.println("Created table Exclusion");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table Exclusion exists");
            else System.out.println("Error en create table Exclusion");
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
            s.execute("DROP TABLE exclusion");
            System.out.println("Dropped table exclusion");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla exclusion not exist");
        }
    }

    public static void insertRow(Statement s, String idICEC, int idRef) {
        try {
            String query;
            ResultSet criteriaExist = s.executeQuery("SELECT * FROM criteria where idICEC = '" + idICEC + "'");
            boolean criteria = criteriaExist.next();
            ResultSet referenceExist = s.executeQuery("SELECT * FROM referencias where idRef = " + idRef);
            boolean reference = referenceExist.next();
            if (criteria && reference) {
                query = "INSERT INTO exclusion(idICEC, idRef) VALUES ('" + idICEC + "', " + idRef + ")";
                s.execute(query);
                System.out.println("Inserted row " + idICEC + ", " + idRef + " in exclusion");
            }
            else {
                if (!criteria)
                    System.out.println("idICEC doesn't exist " + idICEC);
                if (!reference)
                    System.out.println("idRef doesn't exist " + idRef);
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
    }

    public static void deleteRow(Statement s, String idICEC, int idRef) {
        try {
            s.execute("DELETE FROM exclusion where idICEC = '" + idICEC + "' AND idRef = " + idRef);
            System.out.println("Deleted row " + idICEC + ", " + idRef + " in exclusion");
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

    public static List<ExclusionDTO> getByIdRef(Statement s, int idRef) {
        try {
            ResultSet resultSet = s.executeQuery("SELECT * FROM exclusion WHERE idRef = " + idRef);
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

    public static List<ExclusionDTO> getByIdICEC(Statement s, String idICEC) {
        try {
            ResultSet resultSet = s.executeQuery("SELECT * FROM exclusion WHERE idICEC = '" + idICEC + "'");
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
