package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.consensusCriteria;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ConsensusCriteriaController {

    public static void insertRows(List<consensusCriteriaDTO> consensusCriteriaDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        for(consensusCriteriaDTO value: consensusCriteriaDTOList) {
        	consensusCriteria.insertRow(s, value.getIdICEC(), value.getIdRef());	
        }
        s.getConnection().commit();
    }

    public static void deleteRows(List<consensusCriteriaDTO> consensusCriteriaDTOList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        for (consensusCriteriaDTO value: consensusCriteriaDTOList) {
        	consensusCriteria.deleteRow(s, value.getIdICEC(), value.getIdRef());
        }
        s.getConnection().commit();
    }

    public static List<consensusCriteriaDTO> getByIdRef(Statement s, int idRef) throws SQLException {
        return consensusCriteria.getByIdRef(s, idRef);
    }

    public static List<consensusCriteriaDTO> getByIdICEC(int idICEC) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        return consensusCriteria.getByIdICEC(s, idICEC);
    }

}
