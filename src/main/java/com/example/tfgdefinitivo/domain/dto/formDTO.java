package com.example.tfgdefinitivo.domain.dto;

import org.springframework.context.annotation.Bean;

public class formDTO  {
    private String path;
    private String dlNum;

    public String getPath() { return path; }

    public void setPath(String path) {
        this.path = path;
    }

    public String getdlNum() { return dlNum; }

    public void setdlNum(String dlNum) {
        this.dlNum = dlNum;
    }
}
