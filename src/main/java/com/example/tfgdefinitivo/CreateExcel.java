package com.example.tfgdefinitivo;

import com.example.tfgdefinitivo.controller.ReferenceController;
import com.example.tfgdefinitivo.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class CreateExcel {
    private static String[] columnHeadings = {"#referencia",/*article*/ "DOI" , "Type" , "Cite key" , "Title" ,
            "Journal", "Keywords" , "Number","Numpages","Pages","Volume","Year", /*idVen*/ "Venue" , "Abstract",
            /*researchers*/ "Authors" , /*companies*/ "Affiliations", /*dl*/ "Digital Library"  };

    public static Workbook create() throws IOException {

        //format .xlsx format
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("References");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.DARK_BLUE.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columnHeadings.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeadings[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;

        Reference[] references = ReferenceController.getReferences().toArray(new Reference[0]);
        for (Reference ref : references) {

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(ref.getIdRef());
                row.createCell(1).setCellValue(ref.getDoi());
                article art = ref.getArt();

                row.createCell(2).setCellValue(art.getType());
                row.createCell(3).setCellValue(art.getCiteKey());
                row.createCell(4).setCellValue(art.getTitle());
                row.createCell(5).setCellValue(art.getJournal());
                row.createCell(6).setCellValue(art.getKeywords());
                row.createCell(7).setCellValue(art.getNumber());
                row.createCell(8).setCellValue(art.getNumpages());
                row.createCell(9).setCellValue(art.getPages());
                row.createCell(10).setCellValue(art.getVolume());
                row.createCell(11).setCellValue(art.getAny());

                if(art.getVen() != null)
                    row.createCell(12).setCellValue(art.getVen().getName());

                row.createCell(13).setCellValue(art.getAbstractA());

                researcher[] authorsList = art.getResearchers();
                StringBuilder authors = new StringBuilder();
                for (int l = 0; l < authorsList.length - 1; l++) {
                    authors.append(authorsList[l].getName()).append(", ");
                }
                if(authorsList.length > 1)
                    authors.append(authorsList[authorsList.length - 1].getName());
                row.createCell(14).setCellValue(authors.toString());

                company[] affiList = art.getCompanies();
                StringBuilder affiliations = new StringBuilder();
                for (int l = 0; l < affiList.length - 1; l++) {
                    affiliations.append(affiList[l].getName()).append(", ");
                }
                if(affiList.length > 1)
                    affiliations.append(authorsList[affiList.length - 1].getName());
                row.createCell(15).setCellValue(affiliations.toString());

                row.createCell(16).setCellValue(ref.getDl().getName());

        }
        // Resize all columns to fit the content size
        for (int i = 0; i < columnHeadings.length; i++) {
            sheet.autoSizeColumn(i);
        }
        //FileOutputStream fileOut = new FileOutputStream("references.xlsx");
        //workbook.write(fileOut);
        //fileOut.close();

        return workbook;

    }

    public static void main(String[] args) throws IOException {
        create();
    }

}
