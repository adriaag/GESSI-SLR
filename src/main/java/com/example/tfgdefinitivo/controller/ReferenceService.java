package com.example.tfgdefinitivo.controller;

import com.example.tfgdefinitivo.dao.ReferenceDao;
import com.example.tfgdefinitivo.model.Reference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Properties;

@RestController
public class ReferenceService {
    static String framework = "embedded";
    static String dbName = "derbyDB";
    static String protocol = "jdbc:derby:";
    static Properties props = iniProperties(); // connection properties

    private static Properties iniProperties() {
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        return props;
    }

    ReferenceDao refDao = new ReferenceDao();

    @RequestMapping("/0")
    public String getHelloWorld(){
        return "Hello World";
    }

    @GetMapping(value = "/getAllReferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Reference> getReferences(){
        return refDao.getAllReferences();
    }

    @RequestMapping(value = "/createTables")
    public void createTables(){
        refDao.create();
    }
    @RequestMapping(value = "/deleteTables")
    public void deleteTables(){
        refDao.delete();
    }




    //@PostMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
    //public String postReference(@RequestParam String name) {}
}
