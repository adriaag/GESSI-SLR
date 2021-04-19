package com.example.tfgdefinitivo.controller;

import com.example.tfgdefinitivo.CreateExcel;
import com.example.tfgdefinitivo.dao.ReferenceDao;
import com.example.tfgdefinitivo.model.Reference;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class IndexController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    ReferenceDao refDao = new ReferenceDao();

    @GetMapping(value = "/")
    public String greeting(@RequestParam(name= "path", required=false, defaultValue="path no") String path,
                           @RequestParam(name="nameDL", required=false, defaultValue= "0") int nameDL, Model model) {
        model.addAttribute("path", path);
        model.addAttribute("nameDL", nameDL);
        model.addAttribute("referenceJSON", refDao.getAllReferences());
        return "index";
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

