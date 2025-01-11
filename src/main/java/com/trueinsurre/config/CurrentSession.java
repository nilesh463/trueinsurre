package com.trueinsurre.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import com.trueinsurre.modal.User;
import com.trueinsurre.user.repository.UserRepository;

@Service
public class CurrentSession {
	
	@Autowired
	public UserRepository userRepo;
	
	public long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDb = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername(); 
            User user = userRepo.findByEmail(username);
            long id = user.getId();
            return id;
        } else {
        	 userDb= currentUser();
    		return userDb.getId();
        	//return 0;
        }
    }
	
	public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDb = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername(); 
            userDb = userRepo.findByEmail(username);
            return userDb;
        } else {
        	SecurityContext securityContext = SecurityContextHolder.getContext();
        	if(securityContext.getAuthentication().getPrincipal() instanceof DefaultOAuth2User) {
        	DefaultOAuth2User user = (DefaultOAuth2User) securityContext.getAuthentication().getPrincipal();
        	String username = user.getName(); 
            userDb = userRepo.findByEmail(username);
        	
        	return userDb;
        	}else {
//        		User user = (User) securityContext.getAuthentication().getPrincipal();
//        		userDb = userRepo.findByEmail(user.getEmail());
//        		
//        		System.out.println("Login With Google: "+userDb);
//        		System.out.println("Login With Google: "+userDb);
//        		System.out.println("Login With Google: "+userDb);
//        		System.out.println("Login With Google: "+userDb);
        		return null;
        	}
//        	return null;
        }
    }
	
	
	
//	public User currentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User userDb = null;
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername(); 
//            userDb = userRepo.findByEmail(username);
//            return userDb;
//        } else {
//        	
//            return null;
//        }
//    }
}


