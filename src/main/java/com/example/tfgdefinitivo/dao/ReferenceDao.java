package com.example.tfgdefinitivo.dao;

import com.example.tfgdefinitivo.model.*;
import org.jbibtex.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReferenceDao {

    public static int insertRow(Statement s, String doi, String idDL) throws SQLException {
        try {
            String query = "INSERT INTO referencias(doi,idDL) VALUES (\'" + doi + "\', " + idDL + ")";
            System.out.println(query);
            s.execute(query);
            System.out.println("Inserted row with doi, idDL in referencias");
            s.getConnection().commit();
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        ResultSet rs = s.executeQuery("SELECT idRef FROM referencias where doi = '" + doi + "' and idDL =" + idDL);
        rs.next();
        return rs.getInt(1);
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table referencias(idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, " +
                    "INCREMENT BY 1), doi varchar(50), idDL int, " +
                    "PRIMARY KEY (doi,idDL), CONSTRAINT DL_FK_R FOREIGN KEY (idDL) REFERENCES digitalLibraries (idDL)," +
                    "CONSTRAINT AR_FK_R FOREIGN KEY (doi) REFERENCES articles (doi))");
            //UNIQUE EN DOI
            System.out.println("Created table referencias");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32"))
                System.out.println("Table referencias exists");
            else if (e.getMessage().contains("primary key"))
                System.out.println("Referencia ya importada");
            else {
                while (e != null) {
                    System.err.println("\n----- SQLException -----");
                    System.err.println("  SQL State:  " + e.getSQLState());
                    System.err.println("  Error Code: " + e.getErrorCode());
                    System.err.println("  Message:    " + e.getMessage());
                    e = e.getNextException();
                }
            }
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        s.execute("drop table referencias");
        System.out.println("Dropped table referencias");
    }

    public static int getLastID(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT idRef FROM referencias ORDER BY idref DESC");
        rs.next();
        return rs.getInt(1);
    }

    public static ResultSet getAll(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM referencias ");
        return rs;
    }

    public static List<Reference> getAllReferences() {

        List<Reference> refList = null;

        String framework = "embedded";
        String dbName = "derbyDB";
        Properties props = new Properties();;
        props.put("user", "user1");
        props.put("password", "user1");
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s = null;
        try {
            String url = "jdbc:derby:derbyDB;create=true";
            conn = DriverManager.getConnection(url, props);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();

            ResultSet rs = getAll(s);

            int number = 1;

            refList = new ArrayList<Reference>();
            while(rs.next()) {
                System.out.println(number++);
                int idR = rs.getInt(1);
                String doiR = rs.getString(2);
                int dlR = rs.getInt(3);

                System.out.println(idR + " " + doiR + " " + dlR);
                Reference NewRef = new Reference(idR, doiR, dlR);
                digitalLibrary dl = null;
                Statement s2 = conn.createStatement();
                ResultSet rsDL = digitalLibraryDao.getdigitalLibrary(s2,dlR);
                if(rsDL.next())
                    dl = new digitalLibrary(rsDL.getInt(1),rsDL.getString(2), rsDL.getString(3));
                NewRef.setDl(dl);

                Statement s3 = conn.createStatement();
                ResultSet rsAr = articleDao.getArticle(s3,doiR);
                article ar = null;
                if(rsAr.next()) {
                    ar = new article(rsAr.getString(1), rsAr.getString(2),
                            rsAr.getString(3), rsAr.getInt(4), rsAr.getString(5),
                            rsAr.getString(6), rsAr.getInt(7),
                            rsAr.getInt(8), rsAr.getString(9), rsAr.getString(10),
                            rsAr.getInt(11), rsAr.getString(12));

                    rsAr = venueDao.getVenue(s3,rsAr.getInt(4));
                    venue v = null;
                    if (rsAr.next())
                        v = new venue(rsAr.getInt(1), rsAr.getString(2), rsAr.getString(3));
                    ar.setVen(v);

                    rsAr = companyDao.getCompanies(s3,doiR);
                    List<company> c = new ArrayList<company>();
                    company auxC = null;
                    while (rsAr.next()) {
                        auxC = new company(rsAr.getInt(1), rsAr.getString(2));
                        c.add(auxC);
                    }
                    ar.setCompanies(c);

                    rsAr = researcherDao.getResearchers(s3,doiR);
                    List<researcher> rss = new ArrayList<researcher>();
                    researcher auxR = null;
                    while (rsAr.next()) {
                        auxR = new researcher(rsAr.getInt(1), rsAr.getString(2));
                        rss.add(auxR);
                    }
                    ar.setResearchers(rss);
                }
                NewRef.setArt(ar);

                refList.add(NewRef);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return refList;
    }
    public static void create() {
        String framework = "embedded";
        String dbName = "derbyDB";
        String protocol = "jdbc:derby:";
        Properties props = new Properties();;
        props.put("user", "user1");
        props.put("password", "user1");
        System.out.println("Program starting in " + framework + " mode");
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s = null;
        try {
            String url = "jdbc:derby:derbyDB;create=true";
            conn = DriverManager.getConnection(url, props);
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            crearTablas(s,conn,statements);
            //deleteTables(s,conn,statements);

            conn.commit();
            System.out.println("Committed the transaction");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally{
            if(s!=null) {
                try {
                    s.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }
    }
    public static void delete() {
        String framework = "embedded";
        String dbName = "derbyDB";
        String protocol = "jdbc:derby:";
        Properties props = new Properties();;
        props.put("user", "user1");
        props.put("password", "user1");
        System.out.println("Program starting in " + framework + " mode");
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s = null;
        try {
            String url = "jdbc:derby:derbyDB;create=true";
            conn = DriverManager.getConnection(url, props);
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            deleteTables(s,conn,statements);

            conn.commit();
            System.out.println("Committed the transaction");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally{
            if(s!=null) {
                try {
                    s.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }
    }
    private static void crearTablas(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        // Create table digitalLibraries if not exist
        if (digitalLibraryDao.createTable(s))
            //insert rows in table
            digitalLibraryDao.insertRows(conn, statements);
        venueDao.createTable(s);
        articleDao.createTable(s);
        researcherDao.createTable(s);
        ReferenceDao.createTable(s);
        authorDao.createTable(s);
        companyDao.createTable(s);
        affiliationDao.createTable(s);
    }

    private static void deleteTables(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        affiliationDao.dropTable(s);
        companyDao.dropTable(s);
        authorDao.dropTable(s);
        ReferenceDao.dropTable(s);
        researcherDao.dropTable(s);
        articleDao.dropTable(s);
        venueDao.dropTable(s);
        digitalLibraryDao.dropTable(s);
    }

    public static void importar(String path, String nameDL, Statement s)
            throws SQLException, IOException, ParseException {
        articleDao.importar(path, nameDL, s);
        s.getConnection().commit();
    }
}
