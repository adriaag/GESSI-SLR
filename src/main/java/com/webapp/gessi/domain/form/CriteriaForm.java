package com.webapp.gessi.domain.form;

import com.webapp.gessi.domain.dto.criteriaDTO;

import java.util.HashMap;
import java.util.Map;

public class CriteriaForm {
    private Map<String, criteriaDTO> properties = new HashMap<>();

    public Map<String, criteriaDTO> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, criteriaDTO> properties) {
        this.properties = properties;
    }
}
