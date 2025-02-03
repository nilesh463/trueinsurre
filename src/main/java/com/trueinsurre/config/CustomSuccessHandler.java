package com.trueinsurre.config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.trueinsurre.modal.Role;
import com.trueinsurre.modal.User;
import com.trueinsurre.user.repository.RoleRepository;
import com.trueinsurre.user.repository.UserRepository;
import com.trueinsurre.user.service.DefaultUserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler{

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	DefaultUserService userService;
	
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
    private JwtUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {

	    String redirectUrl = determineRedirectUrl(authentication);
	    handleUserRegistration(authentication);

	    // Redirect the user
	    response.sendRedirect(request.getContextPath() + redirectUrl);
	}

	private String determineRedirectUrl(Authentication authentication) {
	    for (GrantedAuthority auth : authentication.getAuthorities()) {
	        if ("ROLE_EMPLOYEE".equals(auth.getAuthority())) {
	            return "/emp-dashboard/";
	        } else if ("ROLE_ADMIN".equals(auth.getAuthority())) {
	            return "/admin-dashboard/";
	        }
	    }
	    return "/dashboards/dashboard"; // Default redirection
	}

	private void handleUserRegistration(Authentication authentication) {
	    if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
	        DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
	        String email = userDetails.getAttribute("email") != null ?
	                       userDetails.getAttribute("email") :
	                       userDetails.getAttribute("login") + "@gmail.com";

	        if (userRepo.findByEmail(email) == null) {
	            User user = new User();
	            user.setEmail(email);
	            user.setName(extractUsername(email));
	            user.setPassword(null); // No password for OAuth users
	            user.setCreationTime(System.currentTimeMillis());
	            user.setCountry("Unknown"); // Default country
	            user.setPhone("");

	            // Assign default role
	            Role role = roleRepository.findByName("ROLE_ADMIN");
	            if (role == null) {
	                role = checkRoleExist();
	            }
	            user.setRoles(Collections.singletonList(role));

	            userRepo.save(user);
	        }
	    }
	}

	
//	@Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        String redirectUrl = null;
//
//        // Determine the user's role
//        for (GrantedAuthority auth : authentication.getAuthorities()) {
//        	System.out.println("Role is: "+auth.getAuthority());
//        	System.out.println("Role is: "+auth.getAuthority());
//            if (auth.getAuthority().equals("ROLE_EMPLOYEE")) {
//                redirectUrl = "/emp-dashboard/";
//                break;
//            } else if (auth.getAuthority().equals("ROLE_ADMIN")) {
//            	redirectUrl = "/admin-dashboard/";
//                break;
//            }
//        }
//
//        if (redirectUrl == null) {
//            // Default redirect if no specific role-based URL found
//            redirectUrl = "/emp-dashboard/";
//        }
//
//        // Perform user registration if necessary
//        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
//            DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
//            String username = userDetails.getAttribute("email") != null ?
//                               userDetails.getAttribute("email") :
//                               userDetails.getAttribute("login") + "@gmail.com";
//
//            // Check if the user already exists in the database
//            if (userRepo.findByEmail(username) == null) {
//                User user = new User();
//                String country = getUserCountry(authentication);
//                
//                user.setCountry(""); // Set default value or handle accordingly
//                
//                user.setEmail(username);
//                user.setName(userDetails.getAttribute("email") != null ?
//                             userDetails.getAttribute("email") :
//                             userDetails.getAttribute("login"));
//                user.setName(extractUsername(username));
//                user.setPassword("****");
//                user.setCreationTime(System.currentTimeMillis());
//                
//                user.setPhone("");
//
//                // Assign role based on the logged in user's role
//                
//                User newUser = userRepo.save(user);
//                Role role = roleRepository.findByName("ROLE_ADMIN");
//                List<Role> roleList = new ArrayList<Role>();
//        		if (role == null) {
//        			role = checkRoleExist();
//        		}
//        		roleList.add(role);
//        		newUser.setRoles(roleList);
//        		userRepo.save(newUser);
//				/*
//				 * try { sendEmail(username,newUser); } catch (UnsupportedEncodingException e) {
//				 * // TODO Auto-generated catch block e.printStackTrace(); } catch
//				 * (MessagingException e) { // TODO Auto-generated catch block
//				 * e.printStackTrace(); }
//				 */
//            }
//        }
//        // Redirect the user based on the determined URL
//        response.sendRedirect(request.getContextPath() + redirectUrl);
//    }

    private void handleWebLogin(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        String redirectUrl = null;

        // Determine the user's role
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (auth.getAuthority().equals("ROLE_EMPLOYEE")) {
            	
                redirectUrl = "/emp-dashboard/";
                break;
            } else if (auth.getAuthority().equals("ROLE_ADMIN")) {
            	
                redirectUrl = "/admin-dashboard/";
                break;
            }
        }

        if (redirectUrl == null) {
            redirectUrl = "/dashboards/dashboard";
        }

        // Redirect the user based on the determined URL
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    private void handleMobileLogin(HttpServletRequest request, HttpServletResponse response,
                                   Authentication authentication) throws IOException {
        String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
    }
	

	
	private Role checkRoleExist() {
		Role role = new Role();
		role.setName("ROLE_ADMIN");
		return roleRepository.save(role);
	}
	
	public static String extractUsername(String email) {
        int atIndex = email.indexOf('@'); // Find the position of '@' in the email
        if (atIndex != -1) { // Check if '@' is found
            return email.substring(0, atIndex); // Extract substring from start to '@' (excluding '@')
        } else {
            return email; // Return the full email if '@' is not found (though this case is unlikely)
        }
    }
	
	public static String getUserCountry(Authentication authentication) {
        String country = "";

        if (authentication != null && authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();

            // Extract user attributes from OAuth2 user details
            Map<String, Object> attributes = userDetails.getAttributes();

            // Check if 'country' attribute is available in user details
            if (attributes.containsKey("country")) {
                Object countryObj = attributes.get("country");
                if (countryObj instanceof String) {
                    country = (String) countryObj;
                }
            }
        }

        return country;
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

}
