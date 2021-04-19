package com.example.tfgdefinitivo.controller;

import com.example.tfgdefinitivo.CreateExcel;
import com.example.tfgdefinitivo.dao.ReferenceDao;
import com.example.tfgdefinitivo.model.Reference;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ClientController {
    public String type;

    ReferenceDao refDao = new ReferenceDao();

    @RequestMapping(value=("/"))
    public String index(){
        return "index";
    }

    @GetMapping(value = "/NewReference")
    public String greeting( Model model) throws SQLException {
        String path = "Empty";
        int nameDL = 0;
        model.addAttribute("path", path);
        model.addAttribute("nameDL", nameDL);
        model.addAttribute("DLnames", digitalLibraryController.getNames());
        return "NewReference";
    }

    @PostMapping(value = "/info")
    public String askInformation(@RequestParam(name= "path", required=false, defaultValue="path no") String path,
                                 @RequestParam(name="nameDL", required=false, defaultValue= "0") int nameDL){
        return "NewReference";
    }


    @GetMapping(value = "/getAllReferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getReferences(Model model){
        //List<Reference>
        List<Reference> list = refDao.getAllReferences();
        model.addAttribute("referencesList", list);
        return "allReferences";
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(String param) throws IOException {
        Workbook workbook = CreateExcel.create();
        FileOutputStream fileOut = new FileOutputStream("references.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        File file = new File("references.xlsx");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + "new_excel.xlsx")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    //curl http://localhost:8080/all/?name=Enric
}

