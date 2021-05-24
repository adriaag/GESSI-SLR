package com.example.tfgdefinitivo.controller;

import com.example.tfgdefinitivo.classes.CreateExcel;
import com.example.tfgdefinitivo.model.Reference;
import com.example.tfgdefinitivo.model.formDTO;
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

@Controller
public class ClientController {
    public String type;

    @RequestMapping(value=("/"))
    public String index(){
        return "index";
    }

    @GetMapping(value = "/getReference", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getReference(@RequestParam(name= "id") int idR , Model model){
        Reference r = ReferenceController.getReference(idR);
        model.addAttribute("ref", r);
        return "oneReference";
    }

    @GetMapping(value = "/getAllReferences", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getReferences(Model model){
        List<Reference> list = ReferenceController.getReferences();
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

    @RequestMapping(value = "/askInformation")
    public String askInformation(Model model) throws SQLException {
        model.addAttribute("form", new formDTO());
        model.addAttribute("DLnames", digitalLibraryController.getNames());

        model.addAttribute("path", "");
        model.addAttribute("dl", "");
        return "formReference";
    }

    /*@PostMapping(value = "/NewReference")
    public String addReference( formDTO form, Model model ) throws SQLException {
        model.addAttribute("path", form.getPath());
        model.addAttribute("nameDL", form.getDl());
        model.addAttribute("DLnames", digitalLibraryController.getNames());
        return "NewReference";
    }*/
    //@RequestParam(name= "path", required=false, defaultValue="path no") String path
    @RequestMapping(value = "/NewReference")
    public String addReference( formDTO form, Model model ) throws SQLException {
        model.addAttribute("path", form.getPath());
        model.addAttribute("nameDL", form.getDl());
        return "NewReference";
    }
}

