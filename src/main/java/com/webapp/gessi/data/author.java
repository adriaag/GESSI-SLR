package com.webapp.gessi.data;

import java.sql.SQLException;
import java.sql.Statement;

public class author {
    /* create table authors( name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("CREATE TABLE authors( idRes int , idA varchar(50) , PRIMARY KEY (idRes, idA)," +
                    "CONSTRAINT RES_FK_AU FOREIGN KEY (idRes) REFERENCES researchers (idRes) ON DELETE CASCADE," +
                    "CONSTRAINT ART_FK_AU FOREIGN KEY (idA) REFERENCES articles( doi ) ON DELETE CASCADE)");
            System.out.println("Created table authors");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table authors exists");
            else System.out.println("Error en la creación de table authors");
        }
    }
    public static void insertRows(Integer[] ids, String idA, Statement s) {
        String queryRow = "INSERT INTO authors(idRes,idA) VALUES (";
        String query;
        for(int x : ids) {
            query = queryRow + x + ", '" + idA + "')";
            try {
                s.execute(query);
                //System.out.println("Inserted row with idRes and idA in Authors");
            }
            catch (SQLException e) {
                if (e.getSQLState().equals("23505"))
                    System.out.println("Author exists");
                else System.out.println("Error en insertRow Author");
            }
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table authors");
        System.out.println("Dropped table authors");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla authors not exist");
        }
    }

}
