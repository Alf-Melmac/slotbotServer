package de.webalf.slotbot.controller.website;

import de.webalf.slotbot.controller.Urls;
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
		ModelAndView mav = new ModelAndView("login");
		mav.addObject("startUrl", Urls.START_URL);
		return mav;
	}
}
