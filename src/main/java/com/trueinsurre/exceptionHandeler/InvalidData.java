package com.trueinsurre.exceptionHandeler;

public class InvalidData  extends RuntimeException{

private static final long serialVersionUID = -9087840561998216436L;
	

	public InvalidData(String message) {
		super(message);
		
	}
	public InvalidData(String message,Throwable cause) {
		super(message,cause);
		
	}	
}
