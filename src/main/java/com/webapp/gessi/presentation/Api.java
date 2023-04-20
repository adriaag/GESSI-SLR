package com.webapp.gessi.presentation;


import com.webapp.gessi.domain.controllers.ProjectController;
import com.webapp.gessi.domain.controllers.ReferenceController;
import com.webapp.gessi.domain.controllers.criteriaController;
import com.webapp.gessi.domain.controllers.digitalLibraryController;
import com.webapp.gessi.domain.dto.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.jbibtex.ParseException;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:1025"})
public class Api implements ErrorController{
    private static final String PATH_PATTERN = "^([A-z0-9-_+\\.]+.(bib))$";

	
	@GetMapping(value=("/projects")) 
	public ResponseEntity<?> getProjects() {
		try {
			List<ProjectDTO> resource = ProjectController.getAll(); 
			if (resource != null) {
				return ResponseEntity.ok(resource); 
			}
		}
		catch (SQLException e) {
	    	sqlExcHandler(e);	    	
	    }
    	return internalServerError();
		
	}

    
    @PostMapping(value = "/projects")
    public ResponseEntity<?> newProject(@RequestParam("name") String name) {
    	try {
	        ProjectDTO exist = ProjectController.getByName(name);
	        if (exist != null) {
	        	JSONObject returnData = new JSONObject();
	        	returnData.put("message", "The project " + name + " already exists");
	        	return ResponseEntity.status(HttpStatus.CONFLICT).body(returnData.toString());
	        }
	        else {
	        	ProjectDTO newProject = new ProjectDTO();
	        	newProject.setName(name);
	        			
	        	List<ProjectDTO> projectDTOList = new ArrayList<>();
	            projectDTOList.add(newProject);
	            ProjectController.insertRows(projectDTOList);
	            ProjectDTO insertedProject = ProjectController.getByName(name);
	            return ResponseEntity.status(HttpStatus.CREATED).body(insertedProject);
	        }
    	}
	    catch (SQLException e) {
	    	sqlExcHandler(e);	    	
	    }
    	return internalServerError();
	    	
		
    }
    
    @DeleteMapping(value="/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE +"; charset=utf-8")
    public ResponseEntity<?> deleteProject(@PathVariable("id") int idProj) {
    	try {
	    	JSONObject returnData = new JSONObject();
	    	ProjectDTO projectDTO = ProjectController.getById(idProj);
	        List<ProjectDTO> projectDTOList = new ArrayList<>();
	        projectDTOList.add(projectDTO);
	        ProjectController.deleteRows(projectDTOList);
	        returnData.put("message", "The project has been deleted!");
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	catch (SQLException e) {
	    	sqlExcHandler(e);	    	
	    }
    	return internalServerError();
    }
    
    @GetMapping(value = "/projects/{id}/references", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getReferences(@PathVariable("id") Integer idProject){
        List<referenceDTO> referenceDTOList = ReferenceController.getReferences(idProject);
        //model.addAttribute("ECCriteria", criteriaController.getCriteriasEC(auxIdProject));
        return ResponseEntity.ok(referenceDTOList);
    }
    
    @PutMapping(value=("/projects/{id}/references/{idRef}"))
    public ResponseEntity<?> editReference(@PathVariable("idRef") int idRef, @RequestParam(name = "state") String state, @RequestParam(name = "criteria") List<Integer> criteria) throws SQLException {
        ReferenceController.updateReference(idRef, state, criteria);
        return ResponseEntity.ok(""); 
    }
    
    @PostMapping(value = "/projects/{id}/references", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> submitFile(@PathVariable(name = "id") String idProject, @RequestParam(name = "dlNum") String dlNum, @RequestParam(name = "file") MultipartFile file)
            throws ParseException, SQLException, IOException {
    	
    	
    	formDTO form = new formDTO();
    	form.setdlNum(dlNum);
    	form.setFile(file);
    	form.setIdProject(Integer.parseInt(idProject));
    	
        List<String> names = digitalLibraryController.getNames();
        List<importErrorDTO> errors;
        
        String nameFile = form.getFile().getOriginalFilename();
        JSONObject returnData = new JSONObject();
        /*if(!nameFile.matches(PATH_PATTERN)) {
        	returnData.put("errorFile", "The file selected has to be a BIB file.");
        	returnData.put("importBool", false);
        }
        else {*/
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
        //}
        return ResponseEntity.ok(returnData.toString());
    }
    
    @DeleteMapping(value="/projects/{id}/references/{idRef}", produces = MediaType.APPLICATION_JSON_VALUE +"; charset=utf-8")
    public ResponseEntity<?> deleteReference(@PathVariable("id") int idRef) throws SQLException {
    	JSONObject returnData = new JSONObject();
    	ReferenceController.deleteReference(idRef);
        returnData.put("message", "The reference has been deleted!");
        return ResponseEntity.ok(returnData.toString());
    }
    
    @GetMapping(value = "/projects/{id}/export/references")
    public ResponseEntity<ByteArrayResource> download(@PathVariable(value = "id") Integer idProject) {
        try {
            int project = idProject;
            List<referenceDTO> p = ReferenceController.getReferences(project);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Workbook workbook = creationExcel.create(p);
            workbook.write(stream);
            workbook.close();

            return ResponseEntity.ok(new ByteArrayResource(stream.toByteArray()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @GetMapping(value = "/digitalLibraries", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getDLs() throws SQLException{
    	List<String> dlNames = digitalLibraryController.getNames();
        return ResponseEntity.ok(dlNames);
    }
    
    
    @GetMapping(value = "/projects/{id}/errors", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> importErrors(@PathVariable("id") Integer idProject) throws SQLException, IOException, ParseException {
        List<importErrorDTO> errors = ReferenceController.getErrors(idProject);
        return ResponseEntity.ok(errors);
    }
    
    @GetMapping(value = "/projects/{id}/criterias", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getCriteria(@PathVariable("id") Integer idProject) {
        List<CriteriaDTO> lIC = criteriaController.getCriteriasIC(idProject);
        List<CriteriaDTO> lEC = criteriaController.getCriteriasEC(idProject);
        JSONObject returnData = new JSONObject();
        returnData.put("inclusionCriteria", lIC);
        returnData.put("exclusionCriteria", lEC);
        return ResponseEntity.ok(returnData.toString());
    }
    
    @PostMapping(value=("/projects/{id}/criterias"))
    public ResponseEntity<?> newCriteria(@RequestParam(name = "name") String name, @RequestParam(name = "text") String text, @RequestParam(name = "type") String type, @PathVariable("id") Integer idProject) {
        String messageError = criteriaController.addCriteria(name, text, type, idProject);
        JSONObject returnData = new JSONObject();
        returnData.put("message",messageError);
        return ResponseEntity.ok(returnData.toString());
    }
    
    @PutMapping(value = "/projects/{id}/criterias/{idCri}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> updateCriteria(@PathVariable("idCri") int idCri, @RequestParam(name = "name") String name, @RequestParam(name = "text") String text, @RequestParam(name = "type") String type, @PathVariable(name = "id") Integer idProject) {
    	CriteriaDTO criteria = new CriteriaDTO(idCri, name, text, type, idProject);
        criteriaController.updateCriteria(idCri, criteria);
        return ResponseEntity.ok("");   
    }
    
    @DeleteMapping(value = "/projects/{id}/criterias/{idCri}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> deleteCriteria(@PathVariable("idCri") int idICEC) throws SQLException {
        criteriaController.deleteCriteria(idICEC);
        return ResponseEntity.ok(""); 
    }
    
    @DeleteMapping(value = "/")
    public ResponseEntity<?> deleteDB() {
        ReferenceController.reset();
        JSONObject returnData = new JSONObject();
        returnData.put("message", "The database has been reset!");
        return ResponseEntity.ok(returnData.toString());
    }
    
    @RequestMapping(value = "/error") 
    public ResponseEntity<?> errorPage() {
    	JSONObject returnData = new JSONObject();
    	returnData.put("message", "Resource not found");
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(returnData.toString());	
    }

    /*@GetMapping(value = "/reference", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getReference(@RequestParam(name= "idReference") int idR){
        referenceDTO r = ReferenceController.getReference(idR);
        return ResponseEntity.ok(r);
    }*/
    
    private void sqlExcHandler(SQLException e) {
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e = e.getNextException();
        }
    }
    
    private ResponseEntity<?> internalServerError() {
    	JSONObject returnData = new JSONObject();
    	returnData.put("message", "Resource not found");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(returnData.toString());
    }
    

}

