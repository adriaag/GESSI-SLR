package com.webapp.gessi.data;

import com.webapp.gessi.domain.dto.importErrorDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class importationLogError {
    public static boolean createTable(Statement s) {
        try {
            s.execute("create TABLE ImportationLogError(" +
                    "idLogErr INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), time timestamp, " +
                    "doi VARCHAR(100), idDL int NOT NULL, idProject int NOT NULL, BibTex VARCHAR(10000),  PRIMARY KEY (idLogErr)," +
                    "CONSTRAINT DL_FK_I FOREIGN KEY (idDL) REFERENCES DIGITALLIBRARIES(idDL) ON DELETE CASCADE," +
                    "CONSTRAINT PR_FK_I FOREIGN KEY (idProject) REFERENCES PROJECT(id) ON DELETE CASCADE)");
            System.out.println("Created table importationLogError");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table importationLogError exists");
            else System.out.println("Error en create table importationLogError");
            return false;
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table ImportationLogError");
        System.out.println("Dropped table importationLogError");        }
        catch (SQLException sqlException) {
        System.out.println("Tabla ImportationLogError not exist");
    }
    }

    public static boolean ifExistsDOI(Statement s, String key) throws SQLException {
        //ejemplo key = 8984351
        System.out.println("DOI exists in articles");
        ResultSet rs = s.executeQuery("SELECT * FROM referencias r INNER JOIN articles a on r.DOI = a.DOI AND a.CITEKEY ='" + key + "'");
        return rs.next();
    }

    public static void insertRow(Statement s, String doi, String data, String idDL, int idProject, Timestamp timesql) throws SQLException {

            PreparedStatement prepStatement = s.getConnection().
                    prepareStatement("INSERT INTO ImportationLogError(time, doi, idDL, idProject, BibTex) VALUES (?,?,?,?,?)");
            prepStatement.setTimestamp(1, timesql);
            prepStatement.setString(2, doi);
            prepStatement.setInt(3, Integer.parseInt(idDL));
            prepStatement.setInt(4, idProject);
            prepStatement.setString(5, data);

            int numberOfRowsInserted = prepStatement.executeUpdate();
            System.out.println("numberOfRowsInserted=" + numberOfRowsInserted);

            prepStatement.close(); // close PreparedStatement
            System.out.println("Inserted row in importation log error");
    }

    public static List<importErrorDTO> getErrors(Statement s, Timestamp timesql) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT time, idDL,doi,BibTex, idProject FROM IMPORTATIONLOGERROR WHERE time=TIMESTAMP('"+ timesql.toString()+"')");
        ArrayList<importErrorDTO> ret = new ArrayList<importErrorDTO>();
        while (rs.next()) {
            ret.add(new importErrorDTO(rs.getTimestamp(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getInt(5)));
        }
        return ret;
    }

    public static List<importErrorDTO> getAllErrors(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT time, idDL,doi,BibTex, idProject FROM IMPORTATIONLOGERROR");
        ArrayList<importErrorDTO> ret = new ArrayList<importErrorDTO>();
        while (rs.next()) {
            ret.add(new importErrorDTO(rs.getTimestamp(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getInt(5)));
        }
        return ret;
    }
    
    public static List<importErrorDTO> getErrorsFromProject(Statement s, int idProject) throws SQLException {
        ResultSet rs;
        String query = "SELECT time, idDL,doi,BibTex, idProject FROM IMPORTATIONLOGERROR WHERE idProject = ?";
        PreparedStatement prepStatement = s.getConnection().prepareStatement(query);
        prepStatement.setInt(1, idProject);
        rs = prepStatement.executeQuery();
        ArrayList<importErrorDTO> ret = new ArrayList<importErrorDTO>();
        while (rs.next()) {
            ret.add(new importErrorDTO(rs.getTimestamp(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getInt(5)));
        }
        prepStatement.close();
        return ret;
    }


    /*
    INSERT INTO IMPORTATIONLOGERROR (IDLOGERR ,TIME) VALUES (100,TIMESTAMP('1960-01-01 23:03:20'));

SELECT * FROM IMPORTATIONLOGERROR WHERE time=TIMESTAMP('1960-01-01 23:03:20');
*/
}
