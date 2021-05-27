package com.example.tfgdefinitivo;

import com.example.tfgdefinitivo.domain.data.*;
import org.jbibtex.ParseException;

import java.io.IOException;
import java.sql.*;
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
        pruebaReference();
    }

    private static void pruebaReference() throws IOException, ParseException, SQLException {
            Connection conn = DriverManager.getConnection(protocol + dbName + ";create=true", props);
            Statement s = conn.createStatement();
            conn.setAutoCommit(false);
            ResultSet rs;
            String[] aux = article.askInfo(s);
            String path = aux[0];
            String nameDL = aux[1];
            System.out.println("SimpleApp starting in " + framework + " mode");

            article.importar(path, nameDL, s);
            // Select data
            rs = article.getAllData(s);

            int numero = 1;
            while (rs.next()){
                System.out.println(numero++);
                for(int i = 1; i<=12; i++) {
                    System.out.println(rs.getString(i));
                }
            }
            conn.commit();
            System.out.println("Committed the transaction");
    }

}
