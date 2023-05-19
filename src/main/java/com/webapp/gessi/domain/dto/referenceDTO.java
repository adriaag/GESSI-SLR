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
    private String state;
    private int idProjRef;
    private List<consensusCriteriaDTO> consensusCriteriaDTOList;
    private articleDTO art;
    private digitalLibraryDTO dl;


    public referenceDTO(int idRef, String doi, int idDL, int idProject, String state, int idProjRef, List<consensusCriteriaDTO> exclusionDTOList) {
        this.idRef = idRef;
        this.doi = doi;
        this.idDL = idDL;
        this.idProject = idProject;
        this.state = state;
        this.idProjRef = idProjRef;
        this.consensusCriteriaDTOList = exclusionDTOList;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getIdProjRef() {
        return idProjRef;
    }

    public void setdProjRef(int idProjRef) {
        this.idProjRef = idProjRef;
    }
    public List<consensusCriteriaDTO> getExclusionDTOList() {
        return consensusCriteriaDTOList;
    }

    public String getApplCriteriaString() {
       if (this.consensusCriteriaDTOList != null) {
           List<String> exclusionList = this.consensusCriteriaDTOList.stream().map(consensusCriteriaDTO::getNameICEC).collect(Collectors.toList());
           return String.join(", ", exclusionList);
       }
       return "";
    }

    public String getExclusionDTOIdList() {
        if (this.consensusCriteriaDTOList != null) {
            List<Integer> exclusionList = this.consensusCriteriaDTOList.stream().map(consensusCriteriaDTO::getIdICEC).collect(Collectors.toList());
            return exclusionList.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
        return "";
    }

    public void setExclusionDTOList(List<consensusCriteriaDTO> exclusionDTOList) {
        this.consensusCriteriaDTOList = exclusionDTOList;
    }
}
