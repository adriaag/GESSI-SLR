package com.webapp.gessi.data;

import java.sql.*;
import java.util.ArrayList;

public class digitalLibrary {
    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM digitalLibraries");
        return rs;
    }

    //obviem maunallyImported
    public static ArrayList<String> getNames(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<String>();
        ResultSet rs = s.executeQuery("SELECT name FROM digitalLibraries WHERE idDl > 0 ORDER BY idDL asc ");
        while(rs.next()) {
            ret.add(rs.getString("name"));
        }
        return ret;
    }

    public static ArrayList<String> getIDs(Statement s) throws SQLException {
        ArrayList<String> ret = new ArrayList<String>();
        ResultSet rs = s.executeQuery("SELECT idDL FROM digitalLibraries ORDER BY idDL asc ");
        while(rs.next()) {
            ret.add(rs.getString("idDL"));
        }
        return ret;
    }

    public static boolean createTable(Statement s) {
        try {
            s.execute("create table digitalLibraries(idDL int, name varchar(50), url varchar(150) , " +
                    "priority int , PRIMARY KEY (idDL), unique(priority))");
            System.out.println("Created table digitalLibraries");
            return true;

        } catch (SQLException t ) {
            if (t.getSQLState().equals("X0Y32")) System.out.println("Table digitalLibraries exists");
            else System.out.println("Error en create table digitalLibraries");
            return false;
        }
    }

    public static void insertRows(Connection conn, ArrayList<Statement> statements) throws SQLException {
        PreparedStatement psInsert;
        psInsert = conn.prepareStatement("insert into digitalLibraries values (?, ?, ?, ?)");
        statements.add(psInsert);

        psInsert.setString(1, "1");
        psInsert.setString(2, "Scopus");
        psInsert.setString(3, "https://www.scopus.com/");
        psInsert.setString(4, "1");
        psInsert.executeUpdate();

        psInsert.setString(1, "2");
        psInsert.setString(2, "Web of Science");
        psInsert.setString(3, "https://mjl.clarivate.com/home");
        psInsert.setString(4, "2");
        psInsert.executeUpdate();
        System.out.println("Inserted rows in digitalLibraries ");

        psInsert.setString(1, "3");
        psInsert.setString(2, "IEEE Xplore");
        psInsert.setString(3, "https://ieeexplore.ieee.org/Xplore/home.jsp");
        psInsert.setString(4, "3");
        psInsert.executeUpdate();

        psInsert.setString(1, "4");
        psInsert.setString(2, "ACM DL");
        psInsert.setString(3, "https://dl.acm.org/");
        psInsert.setString(4, "4");
        psInsert.executeUpdate();

        psInsert.setString(1, "5");
        psInsert.setString(2, "ScienceDirect");
        psInsert.setString(3, "https://www.sciencedirect.com/");
        psInsert.setString(4, "5");
        psInsert.executeUpdate();

        psInsert.setString(1, "6");
        psInsert.setString(2, "SpringerLink");
        psInsert.setString(3, "https://link.springer.com/");
        psInsert.setString(4, "6");
        psInsert.executeUpdate();
        
        psInsert.setString(1, "-1");
        psInsert.setString(2, "Manually Imorted");
        psInsert.setNull(3, java.sql.Types.VARCHAR);
        psInsert.setString(4, "9999");
        psInsert.executeUpdate();
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table digitalLibraries");
        System.out.println("Dropped table digitalLibraries");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla digitalLibraries not exist");
        }
    }

    static ResultSet getdigitalLibrary(Statement s, int idDL) throws SQLException {
        return s.executeQuery("SELECT * FROM digitalLibraries where idDL = " + idDL);
    }

    static int getPriority(Statement s, String idDL) throws SQLException {
        ResultSet r = s.executeQuery("select priority from digitalLibraries where idDL = " + idDL);
        r.next();
        return r.getInt(1);
    }

}
