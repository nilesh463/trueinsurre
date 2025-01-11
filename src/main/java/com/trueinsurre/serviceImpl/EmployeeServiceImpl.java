package com.trueinsurre.serviceImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trueinsurre.dto.EmployeeSelectDto;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.exceptionHandeler.NotFound;
import com.trueinsurre.modal.User;
import com.trueinsurre.service.EmployeeService;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.user.service.UserService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	} 
	
	@Override
	public void addEmployee(UserDto userDto) {
		System.out.println("....! "+userDto.getId());
		System.out.println("....! "+userDto.getId());
		System.out.println("....! "+userDto.getId());
		if(userDto.getId()== null) {
			userService.saveUser(userDto);
		} else {
			updateEmployee(userDto);
			
		}
	}

	@Override
	public Page<UserDto> getEmployeesByRole(String roleName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRoleName(roleName, pageable).map(this::mapDataToDto);
    }
	
	@Override
	public List<EmployeeSelectDto> getEmployeesListForSelect() {
	    return userRepository.findByRoleName("ROLE_EMPLOYEE")
	                         .stream() // Stream the List<User>
	                         .map(this::mapToDto) // Map each User to EmployeeSelectDto
	                         .collect(Collectors.toList()); // Collect back to List<EmployeeSelectDto>
	}

	private EmployeeSelectDto mapToDto(User user) {
	    EmployeeSelectDto dto = new EmployeeSelectDto();
	    String emp = user.getName();
	    dto.setId(user.getId());
	    if (Objects.nonNull(user.getEmpNo()) && !user.getEmpNo().isEmpty() && !user.getEmpNo().equals("N/A")) {
	        emp = emp + " " + user.getEmpNo();
	    }
	    dto.setEmp(emp);
	    return dto;
	}

	
	@SuppressWarnings("static-access")
	public UserDto mapDataToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setFirstName(user.getName());
		dto.setLastName(user.getName());
		dto.setCountry(user.getCountry());
		dto.setEmail(user.getEmail());
		dto.setPhone(user.getPhone());
		dto.setEmpNo(user.getEmpNo());
		dto.setAadharNumber(user.getAadharNumber());
		return dto;
	}

	@Override
	public UserDto findEmployeeById(Long id) {
		return mapDataToDto(userRepository.findByIdAndStatusFalse(id));
	}
	
	@Override
	public Responce deleteEmployee(Long id) {
		Responce responce = new Responce();
		responce.setMessage("Employee deleted..!");
		responce.setStatus(200);
		
		User user = userRepository.findById(id).orElseThrow(()-> new NotFound("Not able to delete..!"));
		user.setStatus(true);
		user.setDeleteTime(System.currentTimeMillis());
		
		userRepository.save(user);
		
		return responce;
	}
	
	@Override
	public Responce updateEmployee(UserDto userDto) {
		Responce responce = new Responce();
		responce.setMessage("Employee details Update..!");
		responce.setStatus(200);
		User user = userRepository.findById(userDto.getId()).orElse(null);
		if (Objects.nonNull(userDto.getEmail()) && !userDto.getEmail().isBlank()) {
			if (!user.getEmail().equals(userDto.getEmail())) {
				User existingUser = userRepository.findByEmail(userDto.getEmail());
				if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
					responce.setMessage("Same Email address already exist");
					responce.setStatus(401);
				} else {
					user.setEmail(userDto.getEmail());
					if (Objects.nonNull(userDto.getFirstName()) && !userDto.getFirstName().isBlank()) {
						user.setName(userDto.getFirstName()+" "+userDto.getLastName());
					}
					
					if (Objects.nonNull(userDto.getCountry()) && !userDto.getCountry().isBlank()) {
						user.setCountry(userDto.getCountry());
					}
					
					if (Objects.nonNull(userDto.getPhone()) && !userDto.getPhone().isBlank()) {
						user.setPhone(userDto.getPhone());
					}
					
					if (Objects.nonNull(userDto.getPassword()) && !userDto.getPassword().isBlank()) {
						user.setPassword(passwordEncoder.encode(userDto.getPassword()));  
					}
					
					if (Objects.nonNull(userDto.getPassword()) && !userDto.getPassword().isBlank()) {
						user.setPassword(passwordEncoder.encode(userDto.getPassword()));  
					}
					
					if (Objects.nonNull(userDto.getEmpNo()) && !userDto.getEmpNo().isBlank()) {
						user.setEmpNo(userDto.getEmpNo());  
					}
					
					if (Objects.nonNull(userDto.getAadharNumber()) && !userDto.getAadharNumber().isBlank()) {
						user.setAadharNumber(userDto.getAadharNumber());  
					}

					userRepository.save(user);
				}
			}
		}
		
		return responce;
	}
}
