package com.trueinsurre.user.serviceImpl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.trueinsurre.dto.Responce;
import com.trueinsurre.dto.UserDto;
import com.trueinsurre.exceptionHandeler.NotFound;
import com.trueinsurre.modal.Role;
import com.trueinsurre.modal.User;
import com.trueinsurre.user.repository.RoleRepository;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.user.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	JavaMailSender javaMailSender;
	@Autowired
	CustomUserDetailsService userDetailsService;
	
	@Autowired
	private SessionRegistry sessionRegistry;

	// register user
	@Override
	public void saveUser(User user) {
		user.setCreationTime(System.currentTimeMillis());
		Role role = roleRepository.findByName("ROLE_EMPLOYEE");
		if (role == null) {
			role = checkRoleExist();
		}
		user.setRoles(List.of(role)); // Use List.of for a single element list
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setStatus(true); // Assuming new users are active by default
		User userDb = userRepository.save(user);

		UserDto userDto = new UserDto();
		userDto.setCountry(user.getCountry());
		userDto.setPhone(user.getPhone());
		userDto.setEmail(user.getEmail());
		userDto.setFirstName(user.getName());

		// Manually authenticate the user after saving their details
		UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getEmail());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		try {
			sendEmail(userDb.getEmail(), userDb);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// register user
	@Override
	public void saveUser(UserDto userDto) {
		User user = new User();
		String phone = userDto.getPhone();
		if(Objects.nonNull(userDto.getCountryCallingCode())) {
			phone = userDto.getCountryCallingCode()+phone;
		}
		user.setPhone(phone);
		user.setName(userDto.getFirstName() + " " + userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setCountry(userDto.getCountry());
		user.setCreationTime(System.currentTimeMillis());
		user.setAadharNumber(userDto.getAadharNumber());
		user.setEmpNo(userDto.getEmpNo());
		// encrypt the password using spring security
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		Role role = roleRepository.findByName("ROLE_EMPLOYEE");
		if (role == null) {
			role = checkRoleExist();
		}
		user.setRoles(List.of(role)); // Use List.of for a single element list
		// User userDb =
		userRepository.save(user);

		// Manually authenticate the user after saving their details
		UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getEmail());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

//		try {
//			sendEmail(userDb.getEmail(), userDb);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void saveMasterUser(UserDto userDto) {
		User user = new User();
		user.setCountry("India");
		user.setName(userDto.getFirstName() + " " + userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setPhone(userDto.getPhone());
		user.setCreationTime(System.currentTimeMillis());
		// encrypt the password using spring security
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));

		// Check for existing user and handle errors similar to the existing
		// registration method

		// Create the user with ROLE_MASTER_ADMIN
		Role masterAdminRole = roleRepository.findByName("ROLE_ADMIN");
		if (masterAdminRole == null) {
			masterAdminRole = checkMasterAdminRoleExist();
		}
		user.setRoles(List.of(masterAdminRole));
		userRepository.save(user);

	}

	public void sendEmail(String to, User user) throws MessagingException, UnsupportedEncodingException {

		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		String subject = "Welcome to Invoicement - Your Registration Was Successful!";
		String fullName = user.getName();
		if (fullName == null || fullName.isBlank()) {
			fullName = "!";
		}
		String emailContent = "<center><p><img src='cid:invoicementLogo' height='100' width='275'></p></center>"
				+ "<center><h1>Congratulations on Registering Successfully with Invoicement!</h1></center>"
				+ "<p>Welcome to Invoicement! We're thrilled to have you join our platform. As a registered member, you now have access to a suite of powerful tools and features designed to streamline your invoicing and billing processes.</p>"
				+ "<p><h3><b>Support and Assistance:</b></h3></p>"
				+ "<p>If you have any questions or need assistance at any point, our dedicated support team is here to help. Feel free to reach out to us via <a href='mailto:singhnilesh199@gmail.com'>support@interestbudsolutions.com</a> or call us at <a href='tel:+91 8076569119'>+91 8076569119</a> for prompt assistance.</p>"
				+ "<p><h3><b>Stay Connected:</b></h3></p>"
				+ "<p>Stay updated with the latest news, tips, and updates by following us on social media:</p>"
				+ "<p><img src='cid:linkedin'><a href='https://www.linkedin.com/company/interest-bud-solutions-pvt-ltd/mycompany/'>LinkedIn</a></p>"
				+ "<p><b>Warm regards,</b></p>" + "<p><b>The Invoicement Team</b></p>";

		helper.setText(emailContent, true);
		helper.setFrom("support@interestbudsolutions.com", "Invoicemint Support");
		helper.setSubject(subject);
		helper.setTo(to);

		FileSystemResource image = new FileSystemResource(
				new File("/opt/javaProjects/ibsInvoice/images/invoicement.png"));
		helper.addInline("invoicementLogo", image, "image/png");

		FileSystemResource image1 = new FileSystemResource(
				new File("/opt/javaProjects/ibsInvoice/images/linkedin.png"));
		helper.addInline("linkedin", image1, "image/png");

		javaMailSender.send(message);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	// Get All User
	@Override
	public List<UserDto> findAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map((user) -> mapToUserDto(user)).collect(Collectors.toList());
	}

	@Override
	public Page<UserDto> findAllUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<User> userPage = userRepository.findAllByOrderByIdDesc(pageable);

		List<UserDto> userDtos = userPage.getContent().stream().map(this::mapToUserDto).collect(Collectors.toList());

		return new PageImpl<>(userDtos, pageable, userPage.getTotalElements());
	}

	private UserDto mapToUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setFirstName(user.getName());
		userDto.setEmail(user.getEmail());
		userDto.setPhone(user.getPhone());
		userDto.setStatus(user.isStatus());
		userDto.setCountry(user.getCountry());
		userDto.setCreationDate(dateConvert(user.getCreationTime()));
		return userDto;
	}

	private Role checkRoleExist() {
		Role role = new Role();
		role.setName("ROLE_EMPLOYEE");
		return roleRepository.save(role);
	}

	private Role checkMasterAdminRoleExist() {
		Role role = new Role();
		role.setName("ROLE_ADMIN");
		return roleRepository.save(role);
	}

	public String dateConvert(long date) {

		String dateText = null;
		if (date > (long) 0) {
			Date dates = new Date(date);
			SimpleDateFormat dateFormate = new SimpleDateFormat("dd-MM-yyyy");
			dateText = dateFormate.format(dates);
		}
		return dateText;
	}

	@Override
	public User edit(Long id) {
		User userDb = userRepository.findById(id).orElseThrow(() -> new NotFound("No user found by Id: " + id));

		return userDb;
	}

	@Override
	public Responce deleteUser(Long userId) {
		User userDb = userRepository.findById(userId).orElseThrow(() -> new NotFound("No user found by Id: " + userId));
		userDb.setStatus(true);
		userRepository.save(userDb);
		Responce responce = new Responce();
		responce.setMessage("User deleted!");
		responce.setStatus(200);
		return responce;
	}

	@Override
	public String updateUser(Long id, User userDto) {
		String message = "Success";
		User user = userRepository.findById(id).orElse(null);
		if (Objects.nonNull(userDto.getEmail()) && !userDto.getEmail().isBlank()) {
			if (!user.getEmail().equals(userDto.getEmail())) {
				User existingUser = userRepository.findByEmail(userDto.getEmail());
				if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
					message = "Same Email address already exist";
				} else {
					message = "success";
					user.setEmail(userDto.getEmail());
				}
			}
		}

		if (Objects.nonNull(userDto.getName()) && !userDto.getName().isBlank()) {
			user.setName(userDto.getName());
		}
		if (Objects.nonNull(userDto.getCountry()) && !userDto.getCountry().isBlank()) {
			user.setCountry(userDto.getCountry());
		}
		if (Objects.nonNull(userDto.getPhone()) && !userDto.getPhone().isBlank()) {
			user.setPhone(userDto.getPhone());
		}
		userRepository.save(user);
		return message;
	}

	@Override
	public Responce deleteUserByEmail(String email) {
		User userDb = userRepository.findByEmail(email);
		userDb.setStatus(true);
		userRepository.save(userDb);
		// Invalidate the user's session(s)
		expireUserSessions(email);
		Responce responce = new Responce();
		responce.setMessage("User deleted!");
		responce.setStatus(200);
		return responce;
	}

	private void expireUserSessions(String email) {
		// Retrieve all active sessions from the session registry
		for (Object principal : sessionRegistry.getAllPrincipals()) {
			if (principal instanceof org.springframework.security.core.userdetails.User) {
				org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;

				// Match the user's email with the session's principal
				if (user.getUsername().equals(email)) {
					// Expire all sessions associated with this user
					for (SessionInformation sessionInfo : sessionRegistry.getAllSessions(user, false)) {
						sessionInfo.expireNow();
					}
				}
			}
		}
	}
	@Override
	public boolean isUserLoggedIn(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof org.springframework.security.core.userdetails.User)
                .map(principal -> (org.springframework.security.core.userdetails.User) principal)
                .anyMatch(user -> user.getUsername().equals(username));
    }

}
