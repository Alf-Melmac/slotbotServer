package de.webalf.slotbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("startUrl", linkTo(methodOn(WebsiteController.class).getStart()).toUri().toString());
		return mav;
	}
}
