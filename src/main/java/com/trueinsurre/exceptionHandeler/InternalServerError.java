package com.trueinsurre.exceptionHandeler;

public class InternalServerError extends RuntimeException {
	
private static final long serialVersionUID = -9087840561998216436L;
	

	public InternalServerError(String message) {
		super(message);
		
	}
	public InternalServerError(String message,Throwable cause) {
		super(message,cause);
		
	}

}
