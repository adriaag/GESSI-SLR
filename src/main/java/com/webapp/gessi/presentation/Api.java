package com.webapp.gessi.presentation;


import com.webapp.gessi.domain.controllers.ProjectController;
import com.webapp.gessi.domain.controllers.ReferenceController;
import com.webapp.gessi.domain.controllers.criteriaController;
import com.webapp.gessi.domain.controllers.digitalLibraryController;
import com.webapp.gessi.domain.dto.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.jbibtex.ParseException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "http://localhost:4200")
public class Api{
    private static final String PATH_PATTERN = "^([A-z0-9-_+\\.]+.(bib))$";

    @GetMapping(value=("/projects"))
    public ResponseEntity<?> getProjects() {
    	List<ProjectDTO> resource = ProjectController.getAll();
        return ResponseEntity.ok(resource);
    }
    
    @GetMapping(value = "/references", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getReferences(@RequestParam(value = "idProject") Integer idProject){
        List<referenceDTO> referenceDTOList = ReferenceController.getReferences(idProject);
        //model.addAttribute("ECCriteria", criteriaController.getCriteriasEC(auxIdProject));
        return ResponseEntity.ok(referenceDTOList);
    }
    
    @GetMapping(value = "/download")
    public ResponseEntity<ByteArrayResource> download(@RequestParam(value = "idProject") Integer idProject) {
        try {
            int project = idProject;
            String nameFile = "All references";
            if (project != 0) {
                ProjectDTO projectDTO = ProjectController.getById(project);
                nameFile = projectDTO.getName();
            }
            List<referenceDTO> p = ReferenceController.getReferences(project);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Workbook workbook = creationExcel.create(p);
            workbook.write(stream);
            workbook.close();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "force-download"));
            header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nameFile + ".xlsx");

            return ResponseEntity.ok(new ByteArrayResource(stream.toByteArray()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "/reference", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getReference(@RequestParam(name= "idReference") int idR){
        referenceDTO r = ReferenceController.getReference(idR);
        return ResponseEntity.ok(r);
    }
    
    @PostMapping(value = "/newReferencesFromFile", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> submitFile(@RequestParam(name = "idProject") String idProject, @RequestParam(name = "dlNum") String dlNum, @RequestParam(name = "file") MultipartFile file)
            throws ParseException, SQLException, IOException {
    	
    	
    	formDTO form = new formDTO();
    	form.setdlNum(dlNum);
    	form.setFile(file);
    	form.setIdProject(Integer.parseInt(idProject));
    	
        List<String> names = digitalLibraryController.getNames();
        List<importErrorDTO> errors;
        
        String nameFile = form.getFile().getOriginalFilename();
        JSONObject returnData = new JSONObject();
        if(!nameFile.matches(PATH_PATTERN)) {
        	returnData.put("errorFile", "The file selected has to be a BIB file.");
        	returnData.put("importBool", false);
        }
        else {
            errors = ReferenceController.addReference(form.getdlNum(), form.getIdProject(), form.getFile());
            int num = Integer.parseInt(form.getdlNum());
            returnData.put("newDL", form.getdlNum());
            returnData.put("newName", StringUtils.cleanPath(nameFile));
            returnData.put("errors", errors);
       
            if (ReferenceController.getReferencesImport() > 0) {
            	returnData.put("refsImp", ReferenceController.getReferencesImport());
                ReferenceController.resetReferencesImport();
            }
            returnData.put("DLnew", names.get(num - 1));
            returnData.put("importBool", true);            
        }
        return ResponseEntity.ok(returnData.toString());
    }
    
    @GetMapping(value = "/dl", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getDLs() throws SQLException{
    	List<String> dlNames = digitalLibraryController.getNames();
        return ResponseEntity.ok(dlNames);
    }
    
    
    @GetMapping(value = "/errors", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> importErrors(@RequestParam(value = "idProject") Integer idProject) throws SQLException, IOException, ParseException {
        List<importErrorDTO> errors = ReferenceController.getAllErrors();
        return ResponseEntity.ok(errors);
    }
    
    @GetMapping(value = "/criteria", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getCriteria(@RequestParam(value = "idProject") Integer idProject) {
        List<CriteriaDTO> lIC = criteriaController.getCriteriasIC(idProject);
        List<CriteriaDTO> lEC = criteriaController.getCriteriasEC(idProject);
        JSONObject returnData = new JSONObject();
        returnData.put("inclusionCriteria", lIC);
        returnData.put("exclusionCriteria", lEC);
        return ResponseEntity.ok(returnData.toString());
    }
    
    @PostMapping(value=("/criteria"))
    public ResponseEntity<?> newCriteria(@RequestParam(name = "name") String name, @RequestParam(name = "text") String text, @RequestParam(name = "type") String type, @RequestParam(name = "idProject") Integer idProject) {
        String messageError = criteriaController.addCriteria(name, text, type, idProject);
        return ResponseEntity.ok(messageError);
    }
    
    
 
    
    //////////////////////////////////FUNCIONS DE CLIENT CONTROLLER/////////////////////////////////////////////////////

    @PostMapping(value = "/newProject")
    public String submitNewProject(@ModelAttribute("newProject") ProjectDTO projectDTO,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttr) throws SQLException {
        List<ProjectDTO> projectDTOList = new ArrayList<>();
        ProjectDTO exist = ProjectController.getByName(projectDTO.getName());
        String[] uriParts = request.getHeader("Referer").split("/");
        String url = uriParts[uriParts.length - 1].split("\\?")[0].length() > 0 ? uriParts[uriParts.length - 1].split("\\?")[0] : "";
        if (exist != null) {
            redirectAttr.addFlashAttribute("projectError", "The project " + projectDTO.getName() + " already exist");
        }
        else {
            projectDTOList.add(projectDTO);
            ProjectController.insertRows(projectDTOList);
            int id = ProjectController.getByName(projectDTO.getName()).getId();
            url = url + "?idProject=" + id;
        }
        return "redirect:" + url;
    }

    @PostMapping(value = "/updateCriteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public static String updateCriteria(@PathVariable("id") int id,
                                         @RequestParam(value = "idProject", required = false) Optional<Integer> idProject,
                                         @ModelAttribute("f") CriteriaDTO f) {
        criteriaController.updateCriteria(id, f);
        String url = idProject.map(integer -> "/editCriteria?idProject=" + integer).orElse("/editCriteria");
        return "redirect:" + url;
    }

    @PostMapping(value = "/deleteCriteria/{id}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public static String deleteCriteria(@PathVariable("id") int idICEC,
                                        @RequestParam(value = "idProject", required = false) Optional<Integer> idProject) throws SQLException {
        criteriaController.deleteCriteria(idICEC);
        String url = idProject.map(integer -> "/editCriteria?idProject=" + integer).orElse("/editCriteria");
        return "redirect:" + url;
    }

    @PostMapping(value=("/editCriteria"))
    public String editCriteria(@ModelAttribute("f") CriteriaDTO f,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttr) {
        String messageError = criteriaController.addCriteria(f.getName(), f.getText(), f.getType(), f.getIdProject());
        redirectAttr.addFlashAttribute("errorM", messageError);
        String[] uriParts = request.getHeader("Referer").split("/");
        String url = uriParts[uriParts.length - 1];
        return "redirect:" + url;
    }

    @PostMapping(value=("/editReference"))
    public String editReference(@RequestParam(value = "idProject") int idProject,
                                @ModelAttribute("f") referenceDTOupdate f) throws SQLException {
        f.getApplCriteria().remove(null);
        ReferenceController.updateReference(f.getId(), f.getState(), f.getApplCriteria());
        return "redirect:/getAllReferences?idProject=" + idProject;
    }

    @RequestMapping(value=("/resetView"))
    public String reset(@RequestParam(value = "idProject") Optional<Integer> idProject,
                        @ModelAttribute("mes") String mes,
                        Model model){
        int project = idProject.orElse(-1);
        model.addAttribute("projectList", ProjectController.getAll());
        model.addAttribute("idProject", project);
        model.addAttribute("newProject", new ProjectDTO());
        ProjectDTO projectDTO = project == -1 ? new ProjectDTO(-1, null, 0) : ProjectController.getById(idProject.get());
        if (project == -1) {
            model.addAttribute("message", "Note that if you press the reset button the content of the database will be completely removed.");
            model.addAttribute("titleDialog", "Reset Database");
            model.addAttribute("messageDialog", " Are you sure you wanna reset the database?");
        }
        else {
            model.addAttribute("message", "Note that if you press the reset button the content of the project " + projectDTO.getName() + " will be completely removed.");
            model.addAttribute("titleDialog", "Delete Project");
            model.addAttribute("messageDialog", " Are you sure you wanna delete the project " + projectDTO.getName() + " and all the references and criterias?");
        }
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

