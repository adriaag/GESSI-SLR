package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class venue {
	
	private static final int nameMaxLength = 300;
	private static final int acronymMAxLength = 20;

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
    	int id = getByName(s,nameVenue);
    	
    	if (id == -1) {
	        String query = "INSERT INTO venues(name) VALUES (?)";
	        //System.out.println(query);
	        Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1,truncate(nameVenue, nameMaxLength));
	        preparedStatement.execute();
	        System.out.println("Inserted row with idVenue, name, acr in venues");
	        s.getConnection().commit();
	        
	        
	        query = "SELECT idVen FROM venues where name = ?";
	        preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1,truncate(nameVenue, nameMaxLength));
	        preparedStatement.execute();
	        ResultSet rs = preparedStatement.getResultSet();
	        rs.next();
	        return rs.getInt(1);
    	}
    	System.out.println("Venue exists");
    	return id;
    }
    public static ResultSet getVenue(Statement s, int idVen) throws SQLException {
        String query = "SELECT * FROM venues where idVen =?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1,idVen);
        preparedStatement.execute();
        return preparedStatement.getResultSet();
        
    }
    
    public static int getByName(Statement s, String name) throws SQLException {
    	String query ="SELECT idVen FROM VENUES WHERE name = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,truncate(name, nameMaxLength));
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
