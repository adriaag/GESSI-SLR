package com.webapp.gessi.presentation;


import com.webapp.gessi.domain.controllers.ProjectController;
import com.webapp.gessi.domain.controllers.ReferenceController;
import com.webapp.gessi.domain.controllers.UserController;
import com.webapp.gessi.domain.controllers.UserDesignationController;
import com.webapp.gessi.domain.controllers.UserDesignationICECController;
import com.webapp.gessi.domain.controllers.criteriaController;
import com.webapp.gessi.domain.controllers.digitalLibraryController;
import com.webapp.gessi.domain.dto.*;
import com.webapp.gessi.exceptions.BadBibtexFileException;
import com.webapp.gessi.exceptions.TruncationException;

import org.apache.poi.ss.usermodel.Workbook;
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
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = {"http://localhost:4200"})
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
	    	return sqlExcHandler(e);	    	
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
	    	return sqlExcHandler(e);	    	
	    }

	    	
		
    }
    
    @PutMapping(value="/projects/{id}")
    public ResponseEntity<?> updateProject(@PathVariable("id") int idProj, @RequestBody ProjectDTO project) {
    	try {
    		ProjectController.updateProject(project);
    		ProjectDTO ret = ProjectController.getById(project.getId());
    		return ResponseEntity.status(HttpStatus.ACCEPTED).body(ret);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    } 
    	catch (IOException e) {
	    	e.printStackTrace();
			return internalServerError();	
		}
    	
    }
    
    @GetMapping(value="/projects/{id}/protocolImg",produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<?> getProtocolImg(@PathVariable("id") int idProj) {
    	try {
    		byte[] img = ProjectController.getProtocolImg(idProj);
    		return ResponseEntity.ok(img); 
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    } 
    	
    }
    
    @PostMapping(value="/projects/{id}/protocolImg")
    public ResponseEntity<?> updateProtocolImg(@PathVariable("id") int idProj, @RequestParam("image") MultipartFile img) {
    	try {
    		ProjectController.updateProtocolImg(idProj, img);
    		return new ResponseEntity<>(HttpStatus.CREATED);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    } 
    	catch (IOException e) {
			e.printStackTrace();
			return internalServerError();
		} 
    	
    }
    
    @PostMapping(value="/projects/{id}/order")
    public ResponseEntity<?> updateOrder(@PathVariable("id") int idProj, @RequestParam("orderColSearch") String orderColSearch, 
    		@RequestParam("orderDirSearch") String orderDirSearch, @RequestParam("orderColScreen") String orderColScreen, 
    		@RequestParam("orderDirScreen") String orderDirScreen) {
    	try {
    		ProjectController.updateOrder(idProj, orderColSearch, orderDirSearch, orderColScreen, orderDirScreen);
    		return new ResponseEntity<>(HttpStatus.CREATED);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    } 
    }
    
    @DeleteMapping(value="/projects/{id}", produces = MediaType.APPLICATION_JSON_VALUE +"; charset=utf-8")
    public ResponseEntity<?> deleteProject(@PathVariable("id") int idProj) {
    	try {
	    	ProjectDTO projectDTO = ProjectController.getById(idProj);
	    	System.out.println(projectDTO);
	        List<ProjectDTO> projectDTOList = new ArrayList<>();
	        projectDTOList.add(projectDTO);
	        ProjectController.deleteRows(projectDTOList);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }

    }
       
    
    @GetMapping(value = "/projects/{id}/references", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getReferences(@PathVariable("id") Integer idProject){
    	try {
	        List<referenceDTO> referenceDTOList = ReferenceController.getReferences(idProject);
	        //model.addAttribute("ECCriteria", criteriaController.getCriteriasEC(auxIdProject));
	        return ResponseEntity.ok(referenceDTOList);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }
    	
    }
    
    @PutMapping(value=("/projects/{id}/references/{idRef}/userDesignations"))
    public ResponseEntity<?> setUserDesignation(@PathVariable("idRef") int idRef, @RequestParam(name = "numDesignation") int numDesignation, 
    		@RequestParam(name = "username") String username) {
        try {
        	if (numDesignation != 1 && numDesignation != 2) 
        		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        	
        	userDesignationDTO ud = UserDesignationController.insertRow(username, idRef,numDesignation);
	        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ud);
        }
        catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }
   
    }

    @PutMapping(value=("/projects/{id}/references/{idRef}/userDesignations/{numDesignation}"))
    public ResponseEntity<?> setUserDesignationICEC(@PathVariable("idRef") int idRef, @PathVariable("numDesignation") int numDesignation, 
    		@RequestParam("username")String username, @RequestParam("processed") boolean processed, @RequestParam("criteriaList") List<Integer> criteriaList) {
        try {
        	if (numDesignation != 1 && numDesignation != 2) 
        		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        	
        	userDesignationDTO designation = new userDesignationDTO(username, idRef, numDesignation, processed,  criteriaList);
        	UserDesignationController.addCriteria(designation);
	        return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }
   
    }
    
    @PutMapping(value=("/projects/{id}/references/{idRef}"))
    public ResponseEntity<?> editReference(@PathVariable("idRef") int idRef, @RequestParam(name = "criteria") List<Integer> criteria) {
        try {
	    	ReferenceController.updateReference(idRef,criteria);
	        return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }
   
    }
    
    @PostMapping(value = "/projects/{id}/references", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> submitFile(@PathVariable(name = "id") String idProject, @RequestParam(name = "dlNum") String dlNum, @RequestParam(name = "file") MultipartFile file) {
    	
    	try {
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
	            returnData.put("newDL",  digitalLibraryController.getNames().get(Integer.parseInt(form.getdlNum())-1));
	            returnData.put("newName", StringUtils.cleanPath(nameFile));
	            returnData.put("errors", errors);
	       
	            if (ReferenceController.getReferencesImport() > 0) {
	            	returnData.put("refsImp", ReferenceController.getReferencesImport());
	                ReferenceController.resetReferencesImport();
	            }
	            else {
	            	returnData.put("refsImp",0);
	            }
	            if (ReferenceController.getReferencesDuplicated() > 0) {
	            	returnData.put("refsDupl", ReferenceController.getReferencesDuplicated());
	                ReferenceController.resetReferencesDuplicated();
	            }
	            else {
	            	returnData.put("refsDupl",0);
	            }
	            returnData.put("importBool", true);            
	        //}
	        return ResponseEntity.status(HttpStatus.CREATED).body(returnData.toString());
	        
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);

	    }
    	catch (IOException e) {
    		return internalServerError();    		
    		
    	}
    	catch(BadBibtexFileException e) {
    		JSONObject returnData = new JSONObject();
    		returnData.put("message", e.getCause()+"\nNo reference was imported");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnData.toString());    		
    	}
    	
    	catch(TruncationException e) {
    		JSONObject returnData = new JSONObject();
    		returnData.put("message",e.getMessage()+"\nAll the references until this error ocurred were imported");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnData.toString());    		
    		
    	}
    	
    }
    
    @PostMapping(value = "/projects/{id}/manualreferences", consumes = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> addReference(@PathVariable("id") int idProj, @RequestBody referenceDTOadd data){
    	try {
    		referenceDTO reference = ReferenceController.addReferenceManually(data, idProj);
    		return ResponseEntity.status(HttpStatus.CREATED).body(reference);
    	}
    	catch (SQLException e) {
    		return sqlExcHandler(e);
    	}
    	
    }
    
    @DeleteMapping(value="/projects/{idProj}/references/{idRef}", produces = MediaType.APPLICATION_JSON_VALUE +"; charset=utf-8")
    public ResponseEntity<?> deleteReference(@PathVariable("idProj") int idProj,@PathVariable("idRef") int idRef) {
    	try {
	    	JSONObject returnData = new JSONObject();
	    	ReferenceController.deleteReference(idRef);
	        returnData.put("message", "The reference has been deleted!");
	        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(returnData.toString());
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);

	    }
    	
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
    public ResponseEntity<?> getDLs(){
    	try {
	    	List<String> dlNames = digitalLibraryController.getNames();
	        return ResponseEntity.ok(dlNames);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);
	    	
	    }
    }
    
    @GetMapping(value = "/article", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getArticle(@RequestParam("doi") String doi){
    	try {
	        articleDTO ar = ReferenceController.getArticle(doi);
	        //if (ar == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        return ResponseEntity.ok(ar);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);	    	
	    }
    	
    }
    
    
    @GetMapping(value = "/projects/{id}/errors", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> importErrors(@PathVariable("id") Integer idProject) {
    	try {
	        List<importErrorDTO> errors = ReferenceController.getErrors(idProject);
	        return ResponseEntity.ok(errors);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);
	    	
	    }
    }
    
    @GetMapping(value = "/projects/{id}/criterias", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getCriteria(@PathVariable("id") Integer idProject) {
    	try {
	        List<CriteriaDTO> lIC = criteriaController.getCriteriasIC(idProject);
	        List<CriteriaDTO> lEC = criteriaController.getCriteriasEC(idProject);
	        JSONObject returnData = new JSONObject();
	        returnData.put("inclusionCriteria", lIC);
	        returnData.put("exclusionCriteria", lEC);
	        return ResponseEntity.ok(returnData.toString());
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);
	
	    }
    }
    
    @PostMapping(value=("/projects/{id}/criterias"))
    public ResponseEntity<?> newCriteria(@RequestParam(name = "name") String name, @RequestParam(name = "text") String text, @RequestParam(name = "type") String type, @PathVariable("id") Integer idProject) {
        try {
	    	String messageError = criteriaController.addCriteria(name, text, type, idProject);
	        JSONObject returnData = new JSONObject();
	        returnData.put("message",messageError);
	        return ResponseEntity.status(HttpStatus.CREATED).body(returnData.toString());
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);

	    }
    }
    
    @PutMapping(value = "/projects/{id}/criterias/{idCri}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> updateCriteria(@PathVariable("idCri") int idCri, @RequestParam(name = "name") String name, @RequestParam(name = "text") String text, @RequestParam(name = "type") String type, @PathVariable(name = "id") Integer idProject) {
    	try {
	    	CriteriaDTO criteria = new CriteriaDTO(idCri, name, text, type, idProject);
	        criteriaController.updateCriteria(idCri, criteria);
	        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);

	    }
    	
    }
    
    @DeleteMapping(value = "/projects/{id}/criterias/{idCri}", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> deleteCriteria(@PathVariable("idCri") int idICEC) {
    	try {
	        criteriaController.deleteCriteria(idICEC);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
	    catch (SQLException e) {
	    	return sqlExcHandler(e);

		}
    }
    
    @DeleteMapping(value = "/")
    public ResponseEntity<?> deleteDB() {
        ReferenceController.reset();
        JSONObject returnData = new JSONObject();
        returnData.put("message", "The database has been reset!");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(returnData.toString());
    }
    
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getUsernames(){
    	try {
	    	List<String> usernames = UserController.getAllUsernames();
	        return ResponseEntity.ok(usernames);
    	}
    	catch (SQLException e) {
	    	return sqlExcHandler(e);
	    	
	    }
    }
    
    @RequestMapping(value = "/error") 
    public ResponseEntity<?> errorPage(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
        
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
            	return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);        	
            }
            else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            	return new ResponseEntity<>(HttpStatus.FORBIDDEN); 
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*@GetMapping(value = "/reference", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public ResponseEntity<?> getReference(@RequestParam(name= "idReference") int idR){
        referenceDTO r = ReferenceController.getReference(idR);
        return ResponseEntity.ok(r);
    }*/
    
    private ResponseEntity<?> sqlExcHandler(SQLException e) {
    	boolean conflict = false;
        while (e != null) {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            e.printStackTrace();
            
            switch(e.getSQLState()) {
            case "23505": //duplicate key contraint violation
            	conflict = true;
            	break;
            //més casos si es desitja
            }
            e = e.getNextException();
        }
        if (conflict) {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body("");	
        }
        else {
        	return internalServerError();
        }
    }
    
    private ResponseEntity<?> internalServerError() {
    	JSONObject returnData = new JSONObject();
    	returnData.put("message", "Resource not found");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(returnData.toString());
    }


	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return null;
	}
    

}

