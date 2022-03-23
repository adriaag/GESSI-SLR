package com.webapp.gessi.domain.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class importErrorDTO {
    private String datetime;
    private int idDL;
    private String doi;
    private String BibTex;

    public importErrorDTO() {}

    public importErrorDTO(Timestamp t, int idDL, String doi, String bibTex) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        LocalDateTime ldt = t.toLocalDateTime();
        this.datetime = ldt.format(dateTimeFormatter);
        this.idDL = idDL;
        this.doi = doi;
        this.BibTex = bibTex;
    }

    static importErrorDTO of() {
        return new importErrorDTO();
    }

    public int getIdDL() {
        return idDL;
    }

    public void setIdDL(int idDL) {
        this.idDL = idDL;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getBibTex() {
        return BibTex;
    }

    public void setBibTex(String bibTex) {
        this.BibTex = bibTex;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
