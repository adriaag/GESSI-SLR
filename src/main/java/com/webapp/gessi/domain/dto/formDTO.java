package com.webapp.gessi.domain.dto;

import org.springframework.web.multipart.MultipartFile;

public class formDTO  {
    private MultipartFile file;
    private String dlNum;

    public String getdlNum() { return dlNum; }

    public void setdlNum(String dlNum) { this.dlNum = dlNum;}

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
