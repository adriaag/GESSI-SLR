package com.example.tfgdefinitivo.data;

import com.example.tfgdefinitivo.config.DBConnection;
import com.example.tfgdefinitivo.domain.dto.*;
import org.jbibtex.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class reference {

    public static int insertRow(Statement s, String doi, String idDL, String estado)
            throws SQLException {
        try {
            String query;
            if (estado!=null)
                query = "INSERT INTO referencias(doi,idDL,estate) VALUES (\'" + doi
                    + "\', " + idDL + ",'" + estado+"')";
            else query = "INSERT INTO referencias(doi,idDL,estate) VALUES (\'" + doi
                    + "\', " + idDL + "," + estado+")";
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
                    "INCREMENT BY 1), doi varchar(50), idDL int, estate VARCHAR(32), " +
                    "PRIMARY KEY(idRef), unique(doi,idDL), CONSTRAINT DL_FK_R FOREIGN KEY (idDL) " +
                    "REFERENCES digitalLibraries (idDL),CONSTRAINT AR_FK_R FOREIGN KEY (doi) REFERENCES articles (doi),"+
                    "CONSTRAINT estate_chk CHECK (estate IN ( 'pending', 'accepted', 'duplicated','eliminated')))");

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

    public static List<referenceDTO> getAllReferences() {

        List<referenceDTO> refList = null;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);

        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s = null;
        try {
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();

            ResultSet rs = getAll(s);

            int number = 1;

            refList = new ArrayList<referenceDTO>();
            while(rs.next()) {
                System.out.println(number++);
                int idR = rs.getInt(1);
                String doiR = rs.getString(2);
                int dlR = rs.getInt(3);

                System.out.println(idR + " " + doiR + " " + dlR);
                referenceDTO NewRef = new referenceDTO(idR, doiR, dlR);
                digitalLibraryDTO dl = null;
                Statement s2 = conn.createStatement();
                ResultSet rsDL = digitalLibrary.getdigitalLibrary(s2,dlR);
                if(rsDL.next())
                    dl = new digitalLibraryDTO(rsDL.getInt(1),rsDL.getString(2), rsDL.getString(3));
                NewRef.setDl(dl);

                Statement s3 = conn.createStatement();
                ResultSet rsAr = article.getArticle(s3,doiR);
                articleDTO ar = null;
                if(rsAr.next()) {
                    ar = new articleDTO(rsAr.getString(1), rsAr.getString(2),
                            rsAr.getString(3), rsAr.getInt(4), rsAr.getString(5),
                            rsAr.getString(6), rsAr.getString(7),
                            rsAr.getInt(8), rsAr.getString(9), rsAr.getString(10),
                            rsAr.getInt(11), rsAr.getString(12));

                    rsAr = venue.getVenue(s3,rsAr.getInt(4));
                    venueDTO v = null;
                    if (rsAr.next())
                        v = new venueDTO(rsAr.getInt(1), rsAr.getString(2), rsAr.getString(3));
                    ar.setVen(v);

                    rsAr = company.getCompanies(s3,doiR);
                    List<companyDTO> c = new ArrayList<companyDTO>();
                    companyDTO auxC = null;
                    while (rsAr.next()) {
                        auxC = new companyDTO(rsAr.getInt(1), rsAr.getString(2));
                        c.add(auxC);
                    }
                    ar.setCompanies(c);

                    rsAr = researcher.getResearchers(s3,doiR);
                    List<researcherDTO> rss = new ArrayList<researcherDTO>();
                    researcherDTO auxR = null;
                    while (rsAr.next()) {
                        auxR = new researcherDTO(rsAr.getInt(1), rsAr.getString(2));
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
        if (digitalLibrary.createTable(s))
            //insert rows in table
            digitalLibrary.insertRows(conn, statements);
        venue.createTable(s);
        article.createTable(s);
        researcher.createTable(s);
        reference.createTable(s);
        author.createTable(s);
        company.createTable(s);
        affiliation.createTable(s);
        importationLogError.createTable(s);
    }

    private static void deleteTables(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        importationLogError.dropTable(s);
        affiliation.dropTable(s);
        company.dropTable(s);
        author.dropTable(s);
        reference.dropTable(s);
        researcher.dropTable(s);
        article.dropTable(s);
        venue.dropTable(s);
        digitalLibrary.dropTable(s);
    }

    public static void importar(String path, String idDL, Statement s) throws SQLException, IOException, ParseException {

        article.importar(path, idDL, s);
        s.getConnection().commit();
    }

    public static referenceDTO getReference(int idR) {
        referenceDTO r = null;

        Properties props = new Properties();;
        props.put("user", "user1");
        props.put("password", "user1");
        Connection conn;
        Statement s;
        try {
            String url = "jdbc:derby:derbyDB;create=true";
            conn = DriverManager.getConnection(url, props);
            conn.setAutoCommit(false);

            s = conn.createStatement();

            referenceDTO NewRef = find(idR,s);

            String doiR = NewRef.getDoi();
            int dlR = NewRef.getidDL();

            System.out.println(idR + " " + doiR + " " + dlR);

            digitalLibraryDTO dl = null;
            Statement s2 = conn.createStatement();
            ResultSet rsDL = digitalLibrary.getdigitalLibrary(s2,dlR);
            if(rsDL.next())
                dl = new digitalLibraryDTO(rsDL.getInt(1),rsDL.getString(2), rsDL.getString(3));
            NewRef.setDl(dl);

            Statement s3 = conn.createStatement();
            ResultSet rsAr = article.getArticle(s3,doiR);
            articleDTO ar = null;
            if(rsAr.next()) {
                ar = new articleDTO(rsAr.getString(1), rsAr.getString(2),
                        rsAr.getString(3), rsAr.getInt(4), rsAr.getString(5),
                        rsAr.getString(6), rsAr.getString(7),
                        rsAr.getInt(8), rsAr.getString(9), rsAr.getString(10),
                        rsAr.getInt(11), rsAr.getString(12));
                rsAr = venue.getVenue(s3,rsAr.getInt(4));
                venueDTO v = null;
                if (rsAr.next())
                    v = new venueDTO(rsAr.getInt(1), rsAr.getString(2), rsAr.getString(3));
                ar.setVen(v);

                rsAr = company.getCompanies(s3,doiR);
                List<companyDTO> c = new ArrayList<companyDTO>();
                companyDTO auxC = null;
                while (rsAr.next()) {
                    auxC = new companyDTO(rsAr.getInt(1), rsAr.getString(2));
                    c.add(auxC);
                }
                ar.setCompanies(c);

                rsAr = researcher.getResearchers(s3,doiR);
                List<researcherDTO> rss = new ArrayList<researcherDTO>();
                researcherDTO auxR = null;
                while (rsAr.next()) {
                    auxR = new researcherDTO(rsAr.getInt(1), rsAr.getString(2));
                    rss.add(auxR);
                }
                ar.setResearchers(rss);
            }
            NewRef.setArt(ar);
            r=NewRef;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return r;
    }

    private static referenceDTO find(int idR, Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM referencias where idRef=" + idR);
        rs.next();
        referenceDTO r = new referenceDTO(rs.getInt(1),rs.getString(2),rs.getInt(3));
        return r;
    }

    public static int getDL(String doi,Statement s) throws SQLException {
        ResultSet r = s.executeQuery("select idDL from referencias where doi = '" + doi + "'");
        r.next();
        return r.getInt(1);
    }
    static ResultSet isDuplicate(Statement s, int priorityImportado, String doi) throws SQLException {
        return s.executeQuery("select * from REFERENCIAS r, DIGITALLIBRARIES dl where r.doi='" + doi
                + "' and r.idDL = dl.idDL and dl.priority > "+ priorityImportado);
    }
    static void updateEstateReferences(Statement s, String doi) throws SQLException {
        s.execute("update referencias set estate = 'duplicated' where doi = '" + doi + "' and estate is null ");
    }
}
