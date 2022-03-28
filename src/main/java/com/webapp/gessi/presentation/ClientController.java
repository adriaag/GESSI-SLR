package com.webapp.gessi.presentation;

import com.webapp.gessi.domain.controllers.ProjectController;
import com.webapp.gessi.domain.controllers.ReferenceController;
import com.webapp.gessi.domain.controllers.criteriaController;
import com.webapp.gessi.domain.controllers.digitalLibraryController;
import com.webapp.gessi.domain.dto.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.jbibtex.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {
    private static final String PATH_PATTERN = "^([A-z0-9-_+\\.]+.(bib))$";

    @RequestMapping(value=("/"))
    public String index(@RequestParam(value = "idProject", required = false) Optional<Integer> idProject,
                        Model model) throws SQLException {
        model.addAttribute("projectList", ProjectController.getAll());
        model.addAttribute("idProject", idProject.orElse(-1));
        model.addAttribute("newProject", new ProjectDTO());
        return "index";
    }

    @PostMapping(value = "/newProject")
    public String submitNewProject(@ModelAttribute("newProject") ProjectDTO projectDTO,
                                   HttpServletRequest request) throws SQLException {
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        projectDTOList.add(projectDTO);
        ProjectController.insertRows(projectDTOList);
        int id = ProjectController.getByName(projectDTO.getName()).getId();
        String[] uriParts = request.getHeader("Referer").split("/");
        String url = uriParts[uriParts.length - 1].split("\\?")[0];
        return "redirect:" + url + "?idProject=" + id;
    }

    @GetMapping(value = "/getReference", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public String getReference(@RequestParam(name= "id") int idR, Model model){
        referenceDTO r = ReferenceController.getReference(idR);
        model.addAttribute("ref", r);
        return "oneReference";
    }

    @GetMapping(value = "/getAllReferences", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public String getReferences(@RequestParam(value = "idProject") Optional<Integer> idProject,
                                Model model){
        model.addAttribute("projectList", ProjectController.getAll());
        List<referenceDTO> referenceDTOList = ReferenceController.getReferences(idProject.orElse(0));
        model.addAttribute("referencesList", referenceDTOList);
        model.addAttribute("f", new referenceDTOupdate());
        model.addAttribute("ECCriteria", criteriaController.getStringListCriteriasEC());
        model.addAttribute("newProject", new ProjectDTO());
        return "allReferences";
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> download(@RequestParam(value = "idProject") Optional<Integer> idProject) throws IOException {
        try {
            List<referenceDTO> p = ReferenceController.getReferences(idProject.orElse(0));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Workbook workbook = creationExcel.create(p);
            workbook.write(stream);
            workbook.close();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductTemplate.xlsx");

            return new ResponseEntity<>(new ByteArrayResource(stream.toByteArray()), header, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/newReference")
    public String askInformation(@RequestParam(value = "idProject", required = false) Optional<Integer> idProject,
                                 @ModelAttribute("errorFile") String errorFile,
                                 @ModelAttribute("importBool") String importBool,
                                 @ModelAttribute("newDL") String newDL,
                                 @ModelAttribute("newName") String newName,
                                 @ModelAttribute("errors") importErrorDTO errors,
                                 @ModelAttribute("refsImp") String refsImp,
                                 @ModelAttribute("DLnew") String DLnew,
                                 Model model) throws SQLException {
        model.addAttribute("projectList", ProjectController.getAll());
        model.addAttribute("idProject", idProject.orElse(-1));
        model.addAttribute("f", new formDTO());
        model.addAttribute("DLnames", digitalLibraryController.getNames());
        model.addAttribute("newProject", new ProjectDTO());
        return "newReference";
    }

    @PostMapping(value = "/new")
    public String submit(@ModelAttribute("f") formDTO f,
                         RedirectAttributes redirectAttr)
            throws ParseException, SQLException, IOException {
        List<String> names = digitalLibraryController.getNames();
        List<importErrorDTO> errors;
        String nameFile = f.getFile().getOriginalFilename();
        if(!nameFile.matches(PATH_PATTERN)) {
            redirectAttr.addFlashAttribute("errorFile", "The file selected has to be a BIB file.");
            redirectAttr.addFlashAttribute("importBool", false);
        }
        else {
            errors = ReferenceController.addReference(f.getdlNum(), f.getIdProject(), f.getFile());
            int num = Integer.parseInt(f.getdlNum());
            redirectAttr.addFlashAttribute("newDL", f.getdlNum());
            redirectAttr.addFlashAttribute("newName", StringUtils.cleanPath(nameFile));
            redirectAttr.addFlashAttribute("errors", errors);
            if (ReferenceController.getReferencesImport() > 0) {
                redirectAttr.addFlashAttribute("refsImp", ReferenceController.getReferencesImport());
                ReferenceController.resetReferencesImport();
            }
            redirectAttr.addFlashAttribute("importBool", true);
            redirectAttr.addFlashAttribute("errorFile", "");
            redirectAttr.addFlashAttribute("DLnew", names.get(num - 1));
        }
        return "redirect:/newReference?idProject=" + f.getIdProject();
    }

    @GetMapping(value = "/errors")
    public String importErrors(@RequestParam(value = "idProject") Optional<Integer> idProject,
                               Model model) throws SQLException, IOException, ParseException {
        model.addAttribute("projectList", ProjectController.getAll());
        model.addAttribute("idProject", idProject.orElse(-1));
        model.addAttribute("errorsList", ReferenceController.getAllErrors());
        model.addAttribute("newProject", new ProjectDTO());
        return "importErrors";
    }

    @GetMapping(value = "/editCriteria")
    public String askInformationCriteria(@RequestParam(value = "idProject", required = false) Optional<Integer> idProject,
                                         Model model){
        model.addAttribute("idProject", idProject.orElse(-1));
        List<ProjectDTO> projectDTOList = ProjectController.getAll();
        model.addAttribute("projectList", projectDTOList);
        List<criteriaDTO> lIC = criteriaController.getCriteriasIC();
        model.addAttribute("listIC", lIC);
        List<criteriaDTO> lEC = criteriaController.getCriteriasEC();
        model.addAttribute("listEC", lEC);
        model.addAttribute("f", new criteriaDTO());
        model.addAttribute("newProject", new ProjectDTO());
        return "editCriteria";
    }

    @PostMapping(value = "/updateCriteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public static String updateCriteriaI(@PathVariable("id") String oldIdICEC,
                                         @RequestParam(value = "idProject", required = false) Optional<Integer> idProject,
                                         @ModelAttribute("f")  criteriaDTO f) {
        System.out.println(oldIdICEC);
        criteriaController.updateCriteria(oldIdICEC, f);
        String url = idProject.map(integer -> "/editCriteria?idProject=" + integer).orElse("/editCriteria");
        return "redirect:" + url;
    }

    @PostMapping(value = "/deleteCriteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public static String deleteCriteria(@PathVariable("id") String idICEC,
                                        @RequestParam(value = "idProject", required = false) Optional<Integer> idProject) throws SQLException {
        criteriaController.deleteCriteria(idICEC);
        String url = idProject.map(integer -> "/editCriteria?idProject=" + integer).orElse("/editCriteria");
        return "redirect:" + url;
    }

    @PostMapping(value=("/editCriteria"))
    public String editCriteria(@ModelAttribute("f") criteriaDTO f,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttr){
        String messageError = criteriaController.addCriteria(f.getIdICEC(),f.getText(),f.getType());
        redirectAttr.addFlashAttribute("errorM", messageError);
        String[] uriParts = request.getHeader("Referer").split("/");
        String url = uriParts[uriParts.length - 1];
        return "redirect:" + url;
    }

    @PostMapping(value=("/editReference"))
    public String editReference(@RequestParam(value = "idProject") int idProject,
                                @ModelAttribute("f") referenceDTOupdate f) throws SQLException {
        if (f.getApplCriteria() == null) f.setApplCriteria("");
        ReferenceController.updateReference(f.getId(), f.getEstado(), f.getApplCriteria());
        return "redirect:/getAllReferences?idProject=" + idProject;
    }

    @RequestMapping(value=("/resetView"))
    public String reset(@RequestParam(value = "idProject") Optional<Integer> idProject,
                        @ModelAttribute("mes") String mes,
                        Model model){
        model.addAttribute("projectList", ProjectController.getAll());
        model.addAttribute("idProject", idProject.orElse(-1));
        model.addAttribute("newProject", new ProjectDTO());
        ProjectDTO projectDTO = new ProjectDTO(idProject.orElse(-1), null);
        model.addAttribute("projectDTO", projectDTO);
        return "resetBD";
    }

    @PostMapping(value=("/reset"))
    public String resetBD(@ModelAttribute("projectDTO") ProjectDTO projectDTO,
                          RedirectAttributes redirectAttr) throws SQLException {
        if (projectDTO.getId() < 1) {
            ReferenceController.reset();
            redirectAttr.addFlashAttribute("mes", "The database has been reset!");
        }
        else {
            List<ProjectDTO> projectDTOList = new ArrayList<>();
            projectDTOList.add(projectDTO);
            ProjectController.deleteRows(projectDTOList);
            redirectAttr.addFlashAttribute("mes", "The project has been deleted!");
        }
        return "redirect:/resetView";
    }

}

