package com.trueinsurre.dto;

public class AuthResponse {

	private String jwt;
	private Long userId;

	public AuthResponse(String jwt2, Long userId) {
		this.jwt = jwt2;
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	@Override
	public String toString() {
		return "AuthResponse [jwt=" + jwt + "]";
	}

}
