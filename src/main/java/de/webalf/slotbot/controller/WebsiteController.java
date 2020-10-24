package de.webalf.slotbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alf
 * @since 27.09.2020
 */
@Controller
public class WebsiteController {

	@GetMapping("/")
	public ModelAndView getStart() {
		return new ModelAndView("start");
	}

	@GetMapping("/login")
	public ModelAndView getLogin() {
		return new ModelAndView("login");
	}

	@GetMapping("/events")
	public ModelAndView getEvent() {
		return new ModelAndView("events");
	}
}
