package com.webapp.gessi.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ClientController {
	
	@RequestMapping({"/"})
	public String loadUI() {
		return "forward:/index.html";
	}

}