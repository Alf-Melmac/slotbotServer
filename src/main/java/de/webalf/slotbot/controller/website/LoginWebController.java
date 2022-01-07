package de.webalf.slotbot.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alf
 * @since 25.10.2020
 */
@Controller
@RequestMapping("/login")
public class LoginWebController {
	@GetMapping
	public ModelAndView getLogin() {
		return new ModelAndView("login");
	}
}
