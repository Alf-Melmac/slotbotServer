package de.webalf.slotbot.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static de.webalf.slotbot.util.ControllerUtils.addLayoutSettings;

/**
 * @author Alf
 * @since 25.10.2020
 */
@Controller
@RequestMapping("/login")
public class LoginWebController {
	@GetMapping
	public ModelAndView getLogin() {
		ModelAndView mav = new ModelAndView("login");
		addLayoutSettings(mav);
		return mav;
	}
}
