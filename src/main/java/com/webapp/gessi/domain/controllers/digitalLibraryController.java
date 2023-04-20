package com.webapp.gessi.domain.controllers;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.data.digitalLibrary;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class digitalLibraryController {
    private static Connection iniConnection() throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        return conn;
    }

    public static ArrayList<String> getNames() throws SQLException {
        Connection con = iniConnection();
        Statement s = con.createStatement();
        ArrayList<String> DLs = digitalLibrary.getNames(s);
        ArrayList<String> ret = new ArrayList<String>();
        for (int i = 0; i < DLs.size(); i++)
            ret.add(i+1 + ". " + DLs.get(i));
        con.commit();
        return ret;
    }
}
