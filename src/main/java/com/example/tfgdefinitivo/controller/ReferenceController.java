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

    ReferenceDao refDao = new ReferenceDao();

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
