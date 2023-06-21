package com.webapp.gessi.exceptions;

public class BadBibtexFileException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BadBibtexFileException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }

}
