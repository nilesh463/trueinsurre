package com.trueinsurre.user.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.trueinsurre.config.CurrentSession;
import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.modal.User;
import com.trueinsurre.user.service.UserService;
import com.trueinsurre.user.serviceImpl.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;



@Controller
public class UserController {

	@Autowired
	private UserService userService;

	
	@Autowired
	CurrentSession session;
	
	@GetMapping("/is-logged-in/{email}")
    public ResponseEntity<Boolean> isLoggedIn(@PathVariable String email) {
        
        return ResponseEntity.ok(userService.isUserLoggedIn(email));
    }

	@GetMapping("/login")
	public String login( Model model) {
		UserDto user = new UserDto();
	    model.addAttribute("user", user);
		return "user/login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		// create model object to store form data
		UserDto user = new UserDto();
	    model.addAttribute("user", user);
		return "register";
	}

	@PostMapping("/register")
	public String registrations(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
		User existingUser = userService.findUserByEmail(userDto.getEmail());

		if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
			result.rejectValue("email", null, "There is already an account registered with the same email");
		}

		 if (result.hasErrors()) {
		 	model.addAttribute("user", userDto);
		 	return "/login";
		 }

		System.out.println(userDto.toString());
		userService.saveUser(userDto);
		return "redirect:/dashboards/dashboard?success";
	}

	@GetMapping("/index")
	public String home(Model model) {
		User user = session.currentUser();
		if(user == null) {
			model.addAttribute("user", user);
			return "index";
		}else {
			model.addAttribute("user", user);
			return "redirect:/dashboards/dashboard?success";
		}
	}
	
	@GetMapping("/admin")
	public String showMasterAdminRegistrationForm(Model model) {
	    // create model object to store form data
	    UserDto user = new UserDto();
	    model.addAttribute("user", user);
	    return "user/registerLogin";
	}
	
	@GetMapping("/delete-profile")
	public String deleteProfile() {
		return "deleteProfile";
	}

	@PostMapping("/admin/save")
	public String masterAdminRegistration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
		
		User existingUser = userService.findUserByEmail(userDto.getEmail());

		if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
			result.rejectValue("email", null, "There is already an account registered with the same email");
		}else {
			userService.saveMasterUser(userDto);
		}
		
		if (!result.hasErrors()) {
			model.addAttribute("user", userDto);
			return "/admin";
		}
	    
	    return "redirect:/login?success";
	}
	
	
	
	@GetMapping("/user-edit/{id}") // updateUser
	public ResponseEntity<User> bgetUserById(@PathVariable("id") long id) {

		return ResponseEntity.ok(userService.edit(id));
	}
	
	@GetMapping("/user-update") 
	public ResponseEntity<String> updateUser(@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "email", required = false) String email ) {
		User user = new User();
		user.setName(name);
		user.setPhone(phone);
		user.setEmail(email);

		return ResponseEntity.ok(userService.updateUser(id,user));
	}
	
	@PostMapping("delete-User/{userId}")
	public ResponseEntity<Responce> deleteUser(@PathVariable Long userId){
		return ResponseEntity.ok(userService.deleteUser(userId));
	}
	
	@Autowired
    private OtpService otpService;

	@PostMapping("/request-delete")
	public ResponseEntity<Responce> requestDelete(@RequestParam String email, @RequestParam String password) throws UnsupportedEncodingException, MessagingException {
		
		Responce responce = new Responce();
    	responce.setStatus(200);
		if(otpService.validateUser(email, password)) {
			otpService.generateOtp(email);
			responce.setMessage("OTP sent to your email.");
		}else {
			responce.setStatus(500);
			responce.setMessage("Invalide email or password!");
		}
		return ResponseEntity.ok(responce);
	}

    @PostMapping("/confirm-delete")
    public ResponseEntity<Responce> confirmDelete(@RequestParam String email, @RequestParam String otp,
            HttpServletRequest request) {
        if (otpService.validateOtp(email, otp)) {
            return ResponseEntity.ok(userService.deleteUserByEmail(email));
        } else {
        	Responce responce = new Responce();
        	responce.setMessage("Invalid or expired OTP.");
        	responce.setStatus(500);
            return ResponseEntity.ok(responce);
        }
    }

}
