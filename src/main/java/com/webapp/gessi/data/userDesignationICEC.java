package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.webapp.gessi.domain.dto.CriteriaDTO;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import com.webapp.gessi.domain.dto.userDesignationDTO;
import com.webapp.gessi.domain.dto.userDesignationICECDTO;

public class userDesignationICEC {
	public static boolean createTable (Statement s) {
        try {
            s.execute("create TABLE UserDesignationsICEC(" +
                    "username VARCHAR("+user.getUsernameMaxLength()+"), " +
                    "idRef INT, " +
                    "idICEC INT, " +
                    "CONSTRAINT ud_FK FOREIGN KEY (username, idRef) REFERENCES userDesignations(username, idRef) ON DELETE CASCADE, " +
                    "CONSTRAINT criteriaUD_FK FOREIGN KEY (idICEC) REFERENCES criteria(id) ON UPDATE RESTRICT ON DELETE CASCADE, "+
                    "PRIMARY KEY(username, idRef, idICEC))");
            System.out.println("Created table UserDesignationICEC");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table UserDesignationICEC exists");
            else System.out.println("Error en create UserDesignationICEC");
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
            s.execute("DROP TABLE UserDesignationsICEC");
            System.out.println("Dropped table UserDesignationICEC");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla UserDesignationICECnot exist");
        }
    }
    
    public static void insertRow(Statement s, String username, int idRef,  int idICEC) throws SQLException {
        String query = "INSERT INTO userDesignationsICEC(username, idRef, idICEC) VALUES (?, ?, ?)";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setInt(2, idRef);
        preparedStatement.setInt(3, idICEC);
        preparedStatement.execute();
        System.out.println("Inserted row in userDesignationICEC");
        
    }
    
    public static void deleteRow(Statement s, String username, int idRef, int idICEC) throws SQLException {
        String query = "DELETE FROM userDesignationsICEC WHERE username = ? and idRef = ? and idICEC = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setInt(2, idRef);
        preparedStatement.setInt(3, idICEC);
        preparedStatement.execute();
        System.out.println("Deleted row in userDesignationICEC");

    }
    
    public static List<Integer> getICECs(Statement s, String username, int idRef) throws SQLException {
        String query = "SELECT * FROM userDesignationsICEC WHERE username = ? and idRef = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setInt(2, idRef);
        ResultSet resultSet = preparedStatement.executeQuery();
        return convertResultSetToICECList(resultSet);
    }


    private static List<Integer> convertResultSetToICECList(ResultSet resultSet) throws SQLException {
        List<Integer> exclusionDTOList = new ArrayList<>();
        while (resultSet.next()) {
            exclusionDTOList.add(resultSet.getInt("idICEC"));
        }
        return exclusionDTOList;
    }

}
