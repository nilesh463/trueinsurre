package com.trueinsurre.exceptionHandeler;

public class NotFound extends RuntimeException {
	
	private static final long serialVersionUID = -9087840561998216436L;
	

	public NotFound(String message) {
		super(message);
		
	}
	public NotFound(String message,Throwable cause) {
		super(message,cause);
		
	}		
}
