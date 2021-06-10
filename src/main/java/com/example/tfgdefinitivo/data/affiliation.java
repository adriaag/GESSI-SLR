package com.example.tfgdefinitivo.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class affiliation {
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
        try{
        s.execute("drop table affiliations");
        System.out.println("Dropped table affiliations");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla affiliations not exist");
        }
    }


    public static Integer insertRow(Statement s, int idCom, String idA) throws SQLException {
        try {
            String queryRow = "INSERT INTO affiliations(idCom, idA) VALUES (";
            String query;
            query = queryRow + idCom + ", '" + idA + "')";
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with idCom and idA in affiliations");
            s.getConnection().commit();
        }
        catch(SQLException e) {
            if (e.getSQLState().equals("23505"))
                System.out.println("Affiliation exists");
            else System.out.println("Error en insertRow Affiliation");
        }
        ResultSet rs = s.executeQuery("SELECT idCom FROM affiliations where idCom = " + idCom + " and idA='" + idA+ "'");
        rs.next();
        return rs.getInt(1);
    }

    public static void insertRows(Statement s, Integer[] idCom, String doi) throws SQLException {
        String queryRow = "INSERT INTO affiliations(idCom,idA) VALUES (";
        String query;
        for(int x : idCom) {
            query = queryRow + x + ", '" + doi + "')";
            try {
                s.execute(query);
                System.out.println("Inserted row with idCom and idA in Affiliation");
            }
            catch (SQLException e) {
                if (e.getSQLState().equals("23505"))
                    System.out.println("Affiliation exists");
                else System.out.println("Error en insertRow Affiliation");
            }
        }
    }
}
