package com.example.tfgdefinitivo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface company {
    public static void createTable(Statement s) {
        try {
            s.execute("create table companies(idCom INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    " name varchar(200) UNIQUE, PRIMARY KEY (idCom) ) ");
            System.out.println("Created table companies");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table companies exists");
            else System.out.println("Error en la creación de table companies");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table companies");
        System.out.println("Dropped table companies");
    }


    public static int insertRow(Statement s, String name) throws SQLException {
        try {

            String query = "INSERT INTO companies(name) VALUES (\'" + name + "\')";
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with idCom, name in companies");
            s.getConnection().commit();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505"))
                System.out.println("Company exists");
            else {
                while (e != null) {
                    System.err.println("\n----- SQLException -----");
                    System.err.println("  SQL State:  " + e.getSQLState());
                    System.err.println("  Error Code: " + e.getErrorCode());
                    System.err.println("  Message:    " + e.getMessage());
                    // for stack traces, refer to derby.log or uncomment this:
                    //e.printStackTrace(System.err);
                    e = e.getNextException();
                }
            }
        }

        ResultSet rs = s.executeQuery("SELECT idCom FROM companies where name = '" + name + "'");
        rs.next();
        return rs.getInt(1);
    }
}
