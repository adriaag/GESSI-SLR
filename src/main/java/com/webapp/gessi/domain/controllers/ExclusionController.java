package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.Exclusion;
import com.webapp.gessi.domain.dto.ExclusionDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ExclusionController {

    public static void insertRows(List<ExclusionDTO> exclusionDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        exclusionDTOList.forEach(value -> Exclusion.insertRow(s, value.getIdICEC(), value.getIdRef()));
        s.getConnection().commit();
    }

    public static void deleteRows(List<ExclusionDTO> exclusionDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        exclusionDTOList.forEach(value -> Exclusion.deleteRow(s, value.getIdICEC(), value.getIdRef()));
        s.getConnection().commit();
    }

    public static void deleteCriteriaFK() {
        Exclusion.deleteCriteriaFK();
    }

    public static void addCriteriaFK() {
        Exclusion.addCriteriaFK();
    }

    public static List<ExclusionDTO> getByIdRef(Statement s, int idRef) {
        return Exclusion.getByIdRef(s, idRef);
    }

    public static List<ExclusionDTO> getByIdICEC(String idICEC) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        return Exclusion.getByIdICEC(s, idICEC);
    }

    public static void UpdateIdICEC(String oldIdICEC, String newIdICEC) {
        Exclusion.updateIdICEC(oldIdICEC, newIdICEC);
    }
}
