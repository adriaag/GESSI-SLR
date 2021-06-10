package com.example.tfgdefinitivo.data;

import org.jbibtex.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.tfgdefinitivo.data.digitalLibrary.getPriority;
import static com.example.tfgdefinitivo.data.reference.isDuplicate;
import static com.example.tfgdefinitivo.data.reference.updateEstateReferences;

public class article {

    private static String authorsToInsert = null;
    private static String affiliationToInsert = null;

    static Key abstractKey = new Key("abstract");
    static Key keywordsKey = new Key("keywords");
    static Key numpagesKey = new Key("numpages");
    static Key articleKey = new Key("article");
    static Key affiliationKey = new Key("affiliation");

    public static String[] askInfo(Statement s) throws SQLException {
        System.out.println("Escribir el path absoluto donde se encuentra el fichero a exportar: ");
        Scanner entrada=new Scanner(System.in);
        String path = entrada.nextLine();
        System.out.println("Path escogido: " + path);
        System.out.println("Escoger el número de la biblioteca de donde se exporta el archivo:");

        ArrayList<String> DLs = digitalLibrary.getNames(s);
        String idDL = null;
        for (int i = 0; i < DLs.size(); i++)
            System.out.println(i+1 + ". " + DLs.get(i));
        try {
            int num=entrada.nextInt() - 1;
            if (num >= 0 & num < DLs.size()) {
                idDL = digitalLibrary.getIDs(s).get(num);
                System.out.println("Se ha escogido " + idDL + ". " + DLs.get(num));
            } else {
                System.out.println("El numero no esta entre el 1 y el 6");
            }
        } catch (Exception e) {
            System.out.println("No se ha escrito un número.");
        }
        return new String[] {path, idDL};
    }

    public static void importar(String path, String idDL, Statement s) throws IOException,ParseException,SQLException {
        Reader reader = new FileReader(path);
        BibTeXParser bibtexParser = new BibTeXParser(); //add Exception
        BibTeXDatabase database = bibtexParser.parse(reader);
        Map<Key, BibTeXEntry> entryMap = database.getEntries();
        Collection<BibTeXEntry> entries = entryMap.values();
        iniCheck(path,s,idDL);
        // add rows of file
        //Buscar prioridad de la idDL de la que estoy importando al inicio de la importacion
        int entriesPriority = getPriority(s,idDL);
        for(BibTeXEntry entry : entries){
            System.out.println(entry.getKey());
            String doi = addArticle(idDL, s, entry, entriesPriority);
            if (doi != null) {
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
        }

        reader.close();
    }

    //Guarda las referencias que no se pueden guardar en la BD
    public static void iniCheck(String path, Statement sta, String idDL) throws FileNotFoundException, SQLException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\\@");
        ArrayList<String> list = new ArrayList<>();

        Pattern patternKey = Pattern.compile("\\{(.*),");
        Pattern patternDOI = Pattern.compile("doi=(.*)\\}");
        Pattern patternDOI2 = Pattern.compile("doi =(.*)\\}");
        Pattern patternDOI3 = Pattern.compile("DOI =(.*)\\}");

        Timestamp timesql = new Timestamp(new java.util.Date().getTime());
        System.out.println(timesql);
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
                //Comprobar si hay citekey duplicada
                System.out.println(citeKey);
                if (list.contains(citeKey)) {
                    if (doi != null ) System.out.println("DOI es: " +doi);
                    importationLogError.insertRow(sta, doi, data, idDL, timesql);
                    //clave primaria timestamp(fecha y hora , el mismo para 1 importacion)
                    System.out.println("insert row bc duplicated cite key: " + citeKey);
                    //Podria devolver este error?
                }
                list.add(citeKey);
            }
        }
    }

    public static ResultSet getAllData(Statement s) throws SQLException {
        ResultSet rs;
        rs = s.executeQuery("SELECT * FROM articles");
        return rs;
    }

    public static ResultSet getArticle(Statement s, String doi) throws SQLException {
        return s.executeQuery("SELECT * FROM articles where doi = '" + doi.replaceAll("'", "''") + "' ");
    }

//Devuelve un string de todos los autores de la referencia
    static String addArticle(String idDL, Statement s, BibTeXEntry entry, int entriesPriority) throws SQLException {
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
            doi = doi.replaceAll("[{-}]", "").replaceAll("'", "''");
            ResultSet rs = getArticle(s, doi);
            String estado = null;
            if (rs.next()) {
                System.out.println("El article se actualiza.");
                updateRow(rs,entry,s,doi, idDL); //añadir informacion en los valores null

                //Check estado de la referencia
                int idDLArtFound = reference.getDL(doi,s);
                int priorityArtFound = getPriority(s,String.valueOf(idDLArtFound));

                if(isDuplicate(s,priorityArtFound,doi).next()) {
                    estado = "duplicated";
                }
                else {
                    updateEstateReferences(s,doi);
                }
            }
            else {
                System.out.println("El article se añade.");
                insertRow(s, entry, doi);//create article nuevo
            }
            reference.insertRow(s,doi,idDL,estado);
            return doi;
        }
        else {
            System.out.println("ERROR: Este article no contiene doi ni citeKey");
            return null;
        }
    }

    private static void insertRow(Statement s, BibTeXEntry entry, String doi) {
        try {
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

            String query;
            StringBuilder atributsOfRow = new StringBuilder("INSERT INTO articles(");
            StringBuilder valuesOfRow = new StringBuilder(") VALUES (");

            atributsOfRow.append("doi");
            valuesOfRow.append("'").append(doi).append("'");

            atributsOfRow.append(", type, citeKey");
            valuesOfRow.append(", '").append(type).append("', '").append(entry.getKey().toString().replaceAll("'", "''")).append("'");
            //Citekey puede contener el DOI

            if (booktitle != null || article != null || journal != null) {
                String ven;
                if (booktitle != null) ven = booktitle.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''");
                else if (journal != null) ven = journal.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''");
                else ven = article.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''");
                int idVen = venue.insertRow(s, ven);
                atributsOfRow.append(", idVen");
                valuesOfRow.append(", ").append(idVen);
            }
            if (title != null) {
                atributsOfRow.append(", title");
                valuesOfRow.append(", '").append(title.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
            }
            if (keywords != null) {
                atributsOfRow.append(", keywords");
                valuesOfRow.append(", '").append(keywords.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
            }
            if (number != null ) {
                if (!number.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''").equals("")) {
                    atributsOfRow.append(", number");
                    valuesOfRow.append(", '").append(number.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
                }
            }
            if (numpages != null) {
                atributsOfRow.append(", numpages");
                valuesOfRow.append(", ").append(numpages.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''"));
            }
            if (pages != null) {
                atributsOfRow.append(", pages");
                valuesOfRow.append(", '").append(pages.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
            }
            if (volume != null) {
                if (!volume.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''").equals("")) {
                    atributsOfRow.append(", volume");
                    valuesOfRow.append(", '").append(volume.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''")).append("'");
                }
            }
            if (year != null) {
                atributsOfRow.append(", año");
                valuesOfRow.append(", ").append(year.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''"));
            }
            if (abstractE != null) {
                String aux1 = abstractE.toUserString().replaceAll("[']", "").replaceAll("'", "''");
                //El simbolo ' dentro del abstract provoca errores
                atributsOfRow.append(", abstract");
                valuesOfRow.append(", \'").append(aux1.replaceAll("[{-}]", "").replaceAll("'", "''")).append("\'");
            }
            authorsToInsert = null;
            if (authors != null)
                authorsToInsert = authors.toUserString().replaceAll("[\n]", " ").replaceAll("[{-}]", "").replaceAll("'", "''");

            affiliationToInsert = null;
            if (affil != null) {
                affiliationToInsert = affil.toUserString().replaceAll("[{-}]", "").replaceAll("[']", "").replaceAll("'", "''");
            }
            query = atributsOfRow.toString() + valuesOfRow.append(") ");
            System.out.println(query);

            s.execute(query);
            System.out.println("Inserted row with author, doi, ....");
        } catch (SQLException e) {
            if (e.getSQLState().equals("X0Y32")) {
                System.out.println("El article ya existe");
            }
            else {
                System.out.println("Error");
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

    private static void updateRow(ResultSet rs, BibTeXEntry entry, Statement s, String doi1, String doi)  {
        try {
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
                if (booktitle != null) ven = booktitle.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''");
                if (journal != null) ven = journal.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''");
                else ven = article.toUserString().replaceAll("[{-}]", "").replaceAll("'", "''");
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
                System.out.println(query );
                s.execute(query);
            }
            System.out.println("Inserted row with author, doi, ....");
        } catch (SQLException e) {
            System.out.println("Error");
            while (e != null) {
                System.err.println("\n----- SQLException -----");
                System.err.println("  SQL State:  " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message:    " + e.getMessage());
                e = e.getNextException();
            }

        }
    }

    public static void createTable(Statement s) {
        try {
            s.execute("create table articles( doi varchar(50), type varchar(50), citeKey varchar(50), " +
                    "idVen int, title varchar(200), keywords varchar(1000), " +
                    "number varchar(10), numpages INT, pages varchar(20), volume varchar(20), año INT, abstract varchar(6000), " +
                    "PRIMARY KEY (doi), CONSTRAINT VEN_FK_R FOREIGN KEY (idVen) REFERENCES venues (idVen))");
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
                // for stack traces, refer to derby.log or uncomment this:
                //e.printStackTrace(System.err);
                e = e.getNextException();
            }
        }
    }

    public static void dropTable(Statement s) throws SQLException {
        try{
        s.execute("drop table articles");
        System.out.println("Dropped table articles");
        }
        catch (SQLException sqlException) {
            System.out.println("Tabla articles not exist");
        }
    }

}
