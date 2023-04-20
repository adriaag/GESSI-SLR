package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.article;
import com.webapp.gessi.data.Reference;
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
    public static List<referenceDTO> getReferences(int idProject) { return Reference.getAllReferences(idProject); }

    @GetMapping(value = "/get/", produces = MediaType.APPLICATION_JSON_VALUE)
    public static referenceDTO getReference(@RequestParam(name= "id", required=false, defaultValue="1") int id){
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        return Reference.getReference(conn, id);
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public static List<importErrorDTO> addReference(String nameDL, int idProject, MultipartFile file)
            throws ParseException, SQLException, IOException {
        return Reference.importar(nameDL, ProjectController.getById(idProject), file);
    }

    public static int getReferencesImport(){
        return article.getReferencesImported();
    }

    public static void resetReferencesImport(){
        article.setReferencesImported(0);
    }

    public static void reset() {
        Reference.delete();
        Reference.create();
    }

    public static void updateReference(int idRef, String estado, List<Integer> applCriteria) throws SQLException {
        Reference.update(idRef, estado);
        List<Integer> applCriteriaList = new LinkedList<>(applCriteria);
        List<Integer> copyApplCriteriaList = new LinkedList<>(applCriteria);
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Statement s = ctx.getBean(Connection.class).createStatement();
        List<Integer> currentExclusionDTOList = ExclusionController.getByIdRef(s, idRef).stream().map(ExclusionDTO::getIdICEC).collect(Collectors.toCollection(LinkedList::new));
        currentExclusionDTOList.forEach(applCriteriaList::remove);
        copyApplCriteriaList.forEach(currentExclusionDTOList::remove);
        if (!applCriteriaList.isEmpty()) {
            List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
            applCriteriaList.forEach(value -> exclusionDTOList.add(new ExclusionDTO(idRef, value, null)));
            ExclusionController.insertRows(exclusionDTOList);
        }
        if (!currentExclusionDTOList.isEmpty()) {
            List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
            currentExclusionDTOList.forEach(value -> exclusionDTOList.add(new ExclusionDTO(idRef, value, null)));
            ExclusionController.deleteRows(exclusionDTOList);
        }
    }
    
    public static void deleteReference(int idRef) {
    	Reference.delete(idRef);
    }

    public static List<importErrorDTO> getAllErrors() throws SQLException {
        return Reference.getAllErrors();
    }
    
    public static List<importErrorDTO> getErrors(int idProject) throws SQLException {
        return Reference.getErrors(idProject);
    }

    public static void updateState(int idRef, String state) {
        Reference.update(idRef, state);
    }
    

    @RequestMapping(value = "/createTables")
    public void createTables(){
        Reference.create();
    }
    @RequestMapping(value = "/deleteTables")
    public void deleteTables(){
        Reference.delete();
    }
}
