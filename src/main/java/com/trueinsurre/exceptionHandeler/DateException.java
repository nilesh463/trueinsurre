package com.trueinsurre.exceptionHandeler;

public class DateException extends RuntimeException {
	
	private static final long serialVersionUID = -9087840561998216436L;
	

	public DateException(String message) {
		super(message);
		
	}
	public DateException(String message,Throwable cause) {
		super(message,cause);
		
	}		
}
