package com.webapp.gessi.exceptions;

public class TruncationException extends Exception{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TruncationException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }

}
