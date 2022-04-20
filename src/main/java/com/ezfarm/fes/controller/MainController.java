package com.ezfarm.fes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RequestMapping("/")
public class MainController {
	
	Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("")
	public String Home(Model model) {
		
		return "main/main";
	}
	
	
}
