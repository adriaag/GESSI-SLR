package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.article;
import com.webapp.gessi.data.Reference;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import com.webapp.gessi.domain.dto.importErrorDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import com.webapp.gessi.domain.dto.referenceDTOadd;
import com.webapp.gessi.exceptions.BadBibtexFileException;

import org.jbibtex.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ReferenceController {

    public static List<referenceDTO> getReferences(int idProject) throws SQLException { 
    	return Reference.getAllReferences(idProject); 
    }

    public static referenceDTO getReference(int id) throws SQLException{
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        return Reference.getReference(conn, id);
    }

    public static List<importErrorDTO> addReference(String nameDL, int idProject, MultipartFile file)
            throws SQLException, IOException, BadBibtexFileException {
        return Reference.importar(nameDL, ProjectController.getById(idProject), file);
    }

    public static int getReferencesImport(){
        return article.getReferencesImported();
    }

    public static void resetReferencesImport(){
        article.setReferencesImported(0);
    }
    
    public static int getReferencesDuplicated(){
        return article.getReferencesDuplicated();
    }

    public static void resetReferencesDuplicated(){
        article.setReferencesDuplicated(0);
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
        List<Integer> currentExclusionDTOList = ConsensusCriteriaController.getByIdRef(s, idRef).stream().map(consensusCriteriaDTO::getIdICEC).collect(Collectors.toCollection(LinkedList::new));
        currentExclusionDTOList.forEach(applCriteriaList::remove);
        copyApplCriteriaList.forEach(currentExclusionDTOList::remove);
        if (!applCriteriaList.isEmpty()) {
            List<consensusCriteriaDTO> exclusionDTOList = new ArrayList<>();
            applCriteriaList.forEach(value -> exclusionDTOList.add(new consensusCriteriaDTO(idRef, value, null)));
            ConsensusCriteriaController.insertRows(exclusionDTOList);
        }
        if (!currentExclusionDTOList.isEmpty()) {
            List<consensusCriteriaDTO> exclusionDTOList = new ArrayList<>();
            currentExclusionDTOList.forEach(value -> exclusionDTOList.add(new consensusCriteriaDTO(idRef, value, null)));
            ConsensusCriteriaController.deleteRows(exclusionDTOList);
        }
    }
    
    public static void deleteReference(int idRef) throws SQLException {
    	Reference.delete(idRef);
    }

    public static List<importErrorDTO> getAllErrors() throws SQLException {
        return Reference.getAllErrors();
    }
    
    public static List<importErrorDTO> getErrors(int idProject) throws SQLException {
        return Reference.getErrors(idProject);
    }

    public static void updateState(int idRef, String state) throws SQLException {
        Reference.update(idRef, state);
    }
    
    public static referenceDTO addReferenceManually (referenceDTOadd referenceData, int idProject) throws SQLException {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Statement s = ctx.getBean(Connection.class).createStatement(); 	
    	return Reference.addReferenceManually(s, referenceData, idProject);
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
