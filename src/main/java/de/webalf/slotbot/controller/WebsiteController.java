package de.webalf.slotbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebsiteController {

	@GetMapping("/")
	public ModelAndView getStart() {
		return new ModelAndView("start");
	}

	@GetMapping("/technicalSetup")
	public ModelAndView getTechnicalSetup() {
		return new ModelAndView("technicalSetup");
	}

}
