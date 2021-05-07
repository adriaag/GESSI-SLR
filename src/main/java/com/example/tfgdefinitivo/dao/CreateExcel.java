package com.example.tfgdefinitivo.dao;

import com.example.tfgdefinitivo.controller.ReferenceController;
import com.example.tfgdefinitivo.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class CreateExcel {
    private static String[] columnHeadings = {"#ref", /*dl*/ "DL-Name", "Year", /*article*/ "DOI" ,   /*researchers*/
            "Authors" , "Title" , "Venue" , "Type" ,  /*companies*/ "Affiliations", "Volume", "Pages",
            "Number","Numpages","Cite key", "Keywords"  , /*idVen*/ "Abstract"};

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
            row.createCell(1).setCellValue(ref.getDl().getName());

            article art = ref.getArt();
            row.createCell(2).setCellValue(art.getAny());
            row.createCell(3).setCellValue(ref.getDoi());

            researcher[] authorsList = art.getResearchers();
            StringBuilder authors = new StringBuilder();
            for (int l = 0; l < authorsList.length - 1; l++) {
                authors.append(authorsList[l].getName()).append(", ");
            }
            if(authorsList.length > 0)
                authors.append(authorsList[authorsList.length - 1].getName());
            row.createCell(4).setCellValue(authors.toString());

            row.createCell(5).setCellValue(art.getTitle());
            if(art.getVen() != null)
                row.createCell(6).setCellValue(art.getVen().getName());
            row.createCell(7).setCellValue(art.getType());

            company[] affiList = art.getCompanies();
            StringBuilder affils = new StringBuilder();
            for (int h = 0; h < affiList.length - 1; h++) {
                affils.append(affiList[h].getName()).append(", ");
            }
            if(affiList.length > 0)
                affils.append(affiList[affiList.length - 1].getName());

            row.createCell(8).setCellValue(affils.toString());
            row.createCell(9).setCellValue(art.getVolume());
            row.createCell(10).setCellValue(art.getPages());

            row.createCell(11).setCellValue(art.getNumber());

            if (art.getNumpages()==0) row.createCell(12).setCellValue("-");
            else row.createCell(12).setCellValue(art.getNumpages());
            row.createCell(13).setCellValue(art.getCiteKey());
            row.createCell(14).setCellValue(art.getKeywords());

            row.createCell(15).setCellValue(art.getAbstractA());
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
}
