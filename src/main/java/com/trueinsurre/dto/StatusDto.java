package com.trueinsurre.dto;

public class StatusDto {

	private Long id;
	private String message;
	private String validateKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getValidateKey() {
		return validateKey;
	}

	public void setValidateKey(String validateKey) {
		this.validateKey = validateKey;
	}

	@Override
	public String toString() {
		return "StatusDto [Id=" + id + ", message=" + message + ", validateKey=" + validateKey + "]";
	}

}
