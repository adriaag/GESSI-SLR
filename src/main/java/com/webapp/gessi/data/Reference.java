package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.*;
import com.webapp.gessi.exceptions.BadBibtexFileException;

import org.jbibtex.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reference {
	
	private static final int doiMaxLength = 50;

    public static int insertRow(Statement s, String doi, String idDL, String estado, int idProject) throws SQLException {
    	String getIdProjRefAntQuery = "SELECT max(idProjRef) FROM referencias where idProject = ?";
        String query = "INSERT INTO referencias(doi, idDL, state, idProject, idProjRef) VALUES (?, ?, ?, ?, ?)";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(getIdProjRefAntQuery);
        preparedStatement.setInt(1,idProject);
        preparedStatement.execute();
        ResultSet getIdProjRefAntRS = preparedStatement.getResultSet();
        int idProjRef = 1;
        if(getIdProjRefAntRS.next()) {
        	idProjRef = getIdProjRefAntRS.getInt(1) + 1;
        }
        System.out.println("IdProjRef: "+ idProjRef);
        
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(doi, doiMaxLength));
        if (idDL != null) preparedStatement.setString(2, idDL);
        else preparedStatement.setNull(2, java.sql.Types.VARCHAR);
        if (estado != null) preparedStatement.setString(3, estado);
        else preparedStatement.setNull(3, java.sql.Types.VARCHAR);
        preparedStatement.setInt(4, idProject);
        preparedStatement.setInt(5, idProjRef);
        preparedStatement.execute();
        System.out.println("Inserted row with doi "+ doi +", idDL.. in referencias");
        conn.commit();
        
        if (idDL != null) query = "SELECT idRef FROM referencias where doi = ? and idProject = ? and idDL = ?";
        else query = "SELECT idRef FROM referencias where doi = ? and idProject = ? and idDL is null";
        preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(doi, doiMaxLength));
        preparedStatement.setInt(2, idProject);
        if (idDL != null) preparedStatement.setString(3, idDL);
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        if (rs.next())
            return rs.getInt("idRef");
        return -2;
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table referencias(" +
                    "idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "doi varchar(50), idDL INT, state VARCHAR(10), idProject INT, " +
                    "idProjRef INT NOT NULL, "+
                    "PRIMARY KEY(idRef), unique(doi, idDL, idProject), unique(idProjRef,idProject), " +
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
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
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
        String query = "SELECT * FROM REFERENCIAS WHERE IDPROJECT = ?";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idProject);
        return preparedStatement.executeQuery();
    }

    public static List<referenceDTO> getAllReferences(int idProject) throws SQLException {
        List<referenceDTO> refList = null;
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        Statement s;
        
        conn.setAutoCommit(false);
        s = conn.createStatement();
        ResultSet rs = getAll(s, idProject);
        int number = 1;
        refList = new ArrayList<>();
        while(rs.next()) {
            //System.out.println(number++);

            int idR = rs.getInt(1);
            String doiR = rs.getString(2);
            int dlR = rs.getInt(3);
            String estado = rs.getString(4);
            int idProjRef = rs.getInt(6);
            List<ExclusionDTO> exclusionDTOList = null;
            if (Objects.equals(estado, "out")) {
                Statement s1 = conn.createStatement();
                exclusionDTOList = Exclusion.getByIdRef(s1, idR);
            }
            referenceDTO NewRef = new referenceDTO( idR, doiR, dlR, idProject, estado, idProjRef, exclusionDTOList);
            obtainReferenceDTO(conn, NewRef, doiR, dlR);

            refList.add(NewRef);
        }
        conn.commit();
            
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
    
    public static void delete(int id) throws SQLException {
        String query = "DELETE FROM REFERENCIAS WHERE IDREF = ?";
        
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.execute();
        conn.commit();
        System.out.println("deleted row in reference");
    }


    private static void crearTablas(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        // Create table digitalLibraries if not exist
        if (digitalLibrary.createTable(s))
            //insert rows in table
            digitalLibrary.insertRows(conn, statements);
        venue.createTable(s);
        Project.createTable(s);
        article.createTable(s);
        researcher.createTable(s);
        Criteria.createTable(s);
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

    public static List<importErrorDTO> importar(String idDL, ProjectDTO project, MultipartFile file) throws SQLException, IOException, BadBibtexFileException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        Statement s = conn.createStatement();
        Timestamp t = article.importar(idDL, project, s, file);
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
    
    public static List<importErrorDTO> getErrors(int idProject) throws SQLException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
        Connection conn = ctx.getBean(Connection.class);
        conn.setAutoCommit(false);
        Statement s = conn.createStatement();
        List<importErrorDTO> r = importationLogError.getErrorsFromProject(s,idProject);
        conn.commit();
        return r;
    }

    public static referenceDTO getReference(Connection conn, int idR) throws SQLException {
        referenceDTO r = null;

        referenceDTO NewRef = find(idR, conn);

        String doiR = NewRef.getDoi();
        int dlR = NewRef.getidDL();

        obtainReferenceDTO(conn, NewRef, doiR, dlR);
        r = NewRef;

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

    private static referenceDTO find(int idR, Connection conn) throws SQLException {
        String query = "SELECT * FROM referencias where idRef = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idR);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        referenceDTO referenceDTO = new referenceDTO(resultSet.getInt("idRef"), resultSet.getString("doi"),
                resultSet.getInt("idDL"), resultSet.getInt("idProject"), resultSet.getString("state"), resultSet.getInt("idProjRef"),null);
        List<ExclusionDTO> exclusionDTOList = Exclusion.getByIdRef(conn.createStatement(), idR);
        referenceDTO.setExclusionDTOList(exclusionDTOList);
        return referenceDTO;
    }

    public static int getDL(String doi,Statement s) throws SQLException {
    	String query = "select idDL from referencias where doi = ?";
    	Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, doi);
        preparedStatement.execute();
        ResultSet r = preparedStatement.getResultSet();
        r.next();
        return r.getInt(1);
    }

    static ResultSet isDuplicate(Statement s, String doi, int idProject) throws SQLException {
        String query = "select * from REFERENCIAS r, DIGITALLIBRARIES dl where r.DOI = ? and r.IDDL = dl.IDDL and IDPROJECT = ?";
        
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, truncate(doi, doiMaxLength));
        preparedStatement.setInt(2, idProject);
        ResultSet resultSet = preparedStatement.executeQuery();
        conn.commit();
        return resultSet;
    }
    static void updateEstateReferences(Statement s, int idRef, int idDuplicateCriteria) throws SQLException {
        String query = "update referencias set state = 'out' where IDREF = ?";

        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idRef);
        preparedStatement.execute();
        conn.commit();
        Exclusion.insertRow(s, idDuplicateCriteria, idRef);
    }

    public static void update(int idRef, String estado) throws SQLException {
    	
    ApplicationContext ctx = new AnnotationConfigApplicationContext(DBConnection.class);
    Connection conn = ctx.getBean(Connection.class);
    Statement s = conn.createStatement();
    
    String query = "update referencias set state = ? WHERE idRef = ?";
    conn = s.getConnection();
    PreparedStatement preparedStatement = conn.prepareStatement(query);
    preparedStatement.setInt(2, idRef);
    
    if ((estado == null || estado.isEmpty()))
    	preparedStatement.setString(1, null);
    else
    	preparedStatement.setString(1, estado);
    preparedStatement.execute();
    conn.commit();
    }
    
    public static referenceDTO addReferenceManually(Statement s, referenceDTOadd referenceData, int idProject) throws SQLException {
    	article.insertRowManually(s, referenceData);
    	int idRef = insertRow(s, referenceData.getDoi(), "-1", null, idProject);  
    	System.out.println(idRef);
    	return getReference(s.getConnection(), idRef);
    }
    
    

    private static List<ExclusionDTO> convertResultSetToExclusionDTO(ResultSet resultSet, List<ExclusionDTO> exclusionDTOList) throws SQLException {
        while (resultSet.next()) {
            exclusionDTOList.add(new ExclusionDTO(resultSet.getInt("idRef"), resultSet.getInt("idICEC"), resultSet.getString("name")));
        }
        return exclusionDTOList;
    }

    private static referenceDTO convertResultSetToReferenceDTO(ResultSet resultSet) throws SQLException {
        List<ExclusionDTO> exclusionDTOList = new ArrayList<>();
        resultSet.next();
        referenceDTO referenceDTO = new referenceDTO(resultSet.getInt("idRef"), resultSet.getString("doi"),
                resultSet.getInt("idDL"), resultSet.getInt("idProject"), resultSet.getString("state"), resultSet.getInt("idProjRef"),null);
        exclusionDTOList.add(new ExclusionDTO(resultSet.getInt("idRef"), resultSet.getInt("idICEC"), resultSet.getString("name")));
        referenceDTO.setExclusionDTOList(convertResultSetToExclusionDTO(resultSet, exclusionDTOList));
        return referenceDTO;
    }
    
    private static String truncate(String text, int maxValue) {
    	if (text.length() > maxValue) {
        	text = text.substring(0, maxValue - 1);	
        }
    	return text;
    }
}
