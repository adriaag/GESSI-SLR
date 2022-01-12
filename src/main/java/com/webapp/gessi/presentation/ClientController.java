package com.webapp.gessi.presentation;

import com.webapp.gessi.domain.controllers.ReferenceController;
import com.webapp.gessi.domain.controllers.criteriaController;
import com.webapp.gessi.domain.controllers.digitalLibraryController;
import com.webapp.gessi.domain.dto.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.jbibtex.ParseException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

@Controller
public class ClientController {
    private static final String PATH_PATTERN = "^([A-z0-9-_+\\.]+.(bib))$";

    @RequestMapping(value=("/"))
    public String index(){
        return "index";
    }

    @GetMapping(value = "/getReference", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public String getReference(@RequestParam(name= "id") int idR , Model model){
        referenceDTO r = ReferenceController.getReference(idR);
        model.addAttribute("ref", r);
        return "oneReference";
    }

    @GetMapping(value = "/getAllReferences", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public String getReferences(Model model){
        List<referenceDTO> list = ReferenceController.getReferences();
        model.addAttribute("referencesList", list);

        model.addAttribute("f", new referenceDTOupdate());
        model.addAttribute("ECCriteria", criteriaController.getStringListCriteriasEC());
        return "allReferences";
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> download() throws IOException {
        List<referenceDTO> p = ReferenceController.getReferences();
        Workbook workbook = creationExcel.create(p);
        FileOutputStream fileOut = new FileOutputStream("../webapps/references.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        File file = new File("../webapps/references.xlsx");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + "references_refman.xlsx")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping(value = "/newReference")
    public String askInformation(Model model) throws SQLException {
        model.addAttribute("DLnames", digitalLibraryController.getNames());
        model.addAttribute("f", new formDTO());
        model.addAttribute("importBool", null);
        return "newReference";
    }

    @PostMapping(value = "/new")
    public String submit( @ModelAttribute("f") formDTO f, Model model)
            throws ParseException, SQLException, IOException {
        List<String> names = digitalLibraryController.getNames();
        List<importErrorDTO> errors;
        String nameFile = f.getFile().getOriginalFilename();
        if(!nameFile.matches(PATH_PATTERN)) {
            model.addAttribute("errorFile", "The file selected has to be a BIB file.");
            model.addAttribute("importBool", false);
        }
        else {
            errors = ReferenceController.addReference(f.getdlNum(), f.getFile());
            int num = Integer.parseInt(f.getdlNum());
            model.addAttribute("newDL", f.getdlNum());
            model.addAttribute("newName", StringUtils.cleanPath(nameFile));
            model.addAttribute("errors", errors);
            if (ReferenceController.getReferencesImport() > 0) {
                model.addAttribute("refsImp", ReferenceController.getReferencesImport());
                ReferenceController.resetReferencesImport();
            }
            model.addAttribute("importBool", true);
            model.addAttribute("errorFile", "");
            model.addAttribute("DLnew", names.get(num - 1));
        }
        model.addAttribute("DLnames", names);
        model.addAttribute("f", new formDTO());
        return "newReference";
    }

    @GetMapping(value = "/errors")
    public String importErrors(Model model) throws SQLException, IOException, ParseException {
        model.addAttribute("errorsList", ReferenceController.getAllErrors());
        return "importErrors";
    }

    @GetMapping(value = "/editCriteria")
    public String askInformationCriteria(Model model){
        List<criteriaDTO> lIC = criteriaController.getCriteriasIC();
        List<criteriaDTO> lEC = criteriaController.getCriteriasEC();
        model.addAttribute("listIC", lIC);
        model.addAttribute("listEC", lEC);
        model.addAttribute("f", new criteriaDTO());

        model.addAttribute("errorM", "");
        return "editCriteria";
    }

    @PostMapping(value = "/updateCriteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public static String updateCriteriaI(@PathVariable("id") String oldIdICEC,  @ModelAttribute("f")  criteriaDTO f) {
        System.out.println(oldIdICEC);
        criteriaController.updateCriteria(oldIdICEC,f);
        return "redirect:/editCriteria";
    }

    @PostMapping(value = "/deleteCriteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public static String deleteCriteria(@PathVariable("id") String idICEC) {
        criteriaController.deleteCriteria(idICEC);
        return "redirect:/editCriteria";
    }

    @PostMapping(value=("/editCriteria"))
    public String editCriteria(@ModelAttribute("f") criteriaDTO f, Model model){
        String messageError = criteriaController.addCriteria(f.getIdICEC(),f.getText(),f.getType());
        List<criteriaDTO> lIC = criteriaController.getCriteriasIC();
        List<criteriaDTO> lEC = criteriaController.getCriteriasEC();
        model.addAttribute("listIC", lIC);
        model.addAttribute("listEC", lEC);
        model.addAttribute("f", new criteriaDTO());
        model.addAttribute("modalf", new criteriaDTO());
        model.addAttribute("errorM", messageError);
        return "editCriteria";
    }

    @PostMapping(value=("/editReference/{id}"))
    public String editReference(@PathVariable("id") int id, @ModelAttribute("f") referenceDTOupdate f){
        ReferenceController.updateReference(id,f.getEstado(),f.getApplCriteria());
        return "redirect:/getAllReferences";
    }

    @RequestMapping(value=("/resetView"))
    public String reset(){
        return "resetBD";
    }

    @GetMapping(value=("/reset"))
    public String resetBD(Model model){
        ReferenceController.reset();
        model.addAttribute("mes","The database has been reset!");
        return "resetBD";
    }

}

