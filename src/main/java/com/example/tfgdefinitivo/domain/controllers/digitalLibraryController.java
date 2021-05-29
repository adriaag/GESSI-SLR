package com.example.tfgdefinitivo.domain.controllers;

import com.example.tfgdefinitivo.data.digitalLibrary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

@RestController
@RequestMapping("/dls")
public class digitalLibraryController {
    private static Statement iniConnection() throws SQLException {
        String url = "jdbc:derby:derbyDB;create=true";
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        Connection conn = DriverManager.getConnection(url, props);
        conn.setAutoCommit(false);
        return conn.createStatement();
    }

    @RequestMapping(path = "/names", method = RequestMethod.GET)
    public static ArrayList<String> getNames() throws SQLException {
        Statement s = iniConnection();
        ArrayList<String> DLs = digitalLibrary.getNames(s);
        ArrayList<String> ret = new ArrayList<>();
        for (int i = 0; i < DLs.size(); i++)
            ret.add(i+1 + ". " + DLs.get(i));
        return ret;
    }
}
