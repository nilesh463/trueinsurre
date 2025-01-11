package com.trueinsurre.exceptionHandeler;

public class EmptyFields extends RuntimeException {
	
	private static final long serialVersionUID = -9087840561998216436L;
	

	public EmptyFields(String message) {
		super(message);
		
	}
	public EmptyFields(String message,Throwable cause) {
		super(message,cause);
		
	}		
}
