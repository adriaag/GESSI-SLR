package com.webapp.gessi.presentation;

import com.webapp.gessi.domain.dto.articleDTO;
import com.webapp.gessi.domain.dto.companyDTO;
import com.webapp.gessi.domain.dto.referenceDTO;
import com.webapp.gessi.domain.dto.researcherDTO;
import org.apache.pdfbox.util.Hex;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

public class creationExcel {
    private static String[] columnHeadings = {"#ref", /*dl*/ "DL-Name", "Year", /*article*/ "DOI" ,   /*researchers*/
            "Estate" , "Criteria", "Authors" , "Title" , "Venue" , "Type" ,  /*companies*/ "Affiliations", "Volume", "Pages",
            "Number","Numpages","Cite key", "Keywords"  , /*idVen*/ "Abstract"};

    public static Workbook create(List<referenceDTO> p) throws IOException {

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

        String rgbS = "FFEBEE";
        byte[] rgbB = Hex.decodeHex(rgbS); // get byte array from hex string
        XSSFColor color = new XSSFColor(rgbB, null); //IndexedColorMap has no usage until now. So it can be set null.
        XSSFCellStyle styleD = (XSSFCellStyle) workbook.createCellStyle();
        styleD.setFillForegroundColor(color);
        styleD.setFillBackgroundColor(color);
        styleD.setFillPattern(FillPatternType.LEAST_DOTS);

        rgbS = "E1F5FE";
        rgbB = Hex.decodeHex(rgbS); // get byte array from hex string
        XSSFColor color2 = new XSSFColor(rgbB, null); //IndexedColorMap has no usage until now. So it can be set null.
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillForegroundColor(color2);
        style.setFillBackgroundColor(color2);
        style.setFillPattern(FillPatternType.LEAST_DOTS);
        
        referenceDTO[] references = p.toArray(new referenceDTO[0]);
        for (referenceDTO ref : references) {
            Row row = sheet.createRow(rowNum++);

            String auxEst = ref.getState();
            CellStyle stRow = style;
            if (auxEst != null && (auxEst.equals("duplicated") || auxEst.equals("out"))) stRow = styleD;

            Cell cell = row.createCell(0);
            cell.setCellValue(ref.getIdProjRef());
            cell.setCellStyle(stRow);

            cell = row.createCell(1);
            cell.setCellValue(ref.getDl().getName());
            cell.setCellStyle(stRow);

            articleDTO art = ref.getArt();
            cell = row.createCell(2);
            cell.setCellValue(art.getAny());
            cell.setCellStyle(stRow);

            cell = row.createCell(3);
            cell.setCellValue(ref.getDoi());
            cell.setCellStyle(stRow);

            cell = row.createCell(4);
            cell.setCellValue(auxEst);
            cell.setCellStyle(stRow);

            cell = row.createCell(5);
            cell.setCellValue(String.join(", ", ref.getApplCriteriaString()));
            cell.setCellStyle(stRow);

            researcherDTO[] authorsList = art.getResearchers();
            StringBuilder authors = new StringBuilder();
            for (int l = 0; l < authorsList.length - 1; l++) {
                authors.append(authorsList[l].getName()).append(", ");
            }
            if(authorsList.length > 0)
                authors.append(authorsList[authorsList.length - 1].getName());
            cell = row.createCell(6);
            cell.setCellValue(authors.toString());
            cell.setCellStyle(stRow);

            cell = row.createCell(7);
            cell.setCellValue(art.getTitle());
            cell.setCellStyle(stRow);

            if(art.getVen() != null) {
                cell = row.createCell(8);
                cell.setCellValue(art.getVen().getName());
                cell.setCellStyle(stRow);
            }

            cell = row. createCell(9);
            cell.setCellValue(art.getType());
            cell.setCellStyle(stRow);

            companyDTO[] affiList = art.getCompanies();
            StringBuilder affils = new StringBuilder();
            for (int h = 0; h < affiList.length - 1; h++) {
                affils.append(affiList[h].getName()).append(", ");
            }
            if(affiList.length > 0)
                affils.append(affiList[affiList.length - 1].getName());

            cell = row.createCell(10);
            cell.setCellValue(affils.toString());
            cell.setCellStyle(stRow);

            cell = row.createCell(11);
            cell.setCellValue(art.getVolume());
            cell.setCellStyle(stRow);

            cell = row.createCell(12);
            cell.setCellValue(art.getPages());
            cell.setCellStyle(stRow);

            cell = row.createCell(13);
            cell.setCellValue(art.getNumber());
            cell.setCellStyle(stRow);

            if (art.getNumpages()==0) {
                cell = row.createCell(14);
                cell.setCellValue("-");
                cell.setCellStyle(stRow);
            }
            else {
                cell = row.createCell(14);
                cell.setCellValue(art.getNumpages());
                cell.setCellStyle(stRow);
            }
            cell = row.createCell(15);
            cell.setCellValue(art.getCiteKey());
            cell.setCellStyle(stRow);
            cell = row.createCell(16);
            cell.setCellValue(art.getKeywords());
            cell.setCellStyle(stRow);

            cell = row.createCell(17);
            cell.setCellValue(art.getAbstractA());
            cell.setCellStyle(stRow);
        }
        // Resize all columns to fit the content size
        for (int i = 0; i < columnHeadings.length; i++) {
            if(i==6 || i==8 || i== 7 || i ==10 || i== 16 || i==17){
                //DO NOTHING
            } else sheet.autoSizeColumn(i);
        }
        return workbook;

    }
}
