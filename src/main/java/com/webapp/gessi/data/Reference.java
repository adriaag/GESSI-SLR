package com.webapp.gessi.data;

import com.webapp.gessi.config.DBConnection;
import com.webapp.gessi.domain.dto.*;
import com.webapp.gessi.exceptions.BadBibtexFileException;
import com.webapp.gessi.exceptions.TruncationException;

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
	
	private static final int doiMaxLength = 100;

    public static int insertRow(Statement s, String doi, String idDL, int idProject) throws SQLException {
    	String getIdProjRefAntQuery = "SELECT max(idProjRef) FROM referencias where idProject = ?";
        String query = "INSERT INTO referencias(doi, idDL, idProject, idProjRef) VALUES (?, ?, ?, ?)";
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
        preparedStatement.setInt(3, idProject);
        preparedStatement.setInt(4, idProjRef);
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
        if (rs.next()) {
        	int idRef = rs.getInt("idRef");
        	userDesignation.insertRow(s, "None" , idRef, 2);
        	return idRef;
        }
        	
        return -2;
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table referencias(" +
                    "idRef INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
                    "doi varchar(100), idDL INT, idProject INT, " +
                    "idProjRef INT NOT NULL, "+
                    "ConsensusCriteriaProcessed BOOLEAN DEFAULT false, " +
                    "PRIMARY KEY(idRef), unique(doi, idDL, idProject), unique(idProjRef,idProject), " +
                    "CONSTRAINT DL_FK_R FOREIGN KEY (idDL) REFERENCES digitalLibraries (idDL) ON DELETE CASCADE," +
                    "CONSTRAINT AR_FK_R FOREIGN KEY (doi) REFERENCES articles (doi) ON DELETE CASCADE," +
                    "CONSTRAINT PR_FK_R FOREIGN KEY (idProject) REFERENCES project (id) ON DELETE CASCADE)");
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
            System.out.println(sqlException.getCause());
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
            int idProjRef = rs.getInt(5);
            boolean processed = rs.getBoolean(6);
            consensusCriteriaDTO exclusionDTOList = null;
            Statement s1 = conn.createStatement();
            exclusionDTOList = consensusCriteria.getByIdRef(s1, idR);
            
            userDesignationDTO usersCriteria1 = userDesignation.getByIdRefNumDes(s,idR, 1);
            userDesignationDTO usersCriteria2 = userDesignation.getByIdRefNumDes(s,idR, 2);
            referenceDTO NewRef = new referenceDTO( idR, doiR, dlR, idProject, idProjRef, exclusionDTOList, usersCriteria1, usersCriteria2, processed);
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
        consensusCriteria.createTable(s);
        author.createTable(s);
        company.createTable(s);
        affiliation.createTable(s);
        importationLogError.createTable(s);
        user.createTable(s);
        userDesignation.createTable(s);
        userDesignationICEC.createTable(s);
        projectUserInvolve.createTable(s);
        projectDigitalLibrary.createTable(s);
    }

    private static void deleteTables(Statement s) throws SQLException {
        projectUserInvolve.dropTable(s);
        projectDigitalLibrary.dropTable(s);
    	userDesignationICEC.dropTable(s);
        userDesignation.dropTable(s);
        user.dropTable(s);   	
        importationLogError.dropTable(s);
        affiliation.dropTable(s);
        company.dropTable(s);
        author.dropTable(s);
        consensusCriteria.dropTable(s);
        researcher.dropTable(s);
        Reference.dropTable(s);
        article.dropTable(s);
        venue.dropTable(s);
        Criteria.dropTable(s); 
        Project.dropTable(s);
        digitalLibrary.dropTable(s);

        
        
        
        
        
        
       
        

    }

    public static List<importErrorDTO> importar(String idDL, ProjectDTO project, MultipartFile file) throws SQLException, IOException, BadBibtexFileException, NumberFormatException, TruncationException {
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
        articleDTO ar = article.obtainArticleDTO(s2, doiR);
        newRef.setArt(ar);
    }

    private static referenceDTO find(int idR, Connection conn) throws SQLException {
        String query = "SELECT * FROM referencias where idRef = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idR);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        referenceDTO referenceDTO = new referenceDTO(resultSet.getInt("idRef"), resultSet.getString("doi"),
                resultSet.getInt("idDL"), resultSet.getInt("idProject"), resultSet.getInt("idProjRef"),null, null, null, resultSet.getBoolean("consensusCriteriaProcessed"));
        consensusCriteriaDTO exclusionDTOList = consensusCriteria.getByIdRef(conn.createStatement(), idR);
        referenceDTO.setConsensusCriteria(exclusionDTOList);
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
    
    public static void updateEstateReferences(Statement s, int idRef, int idDuplicateCriteria) throws SQLException {
        setProcessed(s, idRef);
        consensusCriteria.insertRow(s, idDuplicateCriteria, idRef);
    }

    public static referenceDTO addReferenceManually(Statement s, referenceDTOadd referenceData, int idProject) throws SQLException {
    	article.insertRowManually(s, referenceData);
    	int idRef = insertRow(s, referenceData.getDoi(), "-1", idProject);  
    	System.out.println(idRef);
    	return getReference(s.getConnection(), idRef);
    }
    
    public static void setProcessed(Statement s, int idRef) throws SQLException {
    	String query = "update referencias set ConsensusCriteriaProcessed = true where IDREF = ?";

        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idRef);
        preparedStatement.execute();
        conn.commit(); 	
    }

    public static void setUnprocessed(Statement s, int idRef) throws SQLException {
    	String query = "update referencias set ConsensusCriteriaProcessed = false where IDREF = ?";

        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, idRef);
        preparedStatement.execute();
        conn.commit(); 	
    }
    
    public static boolean getProcessed(Statement s, int idRef) throws SQLException {
        	String query = "select ConsensusCriteriaProcessed from referencias where IDREF = ?";

            Connection conn = s.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, idRef);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rs.getBoolean("ConsensusCriteriaProcessed");
            
            
    }
    
    public static String getState(Statement s, int idRef) throws SQLException {
    	userDesignationDTO ud1 = userDesignation.getByIdRefNumDes(s, idRef, 1);
    	userDesignationDTO ud2 = userDesignation.getByIdRefNumDes(s, idRef, 1);
    	if(ud1 != null && ud2 != null && ud1.getProcessed() && ud2.getProcessed()) {
    		if(ud1.getCriteriaList().equals(ud2.getCriteriaList())) {
    			if(ud1.getCriteriaList().isEmpty()) return "in";
    			else return "out";
    		}
    		else {
    			if(getProcessed(s,idRef)) {
    				consensusCriteriaDTO cc = consensusCriteria.getByIdRef(s, idRef);
    				if (cc.getIdICEC().isEmpty()) return "in";
    				else return "out";
    			}
    		}
    	}
    	return "";
    }

    
    public static String truncate(String text, int maxValue) {
    	if (text.length() > maxValue) {
        	text = text.substring(0, maxValue - 1);	
        }
    	return text;
    }
}
