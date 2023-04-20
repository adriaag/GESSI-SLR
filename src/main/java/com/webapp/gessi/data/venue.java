package com.webapp.gessi.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class venue {

    public static void createTable(Statement s) {
        try {
            s.execute("create table venues(idVen INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    " name varchar(300) UNIQUE, acronym varchar(20), PRIMARY KEY (idVen) ) ");
            System.out.println("Created table venues");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table venues exists");
            else System.out.println("Error en la creación de table venues");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table venues");
        System.out.println("Dropped table venues");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla venues not exist");
        }
    }

    public static int insertRow(Statement s, String nameVenue) throws SQLException {
    	
        String query = "INSERT INTO venues(name) VALUES (\'" + nameVenue + "\')";
        //System.out.println(query);
        s.execute(query);
        System.out.println("Inserted row with idVenue, name, acr in venues");
        s.getConnection().commit();
        
        ResultSet rs = s.executeQuery("SELECT idVen FROM venues where name = '" + nameVenue + "'");
        rs.next();
        return rs.getInt(1);
    }
    public static ResultSet getVenue(Statement s, int idVen) throws SQLException {
        return s.executeQuery("SELECT * FROM venues where idVen = " + idVen);
    }
}
