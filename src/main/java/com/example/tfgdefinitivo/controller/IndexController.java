package com.example.tfgdefinitivo.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class IndexController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping(value = "/allP", produces = MediaType.APPLICATION_JSON_VALUE)
    public String greeting(@RequestParam String name) {
        long first = counter.incrementAndGet();
        String second = String.format(template, name);
        return first + ". " + second;
    }
    //curl http://localhost:8080/all/?name=Enric

    @GetMapping("/articles")
    public void getAllArticles(@RequestParam String name) throws SQLException {
       /* ResultSet rs = articleDao.getAllData(s);

        int numero = 1;
        while (rs.next()){
            System.out.println(numero++);
            for(int i = 1; i<=13; i++) {
                System.out.println(rs.getString(i));
            }
        }*/
    }
}
