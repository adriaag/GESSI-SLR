package com.example.tfgdefinitivo.domain.controllers;

import com.example.tfgdefinitivo.config.DBConnection;
import com.example.tfgdefinitivo.data.criteria;
import com.example.tfgdefinitivo.domain.dto.criteriaDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/criteria")
public class criteriaController {
    private static Connection iniConnection() throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        return conn;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public static void addCriteria(String idICEC, String text, String type) {
        System.out.println("add criteria en controller criteria");
        criteria.insert(idICEC, text, type);
        System.out.println("end add criteria en controller criteria");
    }

    public static List<criteriaDTO> getCriteriasIC() {
        return criteria.getAllCriteria("IC");
    }
    public static List<criteriaDTO> getCriteriasEC() { return criteria.getAllCriteria("EC"); }

    public static List<String> getAllCriteria() {
        ArrayList<String> r = new ArrayList<>();
        List<criteriaDTO> list = criteria.getAllCriteria("");
        for (criteriaDTO i : list) {
            r.add(i.getIdICEC());
            System.out.println(i.getIdICEC());
        }
        return r;
    }

    //@PostMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
 //   HTTP POST request, used to create a new resource.
    //public String postReference(@RequestParam String name) {}


}
