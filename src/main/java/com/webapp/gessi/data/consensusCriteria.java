package com.webapp.gessi.data;

import com.webapp.gessi.domain.dto.CriteriaDTO;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import com.webapp.gessi.domain.dto.referenceDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class consensusCriteria {
    public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE consensusCriteria(" +
                    "idICEC INT, " +
                    "idRef INT, " +
                    "CONSTRAINT criteriaCC_FK FOREIGN KEY (idICEC) REFERENCES criteria(id) ON UPDATE RESTRICT ON DELETE CASCADE, " +
                    "CONSTRAINT referencies_FK FOREIGN KEY (idRef) REFERENCES referencias(idRef) ON DELETE CASCADE, " +
                    "PRIMARY KEY(idICEC, idRef))");
            System.out.println("Created table consensusCriteria");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table consensusCriteria exists");
            else System.out.println("Error en create consensusCriteria");
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
            s.execute("DROP TABLE consensusCriteria");
            System.out.println("Dropped table consensusCriteria");
        }
        catch (SQLException sqlException) {
        	sqlException.printStackTrace();
            
        }
    }

    public static void insertRow(Statement s, int idICEC, int idRef) throws SQLException {
        String query;
        Connection conn = s.getConnection();
        CriteriaDTO criteriaDTO = Criteria.getById(conn, idICEC);
        referenceDTO referenceDTO = Reference.getReference(conn, idRef);
        if (criteriaDTO != null && referenceDTO != null) {
            query = "INSERT INTO consensusCriteria(idICEC, idRef) VALUES (?, ?)";
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

    }

    public static void deleteRow(Statement s, int idICEC, int idRef) throws SQLException {
        String query = "DELETE FROM consensusCriteria where idICEC = ? AND idRef = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idICEC);
        preparedStatement.setInt(2, idRef);
        preparedStatement.execute();
        System.out.println("Deleted row " + idICEC + ", " + idRef + " in exclusion");
    }

    public static consensusCriteriaDTO getByIdRef(Statement s, int idRef) throws SQLException {
        String query = "SELECT * FROM consensusCriteria WHERE IDREF = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idRef);
        ResultSet resultSet = preparedStatement.executeQuery();
        consensusCriteriaDTO exclusionDTOList = convertResultSetToConsensusCriteriaDTO(resultSet, idRef);
        return exclusionDTOList;
    }

    private static consensusCriteriaDTO convertResultSetToConsensusCriteriaDTO(ResultSet resultSet, int idRef) throws SQLException {
        List<Integer> idICEC = new ArrayList<>();
        while (resultSet.next()) {
            idICEC.add(resultSet.getInt("idICEC"));
        }
        return new consensusCriteriaDTO(idRef,idICEC);
    }
}
