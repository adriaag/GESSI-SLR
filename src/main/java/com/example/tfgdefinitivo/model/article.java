package com.example.tfgdefinitivo.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "article")
public class article {
    private final String doi;
    private final String type;
    private String citeKey;
    private int idVen;
    private String title;
    private String journal;
    private String keywords;
    private int number;
    private int numpages;
    private String pages;
    private int volume;
    private int año;
    private String abstractA;

    public article(String doi, String type, String citeKey, int idVen, String title, String journal,String keywords, int number, int numpages, String pages, int volume, int año, String abstractA) {
        this.doi = doi;
        this.type = type;
        this.citeKey = citeKey;
        this.idVen = idVen;
        this.title = title;
        this.journal = journal;
        this.keywords = keywords;
        this.number = number;
        this.numpages = numpages;
        this.pages = pages;
        this.volume = volume;
        this.año = año;
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

    public int getNumber() {
        return number;
    }
    @XmlElement
    public void setNumber(int number) {
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

    public int getVolume() {
        return volume;
    }
    @XmlElement
    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getAño() {
        return año;
    }
    @XmlElement
    public void setAño(int año) {
        this.año = año;
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

    public void setIdVen(int idVen) {
        this.idVen = idVen;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }
}
