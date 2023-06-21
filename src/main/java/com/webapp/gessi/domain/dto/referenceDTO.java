package com.webapp.gessi.domain.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "reference")
public class referenceDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idRef;
    private String doi;
    private int idDL;
    private int idProject;
    private int idProjRef;
    private consensusCriteriaDTO consensusCriteria;
    private articleDTO art;
    private digitalLibraryDTO dl;
    private userDesignationDTO usersCriteria1;
    private userDesignationDTO usersCriteria2;
    private boolean consensusCriteriaProcessed;


    public referenceDTO(int idRef, String doi, int idDL, int idProject, int idProjRef, 
    		consensusCriteriaDTO exclusionDTOList, userDesignationDTO usersCriteria1, 
    		userDesignationDTO usersCriteria2, boolean consensusCriteriaProcessed) {
        this.idRef = idRef;
        this.doi = doi;
        this.idDL = idDL;
        this.idProject = idProject;
        this.idProjRef = idProjRef;
        this.consensusCriteria = exclusionDTOList;
        this.usersCriteria1 = usersCriteria1;
        this.usersCriteria2 = usersCriteria2;
        this.consensusCriteriaProcessed = consensusCriteriaProcessed;
    }

    public int getIdRef() {
            return idRef;
        }
    @XmlElement
    public void setIdRef(int idRef) {
        this.idRef = idRef;
    }

    public String getDoi() {
        return doi;
    }
    @XmlElement
    public void setDoi(String doi) {
        this.doi = doi;
    }

    public int getidDL() {
        return idDL;
    }
    @XmlElement
    public void setidDL(int idDL) {
        this.idDL = idDL;
    }

    public int getIdProject() {
        return idProject;
    }
    @XmlElement
    public void setIdProject(int idProject) {
        this.idProject = idProject;
    }

    public articleDTO getArt() {
        return art;
    }
    @XmlElement
    public void setArt(articleDTO art) {
        this.art = art;
    }

    public digitalLibraryDTO getDl() {
        return dl;
    }
    @XmlElement
    public void setDl(digitalLibraryDTO dl) {
        this.dl = dl;
    }

    public int getIdProjRef() {
        return idProjRef;
    }

    public void setdProjRef(int idProjRef) {
        this.idProjRef = idProjRef;
    }
    public consensusCriteriaDTO getExclusionDTOList() {
        return this.consensusCriteria;
    }

    public void setConsensusCriteria(consensusCriteriaDTO exclusionDTOList) {
        this.consensusCriteria = exclusionDTOList;
    }
    
    public userDesignationDTO getUsersCriteria1() {
    	return this.usersCriteria1;
    }
    
    public void setUsersCriteria1(userDesignationDTO usersCriteria) {
    	this.usersCriteria1 = usersCriteria;
    }
    
    public userDesignationDTO getUsersCriteria2() {
    	return this.usersCriteria2;
    }
    
    public void setUsersCriteria2(userDesignationDTO usersCriteria) {
    	this.usersCriteria2 = usersCriteria;
    }
    
    public boolean getConsensusCriteriaProcessed() {
    	return this.consensusCriteriaProcessed;
 
    }
    
    public void setConsensusCriteriaProcessed(boolean processed) {
    	this.consensusCriteriaProcessed = processed;
    }
    
}
