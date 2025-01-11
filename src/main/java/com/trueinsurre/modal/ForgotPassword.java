package com.trueinsurre.modal;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ForgotPassword {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String token;
    
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name="user_id")
	private User user;
    
    @Column(nullable=false)
    private LocalDateTime expairTime;
    
    @Column(nullable=false)
    private boolean isUsed;

	public ForgotPassword() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ForgotPassword(Long id, String token, User user, LocalDateTime expairTime, boolean isUsed) {
		super();
		this.id = id;
		this.token = token;
		this.user = user;
		this.expairTime = expairTime;
		this.isUsed = isUsed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpairTime() {
		return expairTime;
	}

	public void setExpairTime(LocalDateTime expairTime) {
		this.expairTime = expairTime;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
    
}
