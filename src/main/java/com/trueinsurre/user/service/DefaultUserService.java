package com.trueinsurre.user.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.trueinsurre.dto.UserRegisteredDTO;
import com.trueinsurre.modal.User;


public interface DefaultUserService extends UserDetailsService{

	User save(UserRegisteredDTO userRegisteredDTO);
	
}
