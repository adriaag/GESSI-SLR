package com.webapp.gessi.data;

import com.webapp.gessi.domain.dto.ProjectDTO;
import com.webapp.gessi.exceptions.BadBibtexFileException;
import org.apache.commons.io.IOUtils;
import com.github.adriaag.jbibtex.BibTeXDatabase;
import com.github.adriaag.jbibtex.BibTeXParser;
import com.github.adriaag.jbibtex.Key;
import com.github.adriaag.jbibtex.ParseException;
import com.github.adriaag.jbibtex.TokenMgrException;
import com.github.adriaag.jbibtex.ObjectResolutionException;
import com.github.adriaag.jbibtex.BibTeXEntry;
import com.github.adriaag.jbibtex.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class article {

    private static String authorsToInsert = null;
    private static String affiliationToInsert = null;
    private static int referencesImported = 0;
    private static int referencesDuplicated = 0;

    static Key abstractKey = new Key("abstract");
    static Key keywordsKey = new Key("keywords");
    static Key numpagesKey = new Key("numpages");
    static Key articleKey = new Key("article");
    static Key affiliationKey = new Key("affiliation");
    
    private static final int doiMaxLength = 50;
    private static final int typeMaxLength = 50;
    private static final int citeKeyMaxLength = 50;
    private static final int titleMaxLength = 200;
    private static final int keywordsMaxLength = 1000;
    private static final int numberMaxLength = 10;
    private static final int numpagesMaxValue = Integer.MAX_VALUE;
    private static final int pagesMaxLength = 20;
    private static final int volumeMaxLength = 20;
    private static final int anyMaxValue = Integer.MAX_VALUE;
    private static final int abstractMaxLength = 6000;
    

    public static Timestamp importar(String idDL, ProjectDTO project, Statement s, MultipartFile file) throws SQLException, IOException, BadBibtexFileException{

        //Reader reader = new FileReader(path);
        //Parametro MultipartFile file
        ByteArrayInputStream stream0 = new ByteArrayInputStream(file.getBytes());
        String myString = IOUtils.toString(stream0, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(myString.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        
        Timestamp time = new Timestamp(new java.util.Date().getTime());
        String doi = "";
        //Timestamp time = iniCheck(s,idDL,file);
        
        try {
        	BibTeXParser bibtexParser = new BibTeXParser(); //add Exception
        	BibTeXDatabase database = bibtexParser.parse(reader);
        	Map<Key, BibTeXEntry> entryMap = database.getEntries();
            Collection<BibTeXEntry> entries = entryMap.values();
            
            // add rows of file
            //Buscar prioridad de la idDL de la que estoy importando al inicio de la importacion
            int entriesPriority = digitalLibrary.getPriority(s,idDL);
            //if(!entries.isEmpty()) referencesImported = entries.size();
            for(BibTeXEntry entry : entries){
                System.out.println("Cite key:" + entry.getKey());
                doi = addArticle(idDL, project, s, entry, entriesPriority);
                if (!doi.contains("ERROR")) {
                    if (authorsToInsert != null) {
                        Integer[] idsResearchers = researcher.insertRows(authorsToInsert, s);
                        s.getConnection().commit();
                        author.insertRows(idsResearchers, doi, s);
                    }
                    if (affiliationToInsert !=  null) {
                        Integer[] idsComps = company.insertRows(s, affiliationToInsert);
                        s.getConnection().commit();
                        affiliation.insertRows(s, idsComps, doi);
                    }
                }
                else System.out.println(doi);
            }
            reader.close();
        }
        catch (ParseException | TokenMgrException | ObjectResolutionException e) {
        	//importationLogError.insertRow(s, doi, myString, idDL, project.getId(), time);
        	System.err.println("  Error d'importació");
            System.err.println("  Message:    " + e.getMessage());
            e.printStackTrace();
            throw new BadBibtexFileException(e.getMessage(), e);
        }
        catch (SQLException e) {
        	importationLogError.insertRow(s, doi, myString, idDL, project.getId(), time);
        	System.err.println("  Error d'importació");
            System.err.println("  Message:    " + e.getMessage());
            e.printStackTrace();
        }
        
        return time;
    }

    //Guarda las referencias que no se pueden guardar en la BD
    public static Timestamp iniCheck(Statement sta, String idDL, MultipartFile file) throws IOException {
        ByteArrayInputStream stream0 = new ByteArrayInputStream(file.getBytes());
        String myString = IOUtils.toString(stream0, StandardCharsets.UTF_8);
        ByteArrayInputStream stream = new ByteArrayInputStream(myString.getBytes(StandardCharsets.UTF_8));

        Scanner sc = new Scanner(stream);
        sc.useDelimiter("\\@");
        ArrayList<String> list = new ArrayList<String>();

        Pattern patternKey = Pattern.compile("\\{(.*),");
        Pattern patternDOI = Pattern.compile("doi=(.*)\\}");
        Pattern patternDOI2 = Pattern.compile("doi =(.*)\\}");
        Pattern patternDOI3 = Pattern.compile("DOI =(.*)\\}");

        Timestamp timesql = new Timestamp(new java.util.Date().getTime());
        while(sc.hasNext()) {
            String data = sc.next();
            String doi = null;

            Matcher doiM = patternDOI.matcher(data);
            Matcher doiM2 = patternDOI2.matcher(data);
            Matcher doiM3 = patternDOI3.matcher(data);
            if (doiM.find()) {
                doi = doiM.group(1).replaceAll("\\{", "")
                        .replaceAll(",", "").replaceAll("=", "");
            } else if (doiM2.find()) {
                doi = doiM2.group(1).replaceAll("\\{", "")
                        .replaceAll(",", "").replaceAll("=", "");
            } else if (doiM3.find()) {
                doi = doiM3.group(1).replaceAll("\\{", "")
                        .replaceAll(",", "").replaceAll("=", "");
            }
            Matcher bibM = patternKey.matcher(data);
            if(bibM.find()) {
                String citeKey = bibM.group(1).replaceAll("\\{", "").replaceAll(",", "");
                list.add(citeKey);
            }
        }
        return timesql;
    }

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM articles");
        return rs;
    }

    public static ResultSet getArticle(Statement s, String doi) throws SQLException {
        Connection conn = s.getConnection();
        String query = "SELECT * FROM articles where doi = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setString(1, doi);
        preparedStatement.execute();
        return preparedStatement.getResultSet();
    }

//Devuelve un string de todos los autores de la referencia
    static String addArticle(String idDL, ProjectDTO project, Statement s, BibTeXEntry entry, int entriesPriority) throws SQLException {
        String doi;
        if (entry.getField(BibTeXEntry.KEY_DOI) == null) {
            String str = entry.getKey().toString();
            if (str.contains("http")) {
                String[] arrOfStr = str.split("/");
                StringBuilder doiParts = new StringBuilder();
                for (int i = 3; i < arrOfStr.length; i++) doiParts.append(arrOfStr[i]);
                doi = doiParts.toString();
            }
            else doi = entry.getKey().toString();
        }
        else { //doi no es null
            String str = entry.getField(BibTeXEntry.KEY_DOI).toUserString();
            if(str.contains("http")) {
                String[] arrOfStr = str.split("/");
                StringBuilder doiParts = new StringBuilder();
                for (int i = 3; i < arrOfStr.length; i++) doiParts.append(arrOfStr[i]);
                doi = doiParts.toString();
            }
            else doi = entry.getField(BibTeXEntry.KEY_DOI).toUserString();
        }

        if (doi != null) {
            //doi = doi.replaceAll("[{-}]", "");//.replaceAll("'", "''");
            ResultSet rs = getArticle(s, doi);
            String estado = null;
            int apCriteria = 0;
            if (rs.next()) {
                updateRow(rs, entry, s, doi); //añadir informacion en los valores null del article
                ResultSet duplicate = Reference.isDuplicate(s, doi, project.getId());
                if (duplicate == null)
                    System.err.println("Error Duplicate");
                else {
                	boolean duplicatedLibrary = false;
                	int priority = 1000;
                	int idRefMaxPriority = -1;
                	
                	while (duplicate.next()) {            		
                		if (duplicate.getInt("priority") < priority && (duplicate.getString("state") == null || !duplicate.getString("state").equals("out"))) {
                			priority = duplicate.getInt("priority");
                			idRefMaxPriority = duplicate.getInt("idRef");
                			
                		}                		
                		if (duplicate.getInt("priority") == entriesPriority) {
                			duplicatedLibrary = true;//assumim que no hi 2 DLs amb la mateix prioritat               		
                		}
                		
                	}
                	
                	System.out.println(priority);

                	if (!duplicatedLibrary && priority < 1000) {
	                    if (entriesPriority > priority) {
	                        estado = "out";
	                        apCriteria = project.getIdDuplicateCriteria();
	                        
	                        int idRef = Reference.insertRow(s, doi, idDL, estado, project.getId());
	                        if (idRef == -2) return "ERROR: The reference had problems";
	                        referencesImported += 1;
	                        
		                    Exclusion.insertRow(s, apCriteria, idRef);
	                        
	                    }
	                    else {	                  
	                        Reference.updateEstateReferences(s, idRefMaxPriority, project.getIdDuplicateCriteria());
	                    	int idRef = Reference.insertRow(s, doi, idDL, estado, project.getId());
	                    	if (idRef == -2) return "ERROR: The reference had problems";
	                    	referencesImported += 1;	
	                    }
                	}               	
	                else {
	                	if(duplicatedLibrary) referencesDuplicated += 1;	
	                	else {
	                		int idRef = Reference.insertRow(s, doi, idDL, estado, project.getId());
	                        referencesImported += 1;
	                        if (idRef == -2) return "ERROR: The reference had problems";                		
	                	}
	                }
	            }                
            }
            else {
                insertRow(s, entry, doi);                                       //create article nuevo
                int idRef = Reference.insertRow(s, doi, idDL, estado, project.getId());
                referencesImported += 1;
                if (idRef == -2) return "ERROR: The reference had problems";
            }

                
            return doi;
        }
        else
            return "ERROR: The article does not contain doi or citeKey";
    }

    private static void insertRow(Statement s, BibTeXEntry entry, String doi) throws SQLException {
        String query = "INSERT INTO articles(DOI, TYPE, CITEKEY, IDVEN, TITLE, KEYWORDS, NUMBER, NUMPAGES, PAGES, VOLUME, AÑO, ABSTRACT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = s.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        Key type = entry.getType();
        Value title = entry.getField(BibTeXEntry.KEY_TITLE);
        Value authors = entry.getField(BibTeXEntry.KEY_AUTHOR);
        Value year = entry.getField(BibTeXEntry.KEY_YEAR);
        Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
        Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
        Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
        Value pages = entry.getField(BibTeXEntry.KEY_PAGES);
        Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
        Value abstractE = entry.getField(abstractKey);
        Value keywords = entry.getField(keywordsKey);
        Value numpages = entry.getField(numpagesKey);
        Value article = entry.getField(articleKey);
        Value affil = entry.getField(affiliationKey);
        
        preparedStatement.setString(1, truncate(doi,doiMaxLength));
        preparedStatement.setString(2, truncate(String.valueOf(type), typeMaxLength));
        preparedStatement.setString(3, truncate(entry.getKey().toString(), citeKeyMaxLength));

        if (booktitle != null || article != null || journal != null) {
            String ven;
            if (booktitle != null) ven = booktitle.toUserString().replaceAll("[{-}]", "");//.replaceAll("'", "''");
            else if (journal != null) ven = journal.toUserString().replaceAll("[{-}]", "");//.replaceAll("'", "''");
            else ven = article.toUserString().replaceAll("[{-}]", "");//.replaceAll("'", "''");
            int idVen = venue.insertRow(s, ven);
            preparedStatement.setInt(4, idVen);
        }
        else {
            preparedStatement.setNull(4, java.sql.Types.INTEGER);
        }

        if (title != null) {
            preparedStatement.setString(5, truncate(title.toUserString(), titleMaxLength));
        } else {
            preparedStatement.setString(5, null);
        }

        if (keywords != null) {
            preparedStatement.setString(6, truncate(keywords.toUserString(), keywordsMaxLength));
        } else {
            preparedStatement.setString(6, null);
        }

        if (number != null && !number.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''").equals("")) {
            preparedStatement.setString(7, truncate(number.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''"),numberMaxLength));
        } else {
            preparedStatement.setString(7, null);
        }

        if (numpages != null) {
            preparedStatement.setInt(8, Integer.parseInt(numpages.toUserString().replaceAll("[{-}]", "")));//.replaceAll("'", "''"));
        } else {
            preparedStatement.setNull(8, java.sql.Types.INTEGER);
        }

        if (pages != null) {
            preparedStatement.setString(9, truncate(pages.toUserString(), pagesMaxLength));
        } else {
            preparedStatement.setString(9, null);
        }

        if (volume != null && !volume.toUserString().equals("")) {
            preparedStatement.setString(10, truncate(volume.toUserString(), volumeMaxLength));
        } else {
            preparedStatement.setString(10, null);
        }

        if (year != null) {
            preparedStatement.setInt(11, Integer.parseInt(year.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")));
        } else {
            preparedStatement.setNull(11, java.sql.Types.INTEGER);
        }

        if (abstractE != null) {
            preparedStatement.setString(12, truncate(abstractE.toUserString().replaceAll("[']", ""), abstractMaxLength));
        } else {
            preparedStatement.setString(12, null);
        }

        authorsToInsert = null;
        if (authors != null)
            authorsToInsert = authors.toUserString().replaceAll("[\n]", " ").replaceAll("[{-}]", "");//.replaceAll("'", "''");

        affiliationToInsert = null;
        if (affil != null) {
            affiliationToInsert = affil.toUserString().replaceAll("[{-}]", "").replaceAll("[']", "");//.replaceAll("'", "''");
        }

        preparedStatement.execute();
        System.out.println("Inserted row with author, doi, ....");
    }

    private static void updateRow(ResultSet rs, BibTeXEntry entry, Statement s, String doi) throws SQLException  {
        /*UPDATE Articles
        SET  keywords= 'computer science, software', volume= 15
        WHERE doi = '10.18178/ijiet.2020.10.10.1455' ;
        */
        Value title = entry.getField(BibTeXEntry.KEY_TITLE);
        Value authors = entry.getField(BibTeXEntry.KEY_AUTHOR);
        Value year = entry.getField(BibTeXEntry.KEY_YEAR);
        Value booktitle = entry.getField(BibTeXEntry.KEY_BOOKTITLE);
        Value journal = entry.getField(BibTeXEntry.KEY_JOURNAL);
        Value number = entry.getField(BibTeXEntry.KEY_NUMBER);
        Value pages = entry.getField(BibTeXEntry.KEY_PAGES);
        Value volume = entry.getField(BibTeXEntry.KEY_VOLUME);
        Value abstractE = entry.getField(abstractKey);
        Value keywords = entry.getField(keywordsKey);
        Value numpages = entry.getField(numpagesKey);
        Value article = entry.getField(articleKey);
        Value affil = entry.getField(affiliationKey);

        String query;
        StringBuilder queryIni = new StringBuilder("UPDATE Articles SET ");
        StringBuilder queryEnd = new StringBuilder(" WHERE doi = '" + doi + "'");
        boolean first = true;

       // "idVen int, title varchar(200), journal varchar(100), keywords varchar(1000) number INT, numpages INT, pages varchar(20), volume INT, año INT, abstract "

        if ((rs.getString(4) == null ) & (booktitle != null || article != null || (journal != null))) {
            String ven;
            if (booktitle != null) ven = booktitle.toUserString().replaceAll("[{-}]", "");//.replaceAll("'", "''");
            if (journal != null) ven = journal.toUserString().replaceAll("[{-}]", "");//.replaceAll("'", "''");
            else ven = article.toUserString().replaceAll("[{-}]", "");//.replaceAll("'", "''");
            int idVen = venue.insertRow(s, ven);
            first = false;
            queryIni.append(" idVen = ").append(idVen);
        }
        if ((rs.getString(5) == null ) & (title != null)) {
            if (first) first = false;
            else queryIni.append(", ");
            queryIni.append(" title = '").append(title.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
        }

        if ((rs.getString(6) == null ) & (keywords != null)) {
            if (first) first = false;
            else queryIni.append(", ");
            queryIni.append(" keywords = '").append(keywords.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
        }
        if ((rs.getString(7) == null ) & (number != null )) {
            if (!number.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''").equals("")){
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" number = '").append(number.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
            }
        }
        if ((rs.getString(8) == null ) & (numpages != null)) {
            if (first) first = false;
            else queryIni.append(", ");
            queryIni.append(" numpages = ").append(numpages.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''"));
        }
        if ((rs.getString(9) == null ) & (pages != null)) {
            if (first) first = false;
            else queryIni.append(", ");
            queryIni.append(" pages = '").append(pages.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
        }
        if ((rs.getString(10) == null) & (volume != null )) {
            if (!volume.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''").equals("")) {
                if (first) first = false;
                else queryIni.append(", ");
                queryIni.append(" volume = '").append(volume.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
            }
        }
        if ((rs.getString(11) == null ) &(year != null)) {
            if (first) first = false;
            else queryIni.append(", ");
            queryIni.append(" año = ").append(year.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''"));
        }
        if ((rs.getString(12) == null ) & (abstractE != null)) {
            if (first) first = false;
            else queryIni.append(", ");
            String aux1 = abstractE.toUserString().replaceAll("[']", "").replaceAll("'", "''");
            //El simbolo ' dentro del abstract provoca errores
            queryIni.append(" abstract = \'").append(aux1.replaceAll("[{-}]", "").replaceAll("'", "''")).append("\'");
        }
        authorsToInsert = null;
        if (authors != null)
            authorsToInsert = authors.toUserString().replaceAll("[\n]", " ").replaceAll("[{-}]", "").replaceAll("'", "''");

        affiliationToInsert = null;
        if (affil != null) {
            affiliationToInsert = affil.toUserString().replaceAll("[{-}]", "").replaceAll("[']", "").replaceAll("'", "''");
        }
        query = queryIni.toString() + queryEnd;

        if (!first) {
            //System.out.println(query );
            s.execute(query);
        }
        System.out.println("Inserted row with author, doi, ....");
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table articles( doi varchar(50), type varchar(50), citeKey varchar(50), " +
                    "idVen int, title varchar(200), keywords varchar(1000), number varchar(10), numpages INT, " +
                    "pages varchar(20), volume varchar(20), año INT, abstract varchar(6000)," +
                    "PRIMARY KEY (doi), " +
                    "CONSTRAINT VEN_FK_R FOREIGN KEY (idVen) REFERENCES venues (idVen) ON DELETE CASCADE)");
            //type y citekey not null
            System.out.println("Created table articles");
        } catch (SQLException e  ) {
            if (e.getSQLState().equals("X0Y32"))
                System.out.println("Table articles exists");
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public static void dropTable(Statement s) {
        try {
            s.execute("drop table articles");
            System.out.println("Dropped table articles");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla articles not exist");
        }
    }

    public static int getReferencesImported() {
        return referencesImported;
    }

    public static void setReferencesImported(int i) {
        referencesImported = i;
    }
    
    public static int getReferencesDuplicated() {
        return referencesDuplicated;
    }

    public static void setReferencesDuplicated(int i) {
        referencesDuplicated = i;
    }
    
    private static String truncate(String text, int maxValue) {
    	if (text.length() > maxValue) {
        	text = text.substring(0, maxValue - 1);	
        }
    	return text;
    }
    
    private static int max(int a, int b) {
    	return (a > b ? a : b);
    }
}
