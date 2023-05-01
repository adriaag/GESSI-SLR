package com.webapp.gessi.domain.dto;

public class referenceDTOadd {
	private static final long serialVersionUID = 1L;
    private String doi;
    private String type;
    private String nameVen;
    private String title;
    private String keywords;
    private String number;
    private int numpages;
    private String pages;
    private String volume;
    private int any;
    private String resum;
    private String[] authorNames;
    private String[] affiliationNames;
    
    public referenceDTOadd (String doi, String type, String nameVen, String title,
    		String keywords, String number, int numpages, String pages, String volume,
    		int any, String resum, String[] authorNames, String[] affiliationNames)
    {
    	this.doi = doi;
        this.type = type;
        this.nameVen = nameVen;
        this.title = title;
        this.keywords = keywords;
        this.number = number;
        this.numpages = numpages;
        this.pages = pages;
        this.volume = volume;
        this.any = any;
        this.resum = resum;
        this.authorNames = authorNames;
        this.affiliationNames = affiliationNames;
    	
    }
    
    public String getDoi() {
    	return this.doi;
    }
    
    public String getType() {
    	return this.type;
    }
    
    public String getNameVen() {
    	return this.nameVen;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    public String getKeywords() {
    	return this.keywords;
    }
    
    public String getNumber() {
    	return this.number;
    }
    
    public int getNumPages() {
    	return this.numpages;
    }
    
    public String getVolume() {
    	return this.volume;
    }
    
    public String getPages() {
    	return this.pages;
    }
    
    public int getYear() {
    	return this.any;
    }
    
    public String getAbstract() {
    	return this.resum;
    }
    
    public String[] getAuthorNames() {
    	return this.authorNames;
    }
    
    public String[] getAffiliationNames() {
    	return this.affiliationNames;
    }

}
