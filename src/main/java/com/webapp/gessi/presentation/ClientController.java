package com.webapp.gessi.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ClientController {
	
	@RequestMapping({"/ui"})
	public String loadUI() {
		return "forward:/ui/index.html";
	}
	
	@RequestMapping({"/ui/forgotPassword"})
	public String loadUIa() {
		return "forward:/ui/index.html";
	}
	
}