package com.trueinsurre.exceptionHandeler;

import org.springframework.http.HttpStatus;

public class ExceptionResponce {

	private String message;
	private Throwable throwable;
	private HttpStatus httpStatus;

	public ExceptionResponce(String message, Throwable throwable, HttpStatus httpStatus) {
		super();
		this.message = message;
		this.throwable = throwable;
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	@Override
	public String toString() {
		return "ExceptionResponce [message=" + message + ", throwable=" + throwable + ", httpStatus=" + httpStatus
				+ "]";
	}
}
