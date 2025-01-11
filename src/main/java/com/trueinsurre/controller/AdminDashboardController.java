package com.trueinsurre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.trueinsurre.config.CurrentSession;
import com.trueinsurre.modal.User;

@RequestMapping("/admin-dashboard")
@Controller
public class AdminDashboardController {
	
	@Autowired
	CurrentSession session;
	
	@GetMapping("/")
	public String dashboard(Model model) {
		User user = session.currentUser();
		model.addAttribute("user", user);
	    return "admin/adminDashboard";
	}

}
