package com.webapp.gessi.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class researcher implements Serializable {
	
	private static final int nameMaxLength = 50;

    public static void createTable(Statement s) {
        try {
            s.execute("create table researchers( idRes INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                    "name varchar(50) unique, " +
                    "PRIMARY KEY (idRes)) ");
            System.out.println("Created table researchers");
        } catch (SQLException t  ){
            if (t.getSQLState().equals("X0Y32"))
                System.out.println("Table researchers exists");
            else System.out.println("Error en la creación de table researchers");
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table researchers");
        System.out.println("Dropped table researchers");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla researchers not exist");
        }
    }

    public static ArrayList<String> getNames(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<String>();
        ResultSet rs = s.executeQuery("SELECT name FROM researchers");
        while(rs.next()) {
            ret.add(rs.getString("name"));
        }
        return ret;
    }

    public static int insertRow(Statement s, String name) throws SQLException {
    	int id = getByName(s,name);
    	
    	if (id == -1) {
	        String query = "INSERT INTO researchers(name) VALUES (?)";
	        Connection conn = s.getConnection();
	        PreparedStatement preparedStatement = conn.prepareStatement(query);
	        preparedStatement.setString(1, truncate(name, nameMaxLength));
	        //System.out.println(query);
	        preparedStatement.execute();
	        System.out.println("Inserted row with idRes " + name + " in researchers");
	        s.getConnection().commit();
	        
	        return getByName(s,name);
    	}
    	System.out.println("Researcher exists");
    	return id;
    }

    public static Integer[] insertRows(String names, Statement s) throws SQLException {
        String[] splitArray = names.split("and ");
        Integer[] ret = new Integer[splitArray.length];
        int i = 0;
        for(String x : splitArray) {
            ret[i] = insertRow(s,x);
            ++i;
        }
        return ret;
    }

    static ResultSet getResearchers(Statement s3, String doi) throws SQLException {
    	Connection conn = s3.getConnection();
    	String query = "select rss.IDRES , rss.NAME from RESEARCHERS rss, AUTHORS au, "+
    	"ARTICLES ar where ar.DOI = ?  AND au.IDA = ar.DOI and au.IDRES = rss.IDRES";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, doi);
        preparedStatement.execute();
        return preparedStatement.getResultSet();
    }
    
    public static int getByName(Statement s, String name) throws SQLException {
    	String query ="SELECT idRes FROM RESEARCHERS WHERE name = ?";
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
