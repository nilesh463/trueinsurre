package com.trueinsurre.dto;

public class Responce {

	private String message;
	private long status;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Responce [message=" + message + ", status=" + status + "]";
	}
}
