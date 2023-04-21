package com.webapp.gessi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class company {
    public static void createTable(Statement s) {
        try {
            s.execute("create table companies(idCom INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    " name varchar(500) UNIQUE, PRIMARY KEY (idCom) ) ");
            System.out.println("Created table companies");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table companies exists");
            else System.out.println("Error en la creación de table companies");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table companies");
        System.out.println("Dropped table companies");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla companies not exist");
        }
    }


    public static int insertRow(Statement s, String name) throws SQLException {
    	int id = getByName(s,name);
    	
    	if (id == -1) {
	    	String query = "INSERT INTO companies(name) VALUES (\'" + name + "\')";
	        //System.out.println(query);
	        s.execute(query);
	        System.out.println("Inserted row with idCom, name in companies");
	        s.getConnection().commit();
	      
	        ResultSet rs = s.executeQuery("SELECT idCom FROM companies where name = '" + name + "'");
	        rs.next();
	        return rs.getInt(1);
    	}
    	System.out.println("Company exists");
    	return id;
    }

    public static ResultSet getCompanies(Statement s, String doi) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("select c.IDCOM, c.NAME from companies c, AFFILIATIONS af, ARTICLES a " +
                "where a.DOI = '" + doi.replaceAll("'", "''") + "' AND AF.IDCOM = c.idcom and af.ida = a.DOI" );
        return rs;
    }

    static Integer[] insertRows(Statement s, String affiliationToInsert) throws SQLException {
        String[] splitArray = affiliationToInsert.split(";");
        Integer[] ret = new Integer[splitArray.length];
        int i = 0;
        for(String x : splitArray) {
            ret[i++] = insertRow(s,x);
        }
        return ret;
    }
    
    public static int getByName(Statement s, String name) throws SQLException {
    	String query ="SELECT idCom FROM COMPANIES WHERE name = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1,name);
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        if(rs.next()) {
        	return rs.getInt(1);
        }
        else {
        	return -1;
        }
    	
    }
}
