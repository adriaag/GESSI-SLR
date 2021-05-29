package com.example.tfgdefinitivo.data;

import java.sql.*;

public class importationLogError {
    public static boolean createTable(Statement s) {
        try {
            s.execute("create TABLE ImportationLogError(" +
                    "idLogErr INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), time timestamp, " +
                    "doi VARCHAR(100), idDL int, BibTex VARCHAR(10000),  PRIMARY KEY (idLogErr)," +
                    "CONSTRAINT DL_FK_I FOREIGN KEY (idDL) REFERENCES DIGITALLIBRARIES(idDL))");
            System.out.println("Created table importationLogError");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table importationLogError exists");
            else System.out.println("Error en create table importationLogError");
            return false;
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table ImportationLogError");
        System.out.println("Dropped table importationLogError");
    }

    public static boolean ifExistsDOI(Statement s, String key) throws SQLException {
        //ejemplo key = 8984351
        System.out.println("DOI exists in articles");
        ResultSet rs = s.executeQuery("SELECT * FROM referencias r INNER JOIN articles a on r.DOI = a.DOI AND a.CITEKEY ='" + key + "'");
        return rs.next();
    }

    public static void insertRow(Statement s, String doi, String data, String idDL, Timestamp timesql) {
        try {
            PreparedStatement prepStatement = s.getConnection().
                    prepareStatement("INSERT INTO ImportationLogError(time, doi, idDL, BibTex) VALUES (?,?,?,?)");
            prepStatement.setTimestamp(1, timesql);
            prepStatement.setString(2, doi);
            prepStatement.setInt(3, Integer.parseInt(idDL));
            prepStatement.setString(4, data);

            int numberOfRowsInserted = prepStatement.executeUpdate();
            System.out.println("numberOfRowsInserted=" + numberOfRowsInserted);

            prepStatement.close(); // close PreparedStatement
            System.out.println("Inserted row in importation log error");
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
}
