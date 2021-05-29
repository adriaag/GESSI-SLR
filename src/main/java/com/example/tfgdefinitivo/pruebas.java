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
        iniCheck("C:\\Apache\\tfg-definitivo\\src\\main\\resources\\BibFiles\\ExScopus.bib");
    }

    public static void iniCheck(String path) throws FileNotFoundException, SQLException {
        File file = new File(path);
        Scanner sc = new Scanner(file);
        sc.useDelimiter("\\@");
        ArrayList<String> list = new ArrayList<>();

        Pattern patternKey = Pattern.compile("\\{(.*),");
        Pattern patternDOI = Pattern.compile("doi=(.*)\\}");
        Pattern patternDOI2 = Pattern.compile("doi =(.*)\\}");
        Pattern patternDOI3 = Pattern.compile("DOI =(.*)\\}");

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
                //Comprobar citekey duplicada
                System.out.println(citeKey);
                if (list.contains(citeKey)) {
                    if (doi != null ) System.out.println("DOI es: " +doi);

                    //Añadir bib a la table LogError

                    //debe insert en LogError los repetidos!!!
                    //y ver si escribe bien
                    System.out.println("insert row bc duplicated cite key:" + citeKey);
                }
                list.add(citeKey);
            }
        }
    }
}
