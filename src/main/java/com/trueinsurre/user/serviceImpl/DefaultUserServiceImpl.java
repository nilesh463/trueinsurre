package com.trueinsurre.user.serviceImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.trueinsurre.dto.UserRegisteredDTO;
import com.trueinsurre.modal.Role;
import com.trueinsurre.modal.User;
import com.trueinsurre.user.repository.RoleRepository;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.user.service.DefaultUserService;

@Service
public class DefaultUserServiceImpl implements DefaultUserService {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepo.findByEmailAndStatus(email, false);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				getAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(List<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}

	@Override
	public User save(UserRegisteredDTO userRegisteredDTO) {
		Role role = roleRepo.findByName("ROLE_EMPLOYEE");

		User user = new User();
		user.setEmail(userRegisteredDTO.getEmail_id());
		user.setName(userRegisteredDTO.getName());
		user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));

		if (role == null) {
			role = checkRoleExist();
		}
		user.setCountry("");
		user.setPhone("");
		return userRepo.save(user);
	}

	public Role checkRoleExist() {
		Role role = new Role();
		role.setName("ROLE_EMPLOYEE");
		return roleRepo.save(role);
	}
}
