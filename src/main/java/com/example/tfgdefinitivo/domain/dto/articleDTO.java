package com.example.tfgdefinitivo.domain.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "article")
public class articleDTO {
    private final String doi;
    private final String type;
    private String citeKey;
    private int idVen;
    private String title;
    private String keywords;
    private String number;
    private Integer numpages;
    private String pages;
    private String volume;
    private Integer any;
    private String abstractA;
    private venueDTO ven;
    private companyDTO[] companies;
    private researcherDTO[] researchers;

    public articleDTO(String doi, String type, String citeKey, int idVen, String title,
                      String keywords, String number, int numpages, String pages, String volume, int año, String abstractA) {
        this.doi = doi;
        this.type = type;
        this.citeKey = citeKey;
        this.idVen = idVen;
        this.title = title;
        this.keywords = keywords;
        this.number = number;
        this.numpages = numpages;
        this.pages = pages;
        this.volume = volume;
        this.any = año;
        this.abstractA = abstractA;
    }

    public String getDoi() {
        return doi;
    }

    public String getType() {
        return type;
    }

    public String getCiteKey() {
        return citeKey;
    }
    @XmlElement
    public void setCiteKey(String citeKey) {
        this.citeKey = citeKey;
    }

    public String getTitle() {
        return title;
    }
    @XmlElement
    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }
    @XmlElement
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getNumber() {
        return number;
    }
    @XmlElement
    public void setNumber(String number) {
        this.number = number;
    }

    public int getNumpages() {
        return numpages;
    }
    @XmlElement
    public void setNumpages(int numpages) {
        this.numpages = numpages;
    }

    public String getPages() {
        return pages;
    }
    @XmlElement
    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getVolume() {
        return volume;
    }
    @XmlElement
    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getAny() {
        return any;
    }
    @XmlElement
    public void setAny(int any) {
        this.any = any;
    }

    public String getAbstractA() {
        return abstractA;
    }
    @XmlElement
    public void setAbstractA(String abstractA) {
        this.abstractA = abstractA;
    }

    public int getIdVen() {
        return idVen;
    }
    @XmlElement
    public void setIdVen(int idVen) {
        this.idVen = idVen;
    }

    public venueDTO getVen() {
        return ven;
    }
    @XmlElement
    public void setVen(venueDTO ven) {
        this.ven = ven;
    }


    public companyDTO[] getCompanies() { return companies; }

    public void setCompanies(List<companyDTO> companies) {
        this.companies = companies.toArray(new companyDTO[0]);
    }

    public researcherDTO[] getResearchers() {
        return researchers;
    }

    public void setResearchers(List<researcherDTO> rss) {
        this.researchers = rss.toArray(new researcherDTO[0]);
    }
}
