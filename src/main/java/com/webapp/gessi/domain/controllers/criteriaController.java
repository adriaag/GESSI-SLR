package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.Criteria;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import com.webapp.gessi.domain.dto.CriteriaDTO;
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
    public static String addCriteria(String idICEC, String text, String type) {
        System.out.println("add criteria en controller criteria");
        return Criteria.insert(idICEC, text, type);
    }

    public static void updateCriteria(int id, CriteriaDTO f) {
        Criteria.update(f.getName(), f.getText(), id);
    }

    public static void deleteCriteria(@PathVariable("id") int id) throws SQLException {
        System.out.println("delete criteria en controller criteria");
        List<ExclusionDTO> exclusionDTOList = ExclusionController.getByIdICEC(id);
        for (ExclusionDTO exclusionDTO : exclusionDTOList) {
            referenceDTO referenceDTO = ReferenceController.getReference(exclusionDTO.getIdRef());
            if (referenceDTO.getExclusionDTOList().size() <= 1)
                ReferenceController.updateState(referenceDTO.getIdRef(), null);
        }
        Criteria.delete(id);

    }

    public static List<CriteriaDTO> getCriteriasIC() {
        return Criteria.getAllCriteria("inclusion");
    }
    public static List<CriteriaDTO> getCriteriasEC() { return Criteria.getAllCriteria("exclusion"); }


    public static List<String> getStringListCriteriasEC() {
        List<CriteriaDTO> list = Criteria.getAllCriteria("exclusion");
        return list.stream().map(CriteriaDTO::getName).collect(Collectors.toList());
    }

    public static List<String> getAllCriteria() {
        ArrayList<String> r = new ArrayList<>();
        List<CriteriaDTO> list = Criteria.getAllCriteria(null);
        for (CriteriaDTO i : list) {
            r.add(i.getName());
            System.out.println(i.getName());
        }
        return r;
    }
}
