package com.example.tfgdefinitivo;

import com.example.tfgdefinitivo.dao.*;
import org.jbibtex.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class main {

    static String framework = "embedded";
    static String dbName = "derbyDB";
    static String protocol = "jdbc:derby:";
    static Properties props = iniProperties(); // connection properties

    private static Properties iniProperties() {
        Properties props = new Properties();
        props.put("user", "user1");
        props.put("password", "user1");
        return props;
    }

    public static void main(String[] args) throws IOException, ParseException, SQLException {
        ini();
        pruebaReference();
    }

    private static void ini() {
        System.out.println("Program starting in " + framework + " mode");
        Connection conn;
        ArrayList<Statement> statements = new ArrayList<>(); // list of Statements, PreparedStatements
        Statement s;
        try {
            String url = "jdbc:derby:derbyDB;create=true";
            conn = DriverManager.getConnection(url, props );
            System.out.println("Connected to and created database " + dbName);
            conn.setAutoCommit(false);

            // Statement object for running various SQL statements commands against the database.
            s = conn.createStatement();
            statements.add(s);

            crearTablas(s,conn,statements);
            //deleteTables(s,conn,statements);

            conn.commit();
            System.out.println("Committed the transaction");

        } catch (SQLException e){
            System.out.println("Error");
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

    private static void pruebaReference() throws IOException, ParseException, SQLException {

            Connection conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            Statement s = conn.createStatement();
            conn.setAutoCommit(false);
            ResultSet rs;
            String[] aux = articleDao.pedirInfo(s);
            String path = aux[0];
            String nameDL = aux[1];
            System.out.println("SimpleApp starting in " + framework + " mode");

            articleDao.importar(path, nameDL, s);
            // Select data
            rs = articleDao.getAllData(s);

            int numero = 1;
            while (rs.next()){
                System.out.println(numero++);
                for(int i = 1; i<=13; i++) {
                    System.out.println(rs.getString(i));
                }
            }
            conn.commit();
            System.out.println("Committed the transaction");


    }

    private static void crearTablas(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        // Create table digitalLibraries if not exist
        if (digitalLibraryDao.createTable(s))
            //insert rows in table
            digitalLibraryDao.insertRows(conn, statements);
        researcherDao.createTable(s);
        venueDao.createTable(s);
        companyDao.createTable(s);
        articleDao.createTable(s);
        ReferenceDao.createTable(s);
        affiliationDao.createTable(s);
        authorDao.createTable(s);
    }

    private static void deleteTables(Statement s, Connection conn, ArrayList<Statement> statements) throws SQLException {
        //Primero articles digital libries y venues
        affiliationDao.dropTable(s);
        companyDao.dropTable(s);
        authorDao.dropTable(s);
        ReferenceDao.dropTable(s);
        researcherDao.dropTable(s);
        articleDao.dropTable(s);
        venueDao.dropTable(s);
        digitalLibraryDao.dropTable(s);
    }
}
