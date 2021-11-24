package com.webapp.gessi.domain.dto;


public class criteriaDTO {

    private String idICEC;
    private String text;
    private String type;

    public criteriaDTO(String idICEC, String text, String type) {
        this.idICEC=idICEC;
        this.text=text;
        this.type=type;
    }

    public criteriaDTO() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdICEC() { return idICEC; }

    public void setIdICEC(String idICEC) {
        this.idICEC = idICEC;
    }
}
