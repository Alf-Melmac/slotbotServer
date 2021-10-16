package de.webalf.slotbot.controller.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static de.webalf.slotbot.util.ControllerUtils.addLayoutSettings;
import static de.webalf.slotbot.util.GuildUtils.isDAA;

/**
 * @author Alf
 * @since 27.09.2020
 */
@Controller
public class StartWebController {
	@GetMapping("/")
	public ModelAndView getStart() {
		if (isDAA()) {
			return new ModelAndView("redirect:" + ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
		}

		ModelAndView mav = new ModelAndView("start");
		addLayoutSettings(mav);
		return mav;
	}

	@GetMapping("/error/403")
	public ModelAndView getUnauthorized() {
		return new ModelAndView("error/403");
	}
}
