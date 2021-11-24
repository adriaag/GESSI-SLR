package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.data.article;
import com.webapp.gessi.data.reference;
import com.webapp.gessi.domain.dto.importErrorDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import org.jbibtex.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/references")
public class ReferenceController {

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public static List<referenceDTO> getReferences() { return reference.getAllReferences(); }

    @GetMapping(value = "/get/", produces = MediaType.APPLICATION_JSON_VALUE)
    public static referenceDTO getReference(@RequestParam(name= "id", required=false, defaultValue="1") int id){
        return reference.getReference(id);
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public static List<importErrorDTO> addReference( String nameDL, MultipartFile file)
            throws ParseException, SQLException, IOException {
        return reference.importar( nameDL,file);
    }

    public static int getReferencesImport(){
        return article.getReferencesImported();
    }

    public static void resetReferencesImport(){
        article.setReferencesImported(0);
    }

    public static void reset() {
        reference.delete();
        reference.create();
    }

    public static void updateReference(int idRef, String estado, String applCriteria) {
        //TODO si estado o applcriteria = "" añadir null a la bd!!!
        reference.update(idRef,estado, applCriteria);
    }

    public static List<importErrorDTO> getAllErrors() throws ParseException, SQLException, IOException {
        return reference.getAllErrors();
    }

    public static List<Integer> setNullCriteria(String oldIdICEC) {
        List<Integer> refs = reference.getReferenceOfCriteria(oldIdICEC);
        for (int r : refs)
            reference.setNullCriteria(r);
        return refs;
    }

    public static void setCriteria(int idRef, String idICEC) {
        reference.setCriteria( idRef,  idICEC);
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
