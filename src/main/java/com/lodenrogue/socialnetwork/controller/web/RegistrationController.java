package com.lodenrogue.socialnetwork.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lodenrogue.socialnetwork.form.RegistrationForm;

@Controller
public class RegistrationController {

	@RequestMapping(path = "/register", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("registrationForm", new RegistrationForm());
		return "register";
	}

}
