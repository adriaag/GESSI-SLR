package com.webapp.gessi.domain.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.consensusCriteria;
import com.webapp.gessi.data.userDesignationICEC;
import com.webapp.gessi.domain.dto.consensusCriteriaDTO;
import com.webapp.gessi.domain.dto.userDesignationDTO;
import com.webapp.gessi.domain.dto.userDesignationICECDTO;

public class UserDesignationICECController {
	
    public static void insertRows(List<userDesignationICECDTO> userDesignationICECList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
    	
    	for (userDesignationICECDTO element: userDesignationICECList) {
    		userDesignationICEC.insertRow(s, element.getUsername(), element.getIdRef(), element.getIdICEC());
    	}
    	
    }
    
    public static void deleteRows(List<userDesignationICECDTO> userDesignationICECList) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        s = conn.createStatement();
    	
    	for (userDesignationICECDTO element: userDesignationICECList) {
    		userDesignationICEC.deleteRow(s, element.getUsername(), element.getIdRef(), element.getIdICEC());
    	}
    	
    }
    
    public static List<userDesignationICECDTO> getByPK(Statement s, String username, int idRef) throws SQLException {
        return userDesignationICEC.getByPK(s, username, idRef);
    }

}
