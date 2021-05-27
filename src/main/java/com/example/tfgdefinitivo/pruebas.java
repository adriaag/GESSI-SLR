package com.example.tfgdefinitivo;
// Java Program to illustrate reading from FileReader
// using Scanner Class reading entire File
// without using loop

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class pruebas {
    public static void main(String[] args) throws FileNotFoundException, SQLException {
        iniCheck("C:\\Apache\\tfg-definitivo\\src\\main\\resources\\BibFiles\\ieee_journal.bib");
    }

    public static void iniCheck(String path) throws FileNotFoundException, SQLException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\\@");
        ArrayList<String> list = new ArrayList<>();
        String bib;

        Pattern patternKey = Pattern.compile("\\{(.*),");
        Pattern patternDOI = Pattern.compile("doi(.*)\\}");
        while(sc.hasNext()) {
            String data = sc.next();

            bib = sc.findInLine(patternKey);
            Matcher matcher = patternDOI.matcher(data);
            if (matcher.find()) {
                String doi = matcher.group(1).replaceAll("=\\{", "").replaceAll(",", "")
                        .replaceAll("=", "");
                System.out.println("DOI es: " +doi);
            }
            if(bib!=null) {
                String citeKey = bib.replaceAll("\\{", "").replaceAll(",", "");
                //Comprobar citekey duplicada
                System.out.println(citeKey);
                if (list.contains(citeKey)) {
                    //buscar doi

                    //Añadir bib a la table LogError

                    //debe insert en LogError los repetidos!!!
                    //y ver si escribe bien
                    System.out.println("insert row if duplicate:" + citeKey);
                }
                list.add(citeKey);
            }
        }
    }
}
