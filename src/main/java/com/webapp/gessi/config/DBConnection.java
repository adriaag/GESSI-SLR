package com.webapp.gessi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        return DriverManager.getConnection(conf.getUrl(), conf.getUsername(), conf.getPassword());

    }

    
}
