package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.CriteriaDTO;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Exclusion {
    public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE exclusion(" +
                    "idICEC INT, " +
                    "idRef INT, " +
                    "CONSTRAINT criteria_FK FOREIGN KEY (idICEC) REFERENCES criteria(id) ON UPDATE RESTRICT ON DELETE CASCADE, " +
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

    public static void insertRow(Statement s, int idICEC, int idRef) {
        String query;
        try {
            Connection conn = s.getConnection();
            CriteriaDTO criteriaDTO = Criteria.getById(conn, idICEC);
            // TODO: IF YOU WANT TO UPDATE A REFERENCE THAT ALREADY EXIST WITH THE STATE OUT IT APPEARS THE ERROR "A lock could not be obtained within the time requested"
            referenceDTO referenceDTO = Reference.getReference(conn, idRef);
            if (criteriaDTO != null && referenceDTO != null) {
                query = "INSERT INTO exclusion(idICEC, idRef) VALUES (?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setInt(1, idICEC);
                preparedStatement.setInt(2, idRef);
                preparedStatement.execute();
                System.out.println("Inserted row " + idICEC + ", " + idRef + " in exclusion");
            }
            else {
                if (criteriaDTO == null)
                    System.out.println("idICEC doesn't exist " + idICEC);
                if (referenceDTO == null)
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

    public static void deleteRow(Statement s, int idICEC, int idRef) {
        String query = "DELETE FROM exclusion where idICEC = ? AND idRef = ?";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idICEC);
            preparedStatement.setInt(2, idRef);
            preparedStatement.execute();
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
        String query = "SELECT * FROM exclusion WHERE IDREF = ?";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idRef);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExclusionDTO> exclusionDTOList = convertResultSetToExclusionDTO(resultSet, null);
            exclusionDTOList.forEach(value -> {
                CriteriaDTO criteriaDTO = Criteria.getById(conn, value.getIdICEC());
                value.setNameICEC(criteriaDTO.getName());
            });
            return exclusionDTOList;
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

    public static List<ExclusionDTO> getByIdICEC(Statement s, int idICEC) {
        String query = "SELECT * FROM exclusion WHERE IDICEC = ?";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idICEC);
            ResultSet resultSet = preparedStatement.executeQuery();
            CriteriaDTO criteriaDTO = Criteria.getById(conn, idICEC);
            return convertResultSetToExclusionDTO(resultSet, criteriaDTO.getName());
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

    private static List<ExclusionDTO> convertResultSetToExclusionDTO(ResultSet resultSet, String nameICEC) throws SQLException {
        List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
        while (resultSet.next()) {
            exclusionDTOList.add(new ExclusionDTO(resultSet.getInt("idRef"), resultSet.getInt("idICEC"), nameICEC));
        }
        return exclusionDTOList;
    }
}
