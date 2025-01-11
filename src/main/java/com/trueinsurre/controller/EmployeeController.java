package com.trueinsurre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trueinsurre.config.CurrentSession;
import com.trueinsurre.dto.EmployeeSelectDto;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.modal.User;
import com.trueinsurre.service.EmployeeService;

@Controller
@RequestMapping("emp/")
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	@Autowired
	CurrentSession session;

	@GetMapping("/")
	public String dashboard(Model model) {
		User user = session.currentUser();
		model.addAttribute("user", user);
		return "admin/employees";
	}

	@PostMapping("/add")
	public ResponseEntity<Responce> registerUser(@RequestBody UserDto user) {
		Responce responce = new Responce();
		if (employeeService.findUserByEmail(user.getEmail()) != null && user.getId() == null) {
			responce.setMessage("Email is already in use!");
			responce.setStatus(200);
			return ResponseEntity.badRequest().body(responce);
		} else {
			employeeService.addEmployee(user);
			responce.setMessage("User registered successfully : " + user.getCountryCallingCode());
			responce.setStatus(200);
			return ResponseEntity.ok(responce);
		}
	}
	
	@GetMapping("/select")
	public ResponseEntity<List<EmployeeSelectDto>> getEmployeeForSelect(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		return ResponseEntity.ok(employeeService.getEmployeesListForSelect());
	}

	@GetMapping("/list")
	public ResponseEntity<Page<UserDto>> getEmployeeByRole(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<UserDto> users = employeeService.getEmployeesByRole("ROLE_EMPLOYEE", page, size);
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/edit/{id}")
	public ResponseEntity<UserDto> editEmployee(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.findEmployeeById(id));
	}
	
	@PostMapping("/delete/{id}")
	public ResponseEntity<Responce> deleteEmployee(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.deleteEmployee(id));
	}
}
