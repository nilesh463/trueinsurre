package com.trueinsurre.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.core.model.Model;

//@RequestMapping("/index")
@Controller
public class IndexCntroller {
	
	@GetMapping("/")
	public ModelAndView indexPage(Model model) {
		return new ModelAndView("user/login") ;
	}

}
