package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.*;
import org.jbibtex.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Reference {

    public static int insertRow(Statement s, String doi, String idDL, String estado, int idProject) throws SQLException {
        String query = "INSERT INTO referencias(doi, idDL, state, idProject) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, doi);
            preparedStatement.setString(2, idDL);
            preparedStatement.setString(3, estado);
            preparedStatement.setInt(4, idProject);
            preparedStatement.execute();
            System.out.println("Inserted row with doi, idDL.. in referencias");
            conn.commit();
        } catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
            return -2;
        }
        ResultSet rs = s.executeQuery("SELECT idRef FROM referencias where doi = '" + doi + "' and idDL =" + idDL);
        if (rs.next())
            return rs.getInt(1);
        return -2;
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table referencias(" +
                    "idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "doi varchar(50), idDL INT, state VARCHAR(10), idProject INT, " +
                    "PRIMARY KEY(idRef), unique(doi,idDL), " +
                    "CONSTRAINT DL_FK_R FOREIGN KEY (idDL) REFERENCES digitalLibraries (idDL) ON DELETE CASCADE," +
                    "CONSTRAINT AR_FK_R FOREIGN KEY (doi) REFERENCES articles (doi) ON DELETE CASCADE," +
                    "CONSTRAINT PR_FK_R FOREIGN KEY (idProject) REFERENCES project (id) ON DELETE CASCADE," +
                    "CONSTRAINT state_chk CHECK (state IN ( 'in', 'out')))");
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
        try{
        s.execute("drop table referencias");
        System.out.println("Dropped table referencias");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla referencias not exist");
        }
    }

    public static ResultSet getAll(Statement s, int idProject) throws SQLException {
        String query = "SELECT * FROM referencias WHERE IDPROJECT = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        return preparedStatement.executeQuery();
    }

    public static List<referenceDTO> getAllReferences(int idProject) {
        List<referenceDTO> refList = null;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        try {
            conn.setAutoCommit(false);
            s = conn.createStatement();
            ResultSet rs = getAll(s, idProject);
            int number = 1;
            refList = new ArrayList<>();
            while(rs.next()) {
                System.out.println(number++);

                int idR = rs.getInt(1);
                String doiR = rs.getString(2);
                int dlR = rs.getInt(3);
                String estado = rs.getString(4);
                List<ExclusionDTO> exclusionDTOList = null;
                if (Objects.equals(estado, "out")) {
                    Statement s1 = conn.createStatement();
                    exclusionDTOList = Exclusion.getByIdRef(s1, idR);
                }
                referenceDTO NewRef = new referenceDTO( idR, doiR, dlR, idProject, estado, exclusionDTOList);
                obtainReferenceDTO(conn, NewRef, doiR, dlR);

                refList.add(NewRef);
            }
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return refList;
    }

    public static void create() {
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s = null;
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            conn = ctx.getBean(Connection.class);
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
        Connection conn;
        Statement s = null;
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            conn = ctx.getBean(Connection.class);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            deleteTables(s);
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
        Project.createTable(s);
        if (Criteria.createTable(s))
            //insert duplicate exclusion
            Criteria.insertRowIni(conn,statements);
        Reference.createTable(s);
        Exclusion.createTable(s);
        author.createTable(s);
        company.createTable(s);
        affiliation.createTable(s);
        importationLogError.createTable(s);
    }

    private static void deleteTables(Statement s) throws SQLException {
        Exclusion.dropTable(s);
        importationLogError.dropTable(s);
        affiliation.dropTable(s);
        company.dropTable(s);
        author.dropTable(s);
        Reference.dropTable(s);
        researcher.dropTable(s);
        article.dropTable(s);
        venue.dropTable(s);
        Criteria.dropTable(s);
        digitalLibrary.dropTable(s);
        Project.dropTable(s);
    }

    public static List<importErrorDTO> importar(String idDL, int idProject, MultipartFile file) throws SQLException, IOException, ParseException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        Statement s = conn.createStatement();
        Timestamp t = article.importar(idDL, idProject, s, file);
        List<importErrorDTO> r =  importationLogError.getErrors(s,t);
        conn.commit();
        return r;
    }

    public static List<importErrorDTO> getAllErrors() throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        Statement s = conn.createStatement();
        List<importErrorDTO> r = importationLogError.getAllErrors(s);
        conn.commit();
        return r;
    }

    public static referenceDTO getReference(int idR) {
        referenceDTO r = null;

        Connection conn;
        Statement s;
        try {
            ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
            conn = ctx.getBean(Connection.class);
            s = conn.createStatement();
            referenceDTO NewRef = find(idR,s);

            String doiR = NewRef.getDoi();
            int dlR = NewRef.getidDL();

            obtainReferenceDTO(conn, NewRef, doiR, dlR);
            r = NewRef;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return r;
    }

    private static void obtainReferenceDTO(Connection conn, referenceDTO newRef, String doiR, int dlR) throws SQLException {
        Statement s2 = conn.createStatement();
        ResultSet rsDL = digitalLibrary.getdigitalLibrary(s2,dlR);
        digitalLibraryDTO dl = null;

        if(rsDL.next())
            dl = new digitalLibraryDTO(rsDL.getInt(1),rsDL.getString(2),
                    rsDL.getString(3),rsDL.getInt(4));
        newRef.setDl(dl);

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
            List<companyDTO> c = new ArrayList<>();
            companyDTO auxC;
            while (rsAr.next()) {
                auxC = new companyDTO(rsAr.getInt(1), rsAr.getString(2));
                c.add(auxC);
            }
            ar.setCompanies(c);

            rsAr = researcher.getResearchers(s3,doiR);
            List<researcherDTO> rss = new ArrayList<>();
            researcherDTO auxR;
            while (rsAr.next()) {
                auxR = new researcherDTO(rsAr.getInt(1), rsAr.getString(2));
                rss.add(auxR);
            }
            ar.setResearchers(rss);
        }
        newRef.setArt(ar);
    }

    private static referenceDTO find(int idR, Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM referencias where idRef=" + idR);
        rs.next();
        referenceDTO referenceDTO = new referenceDTO(rs.getInt("idRef"), rs.getString("doi"),
                rs.getInt("idDL"), rs.getInt("idProject"), rs.getString("state"), null);
        List<ExclusionDTO> exclusionDTOList = Exclusion.getByIdRef(s, idR);
        referenceDTO.setExclusionDTOList(exclusionDTOList);
        return referenceDTO;
    }

    public static int getDL(String doi,Statement s) throws SQLException {
        ResultSet r = s.executeQuery("select idDL from referencias where doi = '" + doi + "'");
        r.next();
        return r.getInt(1);
    }

    static ResultSet isDuplicate(Statement s, int priorityImportado, String doi) {
        String query = "select * from REFERENCIAS r, DIGITALLIBRARIES dl where r.doi = ? and r.idDL = dl.idDL and dl.priority <= ?";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, doi);
            preparedStatement.setInt(2, priorityImportado);
            ResultSet resultSet = preparedStatement.executeQuery();
            conn.commit();
            return resultSet;
        }
        catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
        return null;
    }
    static void updateEstateReferences(Statement s, String doi) {
        String query = "update referencias set state = 'out' where doi = ? and state is null";
        try {
            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, doi);
            preparedStatement.execute();
            query = "select idRef from referencias where doi = ? and state = 'out'";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, doi);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
                Exclusion.insertRow(s, 1, resultSet.getInt("idRef"));
            else
                System.err.println("Hola");
        }
        catch (SQLException e) {
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static void update(int idRef, String estado) {
        try{
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s = conn.createStatement();
        if ((estado == null || estado.isEmpty()))
            s.execute("update referencias set state = " + null + " WHERE idRef = " + idRef );
        else
            s.execute("update referencias set state = '" + estado + "' WHERE idRef = " + idRef );
        } catch (SQLException e) {
            System.out.println("Error en update estado y criteria de una reference");
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
