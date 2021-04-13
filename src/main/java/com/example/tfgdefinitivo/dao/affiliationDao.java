package com.example.tfgdefinitivo.dao;

import java.sql.SQLException;
import java.sql.Statement;

public interface affiliationDao {
    /* create table affiliations(name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("create table affiliations(idCom int, idA varchar(50), PRIMARY KEY (idCom,idA), " +
                    "CONSTRAINT COM_FK_AF FOREIGN KEY (idCom) REFERENCES companies( idCom )," +
                    "CONSTRAINT ART_FK_AF FOREIGN KEY (idA) REFERENCES articles( doi ))");
            System.out.println("Created table affiliations");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table affiliations exists");
            else System.out.println("Error en la creación de table affiliations");
        }
    }
    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table affiliations");
        System.out.println("Dropped table affiliations");
    }
    public static void insertRow(Statement s, int idCom, String idA) throws SQLException {
        String queryRow = "INSERT INTO affiliations(idCom, idA) VALUES (";
        String query;
        query = queryRow + idCom + ", '" + idA + "')";
        try {
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with idCom and idA in affiliations");
        }
        catch(SQLException e) {
            if (e.getSQLState().equals("23505"))
                System.out.println("Affiliation exists");
            else System.out.println("Error en insertRow Affiliation");
        }
    }
}
