package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.Criteria;
import com.webapp.gessi.domain.dto.CriteriaDTO;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class criteriaController {
    private static Connection iniConnection() throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        return conn;
    }

    public static String addCriteria(String idICEC, String text, String type, int idProject) throws SQLException {
        System.out.println("add criteria en controller criteria");
        return Criteria.insert(idICEC, text, type, idProject);
    }

    public static int insertDuplicateCriteria(int idProject) throws SQLException {
        System.out.println("add criteria en controller criteria");
        Criteria.insert("EC1", "Duplicated publication.", "exclusion", idProject);
        return Criteria.getCriteria("EC1", idProject).getId();
    }

    public static void updateCriteria(int id, CriteriaDTO f) throws SQLException {
        Criteria.update(f.getName(), f.getText(), id);
    }

    public static void deleteCriteria(@PathVariable("id") int id) throws SQLException {
        System.out.println("delete criteria en controller criteria");
        List<consensusCriteriaDTO> exclusionDTOList = ConsensusCriteriaController.getByIdICEC(id);
        for (consensusCriteriaDTO exclusionDTO : exclusionDTOList) {
            referenceDTO referenceDTO = ReferenceController.getReference(exclusionDTO.getIdRef());
            if (referenceDTO.getExclusionDTOList().size() <= 1)
                ReferenceController.updateState(referenceDTO.getIdRef(), null);
        }
        Criteria.delete(id);

    }

    public static List<CriteriaDTO> getCriteriasIC(int idProject) throws SQLException {
        return Criteria.getAllCriteria("inclusion", idProject);
    }
    public static List<CriteriaDTO> getCriteriasEC(int idProject) throws SQLException { 
    	return Criteria.getAllCriteria("exclusion", idProject); 
    }


    public static List<String> getStringListCriteriasEC(int idProject) throws SQLException {
        List<CriteriaDTO> list = Criteria.getAllCriteria("exclusion", idProject);
        return list.stream().map(CriteriaDTO::getName).collect(Collectors.toList());
    }

    public static List<String> getAllCriteria(int idProject) throws SQLException {
        ArrayList<String> r = new ArrayList<>();
        List<CriteriaDTO> list = Criteria.getAllCriteria(null, idProject);
        for (CriteriaDTO i : list) {
            r.add(i.getName());
            System.out.println(i.getName());
        }
        return r;
    }
}
