package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.article;
import com.webapp.gessi.data.reference;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import com.webapp.gessi.domain.dto.importErrorDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import org.jbibtex.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void updateReference(int idRef, String estado, String applCriteria) throws SQLException {
        reference.update(idRef, estado);
        List<String> applCriteriaList = new LinkedList<>(Arrays.asList(applCriteria.split(",")));
        List<String> copyApplCriteriaList = new LinkedList<>(Arrays.asList(applCriteria.split(",")));
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Statement s = ctx.getBean(Connection.class).createStatement();
        List<String> currentExclusionDTOList = ExclusionController.getByIdRef(s, idRef).stream().map(ExclusionDTO::getIdICEC).collect(Collectors.toCollection(LinkedList::new));
        currentExclusionDTOList.forEach(applCriteriaList::remove);
        copyApplCriteriaList.forEach(currentExclusionDTOList::remove);
        if (!applCriteriaList.isEmpty()) {
            List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
            applCriteriaList.forEach(value -> exclusionDTOList.add(new ExclusionDTO(idRef, value)));
            ExclusionController.insertRows(exclusionDTOList);
        }
        if (!currentExclusionDTOList.isEmpty()) {
            List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
            currentExclusionDTOList.forEach(value -> exclusionDTOList.add(new ExclusionDTO(idRef, value)));
            ExclusionController.deleteRows(exclusionDTOList);
        }
    }

    public static List<importErrorDTO> getAllErrors() throws SQLException {
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
