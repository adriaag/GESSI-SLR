package com.example.tfgdefinitivo.config;

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
@PropertySource("classpath:/application.properties")
public class DBConnection {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public  String getURL() {
        return url;
    }
    public  String getUser() {
        return username;
    }
    public  String getPass() { return password; }

    @Bean
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        // We use a configuration file for customise the connection to the URL
        return DriverManager.getConnection(getURL(), getUser(), getPass());

    }
}
