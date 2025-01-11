package com.trueinsurre.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.trueinsurre.dto.EmployeeSelectDto;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.modal.User;
@Service
public interface EmployeeService {

	public User findUserByEmail(String email);
	public UserDto findEmployeeById(Long id);
	public void addEmployee(UserDto userDto);
	public Page<UserDto> getEmployeesByRole(String roleName, int page, int size);
	public Responce updateEmployee(UserDto user);
	public Responce deleteEmployee(Long id);
	List<EmployeeSelectDto> getEmployeesListForSelect();
}
