package com.webapp.gessi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.webapp.gessi.data.Reference;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class that encapsulates the database connection information
 */
@Configuration
public class DBConnection {
	
    @Bean
    public Connection getConnection() throws SQLException, ClassNotFoundException {
    	
    	ConfigParser conf = ConfigParser.getConfig();
        //System.out.println(conf.getUrl() + conf.getUsername() + conf.getPassword());
    	
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        // We use a configuration file for customise the connection to the URL
        try {
        return DriverManager.getConnection(conf.getUrl(),conf.getUsername(),conf.getPassword());
        }
        catch (SQLException e) {
        	if(e.getSQLState().equals("08004")) {
        		System.out.println("Creant BD");
        		//Si la BD no existeix salta una excepció i es crea
        		iniDB();
        		return getConnection();
        	}
        	else {
        		e.printStackTrace();
        	}
        
        }
        return null; 
        }
    
    public static void iniDB() throws ClassNotFoundException, SQLException {
    	ConfigParser conf = ConfigParser.getConfig();
    	
    	Class.forName("org.apache.derby.jdbc.ClientDriver");
    	Connection conn = DriverManager.getConnection(conf.getUrl()+";create = true;user="+conf.getUsername()+";");
    	Statement s = conn.createStatement();
    	s.executeUpdate("call SYSCS_UTIL.SYSCS_CREATE_USER('"+conf.getUsername()+"','"+conf.getPassword()+"' )");
    	s.execute("CREATE SCHEMA "+conf.getUsername().toUpperCase()+" AUTHORIZATION "+conf.getUsername());
    	conn.commit();
    	try{
    		DriverManager.getConnection(conf.getUrl()+";shutdown=true;");
    	}
    	catch (SQLException e) {
    		if (e.getErrorCode() == 45000 && e.getSQLState().equals("08006")) {
    			System.out.println("Creant taules");
    			Reference.create();
    		}
    		else {
    			e.printStackTrace();
    		}
    		
    	}
    	
    	
    }

    
}
