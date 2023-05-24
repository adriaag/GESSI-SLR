package com.webapp.gessi.domain.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.userDesignation;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import com.webapp.gessi.domain.dto.userDesignationDTO;
import com.webapp.gessi.domain.dto.userDesignationICECDTO;

public class UserDesignationController {
	
	public static userDesignationDTO insertRow(String username, int idRef, int numDesignation) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        userDesignation.setUserDesignation(s, numDesignation, username, idRef);
        s.getConnection().commit();
        return new userDesignationDTO(username,idRef, numDesignation, false, new ArrayList<Integer>());
    }
	
	public static void addCriteria(userDesignationDTO designation) throws SQLException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
        
        String username = designation.getUsername();
        int idRef = designation.getIdRef();
        
        if (designation.getProcessed())
    		userDesignation.setProcessed(s, username, idRef);
        
        List<Integer> applCriteriaList = new LinkedList<>(designation.getCriteriaList());
        List<Integer> copyApplCriteriaList = new LinkedList<>(designation.getCriteriaList());
        List<Integer> currentExclusionDTOList = UserDesignationICECController.getICECs(s, username, idRef);
        currentExclusionDTOList.forEach(applCriteriaList::remove);
        copyApplCriteriaList.forEach(currentExclusionDTOList::remove);
        if (!applCriteriaList.isEmpty()) {
            List<userDesignationICECDTO> exclusionDTOList = new ArrayList<>();
            applCriteriaList.forEach(value -> exclusionDTOList.add(new userDesignationICECDTO(username, idRef, value)));
            UserDesignationICECController.insertRows(exclusionDTOList);
        }
        if (!currentExclusionDTOList.isEmpty()) {
            List<userDesignationICECDTO> exclusionDTOList = new ArrayList<>();
            currentExclusionDTOList.forEach(value -> exclusionDTOList.add(new userDesignationICECDTO(username,idRef, value)));
            UserDesignationICECController.deleteRows(exclusionDTOList);
        }
	}


}
