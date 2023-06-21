package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class affiliation {
	
	private static final int idAMaxLength = 100;
    /* create table affiliations(name varchar(50),
    PRIMARY KEY (name));*/
    public static void createTable(Statement s) {
        try {
            s.execute("create table affiliations(idCom int, idA varchar(100), PRIMARY KEY (idCom,idA), " +
                    "CONSTRAINT COM_FK_AF FOREIGN KEY (idCom) REFERENCES companies( idCom ) ON DELETE CASCADE," +
                    "CONSTRAINT ART_FK_AF FOREIGN KEY (idA) REFERENCES articles( doi ) ON DELETE CASCADE)");
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
        int id = getByPK(s,idCom,idA);
    	
        if (id == -1) {
	    	String query = "INSERT INTO affiliations(idCom, idA) VALUES (?, ?)";
	    	Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setInt(1, idCom);
	        preparedStatement.setString(2, truncate(idA, idAMaxLength));
	        preparedStatement.execute();
	        System.out.println("Inserted row with idCom and idA in affiliations");
	        s.getConnection().commit();
	      
	        query = "SELECT idCom FROM affiliations where idCom = ? and idA= ?";
	        preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setInt(1, idCom);
	        preparedStatement.setString(2, truncate(idA, idAMaxLength));
	        preparedStatement.execute();
	        ResultSet rs = preparedStatement.getResultSet();
	        rs.next();
	        return rs.getInt(1);
        }
    	System.out.println("Affiliation exists");
    	return id;
    }

    public static void insertRows(Statement s, Integer[] idCom, String doi) throws SQLException {
        for(int x : idCom) {
            insertRow(s, x, doi);
        }
    }
    
    public static int getByPK(Statement s, int idCom, String idA) throws SQLException {
    	String query ="SELECT idCom FROM AFFILIATIONS WHERE idCom = ? and idA = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,idCom);
        preparedStatement.setString(2,truncate(idA, idAMaxLength));
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        if(rs.next()) {
        	return rs.getInt(1);
        }
        else {
        	return -1;
        }
    	
    }
    
    private static String truncate(String text, int maxValue) {
    	if (text.length() > maxValue) {
        	text = text.substring(0, maxValue - 1);	
        }
    	return text;
    }
}
