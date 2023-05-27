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

    public static void insertRows(consensusCriteriaDTO consensusCriteriaDTO) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        for(int value: consensusCriteriaDTO.getIdICEC()) {
        	consensusCriteria.insertRow(s, value, consensusCriteriaDTO.getIdRef());	
        }
        s.getConnection().commit();
    }

    public static void deleteRows(consensusCriteriaDTO consensusCriteriaDTO) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        for(int value: consensusCriteriaDTO.getIdICEC()) {
        	consensusCriteria.deleteRow(s, value, consensusCriteriaDTO.getIdRef());
        }
        s.getConnection().commit();
    }
    
    public static void deleteByIdRef(int idRef) throws SQLException {
    	ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        consensusCriteria.deleteByIdRef(s, idRef);
    	
    }

    public static consensusCriteriaDTO getByIdRef(Statement s, int idRef) throws SQLException {
        return consensusCriteria.getByIdRef(s, idRef);
    }

}
