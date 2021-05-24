package com.example.tfgdefinitivo.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportationLogError {
    public static boolean createTable(Statement s) {
        try {
            s.execute("create TABLE ImportationLogError(" +
                    "idLogErr int, time timestamp, doi VARCHAR(100)," +
                    "BibTex VARCHAR(10000), idDL int, PRIMARY KEY (idLogErr)," +
                    "CONSTRAINT DL_FK_I FOREIGN KEY (idDL) REFERENCES DIGITALLIBRARIES(idDL))");
            System.out.println("Created table ImportationLogError");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table ImportationLogError exists");
            else System.out.println("Error en create table ImportationLogError");
            return false;
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table ImportationLogError");
        System.out.println("Dropped table ImportationLogError");
    }

    public static boolean ifExistsDOI(Statement s, String key) throws SQLException {
        //ejemplo key = 8984351
        System.out.println("DOI exists in articles");
        ResultSet rs = s.executeQuery("SELECT * FROM referencias r INNER JOIN articles a on r.DOI = a.DOI AND a.CITEKEY ='" + key + "'");
        return rs.next();
    }

    public static void insertRow(Statement sta, String bibText) {

    }
}
