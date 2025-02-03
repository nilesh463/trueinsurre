package com.trueinsurre.user.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.modal.User;

public interface UserService {

	void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

	void saveMasterUser(UserDto userDto);

	Page<UserDto> findAllUsers(int page, int size);

	User edit(Long id);

	String updateUser(Long id, User user);

	Responce deleteUser(Long userId);

	void saveUser(User user);
	
	Responce deleteUserByEmail(String userId);

	boolean isUserLoggedIn(String email);
	
}
