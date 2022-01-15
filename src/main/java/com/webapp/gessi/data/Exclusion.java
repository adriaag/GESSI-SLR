package com.webapp.gessi.data;

import com.webapp.gessi.domain.dto.referenceDTO;

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
                    "FOREIGN KEY (idICEC) REFERENCES criteria(idICEC) ON UPDATE RESTRICT ON DELETE CASCADE, " +
                    "FOREIGN KEY (idRef) REFERENCES referencias(idRef) ON DELETE CASCADE, " +
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
            s.execute("drop table exclusion");
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
                s.getConnection().commit();
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

    public static List<String> getByIdRef(Statement s, int idRef) {
        try {
            ResultSet resultSet;
            resultSet = s.executeQuery("SELECT idICEC FROM exclusion WHERE idRef = " + idRef);
            List<String> applCriteria = new ArrayList<>();
            while (resultSet.next()) {
                applCriteria.add(resultSet.getString(1));
            }
            return applCriteria;
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
}
