package com.focusservices.library.controller;

import java.util.logging.Logger;
import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;

import com.focusservices.library.config.SecurityHelper;

@Controller
public class MainController {

	static Logger logger = Logger.getLogger(MainController.class.getName());

	@RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
	public String index(Model modelo) {
            if(SecurityHelper.getLoggedInUserDetails() != null) {
            	String user = SecurityHelper.getLoggedInUserDetails().getUsername();
            	modelo.addAttribute("userLogged", user);
            	
            	return "commons/index";
            }
            else return "redirect:/login";
	}

    @RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model modelo) {
            return "commons/login";
	}

}
