package com.webapp.gessi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/application.properties")
public class ConfigParser {
	@Value("${spring.datasource.url}")
    private String url;
	
	@Value("${spring.datasource.username}")
    private String username;
	
	@Value("${spring.datasource.password}")
    private String password;
	
	private static ConfigParser config;

	@Bean
	public static ConfigParser getConfig() {
		if (config == null) {
			config = new ConfigParser();
		}
		return config;
	}
	
	public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
