package de.webalf.slotbot.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Alf
 * @since 27.09.2020
 */
@Controller
public class StartWebController {
	@GetMapping("/")
	public ModelAndView getStart() {
		return new ModelAndView("start");
	}
}
