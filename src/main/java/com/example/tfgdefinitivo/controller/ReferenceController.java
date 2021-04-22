package com.example.tfgdefinitivo.controller;

import com.example.tfgdefinitivo.dao.ReferenceDao;
import com.example.tfgdefinitivo.model.Reference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/references")
public class ReferenceController {


    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public static List<Reference> getReferences(){ return ReferenceDao.getAllReferences(); }

    @RequestMapping(value = "/createTables")
    public void createTables(){
        ReferenceDao.create();
    }
    @RequestMapping(value = "/deleteTables")
    public void deleteTables(){
        ReferenceDao.delete();
    }

    //@PostMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
    //public String postReference(@RequestParam String name) {}


}
