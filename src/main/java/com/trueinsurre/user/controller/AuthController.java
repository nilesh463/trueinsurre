package com.trueinsurre.user.controller;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trueinsurre.config.JwtUtil;
import com.trueinsurre.dto.AuthRequest;
import com.trueinsurre.dto.AuthResponse;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.modal.User;
import com.trueinsurre.user.repository.RoleRepository;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.user.service.UserService;
import com.trueinsurre.user.serviceImpl.CustomUserDetailsService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity<Responce> registerUser(@RequestBody UserDto user) {
		Responce responce = new Responce();
		if (userRepository.findByEmail(user.getEmail()) != null) {
			responce.setMessage("Email is already in use!");
			responce.setStatus(200);
			return ResponseEntity.badRequest().body(responce);
		}else {
			userService.saveUser(user);
			responce.setMessage("User registered successfully : "+user.getCountryCallingCode());
			responce.setStatus(200);
			
			return ResponseEntity.ok(responce);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> loginUser(@RequestBody AuthRequest authRequest) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Invalid credentials");
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
		final String jwt = jwtUtil.generateToken(userDetails);

		// Save the token to the database
//		Token token = new Token();
		User user = userRepository.findByEmail(authRequest.getEmail());
//		token.setToken(jwt);
//		token.setUser(user);
//		token.setActive(true);
		//tokenRepository.save(token);

		return ResponseEntity.ok(new AuthResponse(jwt,user.getId()));
	}

}
