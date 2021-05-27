package com.example.tfgdefinitivo.domain.controllers;

import com.example.tfgdefinitivo.domain.data.reference;
import com.example.tfgdefinitivo.presentation.dto.referenceDTO;
import org.jbibtex.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/references")
public class ReferenceController {


    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public static List<referenceDTO> getReferences(){ return reference.getAllReferences(); }

    @GetMapping(value = "/get/", produces = MediaType.APPLICATION_JSON_VALUE)
    public static referenceDTO getReference(@RequestParam(name= "id", required=false, defaultValue="1") int id){
        return reference.getReference(id);
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public static void addReference(String path, String nameDL, Statement s)
            throws ParseException, SQLException, IOException {
        reference.importar(path, nameDL, s);
    }

    @RequestMapping(value = "/createTables")
    public void createTables(){
        reference.create();
    }
    @RequestMapping(value = "/deleteTables")
    public void deleteTables(){
        reference.delete();
    }

    //@PostMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE)
 //   HTTP POST request, used to create a new resource.
    //public String postReference(@RequestParam String name) {}


}
